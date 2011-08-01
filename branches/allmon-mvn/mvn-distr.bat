cd allmon-client-agent-active
cmd /C mvn clean appassembler:assemble install
cd ..
cd allmon-client-aggregator
cmd /C mvn clean appassembler:assemble install
cd ..
cd allmon-server
cmd /C mvn clean appassembler:assemble install
cd ..
