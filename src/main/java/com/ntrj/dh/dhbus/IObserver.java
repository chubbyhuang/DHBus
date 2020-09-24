package com.ntrj.dh.dhbus;

/**
 * @author chubbyhuang
 * @time 2020-09-24
 * @explain  观察者接口
 */
public interface IObserver {
    void onNotify(BusBean bean);
}
