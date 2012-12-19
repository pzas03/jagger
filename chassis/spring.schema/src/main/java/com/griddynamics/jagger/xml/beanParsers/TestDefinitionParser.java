package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.user.ProcessingConfig;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;


public class TestDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);
        element.setAttribute(BeanDefinitionParserDelegate.VALUE_TYPE_ATTRIBUTE, ProcessingConfig.Test.Task.class.getCanonicalName());

        List<Element> list = DomUtils.getChildElements(element);
        ManagedList tasks = new ManagedList(list.size());

        for (Element el : list){
            if (!el.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                tasks.add(new RuntimeBeanReference(el.getAttribute(XMLConstants.ATTRIBUTE_REF)));
            }else{
                if (el.getTagName().equals(XMLConstants.TASK)){
                    tasks.add(parserContext.getDelegate().parseCustomElement(el, builder.getBeanDefinition()));
                }else{
                    if (el.getTagName().equals(XMLConstants.ATTRIBUTE_REF)){
                        //parse ref
                        if (!el.getAttribute(XMLConstants.LOCAL).isEmpty()){
                            //local ref
                            tasks.add(new RuntimeBeanReference(el.getAttribute(XMLConstants.LOCAL)));
                        }else{
                            //bean
                            tasks.add(new RuntimeBeanReference(el.getAttribute(XMLConstants.BEAN)));
                        }
                    }
                }
            }
        }

        builder.addPropertyValue(XMLConstants.TASKS,tasks);
    }
}
