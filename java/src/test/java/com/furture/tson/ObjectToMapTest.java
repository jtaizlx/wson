package com.furture.tson;

import com.alibaba.fastjson.JSON;
import com.efurture.tson.Tson;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/3.
 */
public class ObjectToMapTest extends TestCase {

    public void  testObjectToMap() throws InvocationTargetException, IllegalAccessException {
        User user = new User();
        user.name = "中国";
        user.country = "中国";
        Map map = toMap(user);
        System.out.println(map);

        System.out.println(JSON.toJSONString(user));
    }


    public void  testRecursice(){
        Node node = new Node();
        node.name = "测试";
        node.next = node;
        System.out.println(JSON.toJSONString(node));
        System.out.println(new String(Tson.toTson(node)));
    }

    private Map toMap(Object object) throws InvocationTargetException, IllegalAccessException {
        Map map = new HashMap<>();
        Class<?> targetClass = object.getClass();
        String key = targetClass.getName();
        List<Method> methods = methodsCache.get(key);
        if(methods == null){
            methods = new ArrayList<>();
            Class<?> parentClass =  targetClass;
            while (parentClass != Object.class){
                Method[] declaredMethods = parentClass.getDeclaredMethods();
                for(Method declaredMethod : declaredMethods){
                    String methodName = declaredMethod.getName();
                    if(methodName.startsWith(METHOD_PREFIX_GET)
                            || methodName.startsWith(METHOD_PREFIX_IS)) {
                        if(Modifier.isPublic(declaredMethod.getModifiers())) {
                            methods.add(declaredMethod);
                        }
                    }
                }
                parentClass = parentClass.getSuperclass();
            }
            methodsCache.put(key, methods);
        }
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith(METHOD_PREFIX_GET)) {
                Object value = method.invoke(object);
                if(value != null){
                    StringBuilder builder = new StringBuilder(method.getName().substring(3));
                    builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
                    map.put(builder.toString(), (Object) value);
                }
            }else if(methodName.startsWith(METHOD_PREFIX_IS)){
                Object value = method.invoke(object);
                if(value != null){
                    StringBuilder builder = new StringBuilder(method.getName().substring(2));
                    builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
                    map.put(builder.toString(), value);
                }
            }
        }
        Field[] fields = fieldsCache.get(key);
        if(fields == null) {
            fields = targetClass.getFields();
            fieldsCache.put(key, fields);

        }
        for(Field field : fields){
            String fieldName = field.getName();
            if(map.containsKey(fieldName)){
                continue;
            }
            Object value  = field.get(object);
            if(value == null){
                continue;
            }
            map.put(fieldName, value);
        }
        return  map;
    }

    private List<Method> getMethods(Class targetClass){
        List<Method> methods = new ArrayList<>();
        while (targetClass != Object.class){
            Method[] declaredMethods = targetClass.getDeclaredMethods();
            for(Method declaredMethod : declaredMethods){
                String methodName = declaredMethod.getName();
                if(methodName.startsWith(METHOD_PREFIX_GET)
                        || methodName.startsWith(METHOD_PREFIX_IS)) {
                    if(Modifier.isPublic(declaredMethod.getModifiers())) {
                        methods.add(declaredMethod);
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        return methods;
    }

    public static final String METHOD_PREFIX_GET = "get";
    public static final String METHOD_PREFIX_IS = "is";

    private static Tson.LruCache<String, List<Method>> methodsCache = new Tson.LruCache<>(32);
    private static Tson.LruCache<String, Field[]> fieldsCache = new Tson.LruCache<>(32);


}
