package com.daniel.util.fomula.entity;

import com.daniel.util.fomula.constant.FormulaEnum;

/**
 * 公式
 * <p>
 * 可以实现为捕获组 {@link Capture}, 行 {@link ExpressionLine}, 表达式 {@link Expression}, 可以在材料上匹配捕获组的正则式
 */
public interface Formula {

  FormulaEnum getFormulaEnum();

}
