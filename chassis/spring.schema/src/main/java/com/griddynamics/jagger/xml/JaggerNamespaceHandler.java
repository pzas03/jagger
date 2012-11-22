package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.xml.beanParsers.*;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.context.config.ContextNamespaceHandler;

public class JaggerNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("invocation", new InvocationDefinitionParser());
        registerBeanDefinitionParser("task", new TaskDefinitionParser(this));
        registerBeanDefinitionParser("test", new TestDefinitionParser(this));
        registerBeanDefinitionParser("user", new UserDefinitionParser());
        //registerBeanDefinitionParser("ref", new RefsDefinitionParser());

    }
}
