package com.griddynamics.jagger.xml.beanParsers.workload.queryProvider;

import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/24/13
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleQueryProviderDefinitionParser extends CustomBeanDefinitionParser{

    @Override
    protected Class getBeanClass(Element element) {
        return ArrayList.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        addConstructorListArg(element, parserContext, builder.getBeanDefinition());
    }
}
