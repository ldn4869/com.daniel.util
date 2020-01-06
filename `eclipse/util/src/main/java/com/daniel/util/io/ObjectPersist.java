package com.daniel.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**对象持久化
 * @author Daniel
 *
 */
public class ObjectPersist {
  
  /**写入持久化对象
   * @param obj
   * @param file
   */
  public static void writeObject(Object obj, File file) {
    // 创建文件
    FileGenerator.create(file);
    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
      objectOutputStream.writeObject(obj);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**读取持久化对象
   * @param <T>
   * @param file
   * @return
   */
  public static <T> T readObject(File file) {
    try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
      return (T) objectInputStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
}
