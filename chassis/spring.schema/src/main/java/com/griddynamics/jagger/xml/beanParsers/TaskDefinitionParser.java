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

    private static final Logger log = LoggerFactory.getLogger(TaskDefinitionParser.class);

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.Task.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);

        if (!DomUtils.getChildElementsByTagName(element, "user").isEmpty()) {
            element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, ProcessingConfig.Test.Task.User.class.getCanonicalName());
            List users = parserContext.getDelegate().parseListElement(element, builder.getBeanDefinition());
            builder.addPropertyValue("users",users);
        } else {
            if(!DomUtils.getChildElementsByTagName(element, "tps").isEmpty()){
                element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, ProcessingConfig.Test.Task.Tps.class.getCanonicalName());
                List<Element> tpsElements = DomUtils.getChildElementsByTagName(element, "tps");
                builder.addPropertyValue("tps",parserContext.getDelegate().parseCustomElement(tpsElements.get(0), builder.getBeanDefinition()));
            }else{
                if(!DomUtils.getChildElementsByTagName(element, "virtual-user").isEmpty()){
                    element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, ProcessingConfig.Test.Task.VirtualUser.class.getCanonicalName());
                    List<Element> tpsElements = DomUtils.getChildElementsByTagName(element, "virtual-user");
                    builder.addPropertyValue("virtualUser",parserContext.getDelegate().parseCustomElement(tpsElements.get(0), builder.getBeanDefinition()));
                }else{
                    element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, ProcessingConfig.Test.Task.Invocation.class.getCanonicalName());
                    List<Element> tpsElements = DomUtils.getChildElementsByTagName(element, "invocation");
                    builder.addPropertyValue("invocation",parserContext.getDelegate().parseCustomElement(tpsElements.get(0), builder.getBeanDefinition()));
                }
            }
        }
    }
}
