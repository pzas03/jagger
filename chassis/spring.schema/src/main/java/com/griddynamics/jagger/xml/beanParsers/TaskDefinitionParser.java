package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.user.ProcessingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import java.util.List;


public class TaskDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.Task.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);

        if (!DomUtils.getChildElementsByTagName(element, XMLConstants.USER).isEmpty()) {
            element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, ProcessingConfig.Test.Task.User.class.getCanonicalName());
            List users = parserContext.getDelegate().parseListElement(element, builder.getBeanDefinition());
            builder.addPropertyValue(XMLConstants.USERS,users);
        } else {
            if(!DomUtils.getChildElementsByTagName(element, XMLConstants.TPS).isEmpty()){
                Element tps = DomUtils.getChildElementByTagName(element, XMLConstants.TPS);
                builder.addPropertyValue(XMLConstants.TPS,parserContext.getDelegate().parseCustomElement(tps, builder.getBeanDefinition()));
            }else{
                if(!DomUtils.getChildElementsByTagName(element, XMLConstants.VIRTUAL_USER).isEmpty()){
                    Element vu = DomUtils.getChildElementByTagName(element, XMLConstants.VIRTUAL_USER);
                    builder.addPropertyValue(XMLConstants.VIRTUAL_USER_CLASS_FIELD, parserContext.getDelegate().parseCustomElement(vu, builder.getBeanDefinition()));
                }else{
                    Element inv = DomUtils.getChildElementByTagName(element, XMLConstants.INVOCATION);
                    builder.addPropertyValue(XMLConstants.INVOCATION,parserContext.getDelegate().parseCustomElement(inv, builder.getBeanDefinition()));
                }
            }
        }
    }
}
