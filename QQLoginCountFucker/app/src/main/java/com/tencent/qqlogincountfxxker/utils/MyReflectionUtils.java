package com.tencent.qqlogincountfxxker.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Copyright (C), 2015-2020
 *
 * @author Cooper
 * @date 2021/3/29 13:03
 * History:
 * <author> <time> <version> <desc>
 * Cooper 2021/3/29 13:03 1  反射工具类
 * <p>
 * 参考：
 * http://www.xwood.net/_site_domain_/_root/5870/5874/t_c273927.html
 * https://blog.csdn.net/u010675669/article/details/86625499
 */
public class MyReflectionUtils {

    /**
     * 获取私有成员变量的值
     *
     * @param instance  对象
     * @param fieldName 字段名
     * @return 结果，可能为null
     */
    public static Object getValue(Object instance, String fieldName) {
        //throws IllegalAccessException, NoSuchFieldException {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true); // 参数值为true，禁止访问控制检查
            return field.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置私有成员变量的值
     *
     * @param instance  对象
     * @param fieldName 字段名
     * @param value     要设置的值
     */
    public static void setValue(Object instance, String fieldName, Object value) {
        //throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 访问私有方法
     *
     * @param instance   对象
     * @param methodName 方法名
     * @param classes    方法的参数类，用于搜索方法
     * @param objects    方法的输入参数内容，用于调用方法
     * @return 结果，可能为null
     */
    public static Object callMethod(Object instance, String methodName, Class[] classes, Object[] objects) {
//            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
//            InvocationTargetException {
        try {
            Method method = instance.getClass().getDeclaredMethod(methodName, classes);
            method.setAccessible(true);
            return method.invoke(instance, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Indicates whether or not a {@link Member} is both public and is contained in a public class.
     *
     * @param <T>    type of the object whose accessibility to test
     * @param member the Member to check for public accessibility (must not be {@code null}).
     * @return {@code true} if {@code member} is public and contained in a public class.
     * @throws NullPointerException if {@code member} is {@code null}.
     */
    public static <T extends AccessibleObject & Member> boolean isAccessible(final T member) {
        //Objects.requireNonNull(member, "No member provided");
        if (member != null) {
            return Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(member.getDeclaringClass().getModifiers());
        }
        return false;
    }

    /**
     * Makes a {@link Member} {@link AccessibleObject#isAccessible() accessible} if the member is not public.
     *
     * @param <T>    type of the object to make accessible
     * @param member the Member to make accessible (must not be {@code null}).
     * @throws NullPointerException if {@code member} is {@code null}.
     */
    public static <T extends AccessibleObject & Member> void makeAccessible(final T member) {
        if (!isAccessible(member) && !member.isAccessible()) {
            member.setAccessible(true);
        }
    }

    /**
     * Makes a {@link Field} {@link AccessibleObject#isAccessible() accessible} if it is not public or if it is final.
     *
     * <p>Note that using this method to make a {@code final} field writable will most likely not work very well due to
     * compiler optimizations and the like.</p>
     *
     * @param field the Field to make accessible (must not be {@code null}).
     * @throws NullPointerException if {@code field} is {@code null}.
     */
    public static void makeAccessible(final Field field) {
        //Objects.requireNonNull(field, "No field provided");
        if (field != null) {
            if ((!isAccessible(field) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
                field.setAccessible(true);
            }
        }
    }

    /**
     * Gets the value of a {@link Field}, making it accessible if required.
     *
     * @param field    the Field to obtain a value from (must not be {@code null}).
     * @param instance the instance to obtain the field value from or {@code null} only if the field is static.
     * @return the value stored by the field.
     * @throws NullPointerException if {@code field} is {@code null}, or if {@code instance} is {@code null} but
     *                              {@code field} is not {@code static}.
     * @see Field#get(Object)
     */
    public static Object getFieldValue(final Field field, final Object instance) {
        makeAccessible(field);
        if (!Modifier.isStatic(field.getModifiers())) {
            //Objects.requireNonNull(instance, "No instance given for non-static field");
        }
        try {
            return field.get(instance);
        } catch (final IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Gets the value of a static {@link Field}, making it accessible if required.
     *
     * @param field the Field to obtain a value from (must not be {@code null}).
     * @return the value stored by the static field.
     * @throws NullPointerException if {@code field} is {@code null}, or if {@code field} is not {@code static}.
     * @see Field#get(Object)
     */
    public static Object getStaticFieldValue(final Field field) {
        return getFieldValue(field, null);
    }

    /**
     * Sets the value of a {@link Field}, making it accessible if required.
     *
     * @param field    the Field to write a value to (must not be {@code null}).
     * @param instance the instance to write the value to or {@code null} only if the field is static.
     * @param value    the (possibly wrapped) value to write to the field.
     * @throws NullPointerException if {@code field} is {@code null}, or if {@code instance} is {@code null} but
     *                              {@code field} is not {@code static}.
     * @see Field#set(Object, Object)
     */
    public static void setFieldValue(final Field field, final Object instance, final Object value) {
        makeAccessible(field);
        if (!Modifier.isStatic(field.getModifiers())) {
            //Objects.requireNonNull(instance, "No instance given for non-static field");
        }
        try {
            field.set(instance, value);
        } catch (final IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Sets the value of a static {@link Field}, making it accessible if required.
     *
     * @param field the Field to write a value to (must not be {@code null}).
     * @param value the (possibly wrapped) value to write to the field.
     * @throws NullPointerException if {@code field} is {@code null}, or if {@code field} is not {@code static}.
     * @see Field#set(Object, Object)
     */
    public static void setStaticFieldValue(final Field field, final Object value) {
        setFieldValue(field, null, value);
    }

    /**
     * Gets the default (no-arg) constructor for a given class.
     *
     * @param clazz the class to find a constructor for
     * @param <T>   the type made by the constructor
     * @return the default constructor for the given class
     * @throws IllegalStateException if no default constructor can be found
     */
    public static <T> Constructor<T> getDefaultConstructor(final Class<T> clazz) {
        //Objects.requireNonNull(clazz, "No class provided");
        if (clazz != null) {
            try {
                final Constructor<T> constructor = clazz.getDeclaredConstructor();
                makeAccessible(constructor);
                return constructor;
            } catch (final NoSuchMethodException ignored) {
                try {
                    final Constructor<T> constructor = clazz.getConstructor();
                    makeAccessible(constructor);
                    return constructor;
                } catch (final NoSuchMethodException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return null;
    }

    /**
     * Constructs a new {@code T} object using the default constructor of its class. Any exceptions thrown by the
     * constructor will be rethrown by this method, possibly wrapped in an
     * {@link java.lang.reflect.UndeclaredThrowableException}.
     *
     * @param clazz the class to use for instantiation.
     * @param <T>   the type of the object to construct.
     * @return a new instance of T made from its default constructor.
     * @throws IllegalArgumentException if the given class is abstract, an interface, an array class, a primitive type,
     *                                  or void
     * @throws IllegalStateException    if access is denied to the constructor, or there are no default constructors
     */
    public static <T> T instantiate(final Class<T> clazz) {
        //Objects.requireNonNull(clazz, "No class provided");
        if (clazz != null) {
            final Constructor<T> constructor = getDefaultConstructor(clazz);
            try {
                return constructor.newInstance();
            } catch (final LinkageError | InstantiationException e) {
                // LOG4J2-1051
                // On platforms like Google App Engine and Android, some JRE classes are not supported: JMX, JNDI, etc.
                throw new IllegalArgumentException(e);
            } catch (final IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (final InvocationTargetException e) {
                //Throw ables.rethrow(e.getCause());
                throw new InternalError("Unreachable");
            }
        }
        return null;
    }

    /**
     * 寻找指定字段
     *
     * @param o         对象
     * @param fieldName 字段名
     * @param type      类型
     * @return 结果，可能为null
     */
    public static Object getObjectField(Object o, String fieldName, String type) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName) && field.getType().getName().equals(type)) {
                field.setAccessible(true);
                try {
                    return field.get(o);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 寻找指定字段
     *
     * @param o         对象
     * @param fieldName 字段名
     * @param type      类型
     * @return 结果，可能为null
     */
    public static Object getObjectField(Object o, String fieldName, Class<?> type) {
        return getObjectField(o, fieldName, type.getName());
    }

    /**
     * 寻找指定字段
     *
     * @param cls       类
     * @param o         对象
     * @param fieldName 字段名
     * @return 结果，可能为null
     */
    public static Object getObjectField(Class<?> cls, Object o, String fieldName) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 寻找静态方法
     *
     * @param cls        类
     * @param methodName 方法名
     * @param returnType 返回类型
     * @param parameters 参数，支持多个
     * @return 方法，可能为null
     */
    public static Method getStaticMethod(Class<?> cls, String methodName, Class<?> returnType, Class<?>... parameters) {
        for (Method method : cls.getDeclaredMethods()) {
            if (!method.getName().equals(methodName) || method.getReturnType() != returnType)
                continue;

            Class<?>[] pars = method.getParameterTypes();
            if (parameters.length != pars.length)
                continue;
            boolean found = true;
            for (int i = 0; i < parameters.length; i++) {
                if (pars[i] != parameters[i]) {
                    found = false;
                    break;
                }
            }

            if (found) {
                return method;
            }
        }

        return null;
    }

    /**
     * 寻找方法
     *
     * @param o          对象
     * @param methodName 方法名
     * @param returnType 返回值类型
     * @param parameters 参数，支持多个
     * @return 方法，可能为null
     */
    public static Method getMethod(Object o, String methodName, Class<?> returnType, Class<?>... parameters) {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (!method.getName().equals(methodName) || method.getReturnType() != returnType)
                continue;

            Class<?>[] pars = method.getParameterTypes();
            if (parameters.length != pars.length)
                continue;
            boolean found = true;
            for (int i = 0; i < parameters.length; i++) {
                if (pars[i] != parameters[i]) {
                    found = false;
                    break;
                }
            }

            if (found) {
                return method;
            }
        }

        return null;
    }

    /**
     * 寻找方法
     *
     * @param o          对象
     * @param methodName 方法名
     * @param returnType 返回值类型
     * @param parameters 参数，支持多个
     * @return 方法，可能为null
     */
    public static Method getMethod(Object o, String methodName, String returnType, Class<?>... parameters) {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (!method.getName().equals(methodName) || !method.getReturnType().getName().equals(returnType))
                continue;

            Class<?>[] pars = method.getParameterTypes();
            if (parameters.length != pars.length)
                continue;
            boolean found = true;
            for (int i = 0; i < parameters.length; i++) {
                if (pars[i] != parameters[i]) {
                    found = false;
                    break;
                }
            }

            if (found) {
                return method;
            }
        }

        return null;
    }

    /**
     * 寻找方法
     *
     * @param o          对象
     * @param methodName 方法名
     * @param returnType 返回值类型
     * @param parameters 参数，支持多个
     * @return 方法，可能为null
     */
    public static Method getMethod(Object o, String methodName, String returnType, String... parameters) {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (!method.getName().equals(methodName) || !method.getReturnType().getName().equals(returnType))
                continue;

            Class<?>[] pars = method.getParameterTypes();
            if (parameters.length != pars.length)
                continue;
            boolean found = true;
            for (int i = 0; i < parameters.length; i++) {
                if (!pars[i].getName().equals(parameters[i])) {
                    found = false;
                    break;
                }
            }

            if (found) {
                return method;
            }
        }

        return null;
    }

    /**
     * 调用方法
     *
     * @param method 方法
     * @param o      对象
     * @param args   参数，支持多个
     * @return 调用后的返回值，可能为null
     */
    public static Object invokeMethod(Method method, Object o, Object... args) {
        if (method == null)
            return null;

        try {
            return method.invoke(o, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 调用静态方法
     *
     * @param method 方法
     * @param args   参数，支持多个
     * @return 调用后的返回值，可能为null
     */
    public static Object invokeStaticMethod(Method method, Object... args) {
        if (method == null)
            return null;
        return invokeMethod(method, (Object) null, args);
    }

    /**
     * 是否该类调用的
     *
     * @param className 类名
     * @return 调用者是否包含指定类
     */
    public static boolean isCallingFrom(String className) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTraceElements) {
            if (element.getClassName().contains(className)) {
                return true;
            }
        }
        return false;
    }
}
