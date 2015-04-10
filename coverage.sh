mvn clean install -PcoverageCollect -pl '-maven-plugin,-webapp,-simulation-client,-android' -DskipITs=false
if [ $? != 0 ]
	then 
		echo Error while compiling the code: $?
		exit $?
fi
mvn test -PcoverageReport --non-recursive
if [ $? != 0 ]
	then
		echo Error while creating the report: $?
		exit $?
fi