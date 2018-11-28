package thesis.ageing.utils;

import thesis.ageing.data.build.BuildHierarchy;
import thesis.ageing.data.build.GOType;
import thesis.ageing.data.hierarchystructure.GOTermDigraph;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by pablonsilva on 10/23/16.
 */
public class HierarchyUtils {
    public static GOTermDigraph getHierarchy(Instances data) {
        GOTermDigraph hierarchy = null;
            BuildHierarchy bh = new BuildHierarchy();
            ArrayList<String> terms = new ArrayList<String>();
            for (int i = 0; i < data.numAttributes() - 1; i++)
                terms.add(data.attribute(i).name());

            if (data.relationName().contains("Teste")) {
                hierarchy = bh.fakeHierarchy();
            } else if (data.relationName().contains("LD")) {
                // is an idividual hierarchy
                hierarchy = bh.getHierarchyDigraph(data.relationName(), terms);
            } else {
                // is a biological hierarchy
                hierarchy = bh.getHierarchyDigraph(GOType.biological_process, GOType.cellular_component,
                        GOType.molecular_function, terms);
            }
        return hierarchy;

//        String path = Config.getHierarchyPath();
//
//        if (data.relationName().contains("LD")) {
//            path = path + data.relationName() + ".txt";
//        } else {
//            path = path + data.relationName().replace(" ","") + "-threshold-3.txt";
//        }
//
//        GOTermDigraph hierarchy = GOTermDigraph.loadHierarchy(path);
//        return hierarchy;
    }

    public static ArrayList<Integer> redundantFeatures(GOTermDigraph hierarchy,Instances data, BitSet ind)
    {
        ArrayList<Integer> redundantFeatures = new ArrayList<>();
        double numFeaturesSelected = 0;

        GOTermDigraph reverse_hierarchy = hierarchy.reverse();

        for (int i = 0; i < data.numAttributes() - 1; i++) {
            boolean ehRedundante = false;
            if (ind.get(i)) {
                numFeaturesSelected++;
                String go = data.attribute(i).name();
                // Get ancestors
                ArrayList<String> a = reverse_hierarchy.allReachableGO(go);

                for (int k = 0; k < a.size(); k++) {
                    int index = data.attribute(a.get(k)).index();
                    if (ind.get(index))
                        ehRedundante = true;
                }

                // Get descendants
                ArrayList<String> d = hierarchy.allReachableGO(go);
                for (int k = 0; k < d.size(); k++) {
                    int index = data.attribute(d.get(k)).index();
                    if (ind.get(index))
                        ehRedundante = true;
                }

                if (ehRedundante)
                    redundantFeatures.add(i);
            }
        }

        return redundantFeatures;
    }

    public static double redundancyLevel(GOTermDigraph hierarchy,Instances data, BitSet ind)
    {
        ArrayList<Integer> redundantFeatures = new ArrayList<>();
        double numFeaturesSelected = 0;

        GOTermDigraph reverse_hierarchy = hierarchy.reverse();

        for (int i = 0; i < data.numAttributes() - 1; i++) {
            boolean ehRedundante = false;
            if (ind.get(i)) {
                numFeaturesSelected++;
                String go = data.attribute(i).name();
                // Get ancestors
                ArrayList<String> a = reverse_hierarchy.allReachableGO(go);

                for (int k = 0; k < a.size(); k++) {
                    int index = data.attribute(a.get(k)).index();
                    if (ind.get(index))
                        ehRedundante = true;
                }

                // Get descendants
                ArrayList<String> d = hierarchy.allReachableGO(go);
                for (int k = 0; k < d.size(); k++) {
                    int index = data.attribute(d.get(k)).index();
                    if (ind.get(index))
                        ehRedundante = true;
                }

                if (ehRedundante)
                    redundantFeatures.add(i);
            }
        }

        return ((double)redundantFeatures.size()/numFeaturesSelected);
    }
}
