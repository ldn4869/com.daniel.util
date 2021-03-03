package com.daniel.util.formula.operator;

import com.daniel.util.formula.constant.BoundEnum;
import com.daniel.util.formula.constant.BunchEnum;
import com.daniel.util.formula.constant.FormulaEnum;
import com.daniel.util.formula.constant.PatternConst;
import com.daniel.util.formula.constant.RegexpEnum;
import com.daniel.util.formula.entity.Capture;
import com.daniel.util.formula.entity.CaptureBound;
import com.daniel.util.formula.entity.EscapeBound;
import com.daniel.util.formula.entity.Expression;
import com.daniel.util.formula.entity.ExpressionLine;
import com.daniel.util.formula.entity.Formula;
import com.daniel.util.formula.entity.Indexable;
import com.daniel.util.formula.entity.LineBound;
import com.daniel.util.formula.entity.Regexp;
import com.daniel.util.formula.entity.Solution;
import com.daniel.util.formula.exception.FormulaException;
import com.daniel.util.formula.exception.UnmatchedException;
import com.daniel.util.formula.exception.UnmatchedLineException;
import com.daniel.util.tree.Node;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.daniel.util.formula.constant.PatternConst.CAPTURE_CLOSE_BOUND;
import static com.daniel.util.formula.constant.PatternConst.CAPTURE_OPEN_BOUND;
import static com.daniel.util.formula.constant.PatternConst.EVEN_BCAKSLASH;
import static com.daniel.util.formula.constant.PatternConst.SQUARE_CLOSE_BOUND;
import static com.daniel.util.formula.constant.PatternConst.SQUARE_OPEN_BOUND;

/**
 *
 */
public class RegexpSolver {

  private int materialIx = 0;

  private List<String> materials;

