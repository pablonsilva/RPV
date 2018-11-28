package thesis.ageing.featureselection.hierarchy.published;

import thesis.ageing.classifiers.NaiveBayesLazy;
import thesis.ageing.classifiers.distance.JaccardDistance;
import thesis.ageing.data.hierarchystructure.GOTermDigraph;
import thesis.ageing.data.hierarchystructure.ST;
import thesis.ageing.featureselection.general.FeatureSelectionClassifier;
import thesis.ageing.featureselection.general.RelevanceMeasureEval;
import thesis.ageing.featureselection.general.RelevanceMeasureType;
import thesis.ageing.featureselection.general.TestClassifier;
import thesis.ageing.utils.Config;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
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
    private RelevanceMeasureType _measureType;
    private double[][] _mapEntropy;
    private ArrayList<String> gos;

    private RelevanceMeasureEval _mEval;

    private double[] _r;

    public RPV(GOTermDigraph hierarchy, Classifier cls) {
        super(hierarchy, cls);
        _st = new ST<String, Integer>();

        _ancestor = new HashMap<String, ArrayList<String>>();
        _descendant = new HashMap<String, ArrayList<String>>();
        _measureType = RelevanceMeasureType.R;
    }

    public RPV(Classifier cls) {
        this(null, cls);
    }

    public RPV(Classifier cls, RelevanceMeasureType t) {
        this(null, cls);
        _measureType = t;
    }

    public static void main(String[] args) {
        String base = "MM-BP";
        String path = Config.getDataGOPath() + base + "-threshold-3.arff";
        TestClassifier tc = new TestClassifier();
        tc.test(new NaiveBayesLazy(),path,"NB");
        IBk ibk1 = new IBk(1);
        try {
            ibk1.getNearestNeighbourSearchAlgorithm().setDistanceFunction(new JaccardDistance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tc.test(ibk1,path,"1NN J",true);
        tc.test(new HIP(new NaiveBayesLazy()),path,"HIP");
        tc.test(new RPV(new NaiveBayesLazy(), RelevanceMeasureType.RLazy),path,"RPV");
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        assureHierarchy(data);

        _mEval = new RelevanceMeasureEval();

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
            if (_mEval.isLazyRelevance(_measureType)) {
                _mapEntropy[index][0] = _mEval.getValue(_measureType, _trainingData, go, 0);
                _mapEntropy[index][1] = _mEval.getValue(_measureType, _trainingData, go, 1);
            } else {
                _r[index] = _mEval.getValue(_measureType, _trainingData, go);
            }
        }

        _cls.buildClassifier(_trainingData);
    }

    public double[] distributionForInstance(Instance instance) throws Exception {
        if (_mEval.isLazyRelevance(_measureType)) {
            for (String go : gos) {
                int index = _st.get(go);
                int attIndex = (int) instance.value(index);
                //_r[index] = RelevanceMeasure.getValue(_measureType, _trainingData, go, attIndex);
                _r[index] = _mapEntropy[index][attIndex];
            }
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

        public String toString() {
        return "RPV";
    }
}