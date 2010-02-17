@echo off

REM ------------------------------------------------------------------------
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM
REM http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.
REM ------------------------------------------------------------------------

SET ALLMON_ACTIVEMQ_HOME=C:\apache-activemq-5.2.0\
SET ALLMON_HOME=C:\allmon-0.1.3-snmp-build4\


echo starting ActiveMQ
cd %ALLMON_ACTIVEMQ_HOME%\bin
start activemq.bat

echo starting allmon-client
cd %ALLMON_HOME%\allmon-client\bin
start allmon-client.bat

echo starting allmon-server
cd %ALLMON_HOME%\allmon-server\bin
start allmon-server.bat

:end
@echo on