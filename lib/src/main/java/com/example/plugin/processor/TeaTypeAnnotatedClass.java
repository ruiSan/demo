package com.example.plugin.processor;

import com.example.plugin.Annote.TeaType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;

/**
 * Created by dell on 2018/3/7.
 */

public class TeaTypeAnnotatedClass {

    private String className;

    private String id;

    private String price;

    private String qualifiedGroupClassName;

    private String simpleFactoryGroupName;

    private Class<?> clazz;

    private VariableElement classElement;

    private String variableName;

    public TeaTypeAnnotatedClass(VariableElement classElement) {
        TeaType teaType = classElement.getAnnotation(TeaType.class);
        this.classElement = classElement;

        variableName = classElement.getSimpleName().toString();
        //找出相关信息
        className = teaType.teaClassNmae();
        id = teaType.id();
        price = teaType.price();
        qualifiedGroupClassName = "";
        try {
            clazz = teaType.type();
            qualifiedGroupClassName = clazz.getCanonicalName();
            simpleFactoryGroupName = clazz.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement typeElement1 = (TypeElement) classTypeMirror.asElement();
            qualifiedGroupClassName = typeElement1.getQualifiedName().toString();
            simpleFactoryGroupName = typeElement1.getSimpleName().toString();
        }
    }

    public String getVariableName() {
        return variableName;
    }

    public String getClassName() {
        return className;
    }

    /**
     * 动态生成代码
     */
    public void genatedCode(Elements elementUtils, Filer filer) throws Exception {
        //获取将要生成的类名
        String factoryClassName = className;
        Element typeElement = classElement.getEnclosingElement();
        PackageElement pkg = elementUtils.getPackageOf(typeElement);
        String packageName1 = qualifiedGroupClassName.substring(0, qualifiedGroupClassName.lastIndexOf(simpleFactoryGroupName) - 1);
        String packageName = pkg.getQualifiedName().toString();

        MethodSpec.Builder method = MethodSpec.methodBuilder("getPrice").addModifiers(Modifier.PUBLIC)
                .returns(String.class);
        method.addStatement("return $S", price);

        MethodSpec.Builder getIdMethod = MethodSpec.methodBuilder("getId").addModifiers(Modifier.PUBLIC)
                .returns(String.class);
        getIdMethod.addStatement("return $S", id);

        TypeSpec typeSpec = TypeSpec.classBuilder(factoryClassName).addModifiers(Modifier.PUBLIC).
                addSuperinterface(ClassName.get(packageName1, simpleFactoryGroupName)).
//                addAnnotation(AnnotationSpec.builder(Factory.class).
//                        addMember("id", CodeBlock.builder().add("$S", id).build()).
//                        addMember("type", CodeBlock.builder().add("$L", "Tea.class").build()).
//                        build()).
                addMethod(method.build()).
                        addMethod(getIdMethod.build()).build();
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);

//        TypeElement element = (TypeElement) classElement.getEnclosingElement();
    }

    public String packeName(Elements elementUtils) {
        Element typeElement = classElement.getEnclosingElement();
        PackageElement pkg = elementUtils.getPackageOf(typeElement);
        return pkg.getQualifiedName().toString();
    }

}
