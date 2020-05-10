package com.daniel.util.entity;

import com.daniel.util.exception.FileNotFoundRuntimeException;

import java.io.File;

/**
 * 保存文件块数据
 */
public class FileBlock {

  private File file;

  private long start;

  private long end;

  private long length;

  private FileEnum fileEnum;

  public FileBlock(File file) {
    this.file = file;
  }

  public static FileBlock instance(File file) {
    FileBlock fileBlock = new FileBlock(file);
    fileBlock.instance();
    return fileBlock;
  }

  public void instance() {
    if (!file.exists()) {
      throw new FileNotFoundRuntimeException();
    }
    if (file.isFile()) {
      fileEnum = FileEnum.FILE;
      length = file.length();
      start = 0;
      end = length;
    } else {
      fileEnum = FileEnum.DIRECTORY;
    }
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public long getStart() {
    return start;
  }

  public void setStart(long start) {
    this.start = start;
  }

  public long getEnd() {
    return end;
  }

  public void setEnd(long end) {
    this.end = end;
  }

  public long getLength() {
    return length;
  }

  public void setLength(long length) {
    this.length = length;
  }

  public FileEnum getFileEnum() {
    return fileEnum;
  }

  public void setFileEnum(FileEnum fileEnum) {
    this.fileEnum = fileEnum;
  }

  @Override
  public String toString() {
    return file.toString();
  }
}
