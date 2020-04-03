package edu.bu.ec504.hw3.graphs;

import java.util.*;

public class WeightedGraph extends Graph {
    public Vertex addVertex() {
        Vertex newVertex = super.addVertex();
        vertexWeights.put(newVertex, null);
        adjList.put(newVertex, new ArrayList<>());
        return newVertex;
    }

    public Vertex addVertex(int weight) {
        Vertex newVertex = super.addVertex();
        vertexWeights.put(newVertex, weight);
        adjList.put(newVertex, new ArrayList<>());
        return newVertex;
    }

    public void addEdge(Vertex vertexA, Vertex vertexB) {
        UndirectedEdge E1 = new UndirectedEdge(vertexA, vertexB);
        UndirectedEdge E2 = new UndirectedEdge(vertexB, vertexA);
        adjList.get(vertexA).add(vertexB);
        adjList.get(vertexB).add(vertexA);
        edgeWeights.put(E1, null);
        edgeWeights.put(E2, null);
    }

    public void addEdge(Vertex vertexA, Vertex vertexB, int weight) {
        UndirectedEdge E1 = new UndirectedEdge(vertexA, vertexB);
        UndirectedEdge E2 = new UndirectedEdge(vertexB, vertexA);
        adjList.get(vertexA).add(vertexB);
        adjList.get(vertexB).add(vertexA);
        edgeWeights.put(E1, weight);
        edgeWeights.put(E2, weight);
    }

    public boolean isAdjacentQ(Vertex theVertexA, Vertex theVertexB) {
        if (adjList.get(theVertexA).contains(theVertexB)) {
            return true;
        } else {
            return false;
        }
    }

    public int coloring() {
        // assign first color to all vertices
        HashMap<Integer, Integer> colors = new HashMap<>();
        int newColor = 1; // first color

        for (Vertex v : vertices) {
            if (v.myID == 1) {
                vertexWeights.put(v, 1); // assign first vertex
                colors.put(newColor, 1);
            } else {
                vertexWeights.put(v, 0);
                colors.put(0, colors.getOrDefault(0, 1) + 1);
            }
        }

        for (Vertex v : vertices) {
            if (v.myID != 1) {
                ArrayList<Integer> neighborColor = new ArrayList<>();
                ArrayList<Vertex> neighbors = adjList.get(v);
                for (Vertex neighbor : neighbors) { // find its neighbors' color
                    neighborColor.add(vertexWeights.get(neighbor));
                }
                int leastUsedColor = leastUsedColor(neighborColor, colors);
                // if every color in the map is used, assign new color
                if (leastUsedColor == -1) {
                    vertexWeights.replace(v, newColor + 1);
                    colors.put(newColor + 1, 1);
                    newColor ++;
                } else { // assign least used color
                    vertexWeights.replace(v, leastUsedColor);
                    colors.replace(leastUsedColor, colors.get(leastUsedColor) + 1);
                }
            }
        }
        return newColor;
    }

    public int leastUsedColor(ArrayList<Integer> neighborColor, HashMap<Integer, Integer> colors) {
        int res = Integer.MAX_VALUE;
        HashSet<Integer> unusedColor = new HashSet<>();
        for (int color : colors.keySet()) {
            if (!neighborColor.contains(color) && color != 0) {
                unusedColor.add(color);
            }
        }
        if (unusedColor.size() == 0) return -1;
        for (int color : unusedColor) {
            if (colors.get(color) < res) res = color;
        }
        return res;
    }

    public HashMap<Vertex, Integer> vertexWeights = new HashMap<>();
    public HashMap<UndirectedEdge, Integer> edgeWeights = new HashMap<>();
    public HashMap<Vertex, ArrayList<Vertex>> adjList = new HashMap<>();
}
