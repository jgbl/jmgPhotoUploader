package org.de.jmg.jmgphotouploader;




import java.util.concurrent.TimeUnit;

import java.util.concurrent.locks.Condition;

import java.util.concurrent.locks.ReentrantLock;

public class AutoResetEvent {

private volatile boolean _signaled;
private ReentrantLock _lock;
private Condition _condition;

public AutoResetEvent(boolean initialState) {
    _signaled = initialState;
    _lock = new ReentrantLock();
    _condition = _lock.newCondition();
}

public void waitOne(long miliSecond) throws InterruptedException {
    _lock.lock();
    try {
        while (!_signaled)
            _condition.await(miliSecond, TimeUnit.MILLISECONDS);
        _signaled = false;
    } finally {
        _lock.unlock();
    }
}

public void waitOne() throws InterruptedException {
    _lock.lock();
    try {
        while (!_signaled)
            _condition.await();
        _signaled = false;
    } finally {
        _lock.unlock();
    }
}

public void set() {
    _lock.lock();
    try {
        _condition.signal();
        _signaled = true;
    } finally {
        _lock.unlock();
    }
}

public void reset() {
    _lock.lock();
    try {
        _signaled = false;
    } finally {
        _lock.unlock();
    }
}

}
