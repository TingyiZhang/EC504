package edu.bu.ec504.hw3;


import edu.bu.ec504.hw3.graphs.EdgeListGraph;
import edu.bu.ec504.hw3.graphs.Graph;
import edu.bu.ec504.hw3.graphs.WeightedGraph;
import edu.bu.ec504.hw3.graphs.myGraph;

public class Main {

    public static void main(String[] args) {
        WeightedGraph G = new WeightedGraph();
        WeightedGraph.Vertex Vertex0 = G.addVertex();
        WeightedGraph.Vertex Vertex1 = G.addVertex();
        WeightedGraph.Vertex Vertex2 = G.addVertex();
        WeightedGraph.Vertex Vertex3 = G.addVertex();
//        WeightedGraph.Vertex Vertex4 = G.addVertex();
//        WeightedGraph.Vertex Vertex5 = G.addVertex();
//        WeightedGraph.Vertex Vertex6 = G.addVertex();
//        WeightedGraph.Vertex Vertex7 = G.addVertex();
//        WeightedGraph.Vertex Vertex8 = G.addVertex();
        G.addEdge(Vertex0, Vertex1);
        G.addEdge(Vertex1, Vertex2);
        G.addEdge(Vertex0, Vertex3);
        G.addEdge(Vertex2, Vertex3);
//        G.addEdge(Vertex2, Vertex4);
//        G.addEdge(Vertex3, Vertex4);
//        G.addEdge(Vertex5, Vertex6);
//        G.addEdge(Vertex6, Vertex7);
//        G.addEdge(Vertex5, Vertex7);
        System.out.println(G.coloring());

        for (WeightedGraph.Vertex v : G.vertexWeights.keySet()) {
            System.out.println("The vertex is: " + v.toString());
            System.out.println("Its color is: " + G.vertexWeights.get(v).toString());
        }
//        System.out.println("Your graph is:\n"+G);
//        System.out.println("There are " + G.getTriangles() + " triangles.");
//        System.out.println("There are " + G.getComponents() + " connected components.");
//        System.out.println(G.Euler());
    }
}
