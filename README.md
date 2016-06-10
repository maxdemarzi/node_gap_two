# node_gap_two

Finding the shortest path between two point nodes that have relationships to more than two other point nodes.

Instructions
------------

1. Build it:

        mvn clean package

2. Copy target/node_gap_two-1.0-SNAPSHOT.jar to the plugins/ directory of your Neo4j server.

3a. Configure Neo4j by adding a line to conf/neo4j-server.properties on versions < 3.0:

        org.neo4j.server.thirdparty_jaxrs_classes=com.maxdemarzi=/v1

3b. Configure Neo4j by adding a line to conf/neo4j.conf on version >= 3.0:

        dbms.unmanaged_extension_classes=com.maxdemarzi=/v1


4. Download and copy additional jar to the plugins/ directory of your Neo4j server.

        wget http://central.maven.org/maven2/org/roaringbitmap/RoaringBitmap/0.6.18/RoaringBitmap-0.6.18.jar

5. Start Neo4j server.

6. Check that it is installed correctly over HTTP:

        :GET /v1/service/helloworld

7. Warm up the database (optional)

        :GET /v1/service/warmup

8. Run it:

        :GET /v1/service/gaps

Results should look like this: