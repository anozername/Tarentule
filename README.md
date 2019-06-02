# Tarentule

mvn assembly:assembly
output in reprise/target/*.jar
java -jar Tarentule-jar-with-dependencies.jar

http://localhost:8080/test/index/find?query=SELECT AVG(passenger_count) WHERE (store_and_fwd_flag = M)&beginning=2&ending=10001
