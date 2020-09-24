package com.ntrj.dh.dhbus;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public void unRegister(String tag) {
        synchronized (simpleExecutor) {
            simpleExecutor.execute(() -> {
                if (observerMap.containsKey(tag))
                    observerMap.remove(tag);
            });
        }
    }

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
