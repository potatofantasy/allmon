cd allmon-client-agent-active
cmd /C mvn clean package appassembler:assemble install
cd ..
cd allmon-client-aggregator
cmd /C mvn clean package appassembler:assemble install
cd ..
cd allmon-server
cmd /C mvn clean package appassembler:assemble install
cd ..
cd allmon-server-evaluator
cmd /C mvn clean package appassembler:assemble install
cd ..
