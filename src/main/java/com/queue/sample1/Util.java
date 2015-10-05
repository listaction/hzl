package com.queue.sample1;

public class Util {

    public static int queueNumber;
    public static int consumerNumber;

    public static SimpleBean makeSimpleBean(){
        SimpleBean sb = new SimpleBean();
        sb.setMessage(" random message " + Math.random());
        sb.setStatusCode((int) Math.round(Math.random() * 100));
        return sb;
    }
}
