package com.queue.sample1;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import java.util.logging.Logger;

public class ExclusiveLoop implements Runnable {

    private static final Logger logger = Logger.getLogger(Consumer.class.getName());

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
        boolean loop = true;
        try {
            while(loop) {
                try {
                    lock.lock();
                } catch(Exception e) {
                    logger.warning("lock failed");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e2) {
                        // ignore
                    }
                    continue;
                }
                boolean isLocked = true;
                while(loop && isLocked) {
                    loop = body.run();
                    try {
                        isLocked = lock.isLocked();
                    } catch(Exception e) {
                        logger.warning("is locked failed");
                        isLocked = false;
                    }
                    logger.info("is locked: " + isLocked);
                }
            }
        } finally {
            lock.unlock();
        }
    }

}
