package com.queue.sample1;

import com.hazelcast.core.HazelcastInstance;

import java.util.logging.Logger;

public class ExclusiveHello implements Runnable {

    private static final Logger logger = Logger.getLogger(ExclusiveHello.class.getName());

    private HazelcastInstance hz;

    public ExclusiveHello(HazelcastInstance hz) {
        this.hz = hz;
    }

    public void run() {
        ExclusiveLoop loop = new ExclusiveLoop(hz, new ExclusiveLoop.LoopBody() {
            public boolean run() {
                logger.info("Hello");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // ignore
                }
                return true;
            }
        });
        loop.run();
    }

}
