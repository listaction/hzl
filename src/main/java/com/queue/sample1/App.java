package com.queue.sample1;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IQueue;

public class App {
    
    public static void main( String[] args ) {
        // String queue = "simplebean";
        // Thread t1; 
        // //start consumers first 
        // for (int i = 1; i < 10; i++){
        //     new Thread(new Consumer(queue)).start();
        // }
        // for (int i = 1; i < 3; i++){
        //     t1 = new Thread(new Producer(queue));
        //     t1.setName("PRODUCER THREAD "+i);
        //     t1.start();                        
        // }

        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        ExclusiveHello hello = new ExclusiveHello(hz);
        hello.run();
    }

}
