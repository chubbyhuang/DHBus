package com.ntrj.dh.dhbus;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class DHBus implements IDHBus {
    private static final String _DEFAULT_TAG = "_DEFAULT_TAG";
    private final Map<String,LinkedList<IObserver>> mObserverMap;
    private final Handler mHandler;
    private DHBus() {
        mObserverMap = new LinkedHashMap<>();
        mHandler = new Handler(Looper.getMainLooper());
    }

    private final static class CRATER {
        private final static DHBus INSTANCE = new DHBus();
    }

    public static DHBus getInstance() {
        return DHBus.CRATER.INSTANCE;
    }


    @Override
    public void register(IObserver observer) {
        register(_DEFAULT_TAG,observer);
    }

    @Override
    public void register(String tag, IObserver observer) {
        if (TextUtils.isEmpty(tag)) return;
        if (null == observer) return;
        synchronized (mObserverMap) {
            if(null == mObserverMap.get(tag))
                mObserverMap.put(tag,new LinkedList<>());
            mObserverMap.get(tag).add(observer);
        }
    }

    @Override
    public void unRegister(IObserver observer) {
        unRegister(_DEFAULT_TAG,observer);
    }

    @Override
    public void unRegister(String tag, IObserver observer) {
        if (TextUtils.isEmpty(tag)) return;
        if (null == observer) return;
        synchronized (mObserverMap) {
            if(null == mObserverMap.get(tag)) return;
            Iterator<IObserver> iterator = mObserverMap.get(tag).iterator();
            while (iterator.hasNext()) {
                if(observer == iterator.next()){
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void unRegister(String tag) {
        if (TextUtils.isEmpty(tag)) return;
        synchronized (mObserverMap) {
            if(null == mObserverMap.get(tag)) return;
            Iterator<Map.Entry<String,LinkedList<IObserver>>> mapIterator = mObserverMap.entrySet().iterator();
            while(mapIterator.hasNext()){
                Map.Entry<String,LinkedList<IObserver>> entry = mapIterator.next();
                if(entry.getKey().equals(tag)){
                    Iterator<IObserver> listIterator = entry.getValue().iterator();
                    while (listIterator.hasNext()) {
                        listIterator.remove();
                    }
                }
                mapIterator.remove();
            }
        }
    }

    @Override
    public void clear() {
        synchronized (mObserverMap) {
            Iterator<Map.Entry<String,LinkedList<IObserver>>> mapIterator = mObserverMap.entrySet().iterator();
            while(mapIterator.hasNext()){
                Map.Entry<String,LinkedList<IObserver>> entry = mapIterator.next();
                if(null != entry.getValue()){
                    Iterator<IObserver> listIterator = entry.getValue().iterator();
                    while (listIterator.hasNext()) {
                        listIterator.remove();
                    }
                }
                mapIterator.remove();
            }
        }
    }

    @Override
    public void notifyObserver(BusBean bean) {
        notifyObserver(_DEFAULT_TAG,bean);
    }

    @Override
    public void notifyObserver(String tag, BusBean bean) {
        if (TextUtils.isEmpty(tag)) return;
        synchronized (mObserverMap) {
            if(null == mObserverMap.get(tag)) return;
            Iterator<IObserver> iterator = mObserverMap.get(tag).iterator();
            while (iterator.hasNext()) {
                IObserver _observer = iterator.next();
                if(null != _observer){
                    _observer.onNotify(bean);
                }
            }
        }
    }

    @Override
    public void notifyObserverOnMain(BusBean bean) {
        notifyObserverOnMain(_DEFAULT_TAG,bean);
    }

    @Override
    public void notifyObserverOnMain(String tag, BusBean bean) {
        mHandler.post(() -> notifyObserver(tag,bean));
    }
}
