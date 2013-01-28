package com.griddynamics.jagger.xml.beanParsers.workload.endpointProvider;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/24/13
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class EndpointDefinitionParser implements BeanDefinitionParser {
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(String.class);
        bean.addConstructorArgValue(element.getTextContent());
        return bean.getBeanDefinition();
    }
}
