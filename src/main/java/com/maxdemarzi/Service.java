package com.maxdemarzi;

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.roaringbitmap.IntConsumer;
import org.roaringbitmap.RoaringBitmap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Path("/service")
public class Service {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final  RoaringBitmap endPoints = new RoaringBitmap();

    public Service(@Context GraphDatabaseService db) {
        if (endPoints.isEmpty()) {
            try (Transaction tx = db.beginTx()) {
                for (Node n : db.getAllNodes()) {
                    int node_id = ((Number) n.getId()).intValue();
                    if (n.getDegree(RelationshipTypes.CONNECTED) > 2) {
                        endPoints.add(node_id);
                    }
                }
                tx.success();
            }
        }
    }

    @GET
    @Path("/helloworld")
    public Response helloWorld() throws IOException {
        Map<String, String> results = new HashMap<String,String>(){{
            put("hello","world");
        }};
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/warmup")
    public Response warmUp(@Context GraphDatabaseService db) throws IOException {
        Map<String, String> results = new HashMap<String,String>(){{
            put("warmed","up");
        }};

        try (Transaction tx = db.beginTx()) {
            for (Node n : db.getAllNodes()) {
                n.getPropertyKeys();
                for (Relationship relationship : n.getRelationships()) {
                    relationship.getPropertyKeys();
                    relationship.getStartNode();
                }
            }

            for (Relationship relationship : db.getAllRelationships()) {
                relationship.getPropertyKeys();
                relationship.getNodes();
            }
        }

        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

    @GET
    @Path("/gaps")
    public Response gaps(@Context GraphDatabaseService db) throws IOException {

        ArrayList<ArrayList<Object>> results = new ArrayList<>();
        ArrayList<Long> skip = new ArrayList<>();

        try (Transaction tx = db.beginTx()) {
            TraversalDescription pointFinder = db.traversalDescription()
                    .breadthFirst()
                    .expand(PathExpanders.forType(RelationshipTypes.CONNECTED))
                    .uniqueness(Uniqueness.NODE_PATH)
                    .evaluator(new PointFinderEvaluator());

            endPoints.forEach((IntConsumer) value -> {
                if (!skip.contains((long)value)) {
                    Node endPoint = db.getNodeById(value);
                    for (org.neo4j.graphdb.Path position : pointFinder.traverse(endPoint)) {
                        ArrayList<Object> result = new ArrayList<>();

                        Iterator<Node> iter = position.nodes().iterator();
                        while (iter.hasNext()) {
                            Node node = iter.next();
                            result.add(node.getAllProperties());
                        }
                        results.add(result);
                        skip.add(position.endNode().getId());
                        break;
                    }
                }
            });

            tx.success();
        }

        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();

    }

}
