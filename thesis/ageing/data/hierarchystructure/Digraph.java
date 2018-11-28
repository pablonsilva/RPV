package thesis.ageing.data.hierarchystructure;

import java.io.Serializable;
import java.util.ArrayList;

public class Digraph implements Serializable{
    private final int V;
    private int E;
    private ArrayList<Integer>[] adj;

    @SuppressWarnings("unchecked")
    public Digraph(int v) {
        this.V = v;
        this.E = 0;
        adj = (ArrayList<Integer>[]) new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<Integer>();
        }
    }

    public static void main(String[] args) {
        Digraph d = new Digraph(10);
        d.addEdge(1, 2);
        d.addEdge(2, 3);
        d.addEdge(2, 5);
        d.addEdge(5, 3);
        d.addEdge(6, 7);
        d.addEdge(7, 8);
        d.addEdge(4, 3);
        d.addEdge(1, 4);
        d.addEdge(4, 7);
        d.addEdge(8, 9);

        System.out.println(d.toString());

        Digraph r = d.reverse();
        System.out.println(r.toString());
    }

    public int V() {
        return this.V;
    }

    public int E() {
        return this.E;
    }

    public void addEdge(int v, int w) {
        adj[v].add(w);
        E++;
    }

    public ArrayList<Integer> adj(int v) {
        return adj[v];
    }

    public Digraph reverse() {
        Digraph R = new Digraph(V);
        for (int v = 0; v < this.V; v++)
            for (int w : adj(v))
                R.addEdge(w, v);

        return R;
    }

    public String toString() {
        String s = V + " vertices, " + E + " edges\n";
        for (int v = 0; v < this.V; v++) {
            s += v + ": ";
            for (int w : adj(v))
                s += w + " ";
            s += "\n";
        }

        return s;
    }
}
