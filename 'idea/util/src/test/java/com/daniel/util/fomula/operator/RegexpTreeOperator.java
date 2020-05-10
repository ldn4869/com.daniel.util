package com.daniel.util.fomula.operator;

import com.daniel.util.fomula.entity.BoundEnum;
import com.daniel.util.fomula.entity.CaptureBound;
import com.daniel.util.fomula.entity.LineBound;
import com.daniel.util.fomula.entity.Regexp;
import com.daniel.util.fomula.entity.RegexpEnum;
import com.daniel.util.tree.Node;
import com.daniel.util.tree.TreeConstructor;
import com.daniel.util.tree.TreeOperator;

import java.util.List;

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


  public Node<Regexp> gather(List<Node<Regexp>> traversal) {
    Node<List<Node<Regexp>>> hierachy = hierachy(traversal);
    return null;
  }

}
