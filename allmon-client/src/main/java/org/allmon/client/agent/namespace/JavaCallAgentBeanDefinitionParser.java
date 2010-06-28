package org.allmon.client.agent.namespace;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.allmon.client.agent.advices.JavaCallAdvice;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJPointcutAdvisor;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.aop.config.AspectComponentDefinition;
import org.springframework.aop.config.MethodLocatingFactoryBean;
import org.springframework.aop.config.PointcutComponentDefinition;
import org.springframework.aop.config.SimpleBeanFactoryAwareAspectInstanceFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class JavaCallAgentBeanDefinitionParser extends AbstractPassiveAgentBeanDefinitionParser {
	
	private static final String POINTCUT_EXPRESSION = "pointcutExpression";
	
	private static final String EXPRESSION = "expression";
	
	public JavaCallAgentBeanDefinitionParser(PassiveAgentBeanDefinitionParser parser) {
		super(parser);
	}

	protected void parseSpecifics(Element agentElement, ParserContext parserContext) {
		// add agentContext to advice
		String agentContextName = (String)parseAgentContextProperty(agentElement, parserContext);
		Object agentContext = parserContext.getRegistry().getBeanDefinition(agentContextName);

		Method aopRegistration;
		// Try Spring 2.5 signature, if method not found try for older framework version
		try {
			aopRegistration = AopNamespaceUtils.class.getMethod("registerAspectJAutoProxyCreatorIfNecessary", ParserContext.class, Element.class);
		} catch (NoSuchMethodException e) {
			// Spring 2.0 signature
			try {
				aopRegistration = AopNamespaceUtils.class.getMethod("registerAspectJAutoProxyCreatorIfNecessary", ParserContext.class, Object.class);
			} catch (NoSuchMethodException e2) {
				throw new org.springframework.beans.factory.BeanDefinitionStoreException(
						"Can not find method AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary in this Spring disctribution");
			}
		}
		try {
			aopRegistration.invoke(null, parserContext, agentElement);
		} catch (Exception e) {
			throw new BeanDefinitionStoreException(
					"Can not activate AOP proxies", e);
		}
		
		// TODO generate names
		String aspectId = "someAspect"; 
		String aspectName = "monitoringAdvice";
		String pointcutBeanName = "methodJoinPoint";
		
		// create advice
		RootBeanDefinition agentAdviceDef = new RootBeanDefinition(JavaCallAdvice.class);
		agentAdviceDef.getPropertyValues().addPropertyValue("agentContext", agentContext);
		parserContext.getRegistry().registerBeanDefinition(aspectName, agentAdviceDef);
		
		// create aspect 
		//List beanDefinitions = new ArrayList();
		//List beanReferences = new ArrayList();
		//beanReferences.add(new RuntimeBeanReference(aspectName)); // ???
		
		// create the method factory bean
		RootBeanDefinition methodDefinition = new RootBeanDefinition(MethodLocatingFactoryBean.class);
		methodDefinition.getPropertyValues().addPropertyValue("targetBeanName", aspectName);
		methodDefinition.getPropertyValues().addPropertyValue("methodName", "profile");
		methodDefinition.setSynthetic(true);
		
		// create instance factory definition
		RootBeanDefinition aspectFactoryDefinition = new RootBeanDefinition(SimpleBeanFactoryAwareAspectInstanceFactory.class);
		aspectFactoryDefinition.getPropertyValues().addPropertyValue("aspectBeanName", aspectName);
		aspectFactoryDefinition.setSynthetic(true);
		
		// register the pointcut
		RootBeanDefinition adviceDefinition = new RootBeanDefinition(AspectJAroundAdvice.class);
		adviceDefinition.getPropertyValues().addPropertyValue("aspectName", aspectName);
		adviceDefinition.getPropertyValues().addPropertyValue("declarationOrder", 5);
		ConstructorArgumentValues cav = adviceDefinition.getConstructorArgumentValues();
		cav.addIndexedArgumentValue(0, methodDefinition);
		RuntimeBeanReference pointcutRef = new RuntimeBeanReference((String) pointcutBeanName);
		cav.addIndexedArgumentValue(1, pointcutRef);
		//beanReferences.add(pointcutRef);
		cav.addIndexedArgumentValue(2, aspectFactoryDefinition);

		// configure the advisor - create the advisor 
		RootBeanDefinition advisorDefinition = new RootBeanDefinition(AspectJPointcutAdvisor.class);
		advisorDefinition.getConstructorArgumentValues().addGenericArgumentValue(adviceDefinition);
		// register the final advisor
		parserContext.getReaderContext().registerWithGeneratedName(advisorDefinition);
		//beanDefinitions.add(advisorDefinition);
		
		// create an aspect definition
//		BeanDefinition[] beanDefArray = (BeanDefinition[]) beanDefinitions.toArray(new BeanDefinition[beanDefinitions.size()]); 
//		BeanReference[] beanRefArray = (BeanReference[]) beanReferences.toArray(new BeanReference[beanReferences.size()]); 
//		//parserContext.pushContainingComponent(new AspectComponentDefinition(aspectId, beanDefArray, beanRefArray, null));

		// create pointcut
		RootBeanDefinition pointcutDefinition = new RootBeanDefinition(AspectJExpressionPointcut.class);
		pointcutDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		pointcutDefinition.setSynthetic(true);
		String pointcutExpression = parsePropertyString(agentElement, parserContext, POINTCUT_EXPRESSION);
		pointcutDefinition.getPropertyValues().addPropertyValue(EXPRESSION, pointcutExpression);
		
		parserContext.getRegistry().registerBeanDefinition(pointcutBeanName, pointcutDefinition);
		
		parserContext.registerComponent(
				new PointcutComponentDefinition(pointcutBeanName, pointcutDefinition, pointcutExpression));
		
		//parserContext.popAndRegisterContainingComponent();
		
	}
	
}
