package com.cn.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 三个线程按次序轮流打印a,b,c AtomicInteger 实现
 * @author 
 */
public class CASThreadDemo implements Runnable{
    private static  AtomicInteger currentCount = new AtomicInteger(0);

    private static final Integer MAX_COUNT = 30;

    private static String [] chars = {"a", "b", "c"};

    private String name;

    public CASThreadDemo(String name) {
        this.name =  name;
    }

    @Override
    public void run() {
        while(currentCount.get()<MAX_COUNT){
            if(this.name.equals(chars[currentCount.get()%3])){
                printAndPlusOne(this.name + "\t" + currentCount);
            }
        }
    }

    public void printAndPlusOne(String content){
        System.out.println(content);
        currentCount.getAndIncrement();
    }

    public static void main(String [] args){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 3, 20, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        threadPoolExecutor.execute(new CASThreadDemo("a"));
        threadPoolExecutor.execute(new CASThreadDemo("b"));
        threadPoolExecutor.execute(new CASThreadDemo("c"));
        threadPoolExecutor.shutdown();
    }
}