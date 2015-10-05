package com.queue.sample1;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IQueue;

import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class Producer implements Runnable {
    private static boolean started = false;
    IQueue<SimpleBean> queue;
    private HazelcastInstance hz;
    static Logger logger = Logger.getLogger(Consumer.class.getName());
    int producerNumber;
    
    public  Producer(String queueName){
        Config cfg = new Config();                  
        cfg.getManagementCenterConfig().setEnabled(true);
        cfg.getManagementCenterConfig().setUrl("http://localhost:8080/mancenter");
        
        hz = Hazelcast.newHazelcastInstance(cfg);

        queue = hz.getQueue(queueName);
        ConcurrentMap<String, Integer> members = hz.getMap( "members" );
        members.putIfAbsent("producer_number", 0);
        producerNumber = members.get("producer_number").intValue() + 1;
        members.replace("producer_number", members.get("producer_number"), producerNumber);

    }
    
    public boolean getStarted(){
        return started;
    }
    
    private void produce(){
        //comment these 3 to have multiple producers
//        ILock lock = hz.getLock(queue.getName());
//        logger.info("Producer " +producerNumber + " attempting to acquire lock");
//        lock.lock();
        try { 
            logger.info("Starting Producer: " + producerNumber);
            while (true){
                try {
                    logger.info("adding a message - producer "+ producerNumber);
                    queue.put(Util.makeSimpleBean());
                    ConcurrentMap<String, Integer> members = hz.getMap( "members" );                    
                    members.putIfAbsent("messages_produced", 0);
                    Integer mp =  members.get("messages_produced");
                    members.replace("messages_produced", mp.intValue() + 1);
                    
                    logger.info("messages produced: " + mp.intValue() + 1);                    
                    
                    Thread.sleep(10);
                    //Artificial to understand how well locks move around
//                    if( i++ > 3){
//                        break;
//                    }

                } catch (InterruptedException e) {
                    logger.severe(e.getMessage());
                }
            }
        }finally{ 
            //uncomment to test with single producer
            //lock.unlock();
            
            //uncomment to test locally on a single jvm instance
            //produce();
        }       
    }

    public void run() {
        produce();
    }
}