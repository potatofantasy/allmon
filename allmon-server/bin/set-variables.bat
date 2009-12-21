@echo off

echo setting homes...
call set-homes.bat

echo JAVA_HOME variable is set to: %JAVA_HOME%

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe 
goto setVariables

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe
echo JAVA_HOME environment variable is not set!

:setVariables
set CONF=../conf/

set JVM_PROPERTIES=
set JVM_PROPERTIES=%JVM_PROPERTIES% -Dallmon.properties.path=%CONF%/allmon.properties
set JVM_PROPERTIES=%JVM_PROPERTIES% -Dlog4jallmon.properties.path=%CONF%/log4jallmon.properties
set JVM_PROPERTIES=%JVM_PROPERTIES% -Dcom.sun.management.jmxremote
rem for debug
rem set JVM_PROPERTIES=%JVM_PROPERTIES% -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=12346

set CLASS_PATH=
set CLASS_PATH=%CLASS_PATH%%CONF%;
set CLASS_PATH=%CLASS_PATH%../lib/allmon-server.jar;
set CLASS_PATH=%CLASS_PATH%../lib/allmon-client.jar;
set CLASS_PATH=%CLASS_PATH%../lib/cron4j-2.1.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/cglib-nodep-2.2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/commons-collections-3.2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/c3p0-0.9.1.2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/c3p0-oracle-thin-extras-0.9.1.2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/commons-logging-1.1.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/commons-pool-1.3.jar;
set CLASS_PATH=%CLASS_PATH%../lib/dom4j-1.5.jar;
set CLASS_PATH=%CLASS_PATH%../lib/geronimo-jpa_3.0_spec-1.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/geronimo-jta_1.1_spec-1.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/hibernate3.jar;
set CLASS_PATH=%CLASS_PATH%../lib/hibernate-annotations-3.3.1.GA.jar;
set CLASS_PATH=%CLASS_PATH%../lib/hibernate-commons-annotations-3.3.0.ga.jar;
set CLASS_PATH=%CLASS_PATH%../lib/ojdbc14.jar;
set CLASS_PATH=%CLASS_PATH%../lib/spring-2.5.6.jar;
set CLASS_PATH=%CLASS_PATH%../lib/spring-hibernate3-2.0.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/activation-1.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/activemq-camel-5.2.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/activemq-core-5.2.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/activemq-pool-5.2.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/commons-pool-1.4.jar;
set CLASS_PATH=%CLASS_PATH%../lib/geronimo-j2ee-management_1.0_spec-1.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/geronimo-jms_1.1_spec-1.1.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/spring-jms-2.5.5.jar;
set CLASS_PATH=%CLASS_PATH%../lib/spring-tx-2.5.5.jar;
set CLASS_PATH=%CLASS_PATH%../lib/xbean-spring-3.4.jar;
set CLASS_PATH=%CLASS_PATH%../lib/xmlpull-1.1.3.4d_b4_min.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-core-1.6.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-jaxb-1.6.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-jms-1.6.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-jpa-1.6.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-juel-1.6.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-spring-1.6.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-spring-integration-1.6.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/jaxb-api-2.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/jaxb-impl-2.1.6.jar;
set CLASS_PATH=%CLASS_PATH%../lib/stax-api-1.0-2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/log4j-1.2.15.jar;
set CLASS_PATH=%CLASS_PATH%../lib/commons-logging-1.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/activemq-camel-5.2.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/activemq-core-5.2.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/geronimo-j2ee-management_1.0_spec-1.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/geronimo-jms_1.1_spec-1.1.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/activemq-pool-5.2.0.jar;
set CLASS_PATH=%CLASS_PATH%../lib/commons-pool-1.4.jar;
set CLASS_PATH=%CLASS_PATH%../lib/spring-jms-2.5.5.jar;
set CLASS_PATH=%CLASS_PATH%../lib/spring-tx-2.5.5.jar;
set CLASS_PATH=%CLASS_PATH%../lib/xbean-spring-3.4.jar;
set CLASS_PATH=%CLASS_PATH%../lib/xmlpull-1.1.3.4d_b4_min.jar;
set CLASS_PATH=%CLASS_PATH%../lib/commons-logging-1.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/spring-2.5.6.jar;
set CLASS_PATH=%CLASS_PATH%../lib/servlet-api-5.0.16.jar;
set CLASS_PATH=%CLASS_PATH%../lib/jaxb-impl-2.1.6.jar;
set CLASS_PATH=%CLASS_PATH%../lib/stax-api-1.0-2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/activation-1.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/commons-collections-3.2.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/jaxb-api-2.1.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-test-2.0-M2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-core-2.0-M2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-jaxb-2.0-M2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-jms-2.0-M2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-jpa-2.0-M2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-juel-2.0-M2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-spring-2.0-M2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/camel-spring-integration-2.0-M2.jar;
set CLASS_PATH=%CLASS_PATH%../lib/junit-4.4.jar;
set CLASS_PATH=%CLASS_PATH%../lib/log4j-1.2.15.jar;
set CLASS_PATH=%CLASS_PATH%../lib/cron4j-2.1.1.jar

echo _JAVACMD variable is set to: %_JAVACMD%
echo CLASS_PATH variable is set to: %CLASS_PATH%

@echo on