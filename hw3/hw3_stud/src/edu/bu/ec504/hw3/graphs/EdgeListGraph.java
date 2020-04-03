package edu.bu.ec504.hw3.graphs;

import java.util.ArrayList;

/**
 * Implements an undirected, unweighted graph backed by an ArrayList of edges.
 * @author Ari Trachtenberg
 */

public class EdgeListGraph extends Graph  {
    // CONSTRUCTOR
    /**
     * Constructs a new EdgeListGraph
     */
    public EdgeListGraph() {
        edgeList = new ArrayList<>();
    }

    // METHODS

    // ... QUERY
    /**
     * @inheritDoc
     */
    public boolean isAdjacentQ(Vertex theVertexA, Vertex theVertexB) {
       for (UndirectedEdge undirectedEdge : edgeList)
            if (undirectedEdge.VertexA == theVertexA &&
                    undirectedEdge.VertexB == theVertexB)
                return true;
        return false;
    }


    // ... MANIPULATION
    /**
     * @inheritDoc
     */
    public void addEdge(Vertex vertexA, Vertex vertexB) {
        edgeList.add(new UndirectedEdge(vertexA, vertexB));
        edgeList.add(new UndirectedEdge(vertexB, vertexA));
    }

    // DATA FIELDS

    /** The list of edges for the graph. */
    protected final ArrayList<UndirectedEdge> edgeList;
}