package com.daniel.util.generator;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 生成值
 */
public class KeyGenerator {
  
  /**
   * UUID 值 生成器
   * @return
   */
  public static String uuid() {
    String uuid = UUID.randomUUID().toString();
    char remove = '-';
    final char[] chars = uuid.toCharArray();
    int pos = 0;
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] != remove) {
        chars[pos++] = chars[i];
      }
    }
    return new String(chars, 0, pos);
  }
  
  /**
   * MD% 算法生成固定长度值
   * @param str
   * @return
   */
  public static String md5(String str) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(str.getBytes());
      String md5 = new BigInteger(1, md.digest()).toString(16);
      while (md5.length() != 32)
        md5 = "0" + md5;
      return md5;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
  
  
}
