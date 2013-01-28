package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.xml.beanParsers.*;
import com.griddynamics.jagger.xml.beanParsers.configuration.ConfigDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.configuration.TaskDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.configuration.TestDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.configuration.TestPlanDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.report.*;
import com.griddynamics.jagger.xml.beanParsers.task.InvocationDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.TpsDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.UserDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.task.VirtualUserDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.WorkloadDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.balancer.OneByOneBalancerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.balancer.RoundRobinBalancerDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.endpointProvider.EndpointDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.endpointProvider.SimpleEndpointProviderDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.HttpInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.invoker.SoapInvokerClassDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.NotNullResponseDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.listener.SimpleMetricDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.queryProvider.HttpQueryDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.queryProvider.SimpleQueryProviderDefinitionParser;
import com.griddynamics.jagger.xml.beanParsers.workload.scenario.QueryPoolScenarioDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class JaggerNamespaceHandler extends NamespaceHandlerSupport {

    private FindParserByTypeDefinitionParser findTypeParser = new FindParserByTypeDefinitionParser();

    public void init() {

        registerBeanDefinitionParser("invocation", new InvocationDefinitionParser());
        registerBeanDefinitionParser("task", new TaskDefinitionParser());
        registerBeanDefinitionParser("test", new TestDefinitionParser());
        registerBeanDefinitionParser("user", new UserDefinitionParser());
        registerBeanDefinitionParser("test-plan", new TestPlanDefinitionParser());
        registerBeanDefinitionParser("processing", new TestPlanDefinitionParser());
        registerBeanDefinitionParser("extension", new ExtensionDefinitionParser());
        registerBeanDefinitionParser("extensions", new ExtensionsDefinitionParser());
        registerBeanDefinitionParser("comparator", new ComparatorDefinitionParser());
        registerBeanDefinitionParser("sessionComparators", new SessionComparatorsDefinitionParser());
        registerBeanDefinitionParser("report", new ReportDefinitionParser());
        registerBeanDefinitionParser("tps", new TpsDefinitionParser());
        registerBeanDefinitionParser("virtual-user", new VirtualUserDefinitionParser());
        registerBeanDefinitionParser("configuration", new ConfigDefinitionParser());

        //WORKLOAD
        registerBeanDefinitionParser("workload" , new WorkloadDefinitionParser());

        //listeners

        //validator
        registerBeanDefinitionParser("validator", findTypeParser);

        //validators listeners
        registerBeanDefinitionParser("notNullResponse", new NotNullResponseDefinitionParser());

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

        //endpointProvider
        registerBeanDefinitionParser("endpointProvider", findTypeParser);

        //endpointProviders
        registerBeanDefinitionParser("simpleEndpointProvider", new SimpleEndpointProviderDefinitionParser());
        registerBeanDefinitionParser("endpoint", new EndpointDefinitionParser());

        //queryProvider
        registerBeanDefinitionParser("queryProvider", findTypeParser);

        //queryProviders
        registerBeanDefinitionParser("simpleQueryProvider", new SimpleQueryProviderDefinitionParser());

        //queries
        registerBeanDefinitionParser("query", findTypeParser);
        registerBeanDefinitionParser("httpQuery", new HttpQueryDefinitionParser());

    }
}
