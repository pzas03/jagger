package com.griddynamics.jagger.xml.beanParsers.configuration;

import com.griddynamics.jagger.engine.e1.aggregator.workload.DurationLogProcessor;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.master.configuration.UserTaskGenerator;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/10/12
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return Configuration.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        Element report = DomUtils.getChildElementByTagName(element, XMLConstants.REPORT);
        if (report!=null) {
            BeanDefinition bean = parserContext.getDelegate().parseCustomElement(report, builder.getBeanDefinition());
            parserContext.getRegistry().registerBeanDefinition(XMLConstants.CUSTOM_REPORTING_SERVICE,bean);
        }

        //parse session-listeners
        Element sListenerGroup = DomUtils.getChildElementByTagName(element, XMLConstants.SESSION_EXECUTION_LISTENERS);
        ManagedList slList = new ManagedList();

        //add standard listeners
        for (String sessionListener : XMLConstants.STANDARD_SESSION_EXEC_LISTENERS){
            slList.add(new RuntimeBeanReference(sessionListener));
        }
        builder.addPropertyValue(XMLConstants.SESSION_EXECUTION_LISTENERS_CLASS_FIELD, slList);

        //add user's listeners
        setBeanListProperty(XMLConstants.SESSION_EXECUTION_LISTENERS_CLASS_FIELD, true, sListenerGroup, parserContext, builder.getBeanDefinition());


        //Parse task-listeners
        Element tListenerGroup = DomUtils.getChildElementByTagName(element, XMLConstants.TASK_EXECUTION_LISTENERS);
        ManagedList tlList = new ManagedList();

        //add standard listeners
        for (String sessionListener : XMLConstants.STANDARD_TASK_EXEC_LISTENERS){
            tlList.add(new RuntimeBeanReference(sessionListener));
        }

        //override durationLogProcessor if needed
        Element percentilesTimeElement = DomUtils.getChildElementByTagName(element, XMLConstants.PERCENTILES_TIME);
        Element percentilesGlobalElement = DomUtils.getChildElementByTagName(element, XMLConstants.PERCENTILES_GLOBAL);
        if (percentilesTimeElement != null || percentilesGlobalElement != null){
            BeanDefinitionBuilder durationLogProcessorBean = BeanDefinitionBuilder.genericBeanDefinition(DurationLogProcessor.class);
            durationLogProcessorBean.setParentName(XMLConstants.DURATION_LOG_PROCESSOR);

            if (percentilesTimeElement!=null)
                setBeanProperty(XMLConstants.TIME_WINDOW_PERCENTILES_KEYS, percentilesTimeElement, parserContext, durationLogProcessorBean.getBeanDefinition());

            if (percentilesGlobalElement!=null)
                setBeanProperty(XMLConstants.GLOBAL_PERCENTILES_KEYS, percentilesGlobalElement, parserContext, durationLogProcessorBean.getBeanDefinition());

            tlList.add(durationLogProcessorBean.getBeanDefinition());
        }else{
           tlList.add(new RuntimeBeanReference(XMLConstants.DURATION_LOG_PROCESSOR));
        }
        builder.addPropertyValue(XMLConstants.TASK_EXECUTION_LISTENERS_CLASS_FIELD, tlList);

        //add user's listeners
        setBeanListProperty(XMLConstants.TASK_EXECUTION_LISTENERS_CLASS_FIELD, true, tListenerGroup, parserContext, builder.getBeanDefinition());

        //parse test-plan
        Element testPlan = DomUtils.getChildElementByTagName(element, XMLConstants.TEST_PLAN);

        BeanDefinitionBuilder generator = BeanDefinitionBuilder.genericBeanDefinition(UserTaskGenerator.class);
        parserContext.getRegistry().registerBeanDefinition(XMLConstants.GENERATOR, generator.getBeanDefinition());

        if (!element.getAttribute(XMLConstants.MONITORING_ENABLE).isEmpty()){
            generator.addPropertyValue(XMLConstants.MONITORING_ENABLE, element.getAttribute(XMLConstants.MONITORING_ENABLE));
        }
        setBeanProperty(XMLConstants.CONFIG, testPlan, parserContext, generator.getBeanDefinition());
        builder.addPropertyValue(XMLConstants.TASKS, XMLConstants.GENERATOR_GENERATE);
    }

    @Override
    protected void parseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        //do nothing
    }
}
