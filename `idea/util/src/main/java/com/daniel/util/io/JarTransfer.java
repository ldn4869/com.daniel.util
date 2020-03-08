package com.daniel.util.io;

import java.io.InputStream;
import java.net.URL;

public class JarTransfer {

  public static <T> InputStream getInputStream(String fileName, Class<T> clz) {
    return clz.getClassLoader().getResourceAsStream(fileName);
  }

  public static <T> InputStream getInputStream(String fileName) {
    return JarTransfer.class.getClassLoader().getResourceAsStream(fileName);
  }

  /**
   * 判断 clz 类是否运行在 Jar 内
   * @param clz
   * @param <T>
   * @return
   */
  public static <T> boolean isJar(Class<T> clz) {
    URL url = clz.getResource("");
    return "jar".equals(url.getProtocol());
  }

  public static String getInputStream() {
    return null;
  }


}
