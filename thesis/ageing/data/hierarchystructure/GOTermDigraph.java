package thesis.ageing.data.hierarchystructure;

import thesis.ageing.utils.Config;
import thesis.ageing.utils.DataUtils;
import thesis.ageing.utils.FileUtils;
import thesis.ageing.utils.HierarchyUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;

public class GOTermDigraph implements Serializable{
    private static String root_biological_process = "GO:0008150";
    private static String root_cellular_component = "GO:0005575";
    private static String root_molecular_function = "GO:0003674";
    private ST<String, Integer> st;
    private String[] keys;
    private Digraph D;
    private GOTermDigraph _reverse;

    public static GOTermDigraph loadHierarchy(String path) {
        GOTermDigraph hierarchy = null;
        ArrayList<String> gos = new ArrayList<>();
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);

            String line = null;
            do {
                line = br.readLine();

                if (line != null) {
                    String[] split = line.split(" --::-- ");
                    if (split[0] != null && !gos.contains(split[0])) {
                        gos.add(split[0]);
                    }
                    if (split.length > 1) {
                        String[] adj = split[1].split(" :##: ");
                        for (int i = 0; i < adj.length; i++) {
                            if (!gos.contains(adj[i])) {
                                gos.add(adj[i]);
                            }
                        }
                    }
                }
            } while (line != null);

            String[] gList = new String[gos.size()];
            gos.toArray(gList);
            hierarchy = new GOTermDigraph(gList);

            fr = new FileReader(path);
            br = new BufferedReader(fr);

            line = null;
            do {
                line = br.readLine();
                if (line != null) {
                    String[] split = line.split(" --::-- ");
                    String[] adj = null;

                    if (split.length > 1) {
                        adj = split[1].split(" :##: ");

                        for (int i = 0; i < adj.length; i++) {
                            hierarchy.addEdge(split[0], adj[i]);
                            //System.out.println(split[0] + " - " + adj[i]);
                        }
                    }
                }
            } while (line != null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hierarchy;
    }

    public GOTermDigraph(String[] gos) {
        st = new ST<String, Integer>();
        for (String go : gos)
            if (!st.contains(go))
                st.put(go, st.size());

        keys = gos.clone();
        D = new Digraph(st.size());
    }

    public int getIndex(String go) {
        return st.get(go);
    }

    public String getGO(int index) {
        return keys[index];
    }

    public void addEdge(String v, String w) {
        if (st.contains(v) && st.contains(w))
            D.addEdge(st.get(v), st.get(w));
    }

    public Digraph getGraph() {
        return D;
    }

    public ArrayList<String> adj(String go) {
        ArrayList<String> a = new ArrayList<String>();

        Iterable<Integer> it = this.D.adj(st.get(go));

        for (Integer i : it) {
            a.add(keys[i]);
        }

        return a;
    }

    public int V() {
        return D.V();
    }

    public int E() {
        return D.E();
    }

    public GOTermDigraph reverse() {
        if (_reverse != null) return _reverse;
        _reverse = new GOTermDigraph(keys);
        for (int v = 0; v < D.V(); v++)
            for (int w : D.adj(v))
                _reverse.addEdge(keys[w], keys[v]);
        return _reverse;
    }

