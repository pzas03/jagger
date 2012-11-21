package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.master.configuration.WorkloadTasksGenerator;
import com.griddynamics.jagger.xml.MetricsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;


public class PerformanceProfileDefinitionParser extends AbstractSingleBeanDefinitionParser {
    private static final Logger log = LoggerFactory.getLogger(PerformanceProfileDefinitionParser.class);

    @Override
    protected String getBeanClassName(Element element) {
        return Configuration.class.getCanonicalName();
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        parsePerformanceProfile(element, parserContext, builder);
    }

    private void parsePerformanceProfile(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();
        beanDefinition.setLazyInit(true);
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        //beanDefinition.setFactoryBeanName();
        beanDefinition.setBeanClass(WorkloadTasksGenerator.class);
    }

    private Collection<String> parseMetrics(Node element) {
        Collection<String> result = new ArrayList<String>();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            result.add(children.item(i).getTextContent());
        }
        return result;
    }
}
