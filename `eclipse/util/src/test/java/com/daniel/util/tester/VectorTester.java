package com.daniel.util.tester;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Test;

import com.daniel.util.entity.Student;
import com.daniel.util.json.NodeOperator;
import com.daniel.util.space.DataSpace;

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
      list.sort(Comparator.comparing(Student::getAge).reversed());
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

  /*
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   */

}