    public String printFile() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < D.V(); i++) {
            String node = keys[i];
            //System.out.print("\n" + node + "-");
            sb.append("\n" + node + " --::-- ");
            ArrayList<Integer> adj = D.adj(i);
            for (int j = 0; j < adj.size(); j++) {
                //System.out.print(keys[adj.get(j)] + ",");
                sb.append(keys[adj.get(j)] + " :##: ");
            }
        }

        return sb.toString();
    }

    public String toString() {
        String s = D.V() + " vertices, " + D.E() + " edges\n";
        for (int v = 0; v < D.V(); v++) {
            s += keys[v] + ": ";
            for (int w : D.adj(v))
                s += keys[w] + " ";
            s += "\n";
        }

        return s;
    }

    public boolean thereisapathbetween(String source, String w) {
        if (!this.st.contains(source)) {
            //System.out.println("Nao contem " + source);
            return false;
        }
        if (!this.st.contains(w)) {
            //System.out.println("Nao contem " + w);
            return false;
        }
        DirectedDFS dfs = new DirectedDFS(this.D, this.st.get(source));

        return dfs.marked(this.st.get(w));
    }

    public ArrayList<String> allReachableGOInclusive(String source) {
        if (!this.st.contains(source)) {
            //System.out.println("Nao contem " + source);
            return null;
        }
        DirectedDFS dfs = new DirectedDFS(this.D, this.st.get(source));

        boolean[] marked = dfs.allMarked();
        ArrayList<String> marked_list = new ArrayList<>();

        for (int i = 0; i < marked.length; i++) {
            if (marked[i]) {
                marked_list.add(keys[i]);
            }
        }

        return marked_list;
    }

    public ArrayList<String> allReachableGO(String source) {
        if (!this.st.contains(source)) {
            //System.out.println("Nao contem " + source);
            return null;
        }
        DirectedDFS dfs = new DirectedDFS(this.D, this.st.get(source));

        boolean[] marked = dfs.allMarked();
        ArrayList<String> marked_list = new ArrayList<>();

        for (int i = 0; i < marked.length; i++) {
            if (marked[i] && !keys[i].contains(source)) {
                marked_list.add(keys[i]);
            }
        }

        return marked_list;
    }

    public ArrayList<String> Ancestrais2(String source) {
        if (!this.st.contains(source)) {
            //System.out.println("Nao contem " + source);
            return null;
        }

        ArrayList<String> ancestrais = new ArrayList<>();

        ArrayList<String> a = _reverse.adj(source);
        //while()

        return ancestrais;
    }

    public ArrayList<String[]> findAllPaths(String source) {
        if (!this.st.contains(source)) {
            //System.out.println("Nao contem " + source);
            return null;
        }
        DirectAllPaths dfs = new DirectAllPaths(this.D, this.st.get(source));

        ArrayList<int[]> paths = dfs.getPaths();

        ArrayList<String[]> p = new ArrayList<String[]>();

        for (int i = 0; i < paths.size(); i++) {
            String[] ss = new String[paths.get(i).length];
            for (int j = 0; j < paths.get(i).length; j++) {
                ss[j] = keys[paths.get(i)[j]];
            }
            if (this.adj(ss[ss.length - 1]).size() == 0)
                p.add(ss);
        }

        return p;
    }

    public ArrayList<String[]> findAllPaths() {
        ArrayList<String[]> path = new ArrayList<String[]>();

        if (this.st.contains(root_biological_process)) {
            ArrayList<String[]> pathBF = findAllPaths(root_biological_process);
            path.addAll(pathBF);
        }

        if (this.st.contains(root_cellular_component)) {
            ArrayList<String[]> pathCC = findAllPaths(root_cellular_component);
            path.addAll(pathCC);
        }
        if (this.st.contains(root_molecular_function)) {
            ArrayList<String[]> pathMF = findAllPaths(root_molecular_function);
            path.addAll(pathMF);
        }

        // It is a fake (test) hierarchy
        if (this.st.contains("A")) {
            ArrayList<String[]> pathFake = findAllPaths("J");
            path.addAll(pathFake);

            //pathFake = findAllPaths("K");
            path.addAll(pathFake);
        }

        return path;
    }

    public int[] getAllLevels(String source) {
        ArrayList<String[]> paths = this.reverse().findAllPaths(source);

        ArrayList<Integer> levels = new ArrayList<Integer>();

        for (int i = 0; i < paths.size(); i++) {
            String[] p = paths.get(i);
            if (!levels.contains(p.length))
                levels.add(p.length);
        }

        int[] levels_array = new int[levels.size()];

        for (int i = 0; i < levels.size(); i++)
            levels_array[i] = levels.get(i);

        return levels_array;
    }

    public int getLevelMin(String source) {
        ArrayList<String[]> paths = this.reverse().findAllPaths(source);
        if (paths.size() == 0) return 1;

        int min = 999999;

        for (int i = 0; i < paths.size(); i++) {
            String[] p = paths.get(i);
            if (p.length < min)
                min = p.length;
        }
        return min;
    }

    public int getLevelMax(String source) {
        ArrayList<String[]> paths = this.reverse().findAllPaths(source);

        int max = 1;

        for (int i = 0; i < paths.size(); i++) {
            String[] p = paths.get(i);
            if (p.length > max)
                max = p.length;
        }
        return max;
    }

    public int getLevelRound(String source) {
        ArrayList<String[]> paths = this.reverse().findAllPaths(source);

        double level = 1;

        for (int i = 0; i < paths.size(); i++) {
            String[] p = paths.get(i);
            level += (double) p.length / paths.size();
        }

        return (int) Math.round(level);
    }

    public int getHierarchyLevel(int t) {
        int max = 0;

        for (String k : keys) {
            int lvl = 0;

            if(t == 1) {
                lvl = getLevelMax(k);
                if (lvl > max)
                    max = lvl;
            }
            else if(t == 2) {
                lvl = getLevelMin(k);
                if (lvl > max)
                    max = lvl;
            }
            else if(t == 3) {
                lvl = getLevelRound(k);
                if (lvl > max)
                    max = lvl;
            }
        }

        return max;
    }

    public int getHierarchyLevel() {
        return getHierarchyLevel(1);
    }

    public ArrayList<String> getLevelTerms(int depth, int type) {
        ArrayList<String> gos = new ArrayList<String>();
        int ni = 0;
        for (String key : st.keys()) {

            if(type == 1)
                ni = getLevelMax(key);
            else if(type == 2)
                ni = getLevelMin(key);
            else if(type == 3)
                ni = getLevelRound(key);
            //ni = getLevelMax(key);
            //ni = getLevelMin(key);
            //ni = getLevelMax(key);
            //ni = getLevelRound(key);
            if (ni == depth) {
                if (!gos.contains(key))
                    gos.add(key);
            }
        }
        return gos;
    }

    public ArrayList<String> getLevelTerms(int depth) {
        return getLevelTerms(depth,2);
    }

    public ArrayList<String> getLeaves() {
        ArrayList<String> leaves = new ArrayList<String>();
        for (int i = 0; i < this.V(); i++) {
            String go = keys[i];

            ArrayList<String> adj = this.adj(go);

            if (adj.size() == 0)
                leaves.add(go);
        }

        return leaves;
    }

    public void print(weka.core.Instances data)
    {
        System.out.print("\n\nPrint Ancestors Selected Features: ");

        ArrayList<String> jahPrintado = new ArrayList<>();

        for(int i = 0 ; i < data.numAttributes()-1;i++)
        {
            String attName = data.attribute(i).name();
            System.out.print("\n" + attName + " - ");
            ArrayList<String> anc = _reverse.allReachableGO(attName);

            for(int a = 0 ; a < anc.size(); a++)
            {
                if(data.attribute(anc.get(a)) != null)
                    System.out.print(anc.get(a) + " - ");
            }
        }
    }

    public void print(weka.core.Instances data, boolean[] printableFeat)
    {
        System.out.print("\n\nPrint Ancestors Selected Features: ");

        ArrayList<String> jahPrintado = new ArrayList<>();

        for(int i = 0 ; i < data.numAttributes()-1;i++)
        {
            String attName = data.attribute(i).name();
            int index = data.attribute(i).index();
            if(printableFeat[index]) {
                System.out.print("\n" + attName + " - ");
                if(_reverse == null)
                    _reverse = this.reverse();
                ArrayList<String> anc = _reverse.allReachableGO(attName);

                for (int a = 0; a < anc.size(); a++) {
                    int indexA = data.attribute(anc.get(a)).index();
                    if (data.attribute(anc.get(a)) != null && printableFeat[indexA])
                        System.out.print(anc.get(a) + " - ");
                }
            }
        }
    }

    public void printAll(weka.core.Instances data, boolean[] printableFeat)
    {
        System.out.print("\n\nPrint Hierarchical Structure Selected Features -> ancestors: ");
        System.out.println(D.V() + " - " + D.E());
        //ArrayList<String> leaves = this.getLeaves();
        ArrayList<String> leaves = new ArrayList<>();
        for(int i = 0 ; i < data.numAttributes()-1;i++) {
            String attName = data.attribute(i).name();
            leaves.add(attName);
        }

        for(int i = 0 ; i < leaves.size();i++)
        {
            String attName = leaves.get(i);
            int index = data.attribute(attName).index();
            if(printableFeat[index]) {
                System.out.print("\n" + attName + " (X) - ");
                if(_reverse == null)
                    _reverse = this.reverse();
                ArrayList<String> anc = _reverse.allReachableGO(attName);

                for (int a = 0; a < anc.size(); a++) {
                    int indexA = data.attribute(anc.get(a)).index();
                    if (data.attribute(anc.get(a)) != null && printableFeat[indexA])
                        System.out.print(anc.get(a) + " (X) - ");
                    else
                        System.out.print(anc.get(a) + " ( ) - ");
                }
            }
            else
            {
                System.out.print("\n" + attName + "( ) - ");
                if(_reverse == null)
                    _reverse = this.reverse();
                ArrayList<String> anc = _reverse.allReachableGO(attName);

                for (int a = 0; a < anc.size(); a++) {
                    int indexA = data.attribute(anc.get(a)).index();
                    if (data.attribute(anc.get(a)) != null && printableFeat[indexA])
                        System.out.print(anc.get(a) + " (X) - ");
                    else
                        System.out.print(anc.get(a) + " ( ) - ");
                }
            }
        }

        System.out.println("\n");
    }

    public void printRedundantFeatures(weka.core.Instances data, boolean[] printableFeat)
    {
        System.out.print("\n\nPrint Redundant Features: ");

        ArrayList<String> jahPrintado = new ArrayList<>();

        for(int i = 0 ; i < data.numAttributes()-1;i++)
        {
            String attName = data.attribute(i).name();
            int index = data.attribute(i).index();
            if(printableFeat[index]) {
                ArrayList<String> anc = _reverse.allReachableGO(attName);
                ArrayList<String> dec = this.allReachableGO(attName);
                boolean hasSelectedAnc = false;
                boolean hasSelectedDec = false;
                for (int a = 0; a < anc.size(); a++) {
                    int indexA = data.attribute(anc.get(a)).index();
                    if (data.attribute(anc.get(a)) != null && printableFeat[indexA])
                        hasSelectedAnc = true;
                }

                for (int a = 0; a < dec.size(); a++) {
                    int indexA = data.attribute(dec.get(a)).index();
                    if (data.attribute(dec.get(a)) != null && printableFeat[indexA])
                        hasSelectedDec = true;
                }

                if(hasSelectedAnc || hasSelectedDec)
                    System.out.print("\n" + attName + " - ");
            }
        }
    }

    public void printNonRedundantFeatures(weka.core.Instances data, boolean[] printableFeat)
    {
        System.out.print("\n\nPrint NonRedundant Selected Features: ");

        for(int i = 0 ; i < data.numAttributes()-1;i++)
        {
            String attName = data.attribute(i).name();
            int index = data.attribute(i).index();
            if(printableFeat[index]) {
                ArrayList<String> anc = _reverse.allReachableGO(attName);
                ArrayList<String> dec = this.allReachableGO(attName);
                boolean hasSelectedAnc = false;
                boolean hasSelectedDec = false;
                for (int a = 0; a < anc.size(); a++) {
                    int indexA = data.attribute(anc.get(a)).index();
                    if (data.attribute(anc.get(a)) != null && printableFeat[indexA])
                        hasSelectedAnc = true;
                }

                for (int a = 0; a < dec.size(); a++) {
                    int indexA = data.attribute(dec.get(a)).index();
                    if (data.attribute(dec.get(a)) != null && printableFeat[indexA])
                        hasSelectedDec = true;
                }

                if(!hasSelectedAnc && !hasSelectedDec)
                    System.out.print("\n" + attName + " - ");
            }
        }
    }

    public void printDec(weka.core.Instances data, boolean[] printableFeat)
    {
        System.out.print("\n\nPrint Descendants Selected Features: ");

        ArrayList<String> jahPrintado = new ArrayList<>();

        for(int i = 0 ; i < data.numAttributes()-1;i++)
        {
            String attName = data.attribute(i).name();
            int index = data.attribute(i).index();
            if(printableFeat[index]) {
                System.out.print("\n" + attName + " - ");
                ArrayList<String> dec = this.allReachableGO(attName);

                for (int a = 0; a < dec.size(); a++) {
                    int indexA = data.attribute(dec.get(a)).index();
                    if (data.attribute(dec.get(a)) != null && printableFeat[indexA])
                        System.out.print(dec.get(a) + " - ");
                }
            }
        }
    }

    public double numberOfRedundantFeatures(weka.core.Instances data, boolean[] printableFeat)
    {
        double val = 0;

        for(int i = 0 ; i < data.numAttributes()-1;i++)
        {
            String attName = data.attribute(i).name();
            int index = data.attribute(i).index();
            if(printableFeat[index]) {
                ArrayList<String> anc = _reverse.allReachableGO(attName);
                ArrayList<String> dec = this.allReachableGO(attName);
                boolean hasSelectedAnc = false;
                boolean hasSelectedDec = false;
                for (int a = 0; a < anc.size(); a++) {
                    int indexA = data.attribute(anc.get(a)).index();
                    if (printableFeat[indexA])
                        hasSelectedAnc = true;
                }

                for (int a = 0; a < dec.size(); a++) {
                    int indexA = data.attribute(dec.get(a)).index();
                    if (printableFeat[indexA])
                        hasSelectedDec = true;
                }

                if(hasSelectedAnc || hasSelectedDec)
                    val++;
            }
        }
        return val;
    }

    public static void main(String[] args)
    {
        ArrayList<String> datas = new ArrayList<>();
        datas.add("CE-BP-threshold-3");
        datas.add("CE-CC-threshold-3");
        datas.add("CE-MF-threshold-3");
        datas.add("CE-BP+CC-threshold-3");
        datas.add("CE-BP+MF-threshold-3");
        datas.add("CE-CC+MF-threshold-3");
        datas.add("CE-BP+CC+MF-threshold-3");

        datas.add("DM-BP-threshold-3");
        datas.add("DM-CC-threshold-3");
        datas.add("DM-MF-threshold-3");
        datas.add("DM-BP+CC-threshold-3");
        datas.add("DM-BP+MF-threshold-3");
        datas.add("DM-CC+MF-threshold-3");
        datas.add("DM-BP+CC+MF-threshold-3");

        datas.add("MM-BP-threshold-3");
        datas.add("MM-CC-threshold-3");
        datas.add("MM-MF-threshold-3");
        datas.add("MM-BP+CC-threshold-3");
        datas.add("MM-BP+MF-threshold-3");
        datas.add("MM-CC+MF-threshold-3");
        datas.add("MM-BP+CC+MF-threshold-3");

        datas.add("SC-BP-threshold-3");
        datas.add("SC-CC-threshold-3");
        datas.add("SC-MF-threshold-3");
        datas.add("SC-BP+CC-threshold-3");
        datas.add("SC-BP+MF-threshold-3");
        datas.add("SC-CC+MF-threshold-3");
        datas.add("SC-BP+CC+MF-threshold-3");
        for(String s : datas) {
            FileUtils.saveFile(HierarchyUtils.getHierarchy(DataUtils.loadDataSet(Config.getDataGOPath() + s + ".arff")).printFile(), Config.getHierarchyPath() + s + ".txt");
            GOTermDigraph term = GOTermDigraph.loadHierarchy(Config.getHierarchyPath() + s + ".txt");
            System.out.println(term.toString());
        }

        datas.clear();
        datas.add("LDCities");
        datas.add("LDNYDailyheadings");
        datas.add("LDSportsTweetsc");
        datas.add("LDSportsTweetst");
        datas.add("LDStumbleupon");
        datas.add("LDD2B2");
        datas.add("LDD2B5");
        datas.add("LDD2B10");
        datas.add("LDD4B2");
        datas.add("LDD4B5");
        datas.add("LDD4B10");

        for(String s : datas) {
            FileUtils.saveFile(HierarchyUtils.getHierarchy(DataUtils.loadDataSet(Config.getMixDataPath() + s + ".arff")).printFile(), Config.getHierarchyPath() + s + ".txt");
            GOTermDigraph term = GOTermDigraph.loadHierarchy(Config.getHierarchyPath() + s + ".txt");
            System.out.println(term.toString());
        }
    }
}
