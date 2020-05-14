package bioinformatics.ageing.featureselection.hierarchy.published;

import bioinformatics.ageing.data.hierarchystructure.GOTermDigraph;
import bioinformatics.ageing.data.hierarchystructure.ST;
import bioinformatics.ageing.featureselection.general.FeatureSelectionClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
// RPV
public class RPV extends FeatureSelectionClassifier {
    private ST<String, Integer> _st;

    private HashMap<String, ArrayList<String>> _ancestor;
    private HashMap<String, ArrayList<String>> _descendant;
    private double[][] _mapEntropy;
    private ArrayList<String> gos;

    private double[] _r;

    public RPV(GOTermDigraph hierarchy, Classifier cls) {
        super(hierarchy, cls);
        _st = new ST<String, Integer>();

        _ancestor = new HashMap<String, ArrayList<String>>();
        _descendant = new HashMap<String, ArrayList<String>>();
    }

    public RPV(Classifier cls) {
        this(null, cls);
    }


//    public static void main(String[] args) {
//        String base = "MM-BP";
//        String path = Config.getDataGOPath() + base + "-threshold-3.arff";
//        TestClassifier tc = new TestClassifier();
//        tc.test(new NaiveBayesLazy(),path,"NB");
//        tc.test(new RPV(new NaiveBayesLazy(), RelevanceMeasureType.RLazy),path,"RPV");
//    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        assureHierarchy(data);

        _trainingData = data;

        // Get all GO terms available in dataset
        gos = new ArrayList<String>();
        for (int i = 0; i < data.numAttributes() - 1; i++) {
            gos.add(data.attribute(i).name());
        }
        // Add go term elements to String Table.
        // This aims to facilitate the access to the selected term array.
        // Additionaly, this method finds the ancestor and descendant list of
        // each go term.
        for (String go : gos) {
            if (!_st.contains(go)) {
                _st.put(go, _st.size());

                // Get ancestors
                _ancestor.put(go, _reverseHierarchy.allReachableGO(go));

                // Get descendants
                _descendant.put(go, _hierarchy.allReachableGO(go));
            }
        }
        _r = new double[data.numAttributes() - 1];

        _mapEntropy = new double[gos.size()][2];
        for (String go : gos) {
            int index = _st.get(go);
            _mapEntropy[index][0] = RLazy(_trainingData, go, 0);
            _mapEntropy[index][1] = RLazy(_trainingData, go, 1);
        }

        _cls.buildClassifier(_trainingData);
    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        for (String go : gos) {
            int index = _st.get(go);
            int attIndex = (int) instance.value(index);
            _r[index] = _mapEntropy[index][attIndex];
        }

        // Initialize the array that represents the status of an go term:
        // selected or remove
        boolean[] selected = new boolean[_trainingData.numAttributes() - 1];
        // Every term is initially set to selected.
        for (int i = 0; i < selected.length; i++)
            selected[i] = true;

        // Perform the feature selection
        for (int i = 0; i < _trainingData.numAttributes() - 1; i++) {
            // GO Name that is been evaluated
            String go = _trainingData.attribute(i).name();
            // Presence of go term
            if (instance.value(i) == 1) {
                // Get all ancestors of go
                ArrayList<String> ancestors = _ancestor.get(go);
                // set ancestor to remove
                for (int j = 0; j < ancestors.size(); j++) {
                    String key = ancestors.get(j);
                    if (_st.contains(key)) {
                        if (_r[_st.get(key)] < _r[_st.get(go)]) {
                            int index = _st.get(key);
                            selected[index] = false;
                        }
                    }
                }
            } else {
                // Set all go term whose value is 0 to false (non-selected)
                selected[_st.get(go)] = false;
                // Absence of go term
            }
        }

        return classifyWithReductionDist(instance, selected);
    }

    private double RLazy(Instances data, String go, int val) {
        double count_yes_pro = 0;
        double count_no_pro = 0;
        double count_yes = 0;
        double count_no = 0;

        int indexAtt = data.attribute(go).index();

        for (int i = 0; i < data.numInstances(); i++) {
            Instance inst = data.instance(i);
            double classValue = inst.classValue();

            // P(class|go=yes)
            if (classValue == 1 && inst.value(indexAtt) == 1) {
                count_yes_pro += 1;
            }
            // P(class|go=no)
            else if (classValue == 1 && inst.value(indexAtt) == 0) {
                count_no_pro += 1;
            }

            if (inst.value(indexAtt) == 1)
                count_yes += 1;
            else
                count_no += 1;
        }

        double p_pro_yes = (count_yes_pro + 1) / (count_yes + 2);
        double p_pro_no = (count_no_pro + 1) / (count_no + 2);

        double p_anti_yes = 1 - p_pro_yes;
        double p_anti_no = 1 - p_pro_no;

        double result = 0;
        if (val == 0)
            result = Math.pow((p_pro_no - p_anti_no), 2);
        else
            result = Math.pow((p_pro_yes - p_anti_yes), 2);

        return result;
    }

    public String toString() {
        return "RPV";
    }
}