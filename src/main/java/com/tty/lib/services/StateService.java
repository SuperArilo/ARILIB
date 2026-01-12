package com.tty.lib.services;

import com.tty.lib.Log;
import com.tty.lib.dto.state.State;
import com.tty.lib.Lib;
import com.tty.lib.task.CancellableTask;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class StateService<T extends State> {

    private final JavaPlugin plugin;
    /**
     * 每一次的执行周期 单位tick
     */
    @Getter
    private final long rate;
    /**
     * 延迟多久后开始执行
     */
    @Getter
    private final long c;
    /**
     * 是异步还是同步
     */
    @Getter
    private final boolean isAsync;
    private CancellableTask task;

    protected final List<T> stateList = Collections.synchronizedList(new ArrayList<>());

    public StateService(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        this.rate = rate;
        this.c = c;
        this.isAsync = isAsync;
        this.plugin = javaPlugin;
    }

    private CancellableTask createTask(long rate, long c, boolean isAsync, JavaPlugin javaPlugin) {
        if (isAsync) {
            return Lib.Scheduler.runAsyncAtFixedRate(javaPlugin, i -> this.execute(), c, rate);
        } else {
            return Lib.Scheduler.runAtFixedRate(javaPlugin, i -> this.execute(), c, rate);
        }
    }

    private void execute() {
        if (stateList.isEmpty()) {
            this.abort();
            return;
        }

        synchronized (stateList) {
            Iterator<T> iterator = stateList.iterator();
            while (iterator.hasNext()) {
                T state = iterator.next();

                if (state.isOver()) {
                    iterator.remove();
                    this.onEarlyExit(state);
                    continue;
                }

                if (state.isDone()) {
                    iterator.remove();
                    this.onFinished(state);
                    continue;
                }

                if (!state.isPending()) {
                    state.setPending(true);
                    try {
                        state.increment();
                        this.loopExecution(state);
                        if (state.isOver()) {
                            iterator.remove();
                            this.onEarlyExit(state);
                            continue;
                        }
                        if (state.isDone()) {
                            iterator.remove();
                            this.onFinished(state);
                        }
                    } finally {
                        state.setPending(false);
                    }
                }
            }
        }
    }

    public void abort() {
        if (this.task == null) return;
        this.task.cancel();
        this.task = null;

        for (T i : this.stateList) {
            this.onServiceAbort(i);
        }
        this.stateList.clear();
        Log.debug("state service abort.");
    }

    public boolean addState(T state) {
        synchronized (this.stateList) {
            if (!this.canAddState(state)) {
                this.abortAddState(state);
                return false;
            }
            this.stateList.add(state);
            this.passAddState(state);
            if (task == null) {
                this.task = createTask(rate, c, isAsync, this.plugin);
                Log.debug("create state service");
            }
            return true;
        }
    }

    public boolean isNotHaveState(Entity owner) {
        synchronized (this.stateList) {
            return this.getStates(owner).isEmpty();
        }
    }

    public List<T> getStates(Entity owner) {
        synchronized (this.stateList) {
            return stateList.stream()
                    .filter(i -> i.getOwner().equals(owner))
                    .toList();
        }
    }

    public boolean removeState(T state) {
        synchronized (this.stateList) {
            return stateList.remove(state);
        }
    }

    /**
     * 当前状态列表是否为空
     * @return 空 true
     */
    public boolean stateIsEmpty() {
        return this.stateList.isEmpty();
    }
    /**
     * 检查是否允许添加状态
     * @param state 要添加的状态
     * @return true 表示允许添加，false 表示不允许
     */
    protected abstract boolean canAddState(T state);

    /**
     * 在当前计数下的执行内容
     *
     * @param state 当前检查的状态
     */
    protected abstract void loopExecution(T state);

    /**
     * 终止当前的状态添加
     * @param state 添加的状态
     */
    protected abstract void abortAddState(T state);

    /**
     * 当前的状态可添加
     * @param state 添加成功的状态
     */
    protected abstract void passAddState(T state);

    /**
     * 提前结束检查的回调方法
     * @param state 检查失败的状态
     */
    protected abstract void onEarlyExit(T state);

    /**
     * 计数完成后正常结束
     * @param state 检查通过的状态
     */
    protected abstract void onFinished(T state);

    /**
     * 当状态服务器被终止运行时候的回调
     * @param state 被通知的每个状态
     */
    protected abstract void onServiceAbort(T state);

}
