package com.example.plugin.processor;


import com.example.plugin.Annote.Factory;
import com.example.plugin.Annote.TeaType;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by dell on 2018/3/7.
 * 注解处理器
 */
@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {

    private static ClassName VIEW_BINDER = ClassName.get("com.lyj.test1.CompilerInter", "ObjectBinder");

    private Types typeUtils;

    private Elements elementUtils;

    private Filer filer;

    private Messager messager;

    private Map<String, List<TeaTypeAnnotatedClass>> cacheMaps = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = processingEnvironment.getTypeUtils();
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    /**
     * 获取支持的注解对象
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<String>();
        annotations.add(Factory.class.getCanonicalName());
        annotations.add(TeaType.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //找出所有被fractory注解的类
        FactoryGroupedClasses factoryGroupedClasses = null;
        try {
            //第一步生成Tea的子类
            for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(TeaType.class)) {
                if (annotatedElement.getKind() != ElementKind.FIELD) {
                    throw new ProcessException(annotatedElement, "only field can be annotated with @%s", TeaType.class.getSimpleName());
                }

                Element emclosingElement = annotatedElement.getEnclosingElement();
                String inClassName = emclosingElement.getSimpleName().toString();

                //类型转换
                VariableElement typeElement = (VariableElement) annotatedElement;
                TeaTypeAnnotatedClass annotatedClass = new TeaTypeAnnotatedClass(typeElement);
                if (!cacheMaps.containsKey(inClassName)) {
                    List<TeaTypeAnnotatedClass> annotatedClasses = new ArrayList<>();
                    annotatedClasses.add(annotatedClass);
                    cacheMaps.put(inClassName, annotatedClasses);
                } else {
                    cacheMaps.get(inClassName).add(annotatedClass);
                }
                annotatedClass.genatedCode(elementUtils, filer);
            }
            genetatedMapCode();

            for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(Factory.class)) {
                //判断是否是class类型
                if (annotatedElement.getKind() != ElementKind.CLASS) {
                    throw new ProcessException(annotatedElement, "only class can be annotated with @%s", Factory.class.getSimpleName());
                }
                //类型转换
                TypeElement typeElement = (TypeElement) annotatedElement;
                FactoryAnnotatedClass annotatedClass = new FactoryAnnotatedClass(typeElement);
                //暂时先不做校验
//                checkValidClass(annotatedClass);
                //add
                if (null == factoryGroupedClasses) {
                    factoryGroupedClasses = new FactoryGroupedClasses(annotatedClass.getQualifiedGroupClassName());
                }
                factoryGroupedClasses.add(annotatedClass);
            }
            if (null != factoryGroupedClasses ) {
                factoryGroupedClasses.generateCode(elementUtils, filer);
            }
        } catch (ProcessException e) {
            error(e.getElement(), e.getMessage());
        } catch (Exception e) {
            error(null, e.getMessage());
        }
        cacheMaps.clear();
        return true;
    }

    private void genetatedMapCode() throws IOException {
        Iterator it = cacheMaps.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            List<TeaTypeAnnotatedClass> annotatedClassList = (List<TeaTypeAnnotatedClass>) entry.getValue();

            TypeName typeName = TypeVariableName.get("T");
            MethodSpec.Builder method = MethodSpec.methodBuilder("bind").addModifiers(Modifier.PUBLIC)
                    .addParameter(typeName, "target").returns(TypeVariableName.get("void"));

            String pakeName = "";
            for (TeaTypeAnnotatedClass annotatedClass : annotatedClassList) {
                method.addStatement("target.$L = new $L()", annotatedClass.getVariableName(), annotatedClass.getClassName());
                if ("".equals(pakeName)) {
                    pakeName = annotatedClass.packeName(elementUtils);
                }
            }

            TypeSpec typeSpec = TypeSpec.classBuilder(key + "$Binder").addModifiers(Modifier.PUBLIC).
                    addTypeVariable(TypeVariableName.get("T", TypeVariableName.get(key))).
                    addSuperinterface(ParameterizedTypeName.get(VIEW_BINDER, typeName)).
            addMethod(method.build()).build();
            JavaFile.builder(pakeName, typeSpec).
                    addFileComment("hey, this is the code by robot write").
                    build().writeTo(filer);
        }
    }

    /**
     * Prints an error message
     *
     * @param e The element which has caused the error. Can be null
     * @param msg The error message
     */
    public void error(Element e, String msg) {
        try {
            messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
        } catch (Exception es) {

        }
    }
}
