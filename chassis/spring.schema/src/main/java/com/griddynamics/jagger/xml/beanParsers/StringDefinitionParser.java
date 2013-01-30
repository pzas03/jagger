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
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringDefinitionParser implements BeanDefinitionParser {
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(String.class);
        bean.addConstructorArgValue(element.getTextContent());
        return bean.getBeanDefinition();
    }
}
