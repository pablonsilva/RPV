package bioinformatics.ageing.data.build;

import bioinformatics.ageing.data.hierarchystructure.GOTermDigraph;
import bioinformatics.ageing.data.hierarchystructure.ST;
import bioinformatics.ageing.utils.Config;
import thesis.ageing.utils.FileUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BuildHierarchy {
    private String hierarchyRawFile;
    private String _hierarchyPath;

    public BuildHierarchy(String raw) {
        hierarchyRawFile = raw;
    }

    public BuildHierarchy() {
//        hierarchyRawFile = "/home_nfs/psilva/Data/go-basic.txt";
//        if(!FileUtils.fileExist(hierarchyRawFile))
//            hierarchyRawFile = "/Users/pablonsilva/Dropbox/Compartilhada/Doutorado/Biology of Ageing/Data/GO/biological process/go-basic.obo 08102015.txt"; //v17
//        //hierarchyRawFile = "/Users/pablonsilva/Dropbox/Compartilhada/Doutorado/Biology of Ageing/Data/GenAgev18/go-basic.obo.txt"; //v18
//        _hierarchyPath = "/home_nfs/psilva/Data/Bases_paper_shsel/";
//        if(!FileUtils.directoryExists(_hierarchyPath))
//            _hierarchyPath = "/Users/pablonsilva/Dropbox/Compartilhada/Doutorado/Biology of Ageing/Data/Bases_paper_shsel/";

        hierarchyRawFile = Config.get_path_hierarchy_raw();
        _hierarchyPath = Config.get_path_hierarchy_mix();
    }

    public static void main(String[] args) {
        BuildHierarchy bh = new BuildHierarchy();
        GOTermDigraph goD = bh.getHierarchyDigraph(GOType.biological_process);
        System.out.println(goD.toString());
        System.out.println(goD.V() + "vertices, " + goD.E() + " edges");

        System.out.println(goD.getLevelTerms(4).size() + " - " + goD.getLevelTerms(4).toString());
    }

    public GOTermDigraph getHierarchyDigraph(GOType ontology) {
        boolean bp = false;
        boolean cc = false;
        boolean mf = false;

        if (GOType.biological_process == ontology)
            bp = true;
        else if (GOType.cellular_component == ontology)
            cc = true;
        else if (GOType.molecular_function == ontology)
            mf = true;

        return this.getHierarchyDigraph(bp, cc, mf, null);
    }

    public GOTermDigraph getHierarchyDigraph(GOType ontology, GOType ontology2) {
        boolean bp = false;
        boolean cc = false;
        boolean mf = false;

        if (GOType.biological_process == ontology || GOType.biological_process == ontology2)
            bp = true;
        if (GOType.cellular_component == ontology || GOType.cellular_component == ontology2)
            cc = true;
        if (GOType.molecular_function == ontology || GOType.molecular_function == ontology2)
            mf = true;

        return this.getHierarchyDigraph(bp, cc, mf, null);
    }

    public GOTermDigraph getHierarchyDigraph(GOType ontology, GOType ontology2, GOType ontology3) {
        boolean bp = false;
        boolean cc = false;
        boolean mf = false;

        if (GOType.biological_process == ontology || GOType.biological_process == ontology2
                || GOType.biological_process == ontology3)
            bp = true;
        if (GOType.cellular_component == ontology || GOType.cellular_component == ontology2
                || GOType.cellular_component == ontology3)
            cc = true;
        if (GOType.molecular_function == ontology || GOType.molecular_function == ontology2
                || GOType.molecular_function == ontology3)
            mf = true;

        return this.getHierarchyDigraph(bp, cc, mf, null);
    }

    public GOTermDigraph getHierarchyDigraph(boolean bp, boolean cc, boolean mf) {
        return this.getHierarchyDigraph(bp, cc, mf, null);
    }

    public GOTermDigraph getHierarchyDigraph(GOType ontology, ArrayList<String> limited_go) {
        boolean bp = false;
        boolean cc = false;
        boolean mf = false;

        if (GOType.biological_process == ontology)
            bp = true;
        else if (GOType.cellular_component == ontology)
            cc = true;
        else if (GOType.molecular_function == ontology)
            mf = true;

        return this.getHierarchyDigraph(bp, cc, mf, limited_go);
    }

    public GOTermDigraph getHierarchyDigraph(GOType ontology, GOType ontology2, ArrayList<String> limited_go) {
        boolean bp = false;
        boolean cc = false;
        boolean mf = false;

        if (GOType.biological_process == ontology || GOType.biological_process == ontology2)
            bp = true;
        if (GOType.cellular_component == ontology || GOType.cellular_component == ontology2)
            cc = true;
        if (GOType.molecular_function == ontology || GOType.molecular_function == ontology2)
            mf = true;

        return this.getHierarchyDigraph(bp, cc, mf, limited_go);
    }


    public GOTermDigraph getHierarchyDigraph(GOType ontology, GOType ontology2, GOType ontology3,
                                             ArrayList<String> limited_go) {
        boolean bp = false;
        boolean cc = false;
        boolean mf = false;

        if (GOType.biological_process == ontology || GOType.biological_process == ontology2
                || GOType.biological_process == ontology3)
            bp = true;
        if (GOType.cellular_component == ontology || GOType.cellular_component == ontology2
                || GOType.cellular_component == ontology3)
            cc = true;
        if (GOType.molecular_function == ontology || GOType.molecular_function == ontology2
                || GOType.molecular_function == ontology3)
            mf = true;

        return this.getHierarchyDigraph(bp, cc, mf, limited_go);
    }

    public GOTermDigraph getHierarchyDigraph(String dataName, ArrayList<String> terms) {
        GOTermDigraph D = null;
        BufferedReader br = null;
        ST<String, ArrayList<String>> st_isa = new ST<>();
        try {
            br = new BufferedReader(new FileReader(_hierarchyPath + dataName.replace("_", "") + ".csv"));

            String line = "";

            do {

                line = br.readLine();

                if (line != null) {
                    String[] split = line.split(",");
                    String term = split[0].replace(" ", "");
                    ArrayList<String> parents = new ArrayList<String>();

                    for (int i = 1; i < split.length; i++)
                        parents.add(split[i].replace(" ", ""));

                    st_isa.put(term, parents);
                }
            } while (line != null);

            String[] a = new String[terms.size()];
            D = new GOTermDigraph(terms.toArray(a));

            for (String term : terms) {
                ArrayList<String> list = st_isa.get(term);
                if (list != null)
                    for (int i = 0; i < list.size(); i++) {
                        D.addEdge(list.get(i), term);
                    }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return D;
    }

    public GOTermDigraph getHierarchyDigraph(boolean bp, boolean cc, boolean mf, ArrayList<String> limited_go) {

        String ontology1 = "";
        String ontology2 = "";
        String ontology3 = "";

        if (bp)
            ontology1 = GOType.biological_process.toString();
        if (cc)
            ontology2 = GOType.cellular_component.toString();
        if (mf)
            ontology3 = GOType.molecular_function.toString();

        GOTermDigraph D = null;

        ST<String, String> st = new ST<>();
        ST<String, ArrayList<String>> st_isa = new ST<>();

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(hierarchyRawFile));
            ArrayList<String> gos = new ArrayList<String>();

            String line = "";
            boolean readingGo = false;
            boolean isValidGo = true;
            String go_term = "";
            do {
                line = br.readLine();
                if (line != null) {
                    if (line.startsWith("[Term]")) {
                        readingGo = true;
                        isValidGo = true;
                    } else if (line.startsWith("id:")) {
                        int index = line.indexOf("GO:");
                        if (index > 0) {
                            go_term = line.substring(index);
                            if (limited_go != null) {
                                if (limited_go.contains(go_term))
                                    st_isa.put(go_term, new ArrayList<String>());
                                else
                                    isValidGo = false;
                            } else {
                                st_isa.put(go_term, new ArrayList<String>());
                            }
                        }
                    } else if (line.startsWith("alt_id:") && isValidGo) {
                        int index = line.indexOf("alt_id:");
                        if (index > 0) {
                            String altId = line.substring(index);
                            st.put(altId, go_term);
                        }
                    } else if (line.startsWith("is_a:") && isValidGo) {
                        int index = line.indexOf("GO:");
                        int indexEnd = line.indexOf(" ", index);
                        if (index > 0 && indexEnd > 0) {
                            String is_a_go = line.substring(index, indexEnd);
                            st_isa.get(go_term).add(is_a_go);
                        }
                    } else if (line.contains("namespace: ") && isValidGo) {
                        if (!line.contains("namespace: " + ontology1) && bp && line.contains("namespace: " + ontology2)
                                && cc && line.contains("namespace: " + ontology3) && mf) {
                            isValidGo = false;
                        }

                        if (line.contains("namespace: " + GOType.biological_process) && !bp) {
                            isValidGo = false;
                        }
                        if (line.contains("namespace: " + GOType.cellular_component) && !cc) {
                            isValidGo = false;
                        }
                        if (line.contains("namespace: " + GOType.molecular_function) && !mf) {
                            isValidGo = false;
                        }
                    } else if (line.contains("is_obsolete: true") && isValidGo) {
                        isValidGo = false;
                    } else if (readingGo && line.trim().isEmpty()) {
                        if (isValidGo) {
                            gos.add(go_term);
                        }
                        readingGo = false;
                        isValidGo = true;
                    }
                }
            } while (line != null);
            br.close();

            String[] a = new String[gos.size()];
            D = new GOTermDigraph(gos.toArray(a));

            for (String go : gos) {
                ArrayList<String> list = st_isa.get(go);
                for (int i = 0; i < list.size(); i++) {
                    if (limited_go != null && limited_go.contains(go) && limited_go.contains(list.get(i)))
                        D.addEdge(list.get(i), go);
                    else if (limited_go == null)
                        D.addEdge(list.get(i), go);
                }
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return D;
    }

    public GOTermDigraph fakeHierarchy() {
        String[] goss = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};

        GOTermDigraph d = new GOTermDigraph(goss);
        d.addEdge("K", "C");
        d.addEdge("C", "A");
        d.addEdge("J", "D");
        d.addEdge("D", "A");
        d.addEdge("D", "B");
        d.addEdge("A", "E");
        d.addEdge("E", "L");
        d.addEdge("J", "H");
        d.addEdge("H", "B");
        d.addEdge("H", "G");
        d.addEdge("G", "I");
        d.addEdge("B", "F");
        d.addEdge("F", "L");
        d.addEdge("B", "E");
        d.addEdge("B", "G");

        return d;
    }
}
