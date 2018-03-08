package com.example.plugin.processor;

import com.example.plugin.Annote.Factory;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * Created by dell on 2018/3/7.
 * 记录被@Factory标记的类的相关信息
 */
public class FactoryAnnotatedClass {

    private TypeElement annotatedClassElement;
    private String id;
    private String qualifiedGroupClassName;
    private String simpleFactoryGroupName;

    public FactoryAnnotatedClass(TypeElement classElement) throws ProcessException {
        annotatedClassElement = classElement;
        Factory factory = classElement.getAnnotation(Factory.class);
        id = factory.id();
        if (null == id || "".equals(id)) {
            throw new ProcessException(classElement, "id() in @%s for class %s is null or empty! that not allowed",
                    Factory.class.getSimpleName(), classElement.getQualifiedName().toString());
        }
        try {
            Class<?> clazz = factory.type();
            qualifiedGroupClassName = clazz.getCanonicalName();
            simpleFactoryGroupName = clazz.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement typeElement = (TypeElement) classTypeMirror.asElement();
            qualifiedGroupClassName = typeElement.getQualifiedName().toString();
            simpleFactoryGroupName = typeElement.getSimpleName().toString();
        }

    }

    public String getId() {
        return id;
    }

    public String getQualifiedGroupClassName() {
        return qualifiedGroupClassName;
    }

    public String getSimpleFactoryGroupName() {
        return simpleFactoryGroupName;
    }

    public TypeElement getAnnotatedClassElement() {
        return annotatedClassElement;
    }
}
