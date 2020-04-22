package com.daniel.util.json;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 仿照 Json 节点操作集合以及集合元素, 包括节点的 替换/丢弃/提取.
 *
 * @author Daniel
 */
public class NodeOperator {
  
  // region ################################ 替换: substitute ################################
  
  /**
   * 替换满足谓词条件的节点值
   *
   * @param json
   * @param predicate 谓词条件
   * @param function  替换函数
   */
  public static void substitute(Map json, Predicate predicate, Function function) {
    for (Object parent : json.keySet()) {
      Object node = json.get(parent);
      // 若满足谓词条件, 执行 apply
      if (predicate.test(node)) {
        json.put(parent, function.apply(node));
      }
      // 若还存在嵌套结构, 继续递归
      else if (Map.class.isInstance(node)) {
        substitute((Map) node, predicate, function);
      } else if (List.class.isInstance(node)) {
        substitute((List) node, predicate, function);
      }
    }
  }
  
  /**
   * 替换满足谓词条件的节点值
   *
   * @param json
   * @param predicate 谓词条件
   * @param function  替换函数
   */
  public static void substitute(List json, Predicate predicate, Function function) {
    for (int ix = 0; ix < json.size(); ix++) {
      Object node = json.get(ix);
      // 若满足谓词条件, 执行 apply
      if (predicate.test(node)) {
        json.set(ix, function.apply(node));
      }
      // 若还存在嵌套结构, 继续递归
      else if (Map.class.isInstance(node)) {
        substitute((Map) node, predicate, function);
      } else if (List.class.isInstance(node)) {
        substitute((List) node, predicate, function);
      }
    }
  }
  
  /**
   * 替换满足谓词条件的节点键
   *
   * @param json
   * @param predicate 谓词条件
   * @param function  替换函数
   */
  public static void substituteKey(Map json, Predicate predicate, Function function) {
    for (Object parent : json.keySet().toArray()) {
      Object node = json.get(parent);
      // 若key满足谓词条件, 执行 apply
      if (predicate.test(parent)) {
        json.put(function.apply(parent), json.remove(parent));
      }
      // 不满足条件的 key 原样放回, 以保持顺序性
      else {
        json.put(parent, json.remove(parent));
      }
      // 若还存在嵌套结构, 继续递归
      if (Map.class.isInstance(node)) {
        substitute((Map) node, predicate, function);
      }
    }
  }
  
  /**
   * 替换满足谓词条件的节点键值对
   *
   * @param json
   * @param predicate 谓词条件
   * @param function  替换函数
   */
  public static void substituteEntry(Map json, Predicate<Entry> predicate, Function<Entry, Entry> function) {
    List<Entry> entryArr = new ArrayList<Entry>(json.entrySet());
    for (Entry entry : entryArr) {
      Object key = entry.getKey();
      Object value = entry.getValue();
      // 若key满足谓词条件, 执行 apply
      if (predicate.test(entry)) {
        Entry newEntry = function.apply(entry);
        json.remove(key);
        json.put(newEntry.getKey(), newEntry.getValue());
      }
      // 不满足条件的 key 原样放回, 以保持顺序性
      else {
        json.put(key, json.remove(key));
      }
      // 若还存在嵌套结构, 继续递归
      if (Map.class.isInstance(value)) {
        substitute((Map) value, predicate, function);
      }
    }
  }
  
  /**
   * 谓词条件为类别的替换
   *
   * @param <T>
   * @param <R>
   * @param json
   * @param clz
   * @param function
   */
  public static <T, R> void substitute(Map json, Class<T> clz, Function<T, R> function) {
    substitute(json, node -> clz.isInstance(node), function);
  }
  // endregion
  
  // region ################################ 丢弃: remove ################################
  
  /**
   * 丢弃满足谓词条件的节点值
   *
   * @param json
   * @param predicate 谓词条件
   */
  public static void remove(Map json, Predicate predicate) {
    for (Object parent : json.keySet().toArray()) {
      Object node = json.get(parent);
      // 若满足谓词条件, 执行 apply
      if (predicate.test(node)) {
        json.remove(parent);
      }
      // 若还存在嵌套结构, 继续递归
      else if (Map.class.isInstance(node)) {
        remove((Map) node, predicate);
      } else if (List.class.isInstance(node)) {
        remove((List) node, predicate);
      }
    }
  }
  
  /**
   * 丢弃满足谓词条件的节点值
   *
   * @param json
   * @param predicate 谓词条件
   */
  public static void remove(List json, Predicate predicate) {
    for (int ix = 0; ix < json.size(); ix++) {
      Object node = json.get(ix);
      // 若满足谓词条件, 执行 apply
      if (predicate.test(node)) {
        json.remove(ix--);
      }
      // 若还存在嵌套结构, 继续递归
      else if (Map.class.isInstance(node)) {
        remove((Map) node, predicate);
      } else if (List.class.isInstance(node)) {
        remove((List) node, predicate);
      }
    }
  }
  // endregion
  
