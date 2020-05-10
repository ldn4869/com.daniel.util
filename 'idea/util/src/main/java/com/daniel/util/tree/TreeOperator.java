package com.daniel.util.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 树的结点操作， 继承并实现方法以使用
 *
 * @param <D> 结点数据类型
 */
abstract public class TreeOperator<D> {
  
  // region 遍历
  
  /**
   * 遍历类型
   */
  public enum TraversalEnum {
    DLR,
    LRD,
    LAYER,
    ;
  }
  
  /**
   * 遍历
   *
   * @param root
   * @param traversalEnum
   * @return
   */
  public List<Node<D>> traversal(Node<D> root, TraversalEnum traversalEnum) {
    List<Node<D>> traversal = new ArrayList<Node<D>>();
    switch (traversalEnum) {
      case DLR:
        preorderTraversal(root, traversal);
        break;
      case LRD:
        postorderTraversal(root, traversal);
        break;
      default:
        return null;
    }
    return traversal;
  }
  
  /**
   * 前序遍历
   *
   * @param root
   * @param traversal
   */
  private void preorderTraversal(Node<D> root, List<Node<D>> traversal) {
    traversal.add(root);
    root.getChilds().stream().forEach(child -> preorderTraversal(child, traversal));
  }
  
  /**
   * 后序遍历
   *
   * @param root
   * @param traversal
   */
  private void postorderTraversal(Node<D> root, List<Node<D>> traversal) {
    root.getChilds().stream().forEach(child -> postorderTraversal(child, traversal));
    traversal.add(root);
  }
  
  // endregion
  
  // region 构建
  
  abstract protected TreeConstructor<D> getTreeConstructor();
  
  /**
   * 树的构建， 结构构建法
   *
   * @param root
   * @return
   */
  public Node<D> construct(Node<D> root) {
    if (!getTreeConstructor().isLeafData(root)) {
      // 寻找结点数据自身的子数据
      List<D> childDatas = getTreeConstructor().listChildDatas(root);
      // 利用数据自身的子数据构造子节点
      List<Node<D>> childs = childDatas.stream().map(childData -> new Node<D>(childData, root)).collect(Collectors.toList());
      // 对所有子节点递归
      childs.stream().forEach(this::construct);
      // 设置当前结点的子结点
      root.setChilds(childs);
    }
    return root;
  }
  
  /**
   * 树的构建, 标识构建法
   *
   * @param traversal
   * @return
   */
  public Node<List<Node<D>>> hierachy(List<Node<D>> traversal) {
    Node<List<Node<D>>> root = new Node<List<Node<D>>>(new ArrayList<Node<D>>());
    hierachy(root, traversal);
    root.setData(new ArrayList<>());
    return root;
  }
  
  public Node<List<Node<D>>> hierachy(Node<List<Node<D>>> root, List<Node<D>> traversal) {
    Node<List<Node<D>>> anchor = root;
    for (int ix = 0; ix < traversal.size(); ix++) {
      Node<D> node = traversal.get(ix);
      // 添加到自己
      anchor.getData().add(node);
      // 添加到所有祖先
      Node<List<Node<D>>> ancestor = anchor;
      while (ancestor.getParent() != null) {
        ancestor = ancestor.getParent();
        ancestor.getData().add(node);
      }
      // 创建当前元素为子节点
      Node<List<Node<D>>> childNode = new Node<List<Node<D>>>(new ArrayList<Node<D>>(), anchor);
      childNode.getData().add(node);
      // 向下
      if (getTreeConstructor().isDownward(node)) {
        // 添加子节点
        anchor.getChilds().add(childNode);
        anchor = childNode;
      }
      // 向上
      else if (getTreeConstructor().isUpward(node)) {
        Node<List<Node<D>>> upNode = anchor.getParent();
        anchor = upNode;
      }
      // 普通元素, 添加子节点, 锚点不变
      else {
        anchor.getChilds().add(childNode);
      }
    }
    return root;
  }
  
  
  
  // endregion
  
  
}
