package com.morning;

import java.lang.reflect.Constructor;

public class MyScanner {
    private static final String[] CLASS_NAMES = {
            "com.morning.UserService",
            "com.morning.OrderService",
            "com.morning.CommonUtil"
    };

    public static void main(String[] args) throws Exception{
        System.out.println("=== 开始扫描 ===");

        for(String className : CLASS_NAMES){
            Class<?> clazz = Class.forName(className);
            MyComponent annotation = clazz.getAnnotation(MyComponent.class);
            if(annotation == null){
                System.out.println("跳过（无注解）：" + className);
                continue;
            }
            System.out.println("发现Bean：" + clazz.getSimpleName());

            String beanName = annotation.value();
            if(!beanName.isEmpty()){
                System.out.println("Bean名称：" + beanName);
            }

            Object bean = clazz.getDeclaredConstructor().newInstance();
            clazz.getMethod("hello").invoke(bean);
        }
        System.out.println("=== 扫描结束 ===");
    }
}
