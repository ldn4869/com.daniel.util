package com.daniel.util.tester;

import com.daniel.util.entity.Student;
import com.daniel.util.json.NodeOperator;
import com.daniel.util.space.DataSpace;
import com.daniel.util.tree.Node;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;

public class VectorTester {
  
  // [start]* ################################ 调用方法 ################################ */
  private static List<Student> getStus() {
    List<Student> stus = new ArrayList<Student>() {
      /**
       *
       */
      private static final long serialVersionUID = 1L;
      
      {
        // A1
        add(new Student("STU1", 1, "A"));
        add(new Student("STU1", 2, "A"));
        add(new Student("STU1", 3, "A"));
        // B1
        add(new Student("STU1", 1, "B"));
        // C1
        add(new Student("STU1", 1, "C"));
        // A2
        add(new Student("STU2", 1, "A"));
        // B2
        add(new Student("STU2", 1, "B"));
        add(new Student("STU2", 2, "B"));
        // C2
        add(new Student("STU2", 1, "C"));
        add(new Student("STU2", 2, "C"));
        add(new Student("STU2", 3, "C"));
        add(new Student("STU2", 4, "C"));
        // B3
        add(new Student("STU3", 1, "B"));
        // C3
        add(new Student("STU3", 1, "C"));
        add(new Student("STU3", 2, "C"));
        add(new Student("STU3", 3, "C"));
      }
    };
    return stus;
    
  }
  // [end]
  
  //  @Test
  public void test1() throws Exception {
    List<Student> stus = getStus();
    
    Map<String, Map<String, List<Student>>> stuVecs = DataSpace.vectorify(stus, Student::getName, Student::getSex);
    Predicate pre = node -> {
      if (List.class.isInstance(node)) {
        List nodeList = (List) node;
        return nodeList.size() > 0 && Student.class.isInstance(nodeList.get(0));
      }
      return false;
    };
    NodeOperator.substitute(stuVecs, List.class, node -> {
      List<Student> list = (List) node;
//      list.sort(Comparator.comparing(Student::getAge).reversed());
      return list;
    });
    List<Student> list = NodeOperator.extract(stuVecs, List.class, po -> po.get(0));
    return;
  }
  
  @Test
  public void test2() throws Exception {
    List<Student> stus1 = getStus();
    List<Student> stus2 = new ArrayList<Student>(stus1);
    stus1.removeIf(stu -> "STU1".equals(stu.getName()) && "C".equals(stu.getSex()));
    stus2.removeIf(stu -> "STU2".equals(stu.getName()) && "A".equals(stu.getSex()));
    
    Map<String, Map<String, List<Student>>> stuVecs1 = DataSpace.vectorify(stus1, "name", "sex");
    Map<String, Map<String, List<Student>>> stuVecs2 = DataSpace.vectorify(stus2, "name", "sex");
//    List<Student> removed = stus2.stream()
//        .filter(stu -> stuVecs1.containsKey(stu.getName()) && stuVecs1.get(stu.getName()).containsKey(stu.getSex()))
//        .collect(Collectors.toList());
    Predicate pre1 = node -> Student.class.isInstance(node);
    Predicate<Student> pre2 = (Student stu) -> stuVecs1.containsKey(stu.getName())
        && stuVecs1.get(stu.getName()).containsKey(stu.getSex());
    NodeOperator.remove(stuVecs2, pre1.and(pre2.negate()));
    
    List list = NodeOperator.extract(stuVecs2, Student.class, po -> po);
    return;
    
  }
  
  private int level(Node node) {
    int level = 0;
    for (; node.getParent() != null; level++) {
      node = node.getParent();
    }
    return level;
  }
  
  private List<Node> route(Node head, Node tail) {
    List<Node> headRoute = new ArrayList<>();
    List<Node> tailRoute = new ArrayList<>();
    for (; head == tail; ) {
      int headLevel = level(head);
      int tailLevel = level(tail);
      if (headLevel > tailLevel) {
        headRoute.add(head);
        head = head.getParent();
      } else {
        tailRoute.add(0, tail);
        tail  = tail.getParent();
      }
    }
    headRoute.add(head);
    List<Node> route = new ArrayList<>();
    route.addAll(headRoute);
    return null;
  }
  
  @Test
  public void test3() throws Exception {
    List<Student> stus = getStus();
    Node<Student> grand = new Node<>(stus.get(0));
    Node<Student> parent1 = new Node<>(stus.get(1));
    Node<Student> parent2 = new Node<>(stus.get(2));
    Node<Student> parent3 = new Node<>(stus.get(3));
    Node<Student> child11 = new Node<>(stus.get(4));
    Node<Student> child12 = new Node<>(stus.get(5));
    Node<Student> child21 = new Node<>(stus.get(6));
    Node<Student> child22 = new Node<>(stus.get(7));
    Node<Student> child23 = new Node<>(stus.get(8));
    Node<Student> child31 = new Node<>(stus.get(9));
    Node<Student> child32 = new Node<>(stus.get(10));
    Node<Student> child33 = new Node<>(stus.get(11));
    grand.setChilds(new ArrayList<>(Arrays.asList(parent1, parent2, parent3)));
    parent1.setChilds(new ArrayList<>(Arrays.asList(child11, child12)));
    parent2.setChilds(new ArrayList<>(Arrays.asList(child21, child22, child23)));
    parent3.setChilds(new ArrayList<>(Arrays.asList(child31, child32, child33)));
    List<Node<Student>> childs = grand.getChilds();
    return;
  }
  
  
}
