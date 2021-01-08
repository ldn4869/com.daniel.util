package com.daniel.util.fomula.constant;

/**
 * 正则模式
 */
public final class PatternConst {

  public static final String ONCE_OR_NOT = "?";

  public static final String ZERO_OR_MORE = "*";

  public static final String ONE_OR_MORE = "+";

  public static final String LINE_START = "^";

  public static final String LINE_END = "$";

  /**
   * 前向断言: 偶数个反斜杠
   */
  public static final String EVEN_BCAKSLASH = "((?<!\\\\)|(?<=\\\\\\\\)+)";

  /**
   * 行首缩进
   */
  public static final String INDENT = "^ *";

  /**
   * 捕获组开边界
   */
  public static final String CAPTURE_OPEN_BOUND = "\\((\\?<(?<name>\\w+)>)?";

  /**
   * 捕获组闭边界
   */
  public static final String CAPTURE_CLOSE_BOUND =  "\\)(?<bunchSymbol>\\+)?"; // \\)(?<bunchSymbol>\+|\*|\?|(\{[0-9,]\}))?

  /**
   * 逃逸组开边界
   */
  public static final String SQUARE_OPEN_BOUND = "\\[";

  /**
   * 逃逸组闭边界
   */
  public static final String SQUARE_CLOSE_BOUND = "\\]";


}
