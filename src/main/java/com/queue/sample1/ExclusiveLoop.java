package com.queue.sample1;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;


public class ExclusiveLoop implements Runnable {

    private HazelcastInstance hz;
    private LoopBody body;

    public static abstract class LoopBody {
        public abstract boolean run();
    }

    public ExclusiveLoop(HazelcastInstance hz, LoopBody body) {
        this.hz = hz;
        this.body = body;
    }

    public void run() {
        final ILock lock = hz.getLock(ExclusiveLoop.class.getName());
        lock.lock();
        try {
            boolean loop = true;
            while(loop) {
                loop = body.run();
            }
        } finally {
            lock.unlock();
        }
    }

}
