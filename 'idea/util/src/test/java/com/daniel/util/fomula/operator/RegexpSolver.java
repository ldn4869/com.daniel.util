package com.daniel.util.fomula.operator;

import com.daniel.util.fomula.constant.BoundEnum;
import com.daniel.util.fomula.constant.BunchEnum;
import com.daniel.util.fomula.constant.FormulaEnum;
import com.daniel.util.fomula.constant.PatternConst;
import com.daniel.util.fomula.constant.RegexpEnum;
import com.daniel.util.fomula.entity.Capture;
import com.daniel.util.fomula.entity.CaptureBound;
import com.daniel.util.fomula.entity.Expression;
import com.daniel.util.fomula.entity.ExpressionLine;
import com.daniel.util.fomula.entity.Formula;
import com.daniel.util.fomula.entity.Indexable;
import com.daniel.util.fomula.entity.LineBound;
import com.daniel.util.fomula.entity.Regexp;
import com.daniel.util.fomula.entity.Solution;
import com.daniel.util.fomula.exception.FomulaException;
import com.daniel.util.tree.Node;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class RegexpSolver {

  // region 分割文本为正则式

  public List<Node<Regexp>> split(List<String> formulas) {

    List<Node<Regexp>> regexps = new ArrayList<Node<Regexp>>();
    for (int ix = 0; ix < formulas.size(); ix++) {
      String formula = formulas.get(ix);
      Matcher openBoundMatcher = Pattern.compile(PatternConst.LINE_START + PatternConst.INDENT + PatternConst.CAPTURE_OPEN_BOUND + PatternConst.LINE_END).matcher(formula);
      Matcher closeBoundMatcher = Pattern.compile(PatternConst.LINE_START + PatternConst.INDENT + PatternConst.CAPTURE_CLOSE_BOUND + PatternConst.LINE_END).matcher(formula);
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

  // region 聚集为结构

  /**
   * 将层次化的碎片结构聚集为结构树
   *
   * @param self
   * @param destFormula
   * @return
   */
  public void gather(Node<List<Node<Regexp>>> self, Node<Formula> destFormula) {
    List<Node<Regexp>> regexps = self.getData();
    // 根结点
    if (regexps.size() == 0) {

    }
    // 捕获组结点, 创建捕获组
    else if (RegexpEnum.CAPTURE_BOUND == regexps.get(0).getData().getRegexpEnum()) {
      Capture capture = createCapture(regexps);
      destFormula.setData(capture);
    }
    // 行结点, 创建捕获组
    else if (RegexpEnum.LINE_BOUND == regexps.get(0).getData().getRegexpEnum()) {
      ExpressionLine expressionLine = createExpressionLine(regexps);
      destFormula.setData(expressionLine);
    }
    // 普通内容结点, 创建匹配表达式
    else {
      Node<Regexp> first = regexps.get(0);
      Expression expression = (Expression) first.getData();
      destFormula.setData(expression);
    }
    // 递归子结点
    for (Node<List<Node<Regexp>>> child : self.getChilds()) {
      Node<Formula> destChild = new Node<Formula>();
      destChild.setParent(destFormula);
      destFormula.getChilds().add(destChild);
      gather(child, destChild);
    }
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

  /**
   * 依据行开闭边界内的相关元素创建行表达式
   *
   * @param nodes
   * @return
   */
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
        Node<Regexp> lineNode = new Node<Regexp>(() -> RegexpEnum.LINE);
        lineNode.setChilds(nodes.subList(1, nodes.size() - 1));
        ExpressionLine expressionLine = new ExpressionLine(toLineExpression(lineNode));
        return expressionLine;
      }
      throw new FomulaException();
    }
    throw new FomulaException();
  }

  /**
   * 作为行内表达式结点的正则表达式
   *
   * @param node
   * @return
   */
  private String toLineExpression(Node<Regexp> node) {
    Regexp regexp = node.getData();
    switch (regexp.getRegexpEnum()) {
      // 正则匹配式逻辑
      case EXPRESSION:
        Expression expression = (Expression) regexp;
        return expression.getRegex();
      case LINE:
        StringBuffer lineChildStringBuffer = new StringBuffer();
        for (Node<Regexp> child : node.getChilds()) {
          String childRegex = toLineExpression(child);
          lineChildStringBuffer.append(childRegex);
        }
        return lineChildStringBuffer.toString();
      case CAPTURE_BOUND:
        CaptureBound captureBound = (CaptureBound) regexp;
        return BoundEnum.OPEN == captureBound.getBoundEnum() ? "(?<" + captureBound.getName() + ">" : ")" + captureBound.getRepeatSuffix();
      default:
        return "";
    }
  }
  // endregion

  // region 重建为匹配式

  /**
   * 将行内的结点重建为符合该节点的捕获组匹配式
   *
   * @param node
   * @return
   */
  public String constructExpression(Node<Formula> node) {
    return constructExpression(node, node);
  }

  private String constructExpression(Node<Formula> node, Node<Formula> target) {
    Formula formula = node.getData();
    // 正则匹配式逻辑
    // 末端表达式
    if (FormulaEnum.EXPRESSION == formula.getFormulaEnum()) {
      Expression expression = (Expression) formula;
      return expression.getRegex();
    }
    // 行表达式
    else if (FormulaEnum.LINE == formula.getFormulaEnum()) {
      StringBuffer childStringBuffer = new StringBuffer();
      for (Node<Formula> child : node.getChilds()) {
        String childExpression = constructExpression(child, target);
        childStringBuffer.append(childExpression);
      }
      return childStringBuffer.toString();
    }
    // 捕获组
    else if (FormulaEnum.CAPTURE == formula.getFormulaEnum()) {
      StringBuffer childStringBuffer = new StringBuffer();
      for (Node<Formula> child : node.getChilds()) {
        String childExpression = constructExpression(child, target);
        childStringBuffer.append(childExpression);
      }
      Capture capture = (Capture) formula;
      // 捕获组完整正则式
      String regex = childStringBuffer.toString();
      // 并非匹配目标节点时, 加重复后缀
      if (node != target) {
        regex = "(" + regex + ")" + capture.getRepeatSuffix();
      }
      // 为目标节点下属捕获组命名
      if (node.getParent() == target) {
        regex = "(?<" + capture.getName() + ">" + regex + ")";
      }
      return regex;
    } else {
      return "";
    }
  }
  // endregion

  // region 匹配过程

  public void outerMatch(Node<Formula> formulaNode, Node<Solution> solutionNode, List<String> materials) {
    Formula formula = formulaNode.getData();
    // 行外捕获组
    if (FormulaEnum.CAPTURE == formula.getFormulaEnum()) {
      Solution solution = new Solution(formulaNode.getData());
      solutionNode.setData(solution);
      // 递归子结点
      for (Node<Formula> childFormulaNode : formulaNode.getChilds()) {
        // 捕获组解决方案即为解决方案本身
        outerMatch(childFormulaNode, solutionNode, materials);
      }
    }
    // 行
    else if (FormulaEnum.LINE == formula.getFormulaEnum()) {
      String material = materials.remove(0);
      solutionNode.getData().setMaterial(material);
      for (Node<Formula> childFormulaNode : formulaNode.getChilds()) {
        if (FormulaEnum.CAPTURE == childFormulaNode.getData().getFormulaEnum()) {
          Node<Solution> childSolution = new Node<Solution>(new Solution(childFormulaNode.getData()), solutionNode);
          solutionNode.getChilds().add(childSolution);
          innerMatch(childFormulaNode, childSolution);
        } else {
          innerMatch(childFormulaNode, solutionNode);
        }
      }
    } else {
      throw new FomulaException();
    }
  }

  public void innerMatch(Node<Formula> formulaNode, Node<Solution> solutionNode) {
    Formula formula = formulaNode.getData();
    Solution solution = solutionNode.getData();
    // 行内捕获组
    if (FormulaEnum.CAPTURE == formula.getFormulaEnum()) {
      // 为了提取公式结点对应的捕获组信息, 匹配式为 公式结点 在 公式父结点 上的表达式
      String capturesRegex = constructExpression(formulaNode.getParent());
      // 材料为 解式父结点 的解式材料
      Matcher capturesMatcher = Pattern.compile(capturesRegex).matcher(solutionNode.getParent().getData().getMaterial());
      // 捕获组信息
      Capture capture = (Capture) formula;
      int repeatCount = 0;
      String material = null;
      boolean isListSolution = isListSolution(solutionNode);
      // 将捕获到的信息作为该捕获组的材料, 用于其子结点的匹配
      while (capturesMatcher.find()) {
        repeatCount++;
        material = capturesMatcher.group(capture.getName());
      }
      solution.setMaterial(material);
      // 如果捕获组是簇捕获组, 对簇材料再次进行多次匹配, 将匹配信息作为其子结点的材料
      
      // 递归子结点
      for (Node<Formula> childFormulaNode : formulaNode.getChilds()) {
        // 子结点为捕获组, 新建解式
        if (FormulaEnum.CAPTURE == childFormulaNode.getData().getFormulaEnum()) {
          Solution childSolution = new Solution(childFormulaNode.getData());
          Node<Solution> childSolutionNode = new Node<Solution>(childSolution, solutionNode);
          solutionNode.getChilds().add(childSolutionNode);
          innerMatch(childFormulaNode, childSolutionNode);
        }
        // 仅仅验证表达式是否正确
        else {
          innerMatch(childFormulaNode, solutionNode);
        }
      }
    }
    // 行内表达式
    else if (FormulaEnum.EXPRESSION == formula.getFormulaEnum()) {
//      // 移去未被捕获的
//      destSolution.getParent().getChilds().remove(destSolution);
    } else {
      throw new FomulaException();
    }

  }

  /**
   * 结点是否是父结点解式的逻辑解式组
   *
   * @param solutionNode
   * @return
   */
  private boolean isListSolution(Node<Solution> solutionNode) {
    Formula parentFormula = solutionNode.getParent().getData().getFormula();
    return FormulaEnum.CAPTURE == parentFormula.getFormulaEnum() && BunchEnum.LIST == ((Capture) parentFormula).getBunchEnum();
  }
  // endregion


}
