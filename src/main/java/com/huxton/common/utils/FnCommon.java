package com.huxton.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huxton.common.exceptions.ExceptionOm;
import jakarta.persistence.Tuple;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FnCommon {
  public static boolean emailValidate(String email) {
    return true;
  }

  public static void copyProperties(Object target, Object source) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    //    BeanUtils.copyProperties(source, target);
  }

  public static void copyAllProperties(Object target, Object source) {
    BeanUtils.copyProperties(source, target);
  }

  public static void coppyNonNullProperties(Object target, Object source) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
  }

  public static List<?> listOrEmptyList(List<?> list) {
    return list == null ? new ArrayList<>() : list;
  }

  public static List<?> convertToEntity(List<Tuple> input, Class<?> dtoClass) {
    List<Object> arrayList = new ArrayList();
    input.stream()
        .forEach(
            (tuple) -> {
              Map<String, Object> temp = new HashMap();
              tuple.getElements().stream()
                  .forEach(
                      (tupleElement) -> {
                        Object value = tuple.get(tupleElement.getAlias());
                        temp.put(tupleElement.getAlias().toLowerCase(), value);
                      });
              ObjectMapper map = new ObjectMapper();
              map.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
              map.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

              try {
                String mapToString = map.writeValueAsString(temp);
                arrayList.add(map.readValue(mapToString, dtoClass));
              } catch (JsonProcessingException var6) {
                throw new RuntimeException(var6.getMessage());
              }
            });
    return arrayList;
  }

  public static <T> List<Map<String, Object>> tupleToListMap(
      List<Tuple> input, Map<String, Class<T>> jsonColumns) {
    List<Map<String, Object>> arrayList = new ArrayList();
    input.stream()
        .forEach(
            (tuple) -> {
              Map<String, Object> temp = new HashMap();
              tuple.getElements().stream()
                  .forEach(
                      (tupleElement) -> {
                        Object value = tuple.get(tupleElement.getAlias());
                        if (jsonColumns.containsKey(tupleElement.getAlias().toLowerCase())) {
                          value =
                              JsonParser.entity(
                                  (String) value,
                                  jsonColumns.get(tupleElement.getAlias().toLowerCase()));
                        }
                        temp.put(tupleElement.getAlias().toLowerCase(), value);
                      });
              arrayList.add(temp);
            });
    return arrayList;
  }

  public static <T> T copyProperties(Class<T> clazz, Object source) {
    try {
      Constructor<?> targetIntance = clazz.getDeclaredConstructor();
      targetIntance.setAccessible(true);

      T target = (T) targetIntance.newInstance();
      BeanUtils.copyProperties(source, target, getNullPropertyNames(source));

      return target;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static <T> T copyNonNullProperties(Class<T> clazz, Object source) {
    try {
      Constructor<?> targetIntance = clazz.getDeclaredConstructor();
      targetIntance.setAccessible(true);

      T target = (T) targetIntance.newInstance();
      BeanUtils.copyProperties(source, target, getNullPropertyNames(source));

      return target;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void copyNonNullProperties(Object target, Object source) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
  }

  public static String[] getNullPropertyNames(Object source) {
    BeanWrapper wrappedSource = new BeanWrapperImpl(source);
    return Stream.of(wrappedSource.getPropertyDescriptors())
        .map(FeatureDescriptor::getName)
        .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
        .toArray(String[]::new);
  }

  public static <T> List<T> copyListProperties(Class<T> target, List<?> source) {
    List<T> targetList = new ArrayList<>();

    try {
      Constructor<T> targetConstructor = target.getDeclaredConstructor();
      targetConstructor.setAccessible(true);

      for (Object sou : source) {
        T tar = targetConstructor.newInstance();
        BeanUtils.copyProperties(sou, tar);
        targetList.add(tar);
      }

      return targetList;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static <T> boolean isEmpty(List<T> list) {
    return list == null || list.size() == 0;
  }

  public static boolean checkBlankString(String str) {
    return str == null || str.isEmpty();
  }

  public static Map<String, Object> parameters(Object obj) {
    Map<String, Object> map = new HashMap<>();
    for (Field field : obj.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        map.put(field.getName(), field.get(obj));
      } catch (Exception e) {
      }
    }
    return map;
  }

  public static <V, K> Map<K, List<V>> convertListToMap(List<V> list, Function<V, K> keyExtractor) {
    Map<K, List<V>> result = new HashMap<>();
    for (V v : list) {
      K key = keyExtractor.apply(v);
      result.computeIfAbsent(key, k -> new ArrayList<>()).add(v);
    }
    return result;
  }

  public static String randomCode(String prefix, int randomLen) {
    Random random = new Random();
    return prefix
        + random
            .ints(97, 123)
            .limit(randomLen)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString()
            .toUpperCase();
  }

  public static String randomNumberCode(String prefix, int leng) {
    String minStr = "1000000000000000000";
    String maxStr = "9223372036854775807";
    long min = Long.parseLong(minStr.substring(0, leng));
    long max = Long.parseLong(maxStr.substring(0, leng));
    return prefix + (long) ((Math.random() * (max - min)) + min);
  }

  public static String gencode(String prefix, int lenght, long order) {
    String codeDefault = "0000000000000000000" + order;
    if (prefix == null) {
      prefix = "";
    }
    return prefix + codeDefault.substring(codeDefault.length() - lenght + prefix.length());
  }

  public static <T extends Enum<T>> T getEnumValueFromString(Class<T> enumType, String name) {
    Enum[] arr$ = (Enum[]) enumType.getEnumConstants();
    int len$ = arr$.length;

    for (int i$ = 0; i$ < len$; ++i$) {
      T constant = (T) arr$[i$];
      if (constant.name().compareToIgnoreCase(name) == 0) {
        return constant;
      }
    }

    throw new ExceptionOm(HttpStatus.BAD_REQUEST, "Invalid state");
  }

  /** ids = 1,2,3,4 */
  public static List<Long> stringToListLong(String ids) {
    if (!StringUtils.hasLength(ids)) {
      return new ArrayList<>();
    }
    try {
      return Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
    } catch (Exception e) {
      throw new ExceptionOm(HttpStatus.BAD_REQUEST, "param ids invalid");
    }
  }

  public static Set<Long> toLongSet(String ids) {
    if (!StringUtils.hasLength(ids)) {
      return new HashSet<>();
    }
    try {
      return Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toSet());
    } catch (Exception e) {
      throw new ExceptionOm(HttpStatus.BAD_REQUEST, "param ids invalid");
    }
  }

  public static List<Long> stringToListLongOrNull(String ids) {
    if (!StringUtils.hasLength(ids)) {
      return null;
    }
    try {
      return Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
    } catch (Exception e) {
      throw new ExceptionOm(HttpStatus.BAD_REQUEST, "param ids invalid");
    }
  }

  public static <T> boolean listHasValue(List<T> list) {
    return list != null && !list.isEmpty();
  }

  public static <T> boolean setHasValue(Set<T> set) {
    return set != null && !set.isEmpty();
  }

  /**
   * separatedBy là cách nhau bởi ký tự gì, Với {"address" : {"province:"Hanoi"}} =>
   * {"address.province": "Nanoi"} nếu separatedBy là "."
   *
   * @param object
   * @param separatedBy
   * @return
   */
  public static Map<String, Object> flattenObject(Object object, String separatedBy) {
    if (isPrimitive(object) || object instanceof Collection) {
      throw new RuntimeException("Not accept primitive value or list primitive");
    }
    Map<String, Object> input;
    if (!(object instanceof Map)) {
      input = JsonParser.objectToMap(object);
    } else {
      input = (Map<String, Object>) object;
    }
    Map<String, Object> result = new HashMap<>();
    flattenObjectHelper(result, "", input, separatedBy, null);
    return result;
  }

  /**
   * separatedBy là cách nhau bởi ký tự gì, Với {"address" : {"province:"Hanoi"}} =>
   * {"address.province": "Nanoi"} nếu separatedBy là "."
   *
   * @param object
   * @param separatedBy
   * @return
   */
  public static Map<String, Object> flattenObjectAll(Object object, String separatedBy) {
    if (isPrimitive(object) || object instanceof Collection) {
      throw new RuntimeException("Not accept primitive value or list primitive");
    }
    Map<String, Object> input;
    if (!(object instanceof Map)) {
      input = JsonParser.objectToMap(object);
    } else {
      input = (Map<String, Object>) object;
    }
    Map<String, Object> result = new HashMap<>();
    flattenAllHelper(result, "", input, separatedBy, null);
    return result;
  }

  /**
   * separatedBy là cách nhau bởi ký tự gì, Với {"address" : {"province:"Hanoi"}} =>
   * {"address.province": "Nanoi"} nếu separatedBy là ".", keepKeys là chỉ giữ những key này
   *
   * @param input
   * @param separatedBy
   * @return
   */
  public static Map<String, Object> flattenObject(
      Map<String, Object> input, String separatedBy, List<String> keepKeys) {
    Map<String, Object> result = new HashMap<>();
    flattenObjectHelper(result, "", input, separatedBy, keepKeys);
    return result;
  }

  /**
   * Làm phẳng object
   *
   * @param result
   * @param currentKey
   * @param input
   */
  private static void flattenObjectHelper(
      Map<String, Object> result,
      String currentKey,
      Map<String, Object> input,
      String separatedBy,
      List<String> keepKeys) {
    if (input == null) {
      return;
    }
    for (Map.Entry<String, Object> entry : input.entrySet()) {
      String newKey =
          currentKey.isEmpty() ? entry.getKey() : currentKey + separatedBy + entry.getKey();
      newKey = convertCamelToSnakeCase(newKey);

      if (isPrimitive(entry.getValue()) || entry.getValue() instanceof String) {
        if (keepKeys == null || keepKeys.isEmpty()) {
          result.put(newKey, entry.getValue());
        } else if (keepKeys.contains(newKey)) {
          result.put(newKey, entry.getValue());
        }
      } else if (entry.getValue() instanceof Map) {
        flattenObjectHelper(
            result, newKey, (Map<String, Object>) entry.getValue(), separatedBy, keepKeys);
      } else if (!(entry.getValue() instanceof Collection)) {
        flattenObjectHelper(
            result, newKey, JsonParser.objectToMap(entry.getValue()), separatedBy, keepKeys);
      } else if (entry.getValue() instanceof Collection) {
        List<Object> list = (List<Object>) entry.getValue();
        if (list.stream().allMatch(FnCommon::isPrimitive)) {
          result.put(newKey, list);
          continue;
        }
        List<Map> listFlatten = new ArrayList<>();
        for (Object o : list) {
          Map<String, Object> result1 = new HashMap<>();
          flattenAllHelper(result1, "", JsonParser.objectToMap(o), separatedBy, keepKeys);
          listFlatten.add(result1);
        }
        result.put(newKey, listFlatten);
      }
    }
  }

  /**
   * Làm phẳng object
   *
   * @param result
   * @param currentKey
   * @param input
   */
  private static void flattenAllHelper(
      Map<String, Object> result,
      String currentKey,
      Map<String, Object> input,
      String separatedBy,
      List<String> keepKeys) {
    if (input == null) {
      return;
    }
    for (Map.Entry<String, Object> entry : input.entrySet()) {
      String newKey =
          currentKey.isEmpty() ? entry.getKey() : currentKey + separatedBy + entry.getKey();
      newKey = convertCamelToSnakeCase(newKey);

      if (isPrimitive(entry.getValue()) || entry.getValue() instanceof String) {
        if (keepKeys == null || keepKeys.isEmpty()) {
          result.put(newKey, entry.getValue());
        } else if (keepKeys.contains(newKey)) {
          result.put(newKey, entry.getValue());
        }
      } else if (entry.getValue() instanceof Map) {
        flattenAllHelper(
            result, newKey, (Map<String, Object>) entry.getValue(), separatedBy, keepKeys);
      } else if (!(entry.getValue() instanceof Collection)) {
        flattenAllHelper(
            result, newKey, JsonParser.objectToMap(entry.getValue()), separatedBy, keepKeys);
      } else if (entry.getValue() instanceof Collection) {
        List<Object> list = (List<Object>) entry.getValue();
        int i = 0;
        for (Object o : list) {
          if (list.stream().allMatch(FnCommon::isPrimitive)) {
            result.put(newKey + separatedBy + i, list.get(i));
          }else {
            flattenAllHelper(
                    result, newKey + separatedBy + i, JsonParser.objectToMap(o), separatedBy, keepKeys);
          }
          i++;
        }
      }
    }
  }

  private static boolean isPrimitive(Object obj) {
    return obj instanceof Integer
        || obj instanceof Byte
        || obj instanceof Short
        || obj instanceof Long
        || obj instanceof Float
        || obj instanceof String
        || obj instanceof Double
        || obj instanceof Character
        || obj instanceof Boolean;
  }

  public static String convertCamelToSnakeCase(String camelCase) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < camelCase.length(); i++) {
      char currentChar = camelCase.charAt(i);
      if (Character.isUpperCase(currentChar)) {
        if (i > 0) {
          result.append("_");
        }
        result.append(Character.toLowerCase(currentChar));
      } else {
        result.append(currentChar);
      }
    }
    return result.toString();
  }

  private static Field[] getAllFields(Class<?> clazz) {
    Field[] fields = clazz.getDeclaredFields();

    if (clazz.getSuperclass() != null) {
      Field[] superFields = getAllFields(clazz.getSuperclass());
      Field[] combinedFields = new Field[fields.length + superFields.length];
      System.arraycopy(fields, 0, combinedFields, 0, fields.length);
      System.arraycopy(superFields, 0, combinedFields, fields.length, superFields.length);
      return combinedFields;
    }

    return fields;
  }

  private static Field getFieldFromClassHierarchy(Class<?> clazz, String fieldName)
      throws NoSuchFieldException {
    Class<?> currentClass = clazz;
    while (currentClass != null) {
      try {
        return currentClass.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        currentClass = currentClass.getSuperclass();
      }
    }
    throw new NoSuchFieldException(
        "Không tìm thấy field " + fieldName + " trong class hoặc superclass.");
  }
}
