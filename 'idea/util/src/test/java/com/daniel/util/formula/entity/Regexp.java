package com.daniel.util.formula.entity;

import com.daniel.util.formula.constant.RegexpEnum;

/**
 * 正则式
 * <p>
 * 碎片化的正则表达式, 可以组成 公式 {@link Formula}
 */
public interface Regexp {
  RegexpEnum getRegexpEnum();
}
