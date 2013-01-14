package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.master.configuration.UserTaskGenerator;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/10/12
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigDefinitionParser extends AbstractSimpleBeanDefinitionParser {


    @Override
    protected Class getBeanClass(Element element) {
        return Configuration.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        //parse session-listeners
        Element sListenerGroup = DomUtils.getChildElementByTagName(element, XMLConstants.SESSION_EXECUTION_LISTENERS);
        ManagedList slList = new ManagedList();
        Set<String> sessionExecListeners = new HashSet<String>();
        if (sListenerGroup != null){
            if (!sListenerGroup.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                builder.addPropertyReference(XMLConstants.SESSION_EXECUTION_LISTENERS_CLASS_FIELD, sListenerGroup.getAttribute(XMLConstants.ATTRIBUTE_REF));
            }else{
                List<Element> sl = DomUtils.getChildElements(sListenerGroup);

                for (Element el : sl){
                    if (!el.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                        String beanName = el.getAttribute(XMLConstants.ATTRIBUTE_REF);
                        if (!sessionExecListeners.contains(beanName)){
                            sessionExecListeners.add(beanName);
                            slList.add(new RuntimeBeanReference(beanName));
                        }
                    }else{
                        if (!el.getAttribute(XMLConstants.BEAN).isEmpty()){

                            if (!sessionExecListeners.contains(el.getAttribute(XMLConstants.BEAN))){
                                sessionExecListeners.add(el.getAttribute(XMLConstants.BEAN));
                                slList.add(parserContext.getDelegate().parsePropertySubElement(el, builder.getBeanDefinition()));
                            }
                        }else
                        if (!el.getAttribute(XMLConstants.LOCAL).isEmpty()){

                            if (!sessionExecListeners.contains(el.getAttribute(XMLConstants.LOCAL))){
                                sessionExecListeners.add(el.getAttribute(XMLConstants.LOCAL));
                                slList.add(parserContext.getDelegate().parsePropertySubElement(el, builder.getBeanDefinition()));
                            }
                        }
                    }
                }
            }
        }

        //add standard listeners
        for (String sessionListener : XMLConstants.STANDARD_SESSION_EXEC_LISTENERS){
            if (!sessionExecListeners.contains(sessionListener)){
                slList.add(new RuntimeBeanReference(sessionListener));
            }
        }

        builder.addPropertyValue(XMLConstants.SESSION_EXECUTION_LISTENERS_CLASS_FIELD, slList);

        //parse task-listeners
        Element tListenerGroup = DomUtils.getChildElementByTagName(element, XMLConstants.TASK_EXECUTION_LISTENERS);
        ManagedList tlList = new ManagedList();
        Set<String> taskExecListeners = new HashSet<String>();
        if (tListenerGroup != null){
            if (!sListenerGroup.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                builder.addPropertyReference(XMLConstants.TASK_EXECUTION_LISTENERS_CLASS_FIELD, sListenerGroup.getAttribute(XMLConstants.ATTRIBUTE_REF));
            }else{
                List<Element> tl = DomUtils.getChildElements(tListenerGroup);

                for (Element el : tl){
                    if (!el.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
                        String beanName = el.getAttribute(XMLConstants.ATTRIBUTE_REF);
                        if (!taskExecListeners.contains(beanName)){
                            taskExecListeners.add(beanName);
                            tlList.add(new RuntimeBeanReference(beanName));
                        }
                    }else{
                        if (!el.getAttribute(XMLConstants.BEAN).isEmpty()){

                            if (!taskExecListeners.contains(el.getAttribute(XMLConstants.BEAN))){
                                taskExecListeners.add(el.getAttribute(XMLConstants.BEAN));
                                tlList.add(parserContext.getDelegate().parsePropertySubElement(el, builder.getBeanDefinition()));
                            }
                        }else
                        if (!el.getAttribute(XMLConstants.LOCAL).isEmpty()){

                            if (!taskExecListeners.contains(el.getAttribute(XMLConstants.LOCAL))){
                                taskExecListeners.add(el.getAttribute(XMLConstants.LOCAL));
                                tlList.add(parserContext.getDelegate().parsePropertySubElement(el, builder.getBeanDefinition()));
                            }
                        }
                    }
                }
            }
        }
        //add standard listeners
        for (String sessionListener : XMLConstants.STANDARD_TASK_EXEC_LISTENERS){
            if (!taskExecListeners.contains(sessionListener)){
                tlList.add(new RuntimeBeanReference(sessionListener));
            }
        }

        builder.addPropertyValue(XMLConstants.TASK_EXECUTION_LISTENERS_CLASS_FIELD, tlList);


        //parse test-plan
        Element testPlan = DomUtils.getChildElementByTagName(element, XMLConstants.TEST_PLAN);

        BeanDefinitionBuilder generator = BeanDefinitionBuilder.genericBeanDefinition(UserTaskGenerator.class);
        parserContext.getRegistry().registerBeanDefinition(XMLConstants.GENERATOR, generator.getBeanDefinition());

        if (!element.getAttribute(XMLConstants.MONITORING_ENABLE).isEmpty()){
            generator.addPropertyValue(XMLConstants.MONITORING_ENABLE, element.getAttribute(XMLConstants.MONITORING_ENABLE));
        }

        if (!testPlan.getAttribute(XMLConstants.ATTRIBUTE_REF).isEmpty()){
            generator.addPropertyReference(XMLConstants.CONFIG, testPlan.getAttribute(XMLConstants.ATTRIBUTE_REF));
        }else{
            generator.addPropertyValue(XMLConstants.CONFIG, parserContext.getDelegate().parseCustomElement(testPlan, builder.getBeanDefinition()));
        }
        builder.addPropertyValue(XMLConstants.TASKS, XMLConstants.GENERATOR_GENERATE);
    }
}
