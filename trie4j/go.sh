mvn install -N -DskipTests=true
mvn clean compile -DskipTests=true
mvn javadoc:jar source:jar package deploy -DskipTests=true
