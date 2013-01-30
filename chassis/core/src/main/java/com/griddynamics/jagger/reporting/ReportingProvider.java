package com.griddynamics.jagger.reporting;

import org.springframework.context.ApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 1/30/13
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportingProvider {
    public static ReportingService getService(ApplicationContext context){
        Object customReporting = context.getBean("customReportingService");
        if (customReporting != null){
            return (ReportingService) customReporting;
        }else{
            return (ReportingService)context.getBean("defaultReportingService");
        }
    }
}
