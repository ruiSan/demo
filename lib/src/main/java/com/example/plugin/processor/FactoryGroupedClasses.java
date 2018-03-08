package com.example.plugin.processor;

import com.example.plugin.Annote.Factory;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by dell on 2018/3/7.
 * 集合所有被@Factory标注的类，再生成对应代码文件
 */

public class FactoryGroupedClasses {

    private static final String SUFFIX = "Factory";

    private String qualifiedClassName;

    private Map<String, FactoryAnnotatedClass> itemsMap = new LinkedHashMap<>();

    public FactoryGroupedClasses(String qualifiedClassName) {
        this.qualifiedClassName = qualifiedClassName;
    }

    /**
     * 将注解的解析类加入到map
     * @param toInsert
     * @throws ProcessException
     */
    public void add(FactoryAnnotatedClass toInsert) throws ProcessException{
        FactoryAnnotatedClass existing = itemsMap.get(toInsert.getId());
        if (null != existing) {
            throw new ProcessException(toInsert.getAnnotatedClassElement(),
                    "Conflict: The class %s is the same with class %s using the same @%s id = %s",
                    toInsert.getAnnotatedClassElement().getQualifiedName().toString(), existing.getAnnotatedClassElement().getQualifiedName().toString(),
                    Factory.class.getSimpleName(), toInsert.getId());
        }
        itemsMap.put(toInsert.getId(), toInsert);
    }

    /**
     * 生成代码
     */
    public void generateCode(Elements elementUtils, Filer filer) throws IOException{
        TypeElement superClass = elementUtils.getTypeElement(qualifiedClassName);
        //获取将要生成的类名
        String factoryClassName = superClass.getSimpleName() + SUFFIX;
        String qualifiedFactoryClassName =  qualifiedClassName + SUFFIX;
        PackageElement pkg = elementUtils.getPackageOf(superClass);
        String packageName = pkg.isUnnamed()? null:pkg.getQualifiedName().toString();

        //生成代码
        MethodSpec.Builder method = MethodSpec.methodBuilder("createTea").
                addModifiers(Modifier.PUBLIC).
                addParameter(String.class, "id")
                .returns(TypeName.get(superClass.asType()));

        //检测id是否为null
        method.beginControlFlow("if(id == null)").
                addStatement("throw new IllegalArgumentException($S)", "id is null")
                .endControlFlow();

        //if 判断语句
        for (FactoryAnnotatedClass item : itemsMap.values()){
            method.beginControlFlow(" if ($S.equals(id))", item.getId())
                    .addStatement("return new $L()", item.getAnnotatedClassElement().getQualifiedName().toString())
                    .endControlFlow();
        }

        method.addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ");

        TypeSpec typeSpec = TypeSpec.classBuilder(factoryClassName).addModifiers(Modifier.PUBLIC).addMethod(method.build()).build();
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
    }

}
