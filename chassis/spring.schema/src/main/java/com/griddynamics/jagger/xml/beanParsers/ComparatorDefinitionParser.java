package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring.StdDevMonitoringParameterDecisionMaker;
import com.griddynamics.jagger.engine.e1.sessioncomparation.workload.ThroughputWorkloadDecisionMaker;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: nmusienko
 * Date: 30.11.12
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
public class ComparatorDefinitionParser implements BeanDefinitionParser{

    private static final String FATAL_DEVIATION_THRESHOLD="fatalDeviationThreshold";
    private static final String WARNING_DEVIATION_THRESHOLD="warningDeviationThreshold";

    private static final String WORKLOAD_DECISION_MAKER="workloadDecisionMaker";
    private static final String MONITORING_PARAMETER_DECISION_MAKER="monitoringParameterDecisionMaker";

    private static final String WORKLOAD_FEATURE_COMPARATOR="workloadFeatureComparator";
    private static final String MONITORING_FEATURE_COMPARATOR="monitoringFeatureComparator";

    private static final String DECISION_MAKER_TYPE="decisionMakerType";
    private static final String DECISION_MAKER_REF="decisionMakerRef";

    private static final String WORKLOAD="workload";
    private static final String MONITORING="monitoring";

    private static final String COMPARATOR_TYPE="comparatorType";

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder=null;
        String type=element.getAttribute(DECISION_MAKER_TYPE);

        if(element.getAttribute(COMPARATOR_TYPE).equals(WORKLOAD)){
            builder=BeanDefinitionBuilder.childBeanDefinition(WORKLOAD_FEATURE_COMPARATOR);
            if(StringUtils.hasText(type)){
                BeanDefinition decisionMaker=getThroughputWorkloadDecisionMaker(element, parserContext);
                builder.addPropertyValue(WORKLOAD_DECISION_MAKER,decisionMaker);
            } else{
                builder.addPropertyReference(WORKLOAD_DECISION_MAKER,element.getAttribute(DECISION_MAKER_REF));
            }
        } else if(element.getAttribute(COMPARATOR_TYPE).equals(MONITORING)){
            builder=BeanDefinitionBuilder.childBeanDefinition(MONITORING_FEATURE_COMPARATOR);
            if(StringUtils.hasText(type)){
                BeanDefinition decisionMaker=getStdDevMonitoringParameterDecisionMaker(element, parserContext);
                builder.addPropertyValue(MONITORING_PARAMETER_DECISION_MAKER,decisionMaker);
            } else{
                builder.addPropertyReference(MONITORING_PARAMETER_DECISION_MAKER,element.getAttribute(DECISION_MAKER_REF));
            }
        }

        return builder.getBeanDefinition();
    }


    private BeanDefinition getStdDevMonitoringParameterDecisionMaker(Element element, ParserContext parserContext){
        BeanDefinitionBuilder builder=BeanDefinitionBuilder.genericBeanDefinition(StdDevMonitoringParameterDecisionMaker.class);
        String fatal=element.getAttribute(FATAL_DEVIATION_THRESHOLD);
        String warning=element.getAttribute(WARNING_DEVIATION_THRESHOLD);
        builder.addPropertyValue(FATAL_DEVIATION_THRESHOLD,fatal);
        builder.addPropertyValue(WARNING_DEVIATION_THRESHOLD, warning);
        return builder.getBeanDefinition();
    }

    private BeanDefinition getThroughputWorkloadDecisionMaker(Element element, ParserContext parserContext){
        BeanDefinitionBuilder builder=BeanDefinitionBuilder.genericBeanDefinition(ThroughputWorkloadDecisionMaker.class);
        String fatal=element.getAttribute(FATAL_DEVIATION_THRESHOLD);
        String warning=element.getAttribute(WARNING_DEVIATION_THRESHOLD);
        builder.addPropertyValue(FATAL_DEVIATION_THRESHOLD,fatal);
        builder.addPropertyValue(WARNING_DEVIATION_THRESHOLD,warning);
        return builder.getBeanDefinition();
    }


}