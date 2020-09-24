package com.ntrj.dh.dhbus;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chubbyhuang
 * @time 2020-09-24
 * @explain  bus具体业务类
 */
public class DHBus {
    private DHBus() {
        observerMap = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());
        simpleExecutor = Executors.newSingleThreadExecutor();
    }

    private final static class CRATER {
        private final static DHBus INSTANCE = new DHBus();
    }

    public static DHBus getInstance() {
        return CRATER.INSTANCE;
    }

    private final Map<String, LinkedList<IObserver>> observerMap;
    private static final String _DEFAULT_TAG = "_DEFAULT_TAG";
    private final Handler handler;
    private final ExecutorService simpleExecutor;

    /**
     * 注册
     * @param observer  观察者
     */
    public void register(IObserver observer) {
        if (null == observer) return;
        synchronized (simpleExecutor) {
            simpleExecutor.execute(() -> {
                if (null == observerMap.get(_DEFAULT_TAG))
                    observerMap.put(_DEFAULT_TAG, new LinkedList<>());
                observerMap.get(_DEFAULT_TAG).add(observer);
            });
        }
    }

    /**
     * 注册
     * @param tag  被观察者标记
     * @param observer  观察者
     */
    public void register(String tag, IObserver observer) {
        if (null == observer) return;
        synchronized (simpleExecutor) {
            simpleExecutor.execute(() -> {
                if (null == observerMap.get(tag))
                    observerMap.put(tag, new LinkedList<>());
                observerMap.get(tag).add(observer);
            });
        }
    }

    /**
     * 取消注册
     * @param observer  观察者
     */
    public void unRegister(IObserver observer) {
        if (null == observer) return;
        synchronized (simpleExecutor) {
            simpleExecutor.execute(() -> {
                if (null == observerMap.get(_DEFAULT_TAG)) {
                    observerMap.put(_DEFAULT_TAG, new LinkedList<>());
                    return;
                }
                if (observerMap.get(_DEFAULT_TAG).contains(observer))
                    observerMap.get(_DEFAULT_TAG).remove(observer);
            });
        }
    }

    /**
     * 取消注册
     * @param tag  被观察者标记
     * @param observer  观察者
     */
    public void unRegister(String tag,IObserver observer) {
        if (null == observer) return;
        synchronized (simpleExecutor) {
            simpleExecutor.execute(() -> {
                if (null == observerMap.get(tag)) {
                    observerMap.put(tag, new LinkedList<>());
                    return;
                }
                if (observerMap.get(tag).contains(observer))
                    observerMap.get(tag).remove(observer);
            });
        }
    }

    /**
     * 取消被观察者
     * @param tag  被观察者标记
     */
    public void unRegister(String tag) {
        synchronized (simpleExecutor) {
            simpleExecutor.execute(() -> {
                if (observerMap.containsKey(tag))
                    observerMap.remove(tag);
            });
        }
    }

    /**
     * 通知观察者
     * @param bean 通知的数据
     */
    public void notifyObserver(BusBean bean) {
        synchronized (simpleExecutor) {
            simpleExecutor.execute(() -> {
                if (null == observerMap.get(_DEFAULT_TAG)) {
                    observerMap.put(_DEFAULT_TAG, new LinkedList<>());
                    return;
                }
                handler.post(() -> {
                    Iterator<IObserver> iterator = observerMap.get(_DEFAULT_TAG).iterator();
                    while (iterator.hasNext()) {
                        iterator.next().onNotify(bean);
                    }
                });
            });
        }
    }

    /**
     * 通知观察者
     * @param tag  被观察者标记
     * @param bean 通知的数据
     */
    public void notifyObserverByTag(String tag, BusBean bean) {
        synchronized (simpleExecutor) {
            simpleExecutor.execute(() -> {
                if (null == observerMap.get(tag)) {
                    observerMap.put(tag, new LinkedList<>());
                    return;
                }
                handler.post(() -> {
                    Iterator<IObserver> iterator = observerMap.get(tag).iterator();
                    while (iterator.hasNext()) {
                        iterator.next().onNotify(bean);
                    }
                });
            });
        }
    }
}