  // region ################################ 提取: extract ################################
  
  /**
   * 递归子函数: 提取满足谓词条件的节点值
   *
   * @param json
   * @param predicate 谓词条件
   * @param function  提取函数
   */
  private static List extract(List extarcts, Map json, Predicate predicate, Function function) {
    for (Object parent : json.keySet()) {
      Object node = json.get(parent);
      // 若满足谓词条件, 执行 apply
      if (predicate.test(node)) {
        extarcts.add(function.apply(node));
      }
      // 若还存在嵌套结构, 继续递归
      else if (Map.class.isInstance(node)) {
        extarcts.addAll(extract(new ArrayList(), (Map) node, predicate, function));
      } else if (List.class.isInstance(node)) {
        extarcts.addAll(extract(new ArrayList(), (List) node, predicate, function));
      }
    }
    return extarcts;
  }
  
  /**
   * 递归子函数: 提取满足谓词条件的节点值
   *
   * @param json
   * @param predicate 谓词条件
   * @param function  提取函数
   */
  private static List extract(List extarcts, List json, Predicate predicate, Function function) {
    for (int ix = 0; ix < json.size(); ix++) {
      Object node = json.get(ix);
      // 若满足谓词条件, 执行 apply
      if (predicate.test(node)) {
        extarcts.add(function.apply(node));
      }
      // 若还存在嵌套结构, 继续递归
      else if (Map.class.isInstance(node)) {
        extarcts.addAll(extract(new ArrayList(), (Map) node, predicate, function));
      } else if (List.class.isInstance(node)) {
        extarcts.addAll(extract(new ArrayList(), (List) node, predicate, function));
      }
    }
    return extarcts;
  }
  
  /**
   * 提取满足谓词条件的节点值
   *
   * @param json
   * @param predicate 谓词条件
   * @param function  提取函数
   */
  public static List extract(Map json, Predicate predicate, Function function) {
    return extract(new ArrayList(), json, predicate, function);
  }
  
  /**
   * 提取满足谓词条件的节点值
   *
   * @param json
   * @param predicate 谓词条件
   * @param function  提取函数
   */
  public static List extract(List json, Predicate predicate, Function function) {
    return extract(new ArrayList(), json, predicate, function);
  }
  
  /**
   * 提取满足谓词条件的节点值
   *
   * @param <T>      类型
   * @param <R>      提取出类型
   * @param json
   * @param clz      谓词类型
   * @param function 提取函数
   * @return
   */
  public static <T> List extract(Map json, Class<T> clz, Function<T, ?> function) {
    return extract(new ArrayList(), json, node -> clz.isInstance(node), function);
  }
  
  /**
   * 提取满足谓词条件的节点值
   *
   * @param <T>      类型
   * @param <R>      提取出类型
   * @param json
   * @param clz      谓词类型
   * @param function 提取函数
   * @return
   */
  public static <T> List extract(List json, Class<T> clz, Function<T, ?> function) {
    return extract(new ArrayList(), json, node -> clz.isInstance(node), function);
  }
  // [end]
  
  // [start]* ################################ 谓词条件 ################################ */
  
  private <N> Predicate generics(Predicate<N> predicate, N node, Class... rest_clz) {
    List<Class> types = Arrays.asList(rest_clz);
    
    return predicate;
  }
  
  // endregion
  
  // region ################################ 常用方法 ################################
  
  /**
   * 映射键替换操作
   *
   * @param source
   * @param cite
   */
  public static <K, V> void alterKey(Map<K, V> source, Map<K, K> cite) {
    substituteKey(source, node -> cite.containsKey(node), node -> cite.get(node));
  }
  
  /**
   * 映射值替换操作: 按 cites: k->v 映射关系替换值
   *
   * @param <T>
   * @param source
   * @param cite
   */
  public static <K, V1, V2> void alterVal(Map<K, V1> source, Map<V1, V2> cite) {
    substitute(source, node -> cite.containsKey(node), node -> cite.get(node));
  }
  
  /**
   * 带键值约束的映射值替换操作: 按 cites: k1->f(k2->v) 的 k1 依次搜索 source 匹配的 key, 按照 k2->v 映射关系替换值
   *
   * @param source
   * @param cites
   * @return
   */
  public static <K, V1, V2> void alterValWithKeyRestrict(Map<K, V1> source, Map<K, Map<V1, V2>> cites) {
    substituteEntry(source, (Entry entry) -> {
      final Object key = entry.getKey();
      final Object value = entry.getValue();
      return cites.containsKey(key) && cites.get(key).containsKey(value);
    }, (Entry entry) -> {
      final Object key = entry.getKey();
      final Object value = entry.getValue();
      final Map map = new HashMap();
      map.put(key, cites.get(key).get(value));
      return (Entry) map.entrySet().iterator().next();
    });
  }
  
  // endregion
  
}
