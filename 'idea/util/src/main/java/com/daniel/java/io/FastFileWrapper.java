package com.daniel.java.io;

import java.io.File;
import java.lang.reflect.Constructor;

public class FastFileWrapper {

  private static Constructor<File> fileConstructor;

  private static WinNTFileSystem fs = new WinNTFileSystem();

  private static String slash = "/";

  static {
    try {
      fileConstructor = File.class.getDeclaredConstructor(String.class, int.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    fileConstructor.setAccessible(true);
  }

  private File file;

  public FastFileWrapper(String path) {
    try {
      file = fileConstructor.newInstance(path, 3);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 列出子文件
   *
   * @return
   */
  public FastFileWrapper[] listFiles() {
    String[] childFilenames = fs.list(file);
    FastFileWrapper[] children = new FastFileWrapper[childFilenames.length];
    for (int ix = 0; ix < children.length; ix++) {
      try {
        children[ix] = new FastFileWrapper(file.getPath() + slash + childFilenames[ix]);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return children;
  }

  /**
   * 文件长度
   *
   * @return
   */
  public long length() {
    return fs.getLength(file);
  }

  /**
   * 是否是文件
   *
   * @return
   */
  public boolean isFile() {
    return ((fs.getBooleanAttributes(file) & 0x02) != 0);
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }
}
