package com.qinglan.tool.util;

import com.qinglan.common.Log;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public abstract class SubThread extends Thread {
    private CyclicBarrier cyclicBarrier;
    private String threadName;

    public SubThread(CyclicBarrier barrier, String name) {
        cyclicBarrier = barrier;
        threadName = name;
    }

    @Override
    public void run() {
        try {
            Log.eln(threadName + ": isBroken = " + cyclicBarrier.isBroken() + ", Number = " + cyclicBarrier.getNumberWaiting());
            execute();
            cyclicBarrier.await();
            Log.eln(threadName + ": isBroken = " + cyclicBarrier.isBroken() + ", Number = " + cyclicBarrier.getNumberWaiting());
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public abstract void execute();

}
