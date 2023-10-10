package assignsShifts.utils;

import java.lang.reflect.ParameterizedType;

public class TypeUtil {
  public static <T> Class<?> getGeneric(Class<T> clazz) {
    return ((ParameterizedType) clazz.getGenericSuperclass())
        .getActualTypeArguments()[0].getClass();
  }
}
