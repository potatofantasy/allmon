AspectJ AOP Configuration
-------------------------
AspectJ comes with 
1.  Compile Time Weaving
2.  Post Compile Weaving
3.  Load Time Weaving (LTW)

This explains load time weaving configuration on eclipse, OC4J and JBoss

Eclipse Configuration
1.  Develop the necessary Pointcut regular expression and apply it to the Pointcut
2.  Write an aop.xml file which includes information for the LTW agent to pick necessary Advice and Advice Targets and place it under a META-INF directory
3.  Write the controller/main/entry point to the application and run with the following VM arguments –javaagent:absolute_path_to_/aspectjweaver.jar
4.  The folder having META-INF should be available on the classpath. This can be done by open the run configuration and adding the containing folder to the classpath.

OC4J Configuration
To be updated

JBOSS Configuration
To be updated
