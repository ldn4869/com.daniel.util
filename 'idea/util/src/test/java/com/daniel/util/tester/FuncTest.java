package com.daniel.util.tester;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.UUID;

public class FuncTest {
  @Test
  public void test1() throws Exception {
    String uuid = UUID.randomUUID().toString();
    StringUtils.remove(uuid, '-');
    return;
  }
}
