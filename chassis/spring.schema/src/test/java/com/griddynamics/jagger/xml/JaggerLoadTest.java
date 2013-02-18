package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.engine.e1.scenario.*;
import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.user.TestConfiguration;
import com.griddynamics.jagger.xml.beanParsers.task.IterationsOrDurationTerminationStrategyDefinitionParser;
import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/14/12
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class JaggerLoadTest {

    private ApplicationContext context;

    @BeforeClass
    public void testContext(){
        context = new ClassPathXmlApplicationContext("/example-new-tps-invocation.conf.xml1");
    }

    @Test
    public void testTpsTask(){
        TpsClockConfiguration tpsClock = (TpsClockConfiguration)context.getBean("tps1");
        Assert.assertEquals(tpsClock.getTickInterval(), 1000);
        Assert.assertEquals(tpsClock.getTps(), 200d);

        tpsClock = (TpsClockConfiguration)context.getBean("tps2");
        Assert.assertEquals(tpsClock.getTickInterval(), 500);
        Assert.assertEquals(tpsClock.getTps(), 100d);
    }

    @Test
    public void testInvocationTask(){
        ExactInvocationsClockConfiguration invClock = (ExactInvocationsClockConfiguration)context.getBean("inv1");
        Assert.assertEquals(invClock.getTickInterval(), 1000);
        Assert.assertEquals(invClock.getSamplesCount(), 100);
        Assert.assertEquals(invClock.getThreadCount(), 1);
        Assert.assertEquals(invClock.getDelay(), 100);

        invClock = (ExactInvocationsClockConfiguration)context.getBean("inv2");
        Assert.assertEquals(invClock.getTickInterval(), 500);
        Assert.assertEquals(invClock.getSamplesCount(), 50);
        Assert.assertEquals(invClock.getThreadCount(), 2);
        Assert.assertEquals(invClock.getDelay(), 0);

    }

    @Test
    public void testTerminationStrategy(){
        IterationsOrDurationStrategyConfiguration conf = (IterationsOrDurationStrategyConfiguration) context.getBean("ts1");
        Assert.assertEquals(conf.getIterations(), -1);
        Assert.assertEquals(conf.getDuration(), "2h");

        conf = (IterationsOrDurationStrategyConfiguration) context.getBean("ts2");
        Assert.assertEquals(conf.getIterations(), 5);
        Assert.assertEquals(conf.getDuration(), "3h");

        InfiniteTerminationStrategyConfiguration  conf1 = (InfiniteTerminationStrategyConfiguration) context.getBean("ts3");

    }

/*    @Test
    public void testNewTest(){
        TestConfiguration testConfiguration = (TestConfiguration) context.getBean("tst1");

    }*/
}
