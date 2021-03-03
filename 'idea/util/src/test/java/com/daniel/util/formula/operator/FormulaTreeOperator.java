package com.daniel.util.formula.operator;

import com.daniel.util.formula.entity.Formula;
import com.daniel.util.tree.TreeConstructor;
import com.daniel.util.tree.TreeOperator;

public final class FormulaTreeOperator extends TreeOperator<Formula> {

  // region 树构造器

  /**
   * 树构造器
   */
  final private TreeConstructor<Formula> treeConstructor = new TreeConstructor<Formula>() {

  };

  /**
   * @return
   */
  @Override
  protected TreeConstructor<Formula> getTreeConstructor() {
    return treeConstructor;
  }

  // endregion
}
