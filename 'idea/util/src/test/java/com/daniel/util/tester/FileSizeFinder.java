package com.daniel.util.tester;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class FileSizeFinder extends RecursiveTask<Long> {

  private File file;

  private int level = 0;

  public FileSizeFinder(File file) {
    this.file = file;
  }

  public FileSizeFinder(File file, int level) {
    this.file = file;
    this.level = level;
  }

  // region implements WinNtFileSystem

  private static final Class fsClz = getFsClz();

  private static Class getFsClz() {
    try {
      return Class.forName("java.io.WinNTFileSystem");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static final MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
  private static final MethodHandles.Lookup privateLookup = MethodHandles.lookup();

  private static final MethodType mtList = MethodType.methodType(String[].class, File.class);

  private static final MethodType mtGetLength = MethodType.methodType(long.class, File.class);

  private static final MethodType mtGetBooleanAttributes = MethodType.methodType(int.class, File.class);

  private static final MethodHandle list = getList();

  private static MethodHandle getList() {
    try {
//      publicLookup.findVirtual(fsClz, "list", mtList);
      Method method = fsClz.getMethod("list", File.class);
      method.setAccessible(true);
      return privateLookup.unreflect(method);
    } catch (Exception e) {
      e.printStackTrace();
    }
    throw new RuntimeException();
  }

  private static final MethodHandle getLength = getGetLength();

  private static MethodHandle getGetLength() {
    try {
//      publicLookup.findVirtual(fsClz, "getLength", mtGetLength);
      Method method = fsClz.getMethod("getLength", File.class);
      method.setAccessible(true);
      return privateLookup.unreflect(method);
    } catch (Exception e) {
      e.printStackTrace();
    }
    throw new RuntimeException();
  }

  private static final MethodHandle getBooleanAttributes = getGetBooleanAttributes();

  private static MethodHandle getGetBooleanAttributes() {
    try {
//      publicLookup.findVirtual(fsClz, "getBooleanAttributes", mtGetBooleanAttributes);
      Method method = fsClz.getMethod("getBooleanAttributes", File.class);
      method.setAccessible(true);
      return privateLookup.unreflect(method);
    } catch (Exception e) {
      e.printStackTrace();
    }
    throw new RuntimeException();
  }

  private static final MethodHandle fileConstructor = getFileConstructor();

  private static MethodHandle getFileConstructor() {
    try {
//      return privateLookup.findConstructor(File.class, MethodType.methodType(void.class, String.class, int.class));
      Constructor constructor = File.class.getDeclaredConstructor(String.class, int.class);
      constructor.setAccessible(true);
      return privateLookup.unreflectConstructor(constructor);
    } catch (Exception e) {
      e.printStackTrace();
    }
    throw new RuntimeException();
  }


  private static final Object fsInstance = getFsInstance();

  private static Object getFsInstance() {
    try {
      Constructor constructor = fsClz.getConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (NoSuchMethodException | InstantiationException | InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    throw new RuntimeException();
  }


  private static final Integer three = Integer.valueOf(3);

  private static final String slash = "/";

//  static {
//    try {
//      MethodHandles.Lookup lookup = MethodHandles.lookup();
//      MethodHandles.Lookup lookup2 = MethodHandles.publicLookup();
//      fileConstructor = File.class.getDeclaredConstructor(String.class, int.class);
//      fileConstructor.setAccessible(true);
//      fsClz = Class.forName("java.io.WinNTFileSystem");
//      Constructor fsConstructor =
//      fsConstructor.setAccessible(true);
//      fsInstance = lookup.findConstructor(fsClz, MethodType.methodType());
//      Method mList = fsClz.getDeclaredMethod("list", File.class);
//      mList.setAccessible(true);
//      Method mGetLength = fsClz.getDeclaredMethod("getLength", File.class);
//      mGetLength.setAccessible(true);
//      Method mGetBooleanAttributes = fsClz.getDeclaredMethod("getBooleanAttributes", File.class);
//      mGetBooleanAttributes.setAccessible(true);
//      list = lookup.findVirtual(fsClz, "list", MethodType.methodType(File.class));
//      getLength = lookup.findVirtual(fsClz, "getLength", MethodType.methodType(File.class));
//      getBooleanAttributes = lookup.findVirtual(fsClz, "getBooleanAttributes", MethodType.methodType(File.class));
//    } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
//      e.printStackTrace();
//    }
//
//  }

  private static File[] listFiles(File file) {
    String[] childFilenames = new String[0];
    try {
      childFilenames = (String[]) list.invoke(fsInstance, file);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    File[] children = new File[childFilenames.length];
    for (int ix = 0; ix < children.length; ix++) {
      try {
        children[ix] = (File) fileConstructor.invoke(file.getPath() + slash + childFilenames[ix], three);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
    return children;
  }

  private static long length(File file) {
    try {
      return (long) getLength.invokeExact(fsInstance, file);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    return 0;
  }

  private static boolean isFile(File file) {
    try {
      return (((int) getBooleanAttributes.invokeExact(fsInstance, file) & 0x02) != 0);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    return false;
  }
  // endregion

  @Override
  protected Long compute() {
    long size = 0;
//    // 文件
//    if (file.isFile()) {
//      return file.length();
//    }
    // 目录
    File[] children = file.listFiles();
//      FastFileWrapper[] children = file.listFiles();
    List<ForkJoinTask<Long>> subtasks = new ArrayList<ForkJoinTask<Long>>(children.length);
    int nextLevel = level + 1;
    for (int ix = 0; ix < children.length; ix++) {
      File child = children[ix];
      if (child.isFile()) {
        size += child.length();
      } else {
//        // level < 2
//        if (level < 2) {
        subtasks.add(new FileSizeFinder(child, nextLevel));
//        } else {
//          size += RecursiveFileSize.getTotalSize(child);
//        }

      }
    }
    for (ForkJoinTask<Long> subtask : invokeAll(subtasks)) {
      size += subtask.join();
    }
    return size;
  }
}
