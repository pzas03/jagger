package com.griddynamics.jagger.xml.beanParsers.workload.queryProvider;

import com.griddynamics.jagger.invoker.http.HttpQuery;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/24/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpQueryDefinitionParser extends AbstractSimpleBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return HttpQuery.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        Element clientParamsElement = DomUtils.getChildElementByTagName(element, XMLConstants.CLIENT_PARAMS);
        Element methodParamsElement = DomUtils.getChildElementByTagName(element, XMLConstants.METHOD_PARAMS);
        String method = element.getAttribute(XMLConstants.METHOD);

        builder.addPropertyValue(XMLConstants.METHOD, method);

        if (clientParamsElement != null){
            builder.addPropertyValue(XMLConstants.CLIENT_PARAMS, parserContext.getDelegate().parseMapElement(clientParamsElement, builder.getBeanDefinition()));
        }

        if (methodParamsElement != null){
            builder.addPropertyValue(XMLConstants.METHOD_PARAMS, parserContext.getDelegate().parseMapElement(methodParamsElement, builder.getBeanDefinition()));
        }
    }
}
