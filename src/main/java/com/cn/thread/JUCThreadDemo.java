package com.cn.thread;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JUCThreadDemo implements Runnable{
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

    private static Integer currentCount = 0;

    private static final Integer MAX_COUNT = 30;

    private static String [] chars = {"a", "b", "c"};

    private String name;

    public JUCThreadDemo(String name) {
        this.name =  name;
    }

    @Override
    public void run() {
        while(currentCount<MAX_COUNT){
            while(this.name.equals(chars[currentCount%3]))
                printAndPlusOne(this.name + "\t" + currentCount);
            try {
                cyclicBarrier.await();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void printAndPlusOne(String name){
        System.out.println(name);
        currentCount ++;
    }

    public static void main(String [] args){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 3, 20, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        threadPoolExecutor.execute(new JUCThreadDemo("a"));
        threadPoolExecutor.execute(new JUCThreadDemo("b"));
        threadPoolExecutor.execute(new JUCThreadDemo("c"));
        threadPoolExecutor.shutdown();
    }
}
