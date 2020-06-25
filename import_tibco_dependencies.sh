mvn install:install-file -Dfile=./lib/jms-2.0.jar -DgroupId=javax.jms -DartifactId=javax.jms-api -Dpackaging=jar -Dversion=2.0 -DgeneratePom=true
mvn install:install-file -Dfile=./lib/tibjms.jar -DgroupId=com.tibco.tibjms -DartifactId=tibjms -Dpackaging=jar -Dversion=8.5.1 -DgeneratePom=true
mvn install:install-file -Dfile=./lib/tibjmsadmin.jar -DgroupId=com.tibco.tibjms -DartifactId=tibjmsadmin -Dpackaging=jar -Dversion=8.5.1 -DgeneratePom=true
