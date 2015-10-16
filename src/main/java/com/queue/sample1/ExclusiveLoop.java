package com.queue.sample1;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import java.util.logging.Logger;

public class ExclusiveLoop implements Runnable {

    private static final Logger logger = Logger.getLogger(ExclusiveLoop.class.getName());

    private long leaseTimeMs = 5000;
    private long lockDelayMs = 3000;

    private HazelcastInstance hz;
    private LoopBody body;

    public static abstract class LoopBody {
        public abstract boolean run();
    }

    public void setLeaseTimeMs(long leaseTimeMs) {
        this.leaseTimeMs = leaseTimeMs;
    }

    public void setLockDelayMs(long lockDelayMs) {
        this.lockDelayMs = lockDelayMs;
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
                    logger.info("Failed locking, sleeping then retrying");
                    try {
                        Thread.sleep(lockDelayMs);
                    } catch (InterruptedException e2) {
                        // ignore
                    }
                    continue;
                }
                long lockTime = System.currentTimeMillis();
                boolean isLocked = true;

                while(loop && isLocked) {
                    try {
                        loop = body.run();
                    } catch (Exception e) {
                        logger.warning("Failed running loop, trying to grab lock, error: " + e);
                        isLocked = false;
                    }
                    if (loop && lockTime + leaseTimeMs < System.currentTimeMillis()) {
                        logger.info("Lease time elapsed, trying to grab lock");
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
