/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.poli.medgraph.main;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

/**
 *
 * @author julio.reyes
 */
public class MedGraph {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Graph<Integer, String> g = new SparseGraph<>();
        g.addVertex((Integer)1);
        g.addVertex((Integer)2);
        g.addVertex((Integer)3); 
        g.addEdge("Edge-A", 1, 2);
        g.addEdge("Edge-B", 2, 3); 
        System.out.println("The graph g = " + g.toString());

    }
    
}
