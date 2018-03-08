package com.lyj.test1.CompilerInter;

/**
 * Created by dell on 2018/3/8.
 * 接口-- 供编译期生成的代码使用
 */
public interface ObjectBinder<T> {

    void bind(T target);

}
