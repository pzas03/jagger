package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.user.ProcessingConfig;
import junit.framework.Assert;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

public class JaggerNamespaceHandlerTest {

    @Test
    public void invocationTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-target-session1_new_user.conf.xml1");
        ProcessingConfig.Test.Task.Invocation invocation = (ProcessingConfig.Test.Task.Invocation)ctx.getBean("i1");
        Assert.assertEquals(2, (int) invocation.getThreads());
        Assert.assertEquals(5, (int) invocation.getExactcount());
    }

    @Test
    public void taskUsersTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-target-session1_new_user.conf.xml1");
        ProcessingConfig.Test.Task task = (ProcessingConfig.Test.Task)ctx.getBean("t1");
        Assert.assertEquals("task1", task.getName());
        Assert.assertEquals(34250,  (int)task.getSample());
        Assert.assertEquals(0,      (int)task.getDelay());
        Assert.assertEquals("bean1", task.getBean());
        Assert.assertEquals("200",    task.getDuration());
        Assert.assertEquals(3, task.getUsers().size());
    }

    @Test
    public void testTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-target-session1_new_user.conf.xml1");
        ProcessingConfig.Test test = (ProcessingConfig.Test)ctx.getBean("tst1");
        Assert.assertEquals("2h", test.getDuration());
        Assert.assertEquals(2, test.getTasks().size());
    }

    @Test
    public void taskInvocationTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-target-session1_new_user.conf.xml1");
        ProcessingConfig.Test.Task task = (ProcessingConfig.Test.Task)ctx.getBean("t2");
        Assert.assertEquals("task2", task.getName());
        Assert.assertEquals(24250,  (int)task.getSample());
        Assert.assertEquals(10,      (int)task.getDelay());
        Assert.assertEquals("bean2", task.getBean());
        Assert.assertEquals("300",    task.getDuration());
        Assert.assertNotNull(task.getInvocation());
        Assert.assertTrue(task.getInvocation() instanceof ProcessingConfig.Test.Task.Invocation);
    }

    @Test
    public void userTest() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/example-test-target-session1_new_user.conf.xml1");
        ProcessingConfig.Test.Task.User user = (ProcessingConfig.Test.Task.User)ctx.getBean("u1");
        Assert.assertEquals("50", user.getCount());
        Assert.assertEquals("2h", user.getLife());
        Assert.assertEquals("0", user.getStartIn());
        Assert.assertEquals("0", user.getStartBy());
    }
}
