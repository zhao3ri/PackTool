package com.qinglan.tool.util;

import com.qinglan.common.Log;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public abstract class SubThread extends Thread {
    CyclicBarrier cyclicBarrier;

    public SubThread(CyclicBarrier barrier) {
        cyclicBarrier = barrier;
    }

    @Override
    public void run() {
        try {
            Log.eln(getThreadName() + " Number3 = " + cyclicBarrier.getNumberWaiting());
            execute();
            cyclicBarrier.await();
            Log.eln(getThreadName() + " Number4 = " + cyclicBarrier.getNumberWaiting());
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public abstract void execute();

    public abstract String getThreadName();
}
