package com.griddynamics.jagger.xml.beanParsers.workload;

import com.griddynamics.jagger.engine.e1.collector.InformationCollectorProvider;
import com.griddynamics.jagger.engine.e1.scenario.OneNodeCalibrator;
import com.griddynamics.jagger.engine.e1.scenario.SkipCalibration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.xml.beanParsers.CustomBeanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.XMLConstants;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/21/13
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkloadDefinitionParser extends CustomBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return WorkloadTask.class;
    }

    @Override
    protected void parse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyValue(XMLConstants.DESCRIPTION, element.getAttribute(XMLConstants.ID));

        ManagedList collectors = new ManagedList();
        collectors.setMergeEnabled(true);

        BeanDefinitionBuilder informationCollector = BeanDefinitionBuilder.genericBeanDefinition(InformationCollectorProvider.class);

        //add user's collectors
        Element listenersGroup = DomUtils.getChildElementByTagName(element, XMLConstants.WORKLOAD_LISTENERS_ELEMENT);
        if (listenersGroup != null){
            List<Element> metricElements = DomUtils.getChildElementsByTagName(listenersGroup, XMLConstants.METRIC);

            ManagedList metrics = parseCustomElements(metricElements, parserContext, builder.getBeanDefinition());
            if (metrics!=null)
                collectors.addAll(metrics);

            List<Element> validatorsElements = DomUtils.getChildElementsByTagName(listenersGroup, XMLConstants.VALIDATOR);
            if (validatorsElements!= null)
                informationCollector.addPropertyValue(XMLConstants.VALIDATORS, parseCustomElements(validatorsElements, parserContext, builder.getBeanDefinition()));
        }

        if (builder.getBeanDefinition().getParentName() == null){
            for (String standardCollector : XMLConstants.STANDARD_WORKLOAD_LISTENERS){
                collectors.add(new RuntimeBeanReference(standardCollector));
            }
        }

        collectors.add(informationCollector.getBeanDefinition());


        builder.addPropertyValue(XMLConstants.WORKLOAD_LISTENERS_CLASS, collectors);

        //add scenario
        Element scenarioElement = DomUtils.getChildElementByTagName(element, XMLConstants.SCENARIO);
        setBeanProperty(XMLConstants.SCENARIO_FACTORY, scenarioElement, parserContext, builder.getBeanDefinition());
    }

    @Override
    protected void preParseAttributes(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        Boolean calibration = false;
        if (!element.getAttribute(XMLConstants.CALIBRATION).isEmpty())
            calibration = Boolean.parseBoolean(element.getAttribute(XMLConstants.CALIBRATION));
        element.removeAttribute(XMLConstants.CALIBRATION);

        if (calibration)
            builder.addPropertyValue(XMLConstants.CALIBRATOR, new OneNodeCalibrator());
        else
            builder.addPropertyValue(XMLConstants.CALIBRATOR, new SkipCalibration());
    }
}
