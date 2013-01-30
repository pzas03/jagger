package com.griddynamics.jagger.xml.beanParsers.configuration;

import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/29/13
 * Time: 6:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class PercentilesDefinitionParser extends CustomBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return ArrayList.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addConstructorArgValue(parserContext.getDelegate().parseListElement(element, builder.getBeanDefinition()));
    }
}
