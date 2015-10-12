package com.queue.sample1;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import java.util.logging.Logger;

public class ExclusiveLoop implements Runnable {

    private static final Logger logger = Logger.getLogger(Consumer.class.getName());

    private static final long LEASE_TIME_MS = 60000;

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
        logger.info("Starting loop");
        final ILock lock = hz.getLock(ExclusiveLoop.class.getName());
        boolean loop = true;
        try {
            while(loop) {

                try {
                    lock.lock();
                } catch(Exception e) {
                    logger.warning("Failed locking, sleeping then retrying");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e2) {
                        // ignore
                    }
                    continue;
                }
                long lockTime = System.currentTimeMillis();
                boolean isLocked = true;

                while(loop && isLocked) {
                    loop = body.run();
                    if (loop && lockTime + LEASE_TIME_MS < System.currentTimeMillis()) {
                        logger.warning("Lease time elapsed, trying to grab lock");
                        isLocked = false;
                    }
                }

            }
        } finally {
            lock.unlock();
        }

        logger.info("Loop done");
    }

}
