package edu.bu.ec504.hw3.graphs;

import java.util.ArrayList;

/**
 * Represents an undirected, unweighted graph.
 * @author Ari Trachtenberg
 */

public abstract class Graph {

    // SUBCLASSES
    /**
     * One vertex with a given ID.
     */
    public class Vertex {
        private Vertex(int ID) {
            if (ID > numVertices()+1) throw new IndexOutOfBoundsException(Integer.toString(ID));
            else myID = ID;
        }
        public String toString() { return "[Vertex #"+myID+"]"; }
        final public int myID;
    }

    /**
     * One edge between two vertices.
     */
    public class UndirectedEdge {
        /**
         * Creates an edge between VertexA <code>VertexA</code> and <code>VertexB</code>
         * @param myVertexA One vertex for the edge.
         * @param myVertexB The other vertex for the edge.
         */
        public UndirectedEdge(Vertex myVertexA, Vertex myVertexB) { VertexA = myVertexA; VertexB = myVertexB;}
        public String toString() { return "(Edge from "+VertexA+" to "+VertexB+")"; }

        final public Vertex VertexA;
        final public Vertex VertexB;
    }

    // METHODS

    // ... QUERY
    /**
     * @return the number of vertices currently in the graph.
     */
    public int numVertices() {
        return vertices.size();
    }

    /**
     * @param ii An integer between 0 [inclusive] and numVertices() [exclusive]
     * @return The ii-th vertex that was inserted into the graph.
     */
    public Vertex ithVertex(int ii) { return vertices.get(ii); }

    /**
     * @return true iff there is an edge connecting vertexA to vertexB
     */
    abstract public boolean isAdjacentQ(Vertex vertexA, Vertex vertexB);


    /**
     * @return A human-readable version of the graph
     */
    public String toString() {
        String result = "";
        for (int ii=0; ii<numVertices(); ii++) {
            result += ii + ": ";
            for (int jj=0; jj<numVertices(); jj++)
                if (isAdjacentQ(ithVertex(ii),ithVertex(jj)))
                    result += jj + " ";
            result+="\n";
        }
        return result;
    }

    // ... MANIPULATION

    /**
     * Adds a new vertex to the graph (not part of any edges).
     * @return Returns the newly created vertex.
     */
    public Vertex addVertex() {
        Vertex newVertex = new Vertex(numVertices()+1);
        vertices.add(newVertex);
        return newVertex;
    }

    /**
     * Adds to the graph a directed edge from <code>VertexA</code> to <code>VertexB</code>
     * @param vertexA The originating vertex for the edge.
     * @param vertexB The concluding vertex for the edge.
     */
    public abstract void addEdge(Vertex vertexA, Vertex vertexB);


    // DATA FIELDS

    /** The vertices of the graph. */
    protected final ArrayList<Vertex> vertices = new ArrayList<>();
}