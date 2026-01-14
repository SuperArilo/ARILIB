package com.tty.lib.services;

import com.tty.lib.Log;
import com.tty.lib.dto.PageResult;
import com.tty.lib.entity.PageKey;
import com.tty.lib.tool.BaseDataManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一实体缓存 + 分页缓存管理
 * @param <K> 复合 Key 类型
 * @param <T> 实体类型
 */
public abstract class EntityRepository<K, T> {

    protected final BaseDataManager<K, T> manager;

    /** 单实体缓存：K -> T */
    private final Map<K, T> entityCache = new ConcurrentHashMap<>();

    /** 分页缓存：PageKey -> PageResult<T> */
    private final Map<PageKey<K>, PageResult<T>> pageCache = new ConcurrentHashMap<>();

    /** 查询条件到分页的映射：查询条件 -> PageKey集合 */
    private final Map<K, Set<PageKey<K>>> queryToPages = new ConcurrentHashMap<>();

    public EntityRepository(BaseDataManager<K, T> manager) {
        this.manager = manager;
        debug("EntityRepository initialized with manager: {}", manager.getClass().getSimpleName());
    }

    /**
     * 从实体中提取缓存键（用于单实体缓存）
     * @param entity 具体实体
     * @return 返回单独查询这个实体需要的key
     */
    protected @NotNull
    abstract K extractCacheKey(T entity);

    /**
     * 从实体中提取分页查询键（用于分页缓存关联）
     * @param entity 具体实体
     * @return 返回查询这个实体列表需要的key
     */
    protected abstract K extractPageQueryKey(T entity);

    protected final void debug(String format, Object... args) {
        Object[] merged = new Object[args.length + 1];
        merged[0] = getClass().getSimpleName();
        System.arraycopy(args, 0, merged, 1, args.length);

        Log.debug("[{}] " + format, merged);
    }

    public CompletableFuture<T> get(K key) {
        T cached = this.entityCache.get(key);
        if (cached != null) {
            debug("Entity cache hit for key: {}", key);
            return CompletableFuture.completedFuture(cached);
        }

        debug("Entity cache miss for key: {}, querying from DB", key);

        if (this.manager == null) {
            debug("Manager is null, returning null for key: {}", key);
            return CompletableFuture.completedFuture(null);
        }

        return this.manager.getInstance(key).thenApply(entity -> {
            if (entity != null) {
                this.entityCache.put(key, entity);
                debug("Entity cached for key: {}", key);
            } else {
                debug("Entity not found for key: {}", key);
            }
            return entity;
        });
    }

    /** 创建新实体 */
    public CompletableFuture<T> create(T entity) {
        debug("Creating new entity: {}", entity);
        return this.manager.createInstance(entity).thenApply(createdEntity -> {
            if (createdEntity != null) {
                K cacheKey = this.extractCacheKey(createdEntity);
                this.entityCache.put(cacheKey, createdEntity);
                debug("New entity cached with key: {}", cacheKey);
                this.handleCreateForAscendingOrder(createdEntity);
                debug("Entity created and cached successfully: {}", cacheKey);
            } else {
                debug("Entity creation failed for: {}", entity);
            }
            return createdEntity;
        });
    }

    public CompletableFuture<Boolean> update(T entity) {
        K cacheKey = this.extractCacheKey(entity);
        debug("updating entity with key: {}", cacheKey);
        return this.manager.modify(entity).thenApply(success -> {
            if (success) {
                this.entityCache.put(cacheKey, entity);
                debug("entity updated in cache: {}", cacheKey);
                this.invalidateRelatedPages(entity);
            } else {
                debug("entity update failed for key: {}", cacheKey);
            }
            return success;
        });
    }

    public CompletableFuture<Boolean> delete(T entity) {
        K cacheKey = this.extractCacheKey(entity);
        return this.manager.getInstance(cacheKey).thenCompose(e -> {
            if (e == null) {
                debug("entity not found for deletion, key: {}", cacheKey);
                return CompletableFuture.completedFuture(false);
            }

            return this.manager.deleteInstance(e).thenApply(success -> {
                if (success) {
                    this.entityCache.remove(cacheKey);
                    debug("entity removed from cache: {}", cacheKey);
                    this.invalidateRelatedPages(e);
                } else {
                    debug("entity deletion failed for key: {}", cacheKey);
                }
                return success;
            });
        });
    }

