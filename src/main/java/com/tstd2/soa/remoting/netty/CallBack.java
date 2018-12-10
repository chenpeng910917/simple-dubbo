package com.tstd2.soa.remoting.netty;


import com.tstd2.soa.remoting.netty.model.Response;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Rpc消息回调
 */
public class CallBack {

    private Response response;
    private Lock lock = new ReentrantLock();
    private Condition finish = lock.newCondition();

    public Object start(int timeout) throws InterruptedException {
        try {
            lock.lock();
            if (this.response != null) {
                return this.response.getResult();
            }

            // 超时设置
            finish.await( timeout, TimeUnit.MILLISECONDS);

            if (this.response != null) {
                return this.response.getResult();
            } else {
                throw new RuntimeException("timeout...");
            }
        } finally {
            lock.unlock();
        }
    }

    public void over(Response response) {
        try {
            lock.lock();
            finish.signal();
            this.response = response;
        } finally {
            lock.unlock();
        }
    }
}