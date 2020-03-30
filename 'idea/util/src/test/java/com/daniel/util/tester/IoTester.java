package com.daniel.util.tester;

import com.daniel.util.io.JarTransfer;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IoTester {

  @Test
  public void test1() throws Exception {
    InputStream is = JarTransfer.getInputStream("input.txt");
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    br.lines().forEach(str -> System.out.println(str));
    return;
  }

  @Test
  public void test2() throws Exception {

    return;
  }

}
