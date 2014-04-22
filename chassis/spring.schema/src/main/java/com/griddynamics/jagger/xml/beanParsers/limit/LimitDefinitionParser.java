package com.griddynamics.jagger.xml.beanParsers.limit;

import com.griddynamics.jagger.engine.e1.collector.limits.Limit;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class LimitDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return Limit.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyValue(XMLConstants.LIMIT_METRIC_NAME, element.getAttribute(XMLConstants.LIMIT_METRIC_NAME));
        builder.addPropertyValue(XMLConstants.LIMIT_LWL, element.getAttribute(XMLConstants.LIMIT_LWL));
        builder.addPropertyValue(XMLConstants.LIMIT_UWL, element.getAttribute(XMLConstants.LIMIT_UWL));
        builder.addPropertyValue(XMLConstants.LIMIT_LEL, element.getAttribute(XMLConstants.LIMIT_LEL));
        builder.addPropertyValue(XMLConstants.LIMIT_UEL, element.getAttribute(XMLConstants.LIMIT_UEL));

        String description = element.getAttribute(XMLConstants.LIMIT_DESCRIPTION);
        if (description != null) {
            builder.addPropertyValue(XMLConstants.LIMIT_DESCRIPTION,description);
        }
        String refValue = element.getAttribute(XMLConstants.LIMIT_REFVALUE);
        if ((refValue != null) && (!refValue.equals(""))) {
            builder.addPropertyValue(XMLConstants.LIMIT_REFVALUE, refValue);
        }

    }
}
