package com.daniel.util.formula.operator;

import com.daniel.util.formula.constant.BoundEnum;
import com.daniel.util.formula.entity.CaptureBound;
import com.daniel.util.formula.entity.LineBound;
import com.daniel.util.formula.entity.Regexp;
import com.daniel.util.formula.constant.RegexpEnum;
import com.daniel.util.tree.Node;
import com.daniel.util.tree.TreeConstructor;
import com.daniel.util.tree.TreeOperator;

public final class RegexpTreeOperator extends TreeOperator<Regexp> {

  // region 树构造器

  /**
   * 树构造器
   */
  final private TreeConstructor<Regexp> treeConstructor = new TreeConstructor<Regexp>() {
    /**
     * 下沉符号为 捕获组开边界 或者 行开边界
     * @param node
     * @return
     */
    @Override
    public boolean isDownward(Node<Regexp> node) {
      if (RegexpEnum.CAPTURE_BOUND == node.getData().getRegexpEnum()) {
        CaptureBound captureBound = (CaptureBound) node.getData();
        return captureBound.getBoundEnum() == BoundEnum.OPEN;
      } else if (RegexpEnum.LINE_BOUND == node.getData().getRegexpEnum()) {
        LineBound lineBound = (LineBound) node.getData();
        return lineBound.getBoundEnum() == BoundEnum.OPEN;
      }
      return false;
    }

    /**
     * 上浮符号为 捕获组闭边界 或者 行闭边界
     * @param node
     * @return
     */
    @Override
    public boolean isUpward(Node<Regexp> node) {
      if (RegexpEnum.CAPTURE_BOUND == node.getData().getRegexpEnum()) {
        CaptureBound captureBound = (CaptureBound) node.getData();
        return captureBound.getBoundEnum() == BoundEnum.CLOSE;
      } else if (RegexpEnum.LINE_BOUND == node.getData().getRegexpEnum()) {
        LineBound lineBound = (LineBound) node.getData();
        return lineBound.getBoundEnum() == BoundEnum.CLOSE;
      }
      return false;
    }
  };

  /**
   * @return
   */
  @Override
  protected TreeConstructor<Regexp> getTreeConstructor() {
    return treeConstructor;
  }

  // endregion

}
