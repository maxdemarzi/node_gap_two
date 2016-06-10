package com.maxdemarzi;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.test.server.HTTP;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class GapsTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withFixture(MODEL_STATEMENT)
            .withExtension("/v1", Service.class);

    public static final String MODEL_STATEMENT =
            new StringBuilder()
                    .append("CREATE (p1:Point {id: 'p1'})")
                    .append("CREATE (p2:Point {id: 'p2'})")
                    .append("CREATE (p3:Point {id: 'p3'})")
                    .append("CREATE (p4:Point {id: 'p4'})")
                    .append("CREATE (p5:Point {id: 'p5'})")
                    .append("CREATE (p6:Point {id: 'p6'})")
                    .append("CREATE (p7:Point {id: 'p7'})")
                    .append("CREATE (p8:Point {id: 'p8'})")
                    .append("CREATE (p9:Point {id: 'p9'})")

                    .append("CREATE (p1)-[:CONNECTED]->(p3)")
                    .append("CREATE (p3)-[:CONNECTED]->(p2)")
                    .append("CREATE (p3)-[:CONNECTED]->(p8)")
                    .append("CREATE (p3)-[:CONNECTED]->(p4)")
                    .append("CREATE (p4)-[:CONNECTED]->(p5)")
                    .append("CREATE (p5)-[:CONNECTED]->(p6)")
                    .append("CREATE (p6)-[:CONNECTED]->(p7)")
                    .append("CREATE (p9)-[:CONNECTED]->(p6)")

                    //       p1
                    // p2    p3    p4   p5  p6   p7
                    //       p8             p9

                    .toString();

    @Test
    public void shouldRespondToGetGapsMethod() {
        HTTP.Response response = HTTP.GET(neo4j.httpURI().resolve("/v1/service/gaps").toString());
        ArrayList actual = response.content();
        System.out.println(actual);
        assertTrue(actual.equals(expected));
    }

    private static final ArrayList<ArrayList<HashMap<String, Object>>> expected = new ArrayList<ArrayList<HashMap<String, Object>>>() {{
        add(new ArrayList<HashMap<String, Object>>() {{
            add (new HashMap<String, Object>() {{
                put("id", "p3");

            }});
            add (new HashMap<String, Object>() {{
                put("id", "p4");

            }});
            add (new HashMap<String, Object>() {{
                put("id", "p5");

            }});
            add (new HashMap<String, Object>() {{
                put("id", "p6");

            }});
        }
    });
    }};
}