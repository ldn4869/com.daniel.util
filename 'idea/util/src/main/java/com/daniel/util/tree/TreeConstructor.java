package com.daniel.util.tree;

import java.util.List;

public interface TreeConstructor<D> {
  /**
   * 依据结点数据自身， 判断是否是叶子数据
   *
   * @param node
   * @return
   */
  default boolean isLeafData(Node<D> node) {
    throw new RuntimeException();
  }
  
  /**
   * 依据结点数据自身， 列出子结点的数据类型的子数据
   *
   * @param node
   * @return
   */
  default List<D> listChildDatas(Node<D> node) {
    throw new RuntimeException();
  }
  
  ;
  
  /**
   * 依据结点数据自身， 判断是否向下
   *
   * @param node
   * @return
   */
  default boolean isDownward(Node<D> node) {
    throw new RuntimeException();
  }
  
  ;
  
  /**
   * 依据结点数据自身， 判断是否向下
   *
   * @param node
   * @return
   */
  default boolean isUpward(Node<D> node) {
    throw new RuntimeException();
  }
  
  ;
  
}
