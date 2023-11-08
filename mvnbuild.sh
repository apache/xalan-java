# Package last to gather source jars into /build
mvn clean site source:jar source:test-jar package

