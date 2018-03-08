package com.example.plugin.Annote;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;

/**
 * Created by dell on 2018/3/7.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface TeaType {

    Class type();

    /**
     * 类名
     *
     * @return
     */
    String teaClassNmae();

    /**
     * 用来表示对象的唯一id
     *
     * @return
     */
    String id();

    /**
     * 价格
     *
     * @return
     */
    String price();

}