  public void setMaterials(List<String> materials) {
    this.materials = materials;
  }

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
    Matcher openBoundMatcher = Pattern.compile(EVEN_BCAKSLASH + CAPTURE_OPEN_BOUND).matcher(regex);
    Matcher closeBoundMatcher = Pattern.compile(EVEN_BCAKSLASH + CAPTURE_CLOSE_BOUND).matcher(regex);
    // 逃逸组边界
    Matcher escapeOpenBoundMatcher = Pattern.compile(SQUARE_OPEN_BOUND).matcher(regex);
    Matcher escapeCloseBoundMatcher = Pattern.compile(SQUARE_CLOSE_BOUND).matcher(regex);
    // 逃逸组边界
    List<Integer> escapeBoundIxs = new ArrayList<Integer>();
    while (escapeOpenBoundMatcher.find()) {
      escapeBoundIxs.add(escapeOpenBoundMatcher.start());
    }
    while (escapeCloseBoundMatcher.find()) {
      escapeBoundIxs.add(escapeCloseBoundMatcher.start());
    }
    escapeBoundIxs.sort(Integer::compareTo);
    List<Indexable> bounds = new ArrayList<Indexable>();
    // 捕获组边界
    while (openBoundMatcher.find()) {
      // [ ] 之内 的 () 不解析为捕获组边界
      if (!betweenEscapes(escapeBoundIxs, openBoundMatcher.start())) {
        bounds.add(createCaptureBound(BoundEnum.OPEN, openBoundMatcher));
      }
    }
    while (closeBoundMatcher.find()) {
      if (!betweenEscapes(escapeBoundIxs, closeBoundMatcher.start())) {
        bounds.add(createCaptureBound(BoundEnum.CLOSE, closeBoundMatcher));
      }
    }
    return handleRegexByCaptureBound(regex, bounds);
  }

  private boolean betweenEscapes(List<Integer> escapeBoundIxs, int boundIx) {
    for (int ix = 0; ix < escapeBoundIxs.size(); ix += 2) {
      int startIx = escapeBoundIxs.get(ix);
      int endIx = escapeBoundIxs.get(ix + 1);
      if (boundIx > startIx && boundIx < endIx) {
        return true;
      }
    }
    return false;
  }

  /**
   * 根据捕获组边界分割表达式元素
   *
   * @param regex
   * @param bounds
   * @return
   */
  public List<Node<Regexp>> handleRegexByCaptureBound(String regex, List<Indexable> bounds) {
    bounds.sort(Comparator.comparing(Indexable::getStart));
    List<Indexable> indexables = new ArrayList<Indexable>();
    // 初始化
    int offset = 0;
    for (Indexable bound : bounds) {
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

  /**
   * 依据正则匹配结果创建捕获组边界
   *
   * @param matcher
   * @return
   */
  public EscapeBound createEscapeBound(BoundEnum boundEnum, Matcher matcher) {
    EscapeBound escapeBound = new EscapeBound();
    escapeBound.setBoundEnum(boundEnum);
    escapeBound.setStart(matcher.start());
    escapeBound.setEnd(matcher.end());
    return escapeBound;
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
      throw new FormulaException();
    }
    throw new FormulaException();
  }

  /**
   * 依据行开闭边界内的相关元素创建行表达式
   *
   * @param nodes
   * @return
   */
  private ExpressionLine createExpressionLine(List<Node<Regexp>> nodes) {
    if (nodes.size() < 2) {
      throw new FormulaException();
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
      throw new FormulaException();
    }
    throw new FormulaException();
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

  /**
   * 除去行捕获组结构中的空捕获组
   *
   * @param node
   */
  public void removeNullCapture(Node<Formula> node) {
    // 只有行子结点下的捕获组需要被移除
    for (int ix = 0; ix < node.getChilds().size(); ix++) {
      Node<Formula> child = node.getChilds().get(ix);
      if (FormulaEnum.LINE == child.getData().getFormulaEnum()) {
        removeInnerCapture(child);
      } else {
        removeNullCapture(child);
      }
    }
  }

  /**
   * 除去行捕获组结构中的空捕获组
   *
   * @param node
   */
  private void removeInnerCapture(Node<Formula> node) {
    // 非空名捕获组, 正常向下递归
    if (FormulaEnum.CAPTURE != node.getData().getFormulaEnum() || ((Capture) node.getData()).getName() != null) {
      for (int ix = 0; ix < node.getChilds().size(); ix++) {
        Node<Formula> child = node.getChilds().get(ix);
        removeInnerCapture(child);
      }
      return;
    }
    // 空名捕获组, 获取表达式
    String exp = constructExpression(node, null);
    // 新建替代的表达式结点
    Node<Formula> substituteNode = new Node<Formula>(new Expression(exp), node.getParent());
    // 移去旧捕获组结点, 附加新的替代表达式结点
    int ix = node.getParent().getChilds().indexOf(node);
    node.getParent().getChilds().remove(ix);
    node.getParent().getChilds().add(ix, substituteNode);
    return;
  }
  // endregion

  // region 重建为匹配式

  /**
   * 用于匹配其子结点公式所代表的捕获组 的表达式
   *
   * @param node 公式结点
   * @return
   */
  private String constructExpression(Node<Formula> node) {
    return constructExpression(node, node);
  }

  /**
   * 对于材料为 target 结点层次上的, 用于匹配 node 公式结点的
   *
   * @param node   捕获组结点
   * @param target 用于匹配材料的公式结点
   * @return
   */
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

  /**
   * 跨行匹配
   * <p>
   * 利用
   *
   * @param formulaNode        公式结点
   * @param parentSolutionNode 父解式结点
   */
  public void outerMatch(Node<Formula> formulaNode, Node<Solution> parentSolutionNode, BunchEnum bunchEnum) {
    Formula formula = formulaNode.getData();
    // 捕获组
    if (FormulaEnum.CAPTURE == formula.getFormulaEnum()) {
      Capture capture = (Capture) formula;
      // 公式是簇捕获组
      boolean isListCapture = BunchEnum.LIST == capture.getBunchEnum();
      // 解式是簇捕获组下的逻辑解式
      boolean isLogicSolution = BunchEnum.LOGIC == bunchEnum;
      // 普通单元素捕获组的解式
      if (!isListCapture && !isLogicSolution) {
        // 新建解式结点, 依附到父解式结点上
        Node<Solution> solutionNode = new Node<Solution>(new Solution(formula), parentSolutionNode);
        parentSolutionNode.addChild(solutionNode);
        // 递归子结点
        for (Node<Formula> childFormulaNode : formulaNode.getChilds()) {
          outerMatch(childFormulaNode, solutionNode, BunchEnum.NONE);
        }
      }
      // 簇捕获组的解式
      else if (isListCapture && !isLogicSolution) {
        // 新建解式结点, 依附到父解式结点上
        Node<Solution> solutionNode = new Node<Solution>(new Solution(formula), parentSolutionNode);
        parentSolutionNode.addChild(solutionNode);
        // 继续捕获 簇捕获组下的逻辑解式
        outerMatch(formulaNode, solutionNode, BunchEnum.LOGIC);
      }
      // 簇捕获组下的逻辑解式
      else if (isLogicSolution) {
        // 不断匹配行文本, 直到匹配错误
        int repeatCount = 0;
        while (materialIx < materials.size()) {
          // 新建解式结点, 待匹配成功后再依附到父结点上
          Capture childCapture = capture.clone();
          childCapture.setBunchEnum(BunchEnum.LOGIC);
          Node<Solution> solutionNode = new Node<Solution>(new Solution(childCapture), parentSolutionNode);
          try {
            // 递归子结点
            for (Node<Formula> childFormulaNode : formulaNode.getChilds()) {
              outerMatch(childFormulaNode, solutionNode, BunchEnum.NONE);
            }
            parentSolutionNode.addChild(solutionNode);
            repeatCount++;
          } catch (UnmatchedLineException e) {
            // 行匹配失败, 终止重复循环匹配
            System.out.println(capture + ": " + repeatCount);
            break;
          }
        }
      }
    }
    // 行公式匹配
    else if (FormulaEnum.LINE == formula.getFormulaEnum()) {
      String line = materials.get(materialIx++);
      try {
        innerMatch(formulaNode, parentSolutionNode, line, BunchEnum.NONE);
      } catch (UnmatchedLineException e) {
        materialIx--;
        throw e;
      }
    } else {
      throw new FormulaException();
    }
  }

  /**
   * 行内匹配
   * <p>
   * 将文本材料依据 公式 全部解析到 目标解式 下
   *
   * @param formulaNode        公式结点
   * @param parentSolutionNode 目标解式结点, 公式的解式将作为其子结点
   * @param material           文本材料
   * @param bunchEnum
   */
  public void innerMatch(Node<Formula> formulaNode, Node<Solution> parentSolutionNode, String material, BunchEnum bunchEnum) {
    Formula formula = formulaNode.getData();
    // 匹配整行文本材料
    if (FormulaEnum.LINE == formula.getFormulaEnum()) {
      // 匹配整行
      Matcher matcher = Pattern.compile(constructExpression(formulaNode)).matcher(material);
      // 整行匹配成功, 则继续往下进行行内匹配
      if (matcher.find()) {
        for (Node<Formula> childFormulaNode : formulaNode.getChilds()) {
          // 将整行文本作为材料用于提取捕获组信息
          innerMatch(childFormulaNode, parentSolutionNode, material, BunchEnum.NONE);
        }
      } else {
        throw new UnmatchedLineException();
      }
    }
    // 行内捕获组
    else if (FormulaEnum.CAPTURE == formula.getFormulaEnum()) {
      Capture capture = (Capture) formula;
      // 公式是簇捕获组
      boolean isListCapture = BunchEnum.LIST == capture.getBunchEnum();
      // 解式是簇捕获组下的逻辑解式
      boolean isLogicSolution = BunchEnum.LOGIC == bunchEnum;
      // 普通单元素捕获组的解式
      if (!isListCapture && !isLogicSolution) {
        // 为了提取公式结点对应的捕获组信息, 匹配式为 公式结点 在 公式父结点 上的表达式
        // 材料为 父公式结点 的解式材料
        Matcher matcher = Pattern.compile(constructExpression(formulaNode.getParent())).matcher(material);
        // 将捕获到的信息作为该捕获组的材料, 用于其子结点的匹配
        if (matcher.find()) {
          // 匹配到的信息作为解式的值
          String value = matcher.group(capture.getName());
          // 新建解式结点, 依附到父解式结点上
          Node<Solution> solutionNode = new Node<Solution>(new Solution(formula, value), parentSolutionNode);
          parentSolutionNode.addChild(solutionNode);
          // 继续匹配捕获组结点的子公式结点
          for (Node<Formula> childFormulaNode : formulaNode.getChilds()) {
            innerMatch(childFormulaNode, solutionNode, value, BunchEnum.NONE);
          }
        } else {
          throw new UnmatchedException();
        }
      }
      // 簇捕获组的解式
      else if (isListCapture && !isLogicSolution) {
        // 为了提取公式结点对应的捕获组信息, 匹配式为 公式结点 在 公式父结点 上的表达式
        // 材料为 父公式结点 的解式材料
        Matcher matcher = Pattern.compile(constructExpression(formulaNode.getParent())).matcher(material);
        // 将捕获到的信息作为该捕获组的材料, 用于其子结点的匹配
        if (matcher.find()) {
          // 匹配到的信息作为解式的值
          String value = matcher.group(capture.getName());
          // 新建解式结点, 依附到父解式结点上
          Node<Solution> solutionNode = new Node<Solution>(new Solution(formula, value), parentSolutionNode);
          parentSolutionNode.addChild(solutionNode);
          // 继续捕获 簇捕获组下的逻辑解式
          innerMatch(formulaNode, solutionNode, value, BunchEnum.LOGIC);
        } else {
          throw new UnmatchedException();
        }
      }
      // 簇捕获组下的逻辑解式
      else if (isLogicSolution) {
        // 为了提取公式结点对应的捕获组信息, 匹配式为 公式结点 在 公式父结点 上的表达式
        // 材料为 父公式结点 的解式材料
        Matcher matcher = Pattern.compile(constructExpression(formulaNode)).matcher(material);
        // 将捕获到的信息作为该捕获组的材料, 用于其子结点的匹配
        while (matcher.find()) {
          // 匹配到的信息作为解式的值
          String value = matcher.group();
          // 新建解式结点, 依附到父解式结点上
          Capture childCapture = capture.clone();
          childCapture.setBunchEnum(BunchEnum.LOGIC);
          Node<Solution> solutionNode = new Node<Solution>(new Solution(childCapture, value), parentSolutionNode);
          parentSolutionNode.addChild(solutionNode);
          // 继续匹配捕获组结点的子公式结点
          for (Node<Formula> childFormulaNode : formulaNode.getChilds()) {
            innerMatch(childFormulaNode, solutionNode, value, BunchEnum.NONE);
          }
        }
      }
    }
    // 行内表达式, 啥也不做
    else if (FormulaEnum.EXPRESSION == formula.getFormulaEnum()) {

    } else {
      throw new FormulaException();
    }
  }


  // endregion

  // region 转化为 map 结构


  /**
   * @param node
   * @param parentMap
   */
  public void toMap(Node<Solution> node, Map<String, Object> parentMap) {
    Solution solution = node.getData();
    Capture capture = (Capture) solution.getFormula();
    if (capture.getName() == null) {
      return;
    }
    BunchEnum bunchEnum = capture.getBunchEnum();
    // 是否有子结点, 无子结点, 则到达递归终点
    if (node.getChilds().size() == 0) {
      // 单结点, 存值
      if (BunchEnum.ELEMENT == bunchEnum) {
        parentMap.put(capture.getName(), solution.getValue());
      }
      // 复结点, 存空目录
      else if (BunchEnum.LIST == bunchEnum) {
        parentMap.put(capture.getName(), new ArrayList<String>());
      }
      return;
    }
    // 单结点
    if (BunchEnum.ELEMENT == bunchEnum) {
      Map<String, Object> map = new HashMap<String, Object>();
      parentMap.put(capture.getName(), map);
      for (Node<Solution> child : node.getChilds()) {
        toMap(child, map);
      }
    }
    // 复结点
    else if (BunchEnum.LIST == bunchEnum) {
      List<Object> list = new ArrayList<Object>();
      parentMap.put(capture.getName(), list);
      for (Node<Solution> child : node.getChilds()) {
        toList(child, list);
      }
    }

  }

  public void toList(Node<Solution> node, List<Object> parentList) {
    Solution solution = node.getData();
    Capture capture = (Capture) solution.getFormula();
    BunchEnum bunchEnum = capture.getBunchEnum();
    // 逻辑结点
    if (BunchEnum.LOGIC == bunchEnum) {
      // 是否有子结点, 无子结点, 则到达递归终点
      if (node.getChilds().size() == 0) {
        parentList.add(solution.getValue());
        return;
      }
      Map<String, Object> map = new HashMap<String, Object>();
      parentList.add(map);
      for (Node<Solution> child : node.getChilds()) {
        toMap(child, map);
      }
    }

  }
  // endregion

}