    public CompletableFuture<PageResult<T>> getList(int pageNum, int pageSize, K queryCondition) {
        PageKey<K> pageKey = new PageKey<>(pageNum, pageSize, queryCondition);

        PageResult<T> cached = this.pageCache.get(pageKey);
        if (cached != null) {
            debug("page cache hit for pageKey: {}, page: {}, size: {}, condition: {}", pageKey, String.valueOf(pageNum), String.valueOf(pageSize), queryCondition);
            return CompletableFuture.completedFuture(cached);
        }

        debug("page cache miss for pageKey: {}, page: {}, size: {}, condition: {}, querying from DB", pageKey, String.valueOf(pageNum), String.valueOf(pageSize), queryCondition);

        if (this.manager == null) {
            debug("manager is null, returning empty page result");
            return CompletableFuture.completedFuture(PageResult.build(Collections.emptyList(), 0, 0, pageNum));
        }

        return this.manager.getList(pageNum, pageSize, queryCondition).thenApply(result -> {
            if (result == null) {
                debug("query returned null result for pageKey: {}", pageKey);
                return null;
            }

            this.pageCache.put(pageKey, result);
            debug("page cached for pageKey: {}, record count: {}", pageKey, String.valueOf(result.getRecords().size()));

            this.queryToPages.computeIfAbsent(queryCondition, k -> ConcurrentHashMap.newKeySet()).add(pageKey);
            debug("added pageKey mapping for query condition: {} -> {}", queryCondition, pageKey);

            int entityCachedCount = 0;
            for (T entity : result.getRecords()) {
                K entityKey = this.extractCacheKey(entity);
                this.entityCache.put(entityKey, entity);
                entityCachedCount++;
            }

            if (entityCachedCount > 0) {
                debug("cached {} entities from page result", String.valueOf(entityCachedCount));
            }

            return result;
        });
    }

    /**
     * 使相关分页缓存失效
     */
    private void invalidateRelatedPages(T entity) {
        if (entity == null) {
            debug("Cannot invalidate pages for null entity");
            return;
        }

        K pageQueryKey = this.extractPageQueryKey(entity);
        if (pageQueryKey == null) {
            debug("entity type does not require page cache invalidation");
            return;
        }

        debug("invalidating pages related to entity with query key: {}", pageQueryKey);
        this.invalidatePagesByQueryKey(pageQueryKey);
    }

    /** 根据查询键使相关分页缓存失效 */
    private void invalidatePagesByQueryKey(K pageQueryKey) {
        Set<PageKey<K>> relatedPages = this.queryToPages.get(pageQueryKey);
        if (relatedPages == null || relatedPages.isEmpty()) {
            debug("No related pages found for query key: {}", pageQueryKey);
            return;
        }

        int invalidatedCount = 0;
        // 失效所有相关分页缓存
        for (PageKey<K> pageKey : relatedPages) {
            this.pageCache.remove(pageKey);
            invalidatedCount++;
        }
        relatedPages.clear();

        debug("invalidated {} pages for query key: {}", String.valueOf(invalidatedCount), pageQueryKey);
    }

    /** 处理自增ID升序排列时的创建操作 */
    private void handleCreateForAscendingOrder(T newEntity) {
        K pageQueryKey = this.extractPageQueryKey(newEntity);
        Set<PageKey<K>> relatedPages = this.queryToPages.get(pageQueryKey);

        if (relatedPages == null || relatedPages.isEmpty()) {
            debug("no cached pages found for new entity, query key: {}", pageQueryKey);
            return;
        }

        // 查找最大的页码
        int maxPageNum = 0;
        for (PageKey<K> pageKey : relatedPages) {
            if (pageKey.pageNum() > maxPageNum) {
                maxPageNum = pageKey.pageNum();
            }
        }

        // 查找最后一页
        PageKey<K> lastPageKey = null;
        for (PageKey<K> pageKey : relatedPages) {
            if (pageKey.pageNum() == maxPageNum) {
                lastPageKey = pageKey;
                break;
            }
        }

        if (lastPageKey != null) {
            PageResult<T> lastPage = this.pageCache.get(lastPageKey);
            if (lastPage != null) {
                int pageSize = lastPageKey.pageSize();
                int recordCount = lastPage.getRecords().size();

                if (recordCount < pageSize) {
                    this.pageCache.remove(lastPageKey);
                    relatedPages.remove(lastPageKey);
                    debug("last page not full ({}/{}), invalidated only last page: {}", String.valueOf(recordCount), String.valueOf(pageSize), lastPageKey);
                } else {
                    this.invalidatePagesByQueryKey(pageQueryKey);
                    debug("last page is full ({}/{}), invalidated all pages for query: {}", String.valueOf(recordCount), String.valueOf(pageSize), pageQueryKey);
                }
            } else {
                debug("last page cache entry not found for pageKey: {}", lastPageKey);
            }
        } else {
            debug("no last page key found for query: {}", pageQueryKey);
        }
    }

    public void setExecutionMode(boolean value) {
        this.manager.setExecutionMode(value);
    }

    public boolean isAsync() {
        return this.manager.isAsync;
    }

    /** 直接获取数据，不会缓存 */
    public CompletableFuture<PageResult<T>> getAllForCheck(K queryCondition) {
        debug("direct query (no cache) for all records with condition: {}", queryCondition);
        return this.manager.getList(1, Integer.MAX_VALUE, queryCondition).thenApply(result -> result);
    }

    public void clearEntityCache() {
        int size = this.entityCache.size();
        this.entityCache.clear();
        debug("cleared entity cache, removed {} entities", String.valueOf(size));
    }

    public void clearPageCache() {
        int pageCount = this.pageCache.size();
        this.pageCache.clear();
        this.queryToPages.clear();
        debug("cleared page cache, removed {} pages", String.valueOf(pageCount));
    }

    public void clearAllCache() {
        int entityCount = this.entityCache.size();
        int pageCount = this.pageCache.size();

        this.clearEntityCache();
        this.clearPageCache();

        debug("cleared all cache, removed {} entities and {} pages", String.valueOf(entityCount), String.valueOf(pageCount));
    }

}