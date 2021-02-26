package com.daniel.util.tester;

import org.junit.Test;

import java.io.File;
import java.util.concurrent.ForkJoinPool;

public class SubsizeTester {

  @Test
  public void test2() throws Exception {
    File dir = new File("D:/Workspace");
    long start = System.currentTimeMillis();
    System.out.println(forkJoin(dir));
    System.out.println((System.currentTimeMillis() - start) / 1e3);
    return;
  }


  private final static ForkJoinPool forkJoinPool = new ForkJoinPool();

  private static long forkJoin(File dir) {
    return forkJoinPool.invoke(new FileSizeFinder(dir));
  }


}
