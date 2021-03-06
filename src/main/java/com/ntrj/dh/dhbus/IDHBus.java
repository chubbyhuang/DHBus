package com.ntrj.dh.dhbus;
/**
 * @author chubbyhuang
 * @time 2020-09-24
 * @explain  接口规范
 */
public interface IDHBus {
    void register(IObserver observer);
    void register(String tag,IObserver observer);
    void unRegister(IObserver observer);
    void unRegister(String tag,IObserver observer);
    void unRegister(String tag);
    void clear();
    void notifyObserver(BusBean bean);
    void notifyObserver(String tag,BusBean bean);
    void notifyObserverOnMain(BusBean bean);
    void notifyObserverOnMain(String tag,BusBean bean);
}
