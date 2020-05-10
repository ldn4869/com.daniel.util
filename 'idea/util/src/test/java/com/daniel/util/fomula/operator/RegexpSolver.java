package com.daniel.util.fomula.operator;

import com.daniel.util.fomula.entity.*;
import com.daniel.util.tree.Node;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexpSolver {

  final private RegexpTreeOperator regexpTreeOperator = new RegexpTreeOperator();

  // region 分割文本为正则式

  public List<Node<Regexp>> split(List<String> formulas) {
//    String openBound = "^ *(?<open>\\()\\?<(?<name>[A-Za-z0-9]+)>$";
//    String closeBound = "^ *(?<finSymbol>(?<close>\\))\\+?)$";
//    // 捕获组边界
//    String captureBoundContent = openBound + "|" + closeBound;
    List<Node<Regexp>> regexps = new ArrayList<Node<Regexp>>();
//    List<Node<Regexp>> lines = new ArrayList<Node<Regexp>>();
//    for (int ix = 0; ix < formulas.size(); ix++) {
//      String formula = formulas.get(ix);
//      Matcher captureBoundMatcher = Pattern.compile(captureBoundContent).matcher(formula);
//      // 按捕获组边界分割正则式
//      if (captureBoundMatcher.matches()) {
//        // 将先前的匹配式存入
//        if (lines.size() > 0) {
//          regexps.addAll(lines);
//          lines = new ArrayList<Node<Regexp>>();
//        }
//        // 按捕获组的开闭括号创建边界匹配对象
//        regexps.add(new Node<Regexp>(createCaptureBound(captureBoundMatcher)));
//      }
//      // 不匹配边界正则式时, 添加匹配式
//      else {
//        lines.add(new Node<Regexp>(new LineBound(BoundEnum.OPEN)));
//        lines.addAll(splitLine(formula));
//        lines.add(new Node<Regexp>(new LineBound(BoundEnum.CLOSE)));
//      }
//    }
//    // 将最后的匹配式存入
//    if (lines.size() > 0) {
//      regexps.addAll(lines);
//    }
    return regexps;
  }

  /**
   * 将一行正则式分割为多个捕获组与表达式
   *
   * @param regex
   * @return
   */
  public List<Node<Regexp>> splitLine(String regex) {
    // 零个或偶数个转义符, 以区分转义括号与捕获组边界
    String evenBackslash = "((?<!\\\\)|(?<=\\\\\\\\)+)";
    String openBound = evenBackslash + "\\((\\?<(?<name>\\w+)>)?";
    String closeBound = evenBackslash + "\\)(?<bunchSymbol>\\+)?";
    String squareOpenBound = evenBackslash + "(?<squareOpen>\\[)";
    String squareCloseBound = evenBackslash + "(?<squareClose>\\])";
    String orRegex = "|";
    // 捕获组边界
    Matcher openBoundMatcher = Pattern.compile(openBound).matcher(regex);
    Matcher closeBoundMatcher = Pattern.compile(closeBound).matcher(regex);
    // 逃逸组边界
    Matcher escapeOpenBoundMatcher = Pattern.compile(openBound).matcher(regex);
    Matcher escapeCloseBoundMatcher = Pattern.compile(closeBound).matcher(regex);
    List<CaptureBound> bounds = new ArrayList<CaptureBound>();
    // 捕获组边界
    while (openBoundMatcher.find()) {
      bounds.add(createCaptureBound(BoundEnum.OPEN, openBoundMatcher));
    }
    while (closeBoundMatcher.find()) {
      bounds.add(createCaptureBound(BoundEnum.CLOSE, closeBoundMatcher));
    }
    bounds.sort(Comparator.comparing(CaptureBound::getStart));
    // 初始化
    List<Node<Regexp>> regexps = new ArrayList<Node<Regexp>>();
    // 头部
    regexps.add(new Node<Regexp>(new LineBound(BoundEnum.OPEN)));
    if (bounds.size() > 0 && bounds.get(0).getStart() > 0) {
      regexps.add(new Node<Regexp>(new Expression(StringUtils.substring(regex, 0, bounds.get(0).getStart()))));
    }
    for (int ix = 0; ix < bounds.size() - 1; ix++) {
      CaptureBound current = bounds.get(ix);
      regexps.add(new Node<Regexp>(current));
      // 考察现一边界结束和下一边界开始之间需不需要添加表达式
      CaptureBound next = bounds.get(ix + 1);
      if (current.getEnd() < next.getStart()) {
        regexps.add(new Node<Regexp>(new Expression(StringUtils.substring(regex, current.getEnd(), next.getStart()))));
      }
    }
    // 尾部
    if (bounds.size() > 0 && bounds.get(bounds.size() - 1).getEnd() < regex.length()) {
      regexps.add(new Node<Regexp>(new Expression(StringUtils.substring(regex, bounds.get(bounds.size() - 1).getEnd(), regex.length()))));
    }
    regexps.add(new Node<Regexp>(new LineBound(BoundEnum.CLOSE)));
    return regexps;
  }

  /**
   * 依据正则匹配结果创建捕获组边界
   *
   * @param matcher
   * @return
   */
  public CaptureBound createCaptureBound(BoundEnum boundEnum, Matcher matcher) {
    CaptureBound captureBound = new CaptureBound();
    captureBound.setBoundEnum(boundEnum);
    captureBound.setStart(matcher.start());
    captureBound.setEnd(matcher.end());
    if (BoundEnum.OPEN == boundEnum) {
      captureBound.setName(matcher.group("name"));
    } else {
      // 设置捕获组簇类型
      String bunchSymbol = matcher.group("bunchSymbol");
      if (StringUtils.equals(bunchSymbol, "+")) {
        captureBound.setBunchEnum(BunchEnum.LIST);
      } else {
        captureBound.setBunchEnum(BunchEnum.ELEMENT);
      }
    }
    return captureBound;
  }

  // endregion


  public Node<Regexp> gather(Node<List<Node<Regexp>>> self, Node<Regexp> destRoot) {
    List<Node<Regexp>> regexps = self.getData();
    // 根结点
    if (regexps.size() == 0) {

    }
    // 捕获组结点, 创建捕获组
    else if (RegexpEnum.CAPTURE_BOUND == regexps.get(0).getData().getRegexpEnum()) {
      Capture capture = createCapture(regexps);
      destRoot.setData(capture);
    }
    // 行结点, 创建捕获组
    else if (RegexpEnum.LINE_BOUND == regexps.get(0).getData().getRegexpEnum()) {
      ExpressionLine expressionLine = createExpressionLine(regexps);
      destRoot.setData(expressionLine);
    }
    // 普通内容结点, 创建匹配表达式
    else {
      Node<Regexp> first = regexps.get(0);
      Expression expression = (Expression) first.getData();
      destRoot.setData(expression);
    }
    // 递归子结点
    for (Node<List<Node<Regexp>>> child : self.getChilds()) {
      Node<Regexp> destChild = new Node<Regexp>();
      destChild.setParent(destRoot);
      destRoot.getChilds().add(destChild);
      gather(child, destChild);
    }
    return destRoot;
  }

  private Capture createCapture(List<Node<Regexp>> nodes) {
    if (nodes.size() < 2) {
      throw new RuntimeException();
    }
    Regexp firstRegexp = nodes.get(0).getData();
    Regexp lastRegexp = nodes.get(nodes.size() - 1).getData();
    if (RegexpEnum.CAPTURE_BOUND == firstRegexp.getRegexpEnum() && RegexpEnum.CAPTURE_BOUND == lastRegexp.getRegexpEnum()) {
      CaptureBound open = (CaptureBound) firstRegexp;
      CaptureBound close = (CaptureBound) lastRegexp;
      if (BoundEnum.OPEN == open.getBoundEnum() && BoundEnum.CLOSE == close.getBoundEnum()) {
        Capture capture = new Capture();
        capture.setName(open.getName());
        capture.setBunchEnum(close.getBunchEnum());
        return capture;
      }
      throw new RuntimeException();
    }
    throw new RuntimeException();
  }

  private ExpressionLine createExpressionLine(List<Node<Regexp>> nodes) {
    if (nodes.size() < 2) {
      throw new RuntimeException();
    }
    Regexp firstRegexp = nodes.get(0).getData();
    Regexp lastRegexp = nodes.get(nodes.size() - 1).getData();
    if (RegexpEnum.LINE_BOUND == firstRegexp.getRegexpEnum() && RegexpEnum.LINE_BOUND == lastRegexp.getRegexpEnum()) {
      LineBound open = (LineBound) firstRegexp;
      LineBound close = (LineBound) lastRegexp;
      if (BoundEnum.OPEN == open.getBoundEnum() && BoundEnum.CLOSE == close.getBoundEnum()) {
        Node<Regexp> lineNode = new Node<Regexp>(new ExpressionLine(""));
        lineNode.setChilds(nodes.subList(1, nodes.size() - 1));
        ExpressionLine expressionLine = new ExpressionLine(toExpression(lineNode));
//        ExpressionLine expressionLine = new ExpressionLine("");
        return expressionLine;
      }
      throw new RuntimeException();
    }
    throw new RuntimeException();
  }

  public String toExpression(Node<Regexp> node, Node<Regexp> target) {
    Regexp regexp = node.getData();
    switch (regexp.getRegexpEnum()) {
      // 正则匹配式逻辑
      case EXPRESSION:
        Expression expression = (Expression) regexp;
        return expression.getRegex();
      // 捕获组逻辑
      case CAPTURE:
        Capture capture = (Capture) node.getData();
        StringBuffer childStringBuffer = new StringBuffer();
        for (Node<Regexp> child : node.getChilds()) {
          String childRegex = toExpression(child, target);
          childStringBuffer.append(childRegex);
        }
        // 捕获组完整正则式
        String regex = capture.getBunchEnum() == BunchEnum.ELEMENT ? childStringBuffer.toString() : "(" + childStringBuffer + ")+";
        // 为目标节点命名
        if (node == target) {
          regex = "(?<" + capture.getName() + ">" + regex + ")";
        }
        return regex;
      case LINE:
        ExpressionLine expressionLine = (ExpressionLine) regexp;
        StringBuffer lineChildStringBuffer = new StringBuffer();
        for (Node<Regexp> child : node.getChilds()) {
          String childRegex = toExpression(child, target);
          lineChildStringBuffer.append(childRegex);
        }
        return lineChildStringBuffer.toString();
      default:
        return "";
    }
  }

  public String toExpression(Node<Regexp> node) {
    Regexp regexp = node.getData();
    switch (regexp.getRegexpEnum()) {
      // 正则匹配式逻辑
      case EXPRESSION:
        Expression expression = (Expression) regexp;
        return expression.getRegex();
      // 捕获组逻辑
      case CAPTURE:
        Capture capture = (Capture) node.getData();
        StringBuffer childStringBuffer = new StringBuffer();
        for (Node<Regexp> child : node.getChilds()) {
          String childRegex = toExpression(child);
          childStringBuffer.append(childRegex);
        }
        // 捕获组完整正则式
        String regex = capture.getBunchEnum() == BunchEnum.ELEMENT ? childStringBuffer.toString() : "(" + childStringBuffer + ")+";
        // 为目标节点命名
        regex = "(?<" + capture.getName() + ">" + regex + ")";
        return regex;
      case LINE:
        ExpressionLine expressionLine = (ExpressionLine) regexp;
        StringBuffer lineChildStringBuffer = new StringBuffer();
        for (Node<Regexp> child : node.getChilds()) {
          String childRegex = toExpression(child);
          lineChildStringBuffer.append(childRegex);
        }
        return lineChildStringBuffer.toString();
      case LINE_BOUND:
        LineBound lineBound = (LineBound) regexp;
        return BoundEnum.OPEN == lineBound.getBoundEnum() ? "^" : "$";
      case CAPTURE_BOUND:
        CaptureBound captureBound = (CaptureBound) regexp;
        return BoundEnum.OPEN == captureBound.getBoundEnum() ? "(?<" + captureBound.getName() + ">" : BunchEnum.LIST == captureBound.getBunchEnum() ? ")+" : ")";
      default:
        return "";
    }
  }


}
