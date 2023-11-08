rem package last to gather source jars into build/
call mvn clean site source:jar source:test-jar package

