package com.example.plugin.Annote;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dell on 2018/3/7.
 * 标注类型class
 * 编译期间执行
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Factory {
    /**
     * 工厂名字
     * @return
     */
    Class type();

    /**
     * 用来表示对象的唯一id
     * @return
     */
    String id();
}
