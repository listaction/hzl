package com.queue.sample1;

import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

public class Consumer implements Runnable{
    static Logger logger = Logger.getLogger(Consumer.class.getName());
    int consumerNumber;
    private HazelcastInstance hz;    
    
    IQueue<SimpleBean> queue;
    public Consumer(String queueName){    
        Config cfg = new Config();                  
        cfg.getManagementCenterConfig().setEnabled(true);
        cfg.getManagementCenterConfig().setUrl("http://localhost:8080/mancenter");
        
        hz = Hazelcast.newHazelcastInstance(cfg);
        
        queue = hz.getQueue( queueName );     
        ConcurrentMap<String, Integer> members = hz.getMap( "members" );
        members.putIfAbsent("consumer_number", 0);
        consumerNumber = members.get("consumer_number").intValue() + 1;
        members.replace("consumer_number", members.get("consumer_number"), new Integer(consumerNumber));
              
    }
    
    public void run() {
        try{
            while(true){
                SimpleBean sb = queue.take();
                logger.info(sb.getMessage() + " : " + sb.getStatusCode() + ": consumer " + consumerNumber);

                ConcurrentMap<String, Integer> members = hz.getMap( "members" );                    
                members.putIfAbsent("messages_processed", 0);
                Integer mp =  members.get("messages_processed");
                members.replace("messages_processed", mp.intValue() + 1);  
                
                logger.info("messages processed: " + mp.intValue() + 1 );
            }
            
        }catch(Exception e){
            logger.severe(e.getMessage());
        }
    }
}
