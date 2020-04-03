package edu.bu.ec504.hw3.graphs;

import java.util.*;

public class myGraph extends EdgeListGraph {
    public myGraph() {
        super();
    }

    public int getTriangles() {
        int num = 0;
        for (UndirectedEdge E : edgeList) {
            for (Vertex v : vertices) {
                if (isAdjacentQ(v, E.VertexA) && isAdjacentQ(v, E.VertexB)) {
                    num++;
                }
            }
        }
        return num / 6;
    }

    public int getComponents() {
        int num = 0;
        List<Boolean> visited = Arrays.asList(new Boolean[numVertices() + 1]);
        for (Vertex v : vertices) {
            visited.set(v.myID, false);
        }
        for (Vertex v : vertices) {
            if (!visited.get(v.myID)) {
                DFS(v, visited);
                num++;
            }
        }
        return num;
    }

    public void DFS(Vertex v, List<Boolean> visited) {
        List<Vertex> neighbors = new ArrayList<>();
        visited.set(v.myID, true);
        for (Vertex adj : vertices) {
            if (isAdjacentQ(v, adj))
                neighbors.add(adj);
        }
        for (Vertex n : neighbors) {
            if (!visited.get(n.myID))
                DFS(n, visited);
        }
    }

    public int Euler() {
        return numVertices() + getTriangles() - edgeList.size() / 2 - getComponents();
    }
}
