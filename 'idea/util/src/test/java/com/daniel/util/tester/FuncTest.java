package com.daniel.util.tester;

import com.daniel.util.formula.constant.BunchEnum;
import com.daniel.util.formula.entity.Capture;
import com.daniel.util.formula.entity.Expression;
import com.daniel.util.entity.FileBlock;
import com.daniel.util.formula.entity.Formula;
import com.daniel.util.formula.entity.Regexp;
import com.daniel.util.formula.entity.Solution;
import com.daniel.util.formula.operator.FormulaTreeOperator;
import com.daniel.util.json.PojoUtil;
import com.daniel.util.tree.Node;
import com.daniel.util.tree.TreeOperator;
import com.daniel.util.util.FileBlockTreeOperator;
import com.daniel.util.formula.operator.RegexpSolver;
import com.daniel.util.formula.operator.RegexpTreeOperator;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FuncTest {

  @Test
  public void test1() throws Exception {
    String uuid = UUID.randomUUID().toString();
    StringUtils.remove(uuid, '-');
    return;
  }

  @Test
  public void test2() throws Exception {
    String uuid = UUID.randomUUID().toString();
    uuid = StringUtils.remove(uuid, '-');
    byte[] bytes = uuid.getBytes();
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    byte[] lineBytes = new byte[10];
    byte[] bigBytes = new byte[50];
    System.out.println(byteBuffer.position());
    byteBuffer.get(lineBytes);
    System.out.println(byteBuffer.position());
    try {
      byteBuffer.get(bigBytes);
    } catch (BufferUnderflowException e) {
      e.printStackTrace();
    }
    System.out.println(byteBuffer.position());
    return;
  }

  @Test
  public void test3() throws Exception {
    Boolean b = null;
    Boolean a = (Boolean) null;
    String str = b != Boolean.valueOf(true) ? "true" : "false";
    System.out.println(str + a);
    return;
  }

  @Test
  public void test4() throws Exception {
    File file = new File("src/main/resources/d0");
    FileBlockTreeOperator fileNodeUtil = new FileBlockTreeOperator();
    Node<FileBlock> root = fileNodeUtil.construct(new Node<FileBlock>(FileBlock.instance(file)));
    List<Node<FileBlock>> DLRTraversal = fileNodeUtil.traversal(root, TreeOperator.TraversalEnum.DLR);
    List<Node<FileBlock>> LRDTraversal = fileNodeUtil.traversal(root, TreeOperator.TraversalEnum.LRD);
    return;
  }

  @Test
  public void test5() throws Exception {
    String fStr = "FFFFFFFFFFFFFFFF";
    String zStr = "0000000000000000";
    String oStr = "1111111111111111";

    String input = "15";

    BigInteger bigInteger = new BigInteger(input);
    BigInteger zero = BigInteger.valueOf(0);
    BigInteger one = BigInteger.valueOf(1);
    String binString;
    String hexString;
    if (bigInteger.compareTo(zero) >= 0) {
      binString = bigInteger.toString(2);
      binString = zStr.substring(0, 16 - binString.length()) + binString;
      hexString = bigInteger.toString(16).toUpperCase();
      hexString = zStr.substring(0, 4 - hexString.length()) + hexString;
    } else {
      BigInteger compBigInteger = bigInteger;
      binString = compBigInteger.toString(2);
      binString = oStr.substring(0, 16 - binString.length()) + binString;
      hexString = compBigInteger.toString(16).toUpperCase();
      hexString = fStr.substring(0, 4 - hexString.length()) + hexString;
    }

    return;
  }

  @Test
  public void test6() throws Exception {
    String regex = "^(?<name>\\w+):(?<tels>\\((?<area>\\d+)-(?<num>\\d+)\\),?)+$";
    String line = "daniel:(0797-8355205),(021-18817392972),(021-18918503239)";
//    String regexpStr = "(((1)(2))3(4))5(6)";
    String regexpStr = "(?<a>(?<b>1)2(?<c>3))(?<d>(?<e>4))";
//    String regexpStr = "(?<a>(1)2(3))";
    RegexpSolver regexpSolver = new RegexpSolver();
    List<Node<Regexp>> traversal = regexpSolver.splitLine(regex);
    RegexpTreeOperator regexpTreeOperator = new RegexpTreeOperator();
    Node<List<Node<Regexp>>> hierachy = regexpTreeOperator.hierachy(traversal);
    Node<Formula> root = new Node<Formula>(new Expression("root"));
    regexpSolver.gather(hierachy, root);

    return;
  }

  @Test
  public void test7() throws Exception {
    File materialFile = new File("src/main/resources/material/test.txt");
    File formulaFile = new File("src/main/resources/material/formula");
    List<String> formulas = new ArrayList<>();
    // 读取材料文件
    List<String> materials = new ArrayList<>();
    try (
        BufferedReader formulaBufferedReader = new BufferedReader(new FileReader(formulaFile))) {
      String line;
      while ((line = formulaBufferedReader.readLine()) != null) {
        formulas.add(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    try (
        BufferedReader materialBufferedReader = new BufferedReader(new FileReader(materialFile))) {
      String line;
      while ((line = materialBufferedReader.readLine()) != null) {
        materials.add(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // 整合为树结构
    RegexpSolver regexpSolver = new RegexpSolver();
    List<Node<Regexp>> chips = regexpSolver.split(formulas);
    RegexpTreeOperator regexpTreeOperator = new RegexpTreeOperator();
    FormulaTreeOperator formulaTreeOperator = new FormulaTreeOperator();
    Node<List<Node<Regexp>>> hierachy = regexpTreeOperator.hierachy(chips);
    Node<Formula> formulaNode = new Node<Formula>(new Capture("root", BunchEnum.ELEMENT));
    regexpSolver.gather(hierachy, formulaNode);
    regexpSolver.removeNullCapture(formulaNode);
//    Node<Solution> solutionNode = regexpSolver.constructSolution(root);
    Node<Solution> solutionRoot = new Node<Solution>(new Solution(formulaNode.getData()));
    regexpSolver.setMaterials(materials);
    regexpSolver.outerMatch(formulaNode, solutionRoot, BunchEnum.NONE);
    Map<String, Object> map = new HashMap<String, Object>();
    regexpSolver.toMap(solutionRoot, map);
    return;
  }

  @Test
  public void test8() throws Exception {
    Capture capture = new Capture();
    capture.setName("capture");
    capture.setBunchEnum(BunchEnum.NONE);
    capture.setMinRepeat(null);
    capture.setMaxRepeat(256);
    Capture c2 = PojoUtil.copy(capture, Capture.class);
    c2.setMaxRepeat(257);
    return;
  }


}
