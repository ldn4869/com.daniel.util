package com.daniel.util.tester;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class FuncTest {
  
  @Test
  public void test1() throws Exception {
    String uuid = UUID.randomUUID().toString();
    StringUtils.remove(uuid, '-');
    return;
  }
  
  @Test
  public void test2() throws Exception {
    String uuid = UUID.randomUUID().toString();
    uuid = StringUtils.remove(uuid, '-');
    byte[] bytes = uuid.getBytes();
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    byte[] lineBytes = new byte[10];
    byte[] bigBytes = new byte[50];
    System.out.println(byteBuffer.position());
    byteBuffer.get(lineBytes);
    System.out.println(byteBuffer.position());
    try {
      byteBuffer.get(bigBytes);
    } catch (BufferUnderflowException e) {
      e.printStackTrace();
    }
    System.out.println(byteBuffer.position());
    return;
  }
  
  @Test
  public void test3() throws Exception {
    Boolean b = null;
    Boolean a = (Boolean) null;
    String str = b != Boolean.valueOf(true) ? "true" : "false";
    System.out.println(str + a);
    return;
  }
  
}
