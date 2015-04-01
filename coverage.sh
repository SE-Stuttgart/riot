mvn clean install -Pcoverage -pl '-maven-plugin,-webapp,-simulation-client,-android'
if [ $? != 0 ]
	then 
		echo Error while compiling the code: $?
		exit $?
fi
ant report
if [ $? != 0 ]
	then
		echo Error while creating the report: $?
		exit $?
fi