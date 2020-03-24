package com.example.imageloader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadManger {
    private ImageLoader imageLoader;
    private SingleThreadManger(){
        init();
    }

    private static SingleThreadManger instance;
    public static SingleThreadManger getInstance()
    {
        if (instance ==null)
        {
            synchronized (SingleThreadManger.class)
            {
             if (instance==null)
             {
                 instance=new SingleThreadManger();
             }
            }
        }
        return instance;
    }

    private ExecutorService service;
    private void init() {
        int threadCount = Runtime.getRuntime().availableProcessors() * 2 + 1;
        service = Executors
                .newFixedThreadPool(Math.min(threadCount, 8));
    }
    public ExecutorService getService()
    {
        return this.service;
    }
    public void execute(Runnable runnable){
        this.service.execute(runnable);
    }
}
