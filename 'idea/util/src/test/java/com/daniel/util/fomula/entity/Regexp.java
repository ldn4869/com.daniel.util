package com.daniel.util.fomula.entity;

import com.daniel.util.fomula.constant.RegexpEnum;

/**
 * 正则式
 * <p>
 * 碎片化的正则表达式, 可以组成 公式 {@link Formula}
 */
public interface Regexp {
  RegexpEnum getRegexpEnum();
}
