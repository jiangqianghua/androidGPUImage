package com.jqh.gpuimagelib.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPool {
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);
}
