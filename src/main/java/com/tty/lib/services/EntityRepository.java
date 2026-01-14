package com.tty.lib.services;

import com.tty.lib.Log;
import com.tty.lib.dto.PageResult;
import com.tty.lib.entity.cache.PageKey;
import com.tty.lib.tool.BaseDataManager;

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
        debug("EntityRepository initialized with manager: %s", manager.getClass().getSimpleName());
    }

    /** 从实体中提取缓存键（用于单实体缓存） */
    protected abstract K extractCacheKey(T entity);

    /** 从实体中提取分页查询键（用于分页缓存关联） */
    protected abstract K extractPageQueryKey(T entity);

    private void debug(String format, Object... args) {
        String className = this.getClass().getSimpleName();
        String message = String.format(format, args);
        Log.debug("[%s] %s", className, message);
    }

    public CompletableFuture<T> get(K key) {
        T cached = this.entityCache.get(key);
        if (cached != null) {
            debug("Entity cache hit for key: %s", key);
            return CompletableFuture.completedFuture(cached);
        }

        debug("Entity cache miss for key: %s, querying from DB", key);

        if (this.manager == null) {
            debug("Manager is null, returning null for key: %s", key);
            return CompletableFuture.completedFuture(null);
        }

        return this.manager.getInstance(key).thenApply(entity -> {
            if (entity != null) {
                this.entityCache.put(key, entity);
                debug("Entity cached for key: %s", key);
            } else {
                debug("Entity not found for key: %s", key);
            }
            return entity;
        });
    }

    /** 创建新实体 */
    public CompletableFuture<T> create(T entity) {
        debug("Creating new entity: %s", entity);
        return this.manager.createInstance(entity).thenApply(createdEntity -> {
            if (createdEntity != null) {
                K cacheKey = this.extractCacheKey(createdEntity);
                this.entityCache.put(cacheKey, createdEntity);
                debug("New entity cached with key: %s", cacheKey);
                this.handleCreateForAscendingOrder(createdEntity);
                debug("Entity created and cached successfully: %s", cacheKey);
            } else {
                debug("Entity creation failed for: %s", entity);
            }
            return createdEntity;
        });
    }

    public CompletableFuture<Boolean> update(T entity) {
        K cacheKey = this.extractCacheKey(entity);
        debug("updating entity with key: %s", cacheKey);
        return this.manager.modify(entity).thenApply(success -> {
            if (success) {
                this.entityCache.put(cacheKey, entity);
                debug("entity updated in cache: %s", cacheKey);
                this.invalidateRelatedPages(entity);
            } else {
                debug("entity update failed for key: %s", cacheKey);
            }
            return success;
        });
    }

    public CompletableFuture<Boolean> delete(T entity) {
        K cacheKey = this.extractCacheKey(entity);
        return this.manager.getInstance(cacheKey).thenCompose(e -> {
            if (e == null) {
                debug("entity not found for deletion, key: %s", cacheKey);
                return CompletableFuture.completedFuture(false);
            }

            return this.manager.deleteInstance(e).thenApply(success -> {
                if (success) {
                    this.entityCache.remove(cacheKey);
                    debug("entity removed from cache: %s", cacheKey);
                    this.invalidateRelatedPages(e);
                } else {
                    debug("entity deletion failed for key: %s", cacheKey);
                }
                return success;
            });
        });
    }

    public CompletableFuture<PageResult<T>> getList(int pageNum, int pageSize, K queryCondition) {
        PageKey<K> pageKey = new PageKey<>(pageNum, pageSize, queryCondition);

        PageResult<T> cached = this.pageCache.get(pageKey);
        if (cached != null) {
            debug("page cache hit for pageKey: %s, page: %s, size: %s, condition: %s", pageKey, String.valueOf(pageNum), String.valueOf(pageSize), queryCondition);
            return CompletableFuture.completedFuture(cached);
        }

        debug("page cache miss for pageKey: %s, page: %s, size: %s, condition: %s, querying from DB", pageKey, String.valueOf(pageNum), String.valueOf(pageSize), queryCondition);

        if (this.manager == null) {
            debug("manager is null, returning empty page result");
            return CompletableFuture.completedFuture(PageResult.build(Collections.emptyList(), 0, 0, pageNum));
        }

        return this.manager.getList(pageNum, pageSize, queryCondition).thenApply(result -> {
            if (result == null) {
                debug("query returned null result for pageKey: %s", pageKey);
                return null;
            }

            this.pageCache.put(pageKey, result);
            debug("page cached for pageKey: %s, record count: %s", pageKey, String.valueOf(result.getRecords().size()));

            this.queryToPages.computeIfAbsent(queryCondition, k -> ConcurrentHashMap.newKeySet()).add(pageKey);
            debug("added pageKey mapping for query condition: %s -> %s", queryCondition, pageKey);

            int entityCachedCount = 0;
            for (T entity : result.getRecords()) {
                K entityKey = this.extractCacheKey(entity);
                this.entityCache.put(entityKey, entity);
                entityCachedCount++;
            }

            if (entityCachedCount > 0) {
                debug("cached %s entities from page result", String.valueOf(entityCachedCount));
            }

            return result;
        });
    }

    /**
     * 使相关分页缓存失效
     */
    private void invalidateRelatedPages(T entity) {
        K pageQueryKey = this.extractPageQueryKey(entity);
        debug("invalidating pages related to entity with query key: %s", pageQueryKey);
        this.invalidatePagesByQueryKey(pageQueryKey);
    }

    /** 根据查询键使相关分页缓存失效 */
    private void invalidatePagesByQueryKey(K pageQueryKey) {
        Set<PageKey<K>> relatedPages = this.queryToPages.get(pageQueryKey);
        if (relatedPages == null || relatedPages.isEmpty()) {
            debug("No related pages found for query key: %s", pageQueryKey);
            return;
        }

        int invalidatedCount = 0;
        // 失效所有相关分页缓存
        for (PageKey<K> pageKey : relatedPages) {
            this.pageCache.remove(pageKey);
            invalidatedCount++;
        }
        relatedPages.clear();

        debug("invalidated %s pages for query key: %s", String.valueOf(invalidatedCount), pageQueryKey);
    }

    /** 处理自增ID升序排列时的创建操作 */
    private void handleCreateForAscendingOrder(T newEntity) {
        K pageQueryKey = this.extractPageQueryKey(newEntity);
        Set<PageKey<K>> relatedPages = this.queryToPages.get(pageQueryKey);

        if (relatedPages == null || relatedPages.isEmpty()) {
            debug("no cached pages found for new entity, query key: %s", pageQueryKey);
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
                    debug("last page not full (%s/%s), invalidated only last page: %s", String.valueOf(recordCount), String.valueOf(pageSize), lastPageKey);
                } else {
                    this.invalidatePagesByQueryKey(pageQueryKey);
                    debug("last page is full (%s/%s), invalidated all pages for query: %s", String.valueOf(recordCount), String.valueOf(pageSize), pageQueryKey);
                }
            } else {
                debug("last page cache entry not found for pageKey: %s", lastPageKey);
            }
        } else {
            debug("no last page key found for query: %s", pageQueryKey);
        }
    }

    /** 直接获取数据，不会缓存 */
    public CompletableFuture<PageResult<T>> getAllForCheck(K queryCondition) {
        debug("direct query (no cache) for all records with condition: %s", queryCondition);
        return this.manager.getList(1, Integer.MAX_VALUE, queryCondition).thenApply(result -> result);
    }

    public void clearEntityCache() {
        int size = this.entityCache.size();
        this.entityCache.clear();
        debug("cleared entity cache, removed %s entities", String.valueOf(size));
    }

    public void clearPageCache() {
        int pageCount = this.pageCache.size();
        this.pageCache.clear();
        this.queryToPages.clear();
        debug("cleared page cache, removed %s pages", String.valueOf(pageCount));
    }

    public void clearAllCache() {
        int entityCount = this.entityCache.size();
        int pageCount = this.pageCache.size();

        this.clearEntityCache();
        this.clearPageCache();

        debug("cleared all cache, removed %s entities and %s pages", String.valueOf(entityCount), String.valueOf(pageCount));
    }

}