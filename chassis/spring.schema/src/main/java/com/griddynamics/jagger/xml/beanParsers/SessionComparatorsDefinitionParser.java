package com.griddynamics.jagger.xml.beanParsers;

import com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider;
import com.griddynamics.jagger.engine.e1.sessioncomparation.ConfigurableSessionComparator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: nmusienko
 * Date: 30.11.12
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class SessionComparatorsDefinitionParser  extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return ConfigurableSessionComparator.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        ManagedList<BeanDefinition> chain=new ManagedList<BeanDefinition>();

        for(Element el: DomUtils.getChildElementsByTagName(element, "comparator")){
            chain.add(parserContext.getDelegate().parseCustomElement(el));
        }

        builder.addPropertyValue("comparatorChain", chain);

        String decisionMaker=element.getAttribute("strategy");
        if(StringUtils.hasText(decisionMaker)){
            builder.addPropertyReference("decisionMaker", getDecisionMaker(decisionMaker));
        }

        String baselineSessionId=element.getAttribute("baselineId");
        if(StringUtils.hasText(baselineSessionId)){
            registerBaselineSessionProvider(baselineSessionId, parserContext, builder);
        }

        element.setAttribute("id", "customSessionComparator");
        try{
            parserContext.getRegistry().removeAlias("sessionComparator");
        }   catch (Exception e){
            //nothing interesting
        }
        parserContext.getRegistry().registerAlias("customSessionComparator","sessionComparator");

    }

    private String getDecisionMaker(String decisionMaker) {
        if(decisionMaker.equals("worstCase")){
            return "worstCaseDecisionMaker";
        }
        return null;
    }

    private void registerBaselineSessionProvider(String baselineSessionId, ParserContext parserContext, BeanDefinitionBuilder builder) {
        BeanDefinitionBuilder provider=BeanDefinitionBuilder.rootBeanDefinition(BaselineSessionProvider.class);
        provider.addPropertyValue("sessionId",baselineSessionId);
        provider.addPropertyReference("sessionIdProvider","sessionIdProvider");
        parserContext.getRegistry().registerBeanDefinition("customBaselineSessionProvider",provider.getBeanDefinition());
        try{
            parserContext.getRegistry().removeAlias("baselineSessionProvider");
        } catch (Exception e){
            //nothing interesting
        }
        parserContext.getRegistry().registerAlias("customBaselineSessionProvider","baselineSessionProvider");
    }
}
