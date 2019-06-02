# Tarentule

mvn assembly:assembly

from Tarentule/ :
java -jar reprise/target/Tarentule-jar-with-dependencies.jar -P 8081 -F load.csv

addExternalNode localhost:8081

http://localhost:8080/test/index/find?query=SELECT AVG(passenger_count) WHERE (store_and_fwd_flag = M)&beginning=2&ending=10001
