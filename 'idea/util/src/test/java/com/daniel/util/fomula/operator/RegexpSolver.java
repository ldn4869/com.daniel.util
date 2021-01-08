package com.daniel.util.fomula.operator;

import com.daniel.util.fomula.constant.PatternConst;
import com.daniel.util.fomula.entity.*;
import com.daniel.util.fomula.exception.FomulaException;
import com.daniel.util.tree.Node;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexpSolver {

  final private RegexpTreeOperator regexpTreeOperator = new RegexpTreeOperator();

  // region 分割文本为正则式

  public List<Node<Regexp>> split(List<String> formulas) {

    List<Node<Regexp>> regexps = new ArrayList<Node<Regexp>>();
    for (int ix = 0; ix < formulas.size(); ix++) {
      String formula = formulas.get(ix);
      Matcher openBoundMatcher = Pattern.compile(PatternConst.LINE_START + PatternConst.INDENT + PatternConst.CAPTURE_OPEN_BOUND + PatternConst.LINE_END).matcher(formula);
      Matcher closeBoundMatcher = Pattern.compile(PatternConst.LINE_END + PatternConst.INDENT + PatternConst.CAPTURE_CLOSE_BOUND + PatternConst.LINE_END).matcher(formula);
      List<CaptureBound> bounds = new ArrayList<CaptureBound>();
      // 匹配捕获组边界时, 创建捕获组
      if (openBoundMatcher.find()) {
        regexps.add(new Node<Regexp>(createCaptureBound(BoundEnum.OPEN, openBoundMatcher)));
      } else if (closeBoundMatcher.find()) {
        regexps.add(new Node<Regexp>(createCaptureBound(BoundEnum.CLOSE, closeBoundMatcher)));
      }
      // 不匹配边界正则式时, 添加匹配式
      else {
        regexps.addAll(splitLine(formula));
      }
    }
    return regexps;
  }

  /**
   * 将一行正则式分割为多个捕获组与表达式
   *
   * @param regex
   * @return
   */
  public List<Node<Regexp>> splitLine(String regex) {
    // 捕获组边界
    Matcher openBoundMatcher = Pattern.compile(PatternConst.EVEN_BCAKSLASH + PatternConst.CAPTURE_OPEN_BOUND).matcher(regex);
    Matcher closeBoundMatcher = Pattern.compile(PatternConst.EVEN_BCAKSLASH + PatternConst.CAPTURE_CLOSE_BOUND).matcher(regex);
//    // 逃逸组边界
//    Matcher escapeOpenBoundMatcher = Pattern.compile(openBound).matcher(regex);
//    Matcher escapeCloseBoundMatcher = Pattern.compile(closeBound).matcher(regex);
    List<CaptureBound> bounds = new ArrayList<CaptureBound>();
    // 捕获组边界
    while (openBoundMatcher.find()) {
      bounds.add(createCaptureBound(BoundEnum.OPEN, openBoundMatcher));
    }
    while (closeBoundMatcher.find()) {
      bounds.add(createCaptureBound(BoundEnum.CLOSE, closeBoundMatcher));
    }
    return handleRegexByCaptureBound(regex, bounds);
  }

  /**
   * 根据捕获组边界分割表达式元素
   *
   * @param regex
   * @param bounds
   * @return
   */
  public List<Node<Regexp>> handleRegexByCaptureBound(String regex, List<CaptureBound> bounds) {
    bounds.sort(Comparator.comparing(CaptureBound::getStart));
    List<Indexable> indexables = new ArrayList<Indexable>();
    // 初始化
    int offset = 0;
    for (CaptureBound bound : bounds) {
      if (offset < bound.getStart()) {
        indexables.add(new Expression(regex, offset, bound.getStart()));
      }
      indexables.add(bound);
      offset = bound.getEnd();
    }
    if (offset < regex.length()) {
      indexables.add(new Expression(regex, offset, regex.length()));
    }
    indexables.sort(Comparator.comparing(Indexable::getStart));
    // 转换为 Regexp 表达式
    List<Node<Regexp>> elements = indexables.stream().map(indexable -> (Regexp) indexable).map(Node<Regexp>::new).collect(Collectors.toList());
    List<Node<Regexp>> regexps = new ArrayList<Node<Regexp>>();
    // 头部
    regexps.add(new Node<Regexp>(new LineBound(BoundEnum.OPEN)));
    regexps.addAll(elements);
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
      if (StringUtils.equals(bunchSymbol, PatternConst.ONCE_OR_NOT)) {
        captureBound.setBunchEnum(BunchEnum.LIST);
        captureBound.setMinRepeat(0);
        captureBound.setMaxRepeat(1);
      } else if (StringUtils.equals(bunchSymbol, PatternConst.ZERO_OR_MORE)) {
        captureBound.setBunchEnum(BunchEnum.LIST);
        captureBound.setMinRepeat(0);
      } else if (StringUtils.equals(bunchSymbol, PatternConst.ONE_OR_MORE)) {
        captureBound.setBunchEnum(BunchEnum.LIST);
        captureBound.setMinRepeat(1);
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

  /**
   * 依据捕获组开闭边界内的相关元素创建捕获组
   *
   * @param nodes
   * @return
   */
  private Capture createCapture(List<Node<Regexp>> nodes) {
    if (nodes.size() < 2) {
      throw new RuntimeException();
    }
    Regexp firstRegexp = nodes.get(0).getData();
    Regexp lastRegexp = nodes.get(nodes.size() - 1).getData();
    // 检验首尾表达式是否是捕获组开闭边界
    if (RegexpEnum.CAPTURE_BOUND == firstRegexp.getRegexpEnum() && RegexpEnum.CAPTURE_BOUND == lastRegexp.getRegexpEnum()) {
      CaptureBound open = (CaptureBound) firstRegexp;
      CaptureBound close = (CaptureBound) lastRegexp;
      if (BoundEnum.OPEN == open.getBoundEnum() && BoundEnum.CLOSE == close.getBoundEnum()) {
        Capture capture = new Capture();
        capture.setName(open.getName());
        capture.setBunchEnum(close.getBunchEnum());
        capture.setMinRepeat(close.getMinRepeat());
        capture.setMaxRepeat(close.getMaxRepeat());
        return capture;
      }
      throw new FomulaException();
    }
    throw new FomulaException();
  }

  private ExpressionLine createExpressionLine(List<Node<Regexp>> nodes) {
    if (nodes.size() < 2) {
      throw new FomulaException();
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
      throw new FomulaException();
    }
    throw new FomulaException();
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
