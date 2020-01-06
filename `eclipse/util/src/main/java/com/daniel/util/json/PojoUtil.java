package com.daniel.util.json;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**对 Pojo 对象的字段进行 解析/切割/构建/映射/转换
 * @author Daniel
 *
 */
public class PojoUtil {

  // [start]* ################################ 解析: 基础方法 ################################ */

  /**获取字段getter/setter方法
   * @param pojo
   * @param fieldName
   * @return
   */
  public static PropertyDescriptor getProp(Class<?> pojoClz, String fieldName) {
    // 获取字段getter/setter方法
    PropertyDescriptor fieldProp = null;
    try {
      fieldProp = new PropertyDescriptor(fieldName, pojoClz);
    } catch (IntrospectionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return fieldProp;
  }
  // * ################################ getProps ################################ */
  
  /**获取 props 方法, 滤去相应修饰符字段
   * @param pojoClz
   * @param modifier
   * @return
   */
  private static List<PropertyDescriptor> getProps(Class<?> pojoClz, int modifier) {
    // 字段全集
    pojoClz.getDeclaredFields();
    Field[] fields = pojoClz.getDeclaredFields();
    List<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();
    for (Field field : fields) {
      // 若符合修饰符过滤, 跳过字段
      if ((field.getModifiers() & modifier) != 0) {
        continue;
      }
      // 获取字段getter/setter方法
      PropertyDescriptor fieldProp = getProp(pojoClz, field.getName());
      props.add(fieldProp);
    }
    return props;
  }
  
  /**获取 props 方法, 滤去 STATIC | FINAL 字段
   * @param pojoClz
   * @return
   */
  public static List<PropertyDescriptor> getProps(Class<?> pojoClz) {
    return getProps(pojoClz, Modifier.STATIC | Modifier.FINAL);
  }
  
  /**获取 props 方法, 由过滤字段获得
   * @param pojoClz
   * @param filters
   * @return
   */
  public static List<PropertyDescriptor> getProps(Class<?> pojoClz, Collection<String> filters) {
    // 字段全集
    List<PropertyDescriptor> props = new ArrayList<PropertyDescriptor>();
    for (String filter : filters) {
      // 获取字段getter/setter方法
      props.add(getProp(pojoClz, filter));
    }
    return props;
  }
  
  /**获取 props 方法
   * @param filters
   * @param pojoClz
   * @return
   */
  public static List<PropertyDescriptor> getProps(Class<?> pojoClz, String... filters) {
    return getProps(pojoClz, Arrays.asList(filters));
  }
  
  // * ################################ getPropMap ################################ */
  
  /**获取 field -> prop 方法, 滤去相应类型字段
   * @param pojoClz
   * @param modifier
   * @return
   */
  private static Map<String, PropertyDescriptor> getPropMap(Class<?> pojoClz, int modifier) {
    // 字段全集
    pojoClz.getDeclaredFields();
    Field[] fields = pojoClz.getDeclaredFields();
    Map<String, PropertyDescriptor> fieldProps = new LinkedHashMap<String, PropertyDescriptor>();
    for (Field field : fields) {
      // 若符合修饰符过滤, 跳过字段
      if ((field.getModifiers() & modifier) != 0) {
        continue;
      }
      // 获取字段getter/setter方法
      PropertyDescriptor fieldProp = getProp(pojoClz, field.getName());
      fieldProps.put(field.getName(), fieldProp);
    }
    return fieldProps;
  }
  
  /**获取 field -> prop 方法, 滤去 STATIC | FINAL 字段
   * @param fieldName
   * @param pojoClz
   * @return
   */
  public static Map<String, PropertyDescriptor> getPropMap(Class<?> pojoClz) {
    return getPropMap(pojoClz, Modifier.STATIC | Modifier.FINAL);
  }

  /**获取 filters: field -> prop 方法
   * @param pojoClz
   * @param filters
   * @return
   */
  public static Map<String, PropertyDescriptor> getPropMap(Class<?> pojoClz, Collection<String> filters) {
    // 字段全集
    Map<String, PropertyDescriptor> props = new LinkedHashMap<String, PropertyDescriptor>();
    for (String filter : filters) {
      // 获取字段getter/setter方法
      props.put(filter, getProp(pojoClz, filter));
    }
    return props;
  }

  // * ################################ getter/setter ################################ */
  
  public static <T> T getter(Object pojo, PropertyDescriptor prop) {
    try {
      return (T) prop.getReadMethod().invoke(pojo);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  public static <T> T getter(Object pojo, String fieldName) {
    return getter(pojo, getProp(pojo.getClass(), fieldName));
  }

  public static void setter(Object pojo, PropertyDescriptor prop, Object val) {
    try {
      prop.getWriteMethod().invoke(pojo, val);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void setter(Object pojo, String fieldName, Object val) {
    setter(pojo, getProp(pojo.getClass(), fieldName), val);
  }
  
  // [end]

  // [start]* ################################ 切割: 按列选取 ################################ */

  public static <R> List<R> getFields(List<?> pojos, String fieldName) {
    // 空数组
    if (pojos.size() == 0)
      return new ArrayList<R>();
    // 获取字段getter/setter方法
    Class<? extends Object> pojoClz = pojos.get(0).getClass();
    PropertyDescriptor prop = getProp(pojoClz, fieldName);
    // 获取字段值列表
    List<R> fields = new ArrayList<R>();
    for (Object pojo : pojos) {
      fields.add(getter(pojo, prop));
    }
    return fields;
  }

  public static <T, R> List<R> getFields(List<T> pojos, Function<? super T, R> getter) {
    List<R> fields = pojos.stream().map(getter).collect(Collectors.toList());
    return fields;
  }

  public static <R, T> Map<R, T> getFieldMap(List<T> pojos, String fieldName) {
    // 空数组
    if (pojos.size() == 0)
      return new HashMap<R, T>();
    // 获取字段getter/setter方法
    Class<? extends Object> pojoClz = pojos.get(0).getClass();
    PropertyDescriptor prop = getProp(pojoClz, fieldName);
    // 获取字段值列表
    Map<R, T> fields = new HashMap<R, T>();
    for (T pojo : pojos) {
      fields.put(getter(pojo, prop), pojo);
    }
    return fields;
  }

  // [end]

  // [start]* ################################ 构建/映射: 对象与 Map 的转换 ################################ */

  /**解析 map 到对象 (提供 props 以提高效率)
   * @param <T>
   * @param map
   * @param props
   * @param pojoClz
   * @return
   */
  private static <T> T parse(Map<String, ?> map, Map<String, PropertyDescriptor> props, Class<T> pojoClz) {
    T pojo = null;
    try {
      pojo = (T) pojoClz.getConstructor().newInstance();
    } catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException
        | InvocationTargetException | NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    for (Iterator<String> iterator = props.keySet().iterator(); iterator.hasNext();) {
      String fieldName = iterator.next();
      setter(pojo, props.get(fieldName), map.get(fieldName));
    }
    return (T) pojo;
  }

  /**解析 map 到对象
   * @param <T>
   * @param map
   * @param pojoClz
   * @return
   */
  public static <T> T parse(Map<String, ?> map, Class<T> pojoClz) {
    T pojo = null;
    try {
      pojo = pojoClz.getConstructor().newInstance();
    } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException
        | InstantiationException | IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    for (String fieldName : map.keySet()) {
      setter(pojo, fieldName, map.get(fieldName));
    }
    return pojo;
  }

  /**解析 map 到对象 (提供 props 以提高效率)
   * @param <T>
   * @param map
   * @param props
   * @param pojoClz
   * @return
   */
  private static <T> T parse(Map<String, ?> map, List<PropertyDescriptor> props, Class<T> pojoClz) {
    T pojo = null;
    try {
      pojo = (T) pojoClz.getConstructor().newInstance();
    } catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException
        | InvocationTargetException | NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    for (PropertyDescriptor prop : props) {
      String fieldName = prop.getName();
      setter(pojo, prop, map.get(fieldName));
    }
    return (T) pojo;
  }

  /**将以 List.Map 形式存储的 pojos 转换为对象清单
   * @deprecated 效率不如 parses(List<Map<String, V>> maps, Class<T> pojoClz)
   * @param <T>
   * @param <V>
   * @param maps
   * @param pojoClz
   * @return
   */
  @SuppressWarnings("unused")
  private static <T, V> List<T> parsesDeprecated(List<Map<String, V>> maps, Class<T> pojoClz) {
    Map<String, PropertyDescriptor> props = getPropMap(pojoClz);
    List<T> pojos = new ArrayList<T>();
    for (Map<String, V> map : maps) {
      pojos.add(parse(map, props, pojoClz));
    }
    return pojos;
  }

  /**将以 List.Map 形式存储的 pojos 转换为对象清单
   * @param <T>
   * @param <V>
   * @param maps
   * @param pojoClz
   * @return
   */
  public static <T, V> List<T> parses(List<Map<String, V>> maps, Class<T> pojoClz) {
    List<PropertyDescriptor> props = getProps(pojoClz);
    List<T> pojos = new ArrayList<T>();
    for (Map<String, V> map : maps) {
      pojos.add(parse(map, props, pojoClz));
    }
    return pojos;
  }

  /**映射对象 到 map (提供 props 以提高效率)
   * @param <T>
   * @param pojo
   * @param props
   * @return
   */
  private static <T> Map<String, Object> mapify(T pojo, List<PropertyDescriptor> props) {
    Map<String, Object> map = new LinkedHashMap<String, Object>();
    for (PropertyDescriptor prop : props) {
      String fieldName = prop.getName();
      Object value = getter(pojo, prop);
      map.put(fieldName, value);
    }
    return map;
  }

  /**映射对象 到 map
   * @param <T>
   * @param pojo
   * @param props
   * @return
   */
  public static <T> Map<String, Object> mapify(T pojo) {
    List<PropertyDescriptor> props = getProps(pojo.getClass());
    return mapify(pojo, props);
  }

  public static <T> List<Map<String, Object>> mapifys(List<T> pojos) {
    List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
    // 空数组检验
    if(pojos.size()==0) {
      return maps;
    }
    List<PropertyDescriptor> props = getProps(pojos.get(0).getClass());
    for (T pojo : pojos) {
      maps.add(mapify(pojo, props));
    }
    return maps;
  }

  // [end]

  // [start]* ################################ 转换: 不同对象间的字段复制 ################################ */

  /**將 source 中的字段复制到 target, 提供属性字段
   * @param source
   * @param target
   * @param srcProps
   * @param tarProps
   * @param filters
   * @param withNull
   */
  private static void copy(Object source, Object target, Map<String, PropertyDescriptor> srcProps,
      Map<String, PropertyDescriptor> tarProps, Collection<String> filters, Boolean withNull) {
    for (String field : filters) {
      // 若过滤字段属于来源字段, 拷贝
      if (srcProps.containsKey(field)) {
        Object srcVal = getter(source, srcProps.get(field));
        // 若为空拷贝模式, 直接setter, 否则判断来源字段值是否为空再拷贝
        if (withNull || srcVal != null) {
          setter(target, tarProps.get(field), srcVal);
        }
      }
    }
  }

  /**將 source 中的所有字段复制到 target, 提供属性字段
   * @param source
   * @param target
   * @param srcProps
   * @param tarProps
   * @param withNull
   */
  private static void copy(Object source, Object target, Map<String, PropertyDescriptor> srcProps,
      Map<String, PropertyDescriptor> tarProps, Boolean withNull) {
    for (Iterator<String> iterator = srcProps.keySet().iterator(); iterator.hasNext();) {
      String field = iterator.next();
      Object srcVal = getter(source, srcProps.get(field));
      // 若为空拷贝模式, 直接setter, 否则判断来源字段值是否为空再拷贝
      if (withNull || srcVal != null) {
        setter(target, tarProps.get(field), srcVal);
      }
    }
  }

  /**將 source 中的非空字段复制到 target
   * @param source
   * @param target
   */
  private static void copy(Object source, Object target, Collection<String> filters, Boolean withNull) {
    // 获取字段方法
    Map<String, PropertyDescriptor> srcProps = getPropMap(source.getClass());
    Map<String, PropertyDescriptor> tarProps = getPropMap(target.getClass());
    copy(source, target, srcProps, tarProps, filters, withNull);
  }

  /**將 source 中的非空字段复制到 target
   * @param source
   * @param target
   */
  private static void copy(Object source, Object target, Boolean withNull) {
    // 获取字段方法
    Map<String, PropertyDescriptor> srcProps = getPropMap(source.getClass());
    Map<String, PropertyDescriptor> tarProps = getPropMap(target.getClass());
    copy(source, target, srcProps, tarProps, withNull);
  }

  /**將 source 中的非空字段复制到 target
   * @param source
   * @param target
   */
  public static void copy(Object source, Object target) {
    copy(source, target, false);
  }

  /**將 source 中的非空字段复制到 target, 空复制模式
   * @param source
   * @param target
   */
  public static void copyWithNull(Object source, Object target) {
    copy(source, target, true);
  }

  /**依据锚点字段在 cites 中搜索与 sources 锚点值相同的 cite 记录, 
   * 将该搜索到的记录的其他字段数据复制到对应的 source 中,
   * filters 有值则只复制 filter 中限定的字段
   * @param <S>
   * @param <C>
   * @param anchorKey
   * @param sources
   * @param cites
   * @param filters
   * @return
   */
  public static <S, C> List<S> searchByAnchor(String anchorKey, List<S> sources, List<C> cites,
      Collection<String> filters) {
    // 检查空数组
    if (sources.size() == 0 || cites.size() == 0) {
      return sources;
    }
    // 对象相关
    Map<String, PropertyDescriptor> srcProps = getPropMap(sources.get(0).getClass());
    Map<String, PropertyDescriptor> citeProps = getPropMap(cites.get(0).getClass());
    PropertyDescriptor srcAnchorProp = srcProps.get(anchorKey);
    Map<?, C> anchor2Cite = getFieldMap(cites, anchorKey);
    for (int ix = 0; ix < sources.size(); ix++) {
      S src = sources.get(ix);
      // 查询来源对象锚定值, 查表
      Object anchor = getter(src, srcAnchorProp);
      // 若表中不存在锚定值, 跳过该来源对象
      if (!anchor2Cite.containsKey(anchor)) {
        continue;
      }
      C anchorCite = anchor2Cite.get(anchor);
      // 依据锚定值拷贝, 按字段过滤标签拷贝
      copy(anchorCite, src, citeProps, srcProps, filters, true);
      sources.set(ix, src);
    }
    return sources;
  }

  // [end]

  /*
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   *  
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   */

}
