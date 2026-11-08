import java.util.*;

/**
 * Investigación: Grafos y algoritmos fundamentales.
 * Implementa BFS, DFS, Prim y Kruskal usando listas de adyacencia.
 * Compilar: javac Main.java
 * Ejecutar: java Main
 */
public class Main {
    static class Edge {
        int to, weight;
        Edge(int to, int weight) { this.to = to; this.weight = weight; }
    }

    static class WeightedEdge {
        int from, to, weight;
        WeightedEdge(int from, int to, int weight) {
            this.from = from; this.to = to; this.weight = weight;
        }
        public String toString() { return from + "-" + to + " (" + weight + ")"; }
    }

    static class Graph {
        int n;
        List<List<Edge>> adj;
        List<WeightedEdge> edges;

        Graph(int n) {
            if (n <= 0) throw new IllegalArgumentException("El grafo debe tener vertices.");
            this.n = n;
            adj = new ArrayList<>();
            edges = new ArrayList<>();
            for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        }

        void addUndirectedEdge(int u, int v, int w) {
            checkVertex(u); checkVertex(v);
            if (u == v) throw new IllegalArgumentException("No se permiten lazos en este ejemplo.");
            adj.get(u).add(new Edge(v, w));
            adj.get(v).add(new Edge(u, w));
            edges.add(new WeightedEdge(u, v, w));
        }

        void checkVertex(int v) {
            if (v < 0 || v >= n) throw new IllegalArgumentException("Vertice fuera de rango: " + v);
        }
    }

    // BFS: explora primero todos los vecinos del nivel actual.
    static List<Integer> bfs(Graph g, int start) {
        g.checkVertex(start);
        boolean[] visited = new boolean[g.n];
        Queue<Integer> queue = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();
        visited[start] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            int u = queue.remove();
            order.add(u);
            for (Edge e : g.adj.get(u)) {
                if (!visited[e.to]) {
                    visited[e.to] = true; // marcar al insertar evita duplicados
                    queue.add(e.to);
                }
            }
        }
        return order;
    }

    // DFS iterativo: profundiza antes de retroceder.
    static List<Integer> dfs(Graph g, int start) {
        g.checkVertex(start);
        boolean[] visited = new boolean[g.n];
        Deque<Integer> stack = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (visited[u]) continue;
            visited[u] = true;
            order.add(u);
            // Al apilar los vecinos en su orden de lista, el ultimo agregado
            // sera el primero que se explore (comportamiento LIFO).
            for (Edge e : g.adj.get(u)) {
                if (!visited[e.to]) stack.push(e.to);
            }
        }
        return order;
    }

    // Prim con cola de prioridad. Devuelve un bosque si el grafo no es conexo.
    static List<WeightedEdge> prim(Graph g, int start) {
        g.checkVertex(start);
        boolean[] inTree = new boolean[g.n];
        PriorityQueue<WeightedEdge> pq =
            new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        List<WeightedEdge> mst = new ArrayList<>();

        for (int rootOffset = 0; rootOffset < g.n; rootOffset++) {
            int root = (start + rootOffset) % g.n;
            if (inTree[root]) continue;
            inTree[root] = true;
            addFrontier(g, root, inTree, pq);

            while (!pq.isEmpty()) {
                WeightedEdge e = pq.poll();
                if (inTree[e.to]) continue;
                inTree[e.to] = true;
                mst.add(e);
                addFrontier(g, e.to, inTree, pq);
            }
        }
        return mst;
    }

    static void addFrontier(Graph g, int u, boolean[] inTree,
                            PriorityQueue<WeightedEdge> pq) {
        for (Edge e : g.adj.get(u)) {
            if (!inTree[e.to]) pq.add(new WeightedEdge(u, e.to, e.weight));
        }
    }

    static class DSU {
        int[] parent, rank;
        DSU(int n) {
            parent = new int[n]; rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }
        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false;
            if (rank[ra] < rank[rb]) parent[ra] = rb;
            else if (rank[ra] > rank[rb]) parent[rb] = ra;
            else { parent[rb] = ra; rank[ra]++; }
            return true;
        }
    }

    // Kruskal: ordena aristas y acepta solo las que no forman ciclos.
    static List<WeightedEdge> kruskal(Graph g) {
        List<WeightedEdge> sorted = new ArrayList<>(g.edges);
        sorted.sort(Comparator.comparingInt(e -> e.weight));
        DSU dsu = new DSU(g.n);
        List<WeightedEdge> mst = new ArrayList<>();
        for (WeightedEdge e : sorted) {
            if (dsu.union(e.from, e.to)) {
                mst.add(e);
                if (mst.size() == g.n - 1) break;
            }
        }
        return mst; // si no es conexo, representa un bosque de expansion minima
    }

    static int totalWeight(List<WeightedEdge> edges) {
        int total = 0;
        for (WeightedEdge e : edges) total += e.weight;
        return total;
    }

    public static void main(String[] args) {
        Graph g = new Graph(6);
        g.addUndirectedEdge(0, 1, 4);
        g.addUndirectedEdge(0, 2, 3);
        g.addUndirectedEdge(1, 2, 1);
        g.addUndirectedEdge(1, 3, 2);
        g.addUndirectedEdge(2, 3, 4);
        g.addUndirectedEdge(2, 4, 5);
        g.addUndirectedEdge(3, 4, 2);
        g.addUndirectedEdge(3, 5, 3);
        g.addUndirectedEdge(4, 5, 6);

        System.out.println("BFS desde 0: " + bfs(g, 0));
        System.out.println("DFS desde 0: " + dfs(g, 0));

        List<WeightedEdge> p = prim(g, 0);
        List<WeightedEdge> k = kruskal(g);
        System.out.println("Prim: " + p + " | peso total = " + totalWeight(p));
        System.out.println("Kruskal: " + k + " | peso total = " + totalWeight(k));
    }
}
