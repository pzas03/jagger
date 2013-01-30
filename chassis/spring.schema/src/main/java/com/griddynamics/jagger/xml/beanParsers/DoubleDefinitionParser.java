package com.griddynamics.jagger.xml.beanParsers;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/29/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class DoubleDefinitionParser implements BeanDefinitionParser {
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinition bean = BeanDefinitionBuilder.genericBeanDefinition(Double.class).getBeanDefinition();
        bean.getConstructorArgumentValues().addGenericArgumentValue(Double.parseDouble(element.getTextContent()));
        return bean;
    }
}
