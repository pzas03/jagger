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


public class TaskDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    private static final Logger log = LoggerFactory.getLogger(TaskDefinitionParser.class);

    JaggerNamespaceHandler handler;

    public TaskDefinitionParser(JaggerNamespaceHandler handler) {
        this.handler = handler;
    }

    @Override
    protected Class getBeanClass(Element element) {
        return ProcessingConfig.Test.Task.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);

        List<Element> userElements = DomUtils.getChildElementsByTagName(element, "user");
        ManagedList users = new ManagedList(userElements.size());
        ParserContext childContext = new ParserContext(parserContext.getReaderContext(), parserContext.getDelegate(), builder.getBeanDefinition());
        for (Element userElement: userElements) {
            users.add(handler.parse(userElement, childContext));
        }
        builder.addPropertyValue("users",users);

        List<Element> invocationElements = DomUtils.getChildElementsByTagName(element, "invocation");
        if (!invocationElements.isEmpty()) {
            builder.addPropertyValue("invocation", handler.parse(invocationElements.get(0), childContext));
        }
    }
}
