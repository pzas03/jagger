package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.xml.JaggerNamespaceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;


public class TestDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    private static final Logger log = LoggerFactory.getLogger(TestDefinitionParser.class);

    JaggerNamespaceHandler handler;

    public TestDefinitionParser(JaggerNamespaceHandler handler) {
        this.handler = handler;
    }

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);

        List<Element> userElements = DomUtils.getChildElements(element);
        ManagedList tasks = new ManagedList(userElements.size());
        ParserContext childContext = new ParserContext(parserContext.getReaderContext(), parserContext.getDelegate(), builder.getBeanDefinition());
        for (Element userElement: userElements) {
            tasks.add(handler.parse(userElement, childContext));
        }

        builder.addPropertyValue("tasks",tasks);
    }
}
