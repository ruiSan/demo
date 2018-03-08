package com.lyj.test1.CompilerInter;

/**
 * Created by dell on 2018/3/8.
 */

public class LyjFrame {

    public static void bind(Object target) {
        //获取target的类名
        //类名加指定后缀，new一个
        //执行binder方法
        Class<?> cls = target.getClass();
        String className = cls.getName() + "$Binder";
        try {
            Class<?> binder = Class.forName(className);
            ObjectBinder objectBinder = (ObjectBinder) binder.newInstance();
            objectBinder.bind(target);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
