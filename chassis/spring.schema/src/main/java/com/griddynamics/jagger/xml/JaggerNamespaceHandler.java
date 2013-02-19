package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.xml.beanParsers.*;
import com.griddynamics.jagger.xml.beanParsers.configuration.*;
import com.griddynamics.jagger.xml.beanParsers.report.*;
import com.griddynamics.jagger.xml.beanParsers.task.*;
import com.griddynamics.jagger.xml.beanParsers.workload.WorkloadDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.balancer.OneByOneBalancerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.balancer.RoundRobinBalancerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.calibration.DefaultCalibratorDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.ClassInvokerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.HttpInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.SoapInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.ConsistencyDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.NotNullResponseDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.SimpleMetricDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.queryProvider.HttpQueryDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.scenario.QueryPoolScenarioDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class JaggerNamespaceHandler extends NamespaceHandlerSupport {

    private FindParserByTypeDefinitionParser findTypeParser = new FindParserByTypeDefinitionParser();
    private ListCustomDefinitionParser listCustomDefinitionParser = new ListCustomDefinitionParser();
    private MapCustomDefinitionParser mapCustomDefinitionParser = new MapCustomDefinitionParser();
    private PrimitiveDefinitionParser primitiveParser = new PrimitiveDefinitionParser();

    public void init() {

        //CONFIGURATION
        registerBeanDefinitionParser("configuration", new ConfigDefinitionParser());
        registerBeanDefinitionParser("test-suite", listCustomDefinitionParser);
        registerBeanDefinitionParser("test-group", new TestDefinitionParser());
        registerBeanDefinitionParser("newTestGroup" , new TestDefinitionParser());
        registerBeanDefinitionParser("percentiles-time", listCustomDefinitionParser);
        registerBeanDefinitionParser("percentiles-global", listCustomDefinitionParser);
        registerBeanDefinitionParser("percentile", primitiveParser);

        //REPORT
        registerBeanDefinitionParser("report", new ReportDefinitionParser());
        registerBeanDefinitionParser("processing", new TestPlanDefinitionParser());
        registerBeanDefinitionParser("extension", new ExtensionDefinitionParser());
        registerBeanDefinitionParser("extensions", new ExtensionsDefinitionParser());
        registerBeanDefinitionParser("comparator", new ComparatorDefinitionParser());
        registerBeanDefinitionParser("sessionComparators", new SessionComparatorsDefinitionParser());

        //TASKS
        registerBeanDefinitionParser("test", new TaskDefinitionParser());

        //type of tasks
        registerBeanDefinitionParser("invocation", new InvocationDefinitionParser());
        registerBeanDefinitionParser("user", new UserDefinitionParser());
        registerBeanDefinitionParser("tps", new TpsDefinitionParser());
        registerBeanDefinitionParser("virtual-user", new VirtualUserDefinitionParser());

        //Test-description
        registerBeanDefinitionParser("test-description" , new WorkloadDefinitionParser());

        //listeners

        //validator
        registerBeanDefinitionParser("validator", findTypeParser);

        //validators listeners
        registerBeanDefinitionParser("notNullResponse", new NotNullResponseDefinitionParser());
        registerBeanDefinitionParser("consistency", new ConsistencyDefinitionParser());

        //metric
        registerBeanDefinitionParser("metric", findTypeParser);

        //metric calculators
        registerBeanDefinitionParser("simpleMetric", new SimpleMetricDefinitionParser());

        //scenario
        registerBeanDefinitionParser("scenario",  findTypeParser);

        //scenarios
        registerBeanDefinitionParser("queryPoolScenario", new QueryPoolScenarioDefinitionParser());

        //balancer
        registerBeanDefinitionParser("loadBalancer", findTypeParser);

        //balancers
        registerBeanDefinitionParser("roundRobinLoadBalancer", new RoundRobinBalancerDefinitionParser());
        registerBeanDefinitionParser("oneByOneLoadBalancer", new OneByOneBalancerDefinitionParser());

        //invoker
        registerBeanDefinitionParser("invoker", findTypeParser);

        //invokers
        registerBeanDefinitionParser("httpInvoker", new HttpInvokerClassDefinitionParser());
        registerBeanDefinitionParser("soapInvoker", new SoapInvokerClassDefinitionParser());
        registerBeanDefinitionParser("invokerClass", new ClassInvokerDefinitionParser());

        //endpointProvider
        registerBeanDefinitionParser("endpointProvider", findTypeParser);
        registerBeanDefinitionParser("endpoint", primitiveParser);

        //endpointProviders
        registerBeanDefinitionParser("simpleEndpointProvider", listCustomDefinitionParser);

        //queryProvider
        registerBeanDefinitionParser("queryProvider", findTypeParser);

        //queryProviders
        registerBeanDefinitionParser("simpleQueryProvider", listCustomDefinitionParser);

        //queries
        registerBeanDefinitionParser("query", findTypeParser);
        registerBeanDefinitionParser("httpQuery", new HttpQueryDefinitionParser());
        registerBeanDefinitionParser("clientParams", mapCustomDefinitionParser);
        registerBeanDefinitionParser("methodParams", mapCustomDefinitionParser);

        //calibrators
        registerBeanDefinitionParser("calibrator", findTypeParser);
        registerBeanDefinitionParser("defaultCalibrator", new DefaultCalibratorDefinitionParser());

        //load
        registerBeanDefinitionParser("load",  findTypeParser);
        registerBeanDefinitionParser("newTest",  new TaskDefinitionParser());
        registerBeanDefinitionParser("userGroups", new UserGroupsDefinitionParser());
        registerBeanDefinitionParser("userGroup", new UserGroupDefinitionParser());

        //termination strategy
        registerBeanDefinitionParser("termination",  findTypeParser);
        registerBeanDefinitionParser("iterationsOrDuration", new IterationsOrDurationTerminationStrategyDefinitionParser());
        registerBeanDefinitionParser("background", new BackgroundTerminationStrategyDefinitionParser());
    }
}
