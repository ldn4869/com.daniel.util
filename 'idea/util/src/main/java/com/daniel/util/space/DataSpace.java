package com.daniel.util.space;

import com.daniel.util.json.PojoUtil;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 数据空间集操作类
 *
 * @author Daniel
 */
public class DataSpace {

  // region ################################ 创建数据空间集 ################################

  /**
   * 递归子函数: 创建构建空间集的基底向量组
   *
   * @param <T>
   * @param <R>
   * @param current_basisGroup 基底向量组
   * @param clz
   * @param rest_bases         剩余基底向量
   * @return
   */
  private static <T, R> Collector<T, ?, ?> basisGroup(Collector<T, ?, ?> current_basisGroup, Class<T> clz,
                                                      Function<? super T, ? extends R>... rest_bases) {
    // 递归终点
    if (rest_bases.length == 0) {
      return current_basisGroup;
    }
    Function<? super T, ? extends R> basis = rest_bases[rest_bases.length - 1];
    // 若现存基底向量组为空, 为递归起点
    Collector<T, ?, ?> basisGroup = (current_basisGroup == null) ? Collectors.groupingBy(basis)
        : Collectors.groupingBy(basis, current_basisGroup);
    List<Function<? super T, ? extends R>> current_rest_bases = Arrays.asList(rest_bases).subList(0, rest_bases.length - 1);
    Function<? super T, ? extends R>[] restBasisArr = current_rest_bases.toArray(new Function[current_rest_bases.size()]);
    return basisGroup(basisGroup, clz, restBasisArr);
  }

  /**
   * 创建构建空间集的基底向量组
   *
   * @param <T>   数据空间集类型
   * @param clz   类性
   * @param bases 基底向量
   * @return
   */
  @SuppressWarnings("rawtypes")
  private static <T> Collector basisGroup(Class<T> clz, Function<? super T, ?>... bases) {
    // 提供的基底向量不为空
    if (bases.length == 0) {
      throw new RuntimeException(clz.toString() + " 基底字段不可为空");
    }
    return basisGroup(null, clz, bases);
  }

  /**
   * 向量化 pojos, 将其映射到向量空间
   *
   * @param <T>
   * @param pojos
   * @param bases 基底向量
   * @return
   */
  public static <T> Map vectorify(List<T> pojos, Function<? super T, ?>... bases) {
    if (pojos.size() == 0) {
      return new HashMap();
    }
    Class<T> pojoClz = (Class<T>) pojos.get(0).getClass();
    Collector<T, ?, Map> dimCollectors = basisGroup(pojoClz, bases);
    return pojos.stream().collect(dimCollectors);
  }

  /**
   * 向量化 pojos, 将其映射到向量空间
   *
   * @param <T>
   * @param pojos
   * @param basisFields 基底向量字段名
   * @return
   */
  public static <T> Map vectorify(List<T> pojos, List<String> basisFields) {
    // 空数组检验
    if (pojos.size() == 0) {
      return new HashMap();
    }
    Class<T> pojoClz = (Class<T>) pojos.get(0).getClass();
    Map<String, PropertyDescriptor> basisPropMap = PojoUtil.getPropMap(pojoClz);
    Function<? super T, ?>[] bases = new Function[basisFields.size()];
    for (int ix = 0; ix < basisFields.size(); ix++) {
      PropertyDescriptor dimProp = basisPropMap.get(basisFields.get(ix));
      bases[ix] = pojo -> PojoUtil.getter(pojo, dimProp);
    }
    return vectorify(pojos, bases);
  }

  /**
   * 向量化 pojos, 将其映射到向量空间
   *
   * @param <T>
   * @param pojos
   * @param basisFields 基底向量字段名
   * @return
   */
  public static <T> Map vectorify(List<T> pojos, String... basisFields) {
    return vectorify(pojos, Arrays.asList(basisFields));
  }

  // endregion

  // region ################################ 数据空间集降维 ################################


  // endregion


}
