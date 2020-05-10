package com.daniel.util.util;

import com.daniel.util.entity.FileBlock;
import com.daniel.util.tree.Node;
import com.daniel.util.tree.TreeConstructor;
import com.daniel.util.tree.TreeOperator;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileBlockTreeOperator extends TreeOperator<FileBlock> {
  
  final private TreeConstructor<FileBlock> treeConstructor = new TreeConstructor<FileBlock>(){
    @Override
    public boolean isLeafData(Node<FileBlock> node) {
      return node.getData().getFile().isFile();
    }
  
    @Override
    public List<FileBlock> listChildDatas(Node<FileBlock> node) {
      File[] childDatas = node.getData().getFile().listFiles();
      return Arrays.stream(childDatas).map(FileBlock::instance).collect(Collectors.toList());
    }
  };
  
  @Override
  protected TreeConstructor<FileBlock> getTreeConstructor() {
    return treeConstructor;
  }
  
  
}
