mvn clean process-classes
if [ $? != 0 ]
	then 
		echo Error while compiling the code: $?
		exit $?
fi
ant instrument
if [ $? != 0 ]
	then
		echo Error while instrumenting the code: $?
		exit $?
fi
mvn verify -DskipITs=false -Pcoverage
if [ $? != 0 ]
	then
		echo Error while running the tests: $?
		exit $?
fi
ant report
if [ $? != 0 ]
	then
		echo Error while creating the report: $?
		exit $?
fi