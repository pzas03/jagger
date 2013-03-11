package com.griddynamics.jagger.xml.beanParsers.workload.listener;

import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 11.03.13
 * Time: 12:48
 * To change this template use File | Settings | File Templates.
 */
public class CustomValidatorDefinitionParser extends AbstractValidatorDefinitionParser {

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyReference(XMLConstants.VALIDATOR, element.getAttribute(XMLConstants.VALIDATOR));
    }
}
