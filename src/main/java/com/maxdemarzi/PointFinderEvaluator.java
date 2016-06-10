package com.maxdemarzi;

import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

public class PointFinderEvaluator implements Evaluator {
    @Override
    public Evaluation evaluate(Path path) {
        if (path.length() > 1) {
            if (Service.endPoints.contains((int) path.endNode().getId())) {
                return Evaluation.INCLUDE_AND_PRUNE;
            }
        }
        return Evaluation.EXCLUDE_AND_CONTINUE;
    }

}
