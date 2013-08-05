all:
	mvn eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=true

package:
	mvn package -Dmaven.test.skip=true -Pdev

clean:
	mvn eclipse:clean
	mvn clean
