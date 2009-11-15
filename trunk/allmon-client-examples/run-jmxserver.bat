set JAVAHOME=C:\jdk1.6.0_04\bin\

rem Start the RMI registry:
start %JAVAHOME%\rmiregistry 9999

rem Start the Server (follow the server's execution steps until it prompts you to start the client on a different shell window)
%JAVAHOME%\java -classpath .;bin org.allmon.client.agent.jmxserver.ServerMain

rem Start the Client (on a different shell window)
rem %JAVAHOME%\java -classpath .;bin org.allmon.client.agent.jmxserver.ClientMain
