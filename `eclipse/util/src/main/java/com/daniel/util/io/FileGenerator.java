package com.daniel.util.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.daniel.util.exception.io.FileAlreadyExistsRuntimeException;

/**文件生成
 * @author Daniel
 *
 */
public class FileGenerator {

  private static final String CREATE = "CREATE";
  private static final String COVER = "COVER";
  private static final String APPEND = "APPEND";
  
  // [start]* ################################ 创建 ################################ */
  /**创建文件夹路径
   * @param folder
   */
  public static void mkdir(File folder) {
    if (!folder.exists()) {
      folder.mkdirs();
    }
  }

  /**不覆盖创建文件
   * @param file
   */
  public static void create(File file) {
    file = file.getAbsoluteFile();
    mkdir(file.getParentFile());
    // 若不存在, 创建文件; 存在, 依据是否为 COVER 模式操作
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  // [end]

  // [start]* ################################ 写入 ################################ */
  /**写入文件
   * @param file 文件
   * @param mode 写入模式
   * @param content 写入内容
   */
  private static void write(File file, String mode, String content) {
    file = file.getAbsoluteFile();

    // 若父目录不存在, 创建
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
    // 若不存在, 创建文件; 存在, 依据是否为 COVER 模式操作
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      // 为 COVER模式 则 覆盖, 否则 return
      if (COVER.equals(mode)) {
        file.delete();
        try {
          file.createNewFile();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    try (FileWriter fw = new FileWriter(file, APPEND.equals(mode)); BufferedWriter bw = new BufferedWriter(fw);) {
      bw.write(content);
      bw.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**不覆盖创建文件, 并写入内容
   * @param file
   * @param content
   */
  public static void create(File file, String content) {
    write(file, CREATE, content);
  }
  
  /**覆盖创建文件, 并写入内容
   * @param file
   * @param content
   */
  public static void cover(File file, String content) {
    write(file, COVER, content);
  }
  
  /**文件不存在时, 创建并写入内容; 文件存在时, 在后追加内容
   * @param file
   * @param content
   */
  public static void append(File file, String content) {
    write(file, APPEND, content);
  }
  
  /**不覆盖创建文件, 并写入内容
   * @param fileName
   * @param content
   */
  public static void create(String fileName, String content) {
    write(new File(fileName), CREATE, content);
  }
  
  /**覆盖创建文件, 并写入内容
   * @param fileName
   * @param content
   */
  public static void cover(String fileName, String content) {
    write(new File(fileName), COVER, content);
  }
  
  /**文件不存在时, 创建并写入内容; 文件存在时, 在后追加内容
   * @param fileName
   * @param content
   */
  public static void append(String fileName, String content) {
    write(new File(fileName), APPEND, content);
  }

  // [end]

}
