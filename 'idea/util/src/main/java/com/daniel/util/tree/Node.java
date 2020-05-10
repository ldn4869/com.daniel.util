package com.daniel.util.tree;

import java.util.ArrayList;
import java.util.List;

public class Node<D> {
  
  private D data;
  
  private Node<D> parent;
  
  private List<Node<D>> childs = new ArrayList<Node<D>>();
  
  public Node() {
  
  }
  
  public Node(D data) {
    this.data = data;
  }
  
  public Node(D data, Node<D> parent) {
    this.data = data;
    this.parent = parent;
  }
  
  public D getData() {
    return data;
  }
  
  public void setData(D data) {
    this.data = data;
  }
  
  public Node<D> getParent() {
    return parent;
  }
  
  public void setParent(Node<D> parent) {
    this.parent = parent;
  }
  
  public List<Node<D>> getChilds() {
    return childs;
  }
  
  public void setChilds(List<Node<D>> childs) {
    this.childs = childs;
  }
  
  @Override
  public String toString() {
    return data.toString();
  }
}
