package com.tty.lib.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tty.lib.Log;
import com.tty.lib.dto.PageResult;
import com.tty.lib.dto.QueryKey;
import com.tty.lib.entity.PageKey;
import com.tty.lib.tool.BaseDataManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityRepository<T> {

    protected final BaseDataManager<T> manager;

    private final Map<QueryKey<T>, T> entityCache = new ConcurrentHashMap<>();

    private final Map<PageKey<QueryKey<T>>, PageResult<T>> pageCache = new ConcurrentHashMap<>();

    private final Map<QueryKey<T>, Set<PageKey<QueryKey<T>>>> queryToPages = new ConcurrentHashMap<>();

    public EntityRepository(BaseDataManager<T> manager) {
        this.manager = manager;
        debug("EntityRepository initialized with manager: {}", manager.getClass().getSimpleName());
    }

    /**
     * 从实体中提取缓存键（返回LambdaQueryWrapper，内部会包装为QueryKey）
     */
    protected abstract @NotNull LambdaQueryWrapper<T> extractCacheKey(T entity);

    /**
     * 从实体中提取分页查询键
     */
    protected abstract LambdaQueryWrapper<T> extractPageQueryKey(T entity);

    /**
     * 将LambdaQueryWrapper包装为QueryKey
     */
    protected QueryKey<T> wrapQueryKey(LambdaQueryWrapper<T> wrapper) {
        return QueryKey.of(wrapper);
    }

    protected final void debug(String format, Object... args) {
        Object[] merged = new Object[args.length + 1];
        merged[0] = getClass().getSimpleName();
        System.arraycopy(args, 0, merged, 1, args.length);
        Log.debug("[{}] " + format, merged);
    }

    public CompletableFuture<T> get(LambdaQueryWrapper<T> key) {
        QueryKey<T> queryKey = this.wrapQueryKey(key);
        T cached = this.entityCache.get(queryKey);
        if (cached != null) {
            debug("Entity cache hit for key: {}", queryKey);
            return CompletableFuture.completedFuture(cached);
        }

        debug("Entity cache miss for key: {}, querying from DB", queryKey);

        if (this.manager == null) {
            debug("Manager is null, returning null for key: {}", queryKey);
            return CompletableFuture.completedFuture(null);
        }

        return this.manager.getInstance(key).thenApply(entity -> {
            if (entity != null) {
                this.entityCache.put(queryKey, entity);
                debug("Entity cached for key: {}", queryKey);
            } else {
                debug("Entity not found for key: {}", queryKey);
            }
            return entity;
        });
    }

    public CompletableFuture<T> create(T entity) {
        debug("Creating new entity: {}", entity);
        return this.manager.createInstance(entity).thenApply(createdEntity -> {
            if (createdEntity != null) {
                LambdaQueryWrapper<T> cacheKeyWrapper = this.extractCacheKey(createdEntity);
                QueryKey<T> cacheKey = this.wrapQueryKey(cacheKeyWrapper);
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
        LambdaQueryWrapper<T> cacheKeyWrapper = this.extractCacheKey(entity);
        QueryKey<T> cacheKey = this.wrapQueryKey(cacheKeyWrapper);
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
        LambdaQueryWrapper<T> cacheKeyWrapper = this.extractCacheKey(entity);
        QueryKey<T> cacheKey = this.wrapQueryKey(cacheKeyWrapper);

        return this.manager.getInstance(cacheKeyWrapper).thenCompose(e -> {
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

    public CompletableFuture<PageResult<T>> getList(int pageNum, int pageSize,
                                                    LambdaQueryWrapper<T> queryCondition) {
        QueryKey<T> queryKey = this.wrapQueryKey(queryCondition);
        PageKey<QueryKey<T>> pageKey = new PageKey<>(pageNum, pageSize, queryKey);

        PageResult<T> cached = this.pageCache.get(pageKey);
        if (cached != null) {
            debug("page cache hit for pageKey: {}", pageKey);
            return CompletableFuture.completedFuture(cached);
        }

        debug("page cache miss for pageKey: {}, querying from DB", pageKey);

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
            debug("page cached for pageKey: {}, record count: {}", pageKey, result.getRecords().size());

            this.queryToPages.computeIfAbsent(queryKey, k -> ConcurrentHashMap.newKeySet()).add(pageKey);
            debug("added pageKey mapping for query condition: {} -> {}", queryKey, pageKey);

            int entityCachedCount = 0;
            for (T entity : result.getRecords()) {
                LambdaQueryWrapper<T> entityKeyWrapper = this.extractCacheKey(entity);
                QueryKey<T> entityKey = this.wrapQueryKey(entityKeyWrapper);
                this.entityCache.put(entityKey, entity);
                entityCachedCount++;
            }

            if (entityCachedCount > 0) {
                debug("cached {} entities from page result", entityCachedCount);
            }

            return result;
        });
    }

    private void invalidateRelatedPages(T entity) {
        if (entity == null) {
            debug("Cannot invalidate pages for null entity");
            return;
        }

        LambdaQueryWrapper<T> pageQueryKeyWrapper = this.extractPageQueryKey(entity);
        if (pageQueryKeyWrapper == null) {
            debug("entity type does not require page cache invalidation");
            return;
        }

        QueryKey<T> queryKey = this.wrapQueryKey(pageQueryKeyWrapper);
        debug("invalidating pages related to entity with query key: {}", queryKey);
        this.invalidatePagesByQueryKey(queryKey);
    }

    private void invalidatePagesByQueryKey(QueryKey<T> queryKey) {
        Set<PageKey<QueryKey<T>>> relatedPages = this.queryToPages.get(queryKey);
        if (relatedPages == null || relatedPages.isEmpty()) {
            debug("No related pages found for query key: {}", queryKey);
            return;
        }

        int invalidatedCount = 0;
        for (PageKey<QueryKey<T>> pageKey : relatedPages) {
            this.pageCache.remove(pageKey);
            invalidatedCount++;
        }
        relatedPages.clear();

        debug("invalidated {} pages for query key: {}", invalidatedCount, queryKey);
    }

    private void handleCreateForAscendingOrder(T newEntity) {
        LambdaQueryWrapper<T> pageQueryKeyWrapper = this.extractPageQueryKey(newEntity);
        if (pageQueryKeyWrapper == null) return;

        QueryKey<T> queryKey = this.wrapQueryKey(pageQueryKeyWrapper);
        Set<PageKey<QueryKey<T>>> relatedPages = this.queryToPages.get(queryKey);

        if (relatedPages == null || relatedPages.isEmpty()) {
            debug("no cached pages found for new entity, query key: {}", queryKey);
            return;
        }

        int maxPageNum = 0;
        for (PageKey<QueryKey<T>> pageKey : relatedPages) {
            if (pageKey.pageNum() > maxPageNum) {
                maxPageNum = pageKey.pageNum();
            }
        }

        PageKey<QueryKey<T>> lastPageKey = null;
        for (PageKey<QueryKey<T>> pageKey : relatedPages) {
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
                    debug("last page not full ({}/{}), invalidated only last page: {}",
                            recordCount, pageSize, lastPageKey);
                } else {
                    this.invalidatePagesByQueryKey(queryKey);
                    debug("last page is full ({}/{}), invalidated all pages for query: {}",
                            recordCount, pageSize, queryKey);
                }
            } else {
                debug("last page cache entry not found for pageKey: {}", lastPageKey);
            }
        } else {
            debug("no last page key found for query: {}", queryKey);
        }
    }

    public void setExecutionMode(boolean value) {
        this.manager.setExecutionMode(value);
    }

    public boolean isAsync() {
        return this.manager.isAsync;
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