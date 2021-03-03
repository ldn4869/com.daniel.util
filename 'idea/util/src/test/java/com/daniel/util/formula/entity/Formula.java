package com.daniel.util.formula.entity;

import com.daniel.util.formula.constant.FormulaEnum;

/**
 * 公式
 * <p>
 * 可以实现为捕获组 {@link Capture}, 行 {@link ExpressionLine}, 表达式 {@link Expression}, 可以在文本材料上匹配捕获组的正则式
 */
public interface Formula extends Cloneable {

  FormulaEnum getFormulaEnum();

  Formula clone();

}
