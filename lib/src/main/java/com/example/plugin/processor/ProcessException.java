package com.example.plugin.processor;

import javax.lang.model.element.Element;

/**
 * Created by dell on 2018/3/7.
 */

public class ProcessException extends Exception {

    Element element;

    public ProcessException(Element element, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

}
