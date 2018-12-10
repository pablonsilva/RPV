package bioinformatics.ageing.featureselection.general;

import bioinformatics.ageing.classifiers.NaiveBayesLazy;
import bioinformatics.ageing.data.build.BuildHierarchy;
import bioinformatics.ageing.data.build.GOType;
import bioinformatics.ageing.data.hierarchystructure.GOTermDigraph;
import bioinformatics.ageing.utils.DataUtils;
import bioinformatics.ageing.utils.MathUtils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

public class FeatureSelectionClassifier extends AbstractClassifier {
    protected ArrayList<Integer> _numFeatures;
    protected ArrayList<ArrayList<Integer>> _features;
    protected Instances _trainingData;
    protected Classifier _cls;

    protected GOTermDigraph _hierarchy;
    protected GOTermDigraph _reverseHierarchy;

    public FeatureSelectionClassifier(GOTermDigraph hierarchy, Classifier cls) {
        _numFeatures = new ArrayList<Integer>();
        _features = new ArrayList<ArrayList<Integer>>();
        _cls = cls;

        _numFeatures = new ArrayList<Integer>();
        if (hierarchy != null) {
            _hierarchy = hierarchy;
            _reverseHierarchy = _hierarchy.reverse();
        }
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {

    }

    public int getLastInstanceNumberOfFeatures() {
        return _numFeatures.get(_numFeatures.size() - 1);
    }

    public ArrayList<Integer> getLastInstanceSelectedFeatures() {
        if(_features == null)
            return null;
        else
            return _features.get(_features.size() - 1);
    }

    public int[] getNumberFeatures() {

        int[] nFeat = new int[_numFeatures.size()];

        for (int i = 0; i < _numFeatures.size(); i++) {
            nFeat[i] = _numFeatures.get(i);
        }

        return nFeat;
    }

    public double avgNumberFeatures() {
        return MathUtils.average(getNumberFeatures());
    }

    public double sdNumberFeatures() {
        return MathUtils.sd(getNumberFeatures());
    }

    public double seNumberFeatures() {
        return MathUtils.se(getNumberFeatures());
    }

    protected void assureHierarchy(Instances data) {
        if (_hierarchy == null) {
            BuildHierarchy bh = new BuildHierarchy();
            ArrayList<String> terms = new ArrayList<String>();
            for (int i = 0; i < data.numAttributes() - 1; i++)
                terms.add(data.attribute(i).name());

            if (data.relationName().contains("Teste")) {
                _hierarchy = bh.fakeHierarchy();
            } else if (data.relationName().contains("LD")) {
                // is an idividual hierarchy
                _hierarchy = bh.getHierarchyDigraph(data.relationName(), terms);
            } else {
                // is a biological hierarchy
                _hierarchy = bh.getHierarchyDigraph(GOType.biological_process, GOType.cellular_component,
                        GOType.molecular_function, terms);
            }
        }
        _reverseHierarchy = _hierarchy.reverse();
    }

    protected double classifyWithReduction(Instance instance, boolean[] selected) throws Exception {
        // Reduces the dataset and employ the base classifier
        String indices = "";
        ArrayList<Integer> features = new ArrayList<Integer>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                features.add(i);
                indices += (i + 1) + ",";
                //System.out.print(instance.attribute(i).name() +" - ");
            }
        }
        indices += (_trainingData.classIndex() + 1);

        //System.out.println(" Num Selected Features: " + features.size());

        _numFeatures.add(features.size());
        _features.add(features);

        Instances n_Train = DataUtils.reduce(_trainingData,selected);

        Instances aux = new Instances(_trainingData,0);
        aux.add(instance);

        Instance n_Test = DataUtils.reduce(aux,selected).firstInstance();

        // Use the classifier to produces the result

        if (!(_cls instanceof NaiveBayesLazy))
            _cls.buildClassifier(n_Train);

        double result = _cls.classifyInstance(n_Test);

        //_hierarchy.print(n_Train);

        //System.out.println("result: " + result);

        return result;
    }

    protected double[] classifyWithReductionDist(Instance instance, boolean[] selected) throws Exception {
        // Reduces the dataset and employ the base classifier
        String indices = "";
        ArrayList<Integer> features = new ArrayList<Integer>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                features.add(i);
                indices += (i + 1) + ",";
                //System.out.print(instance.attribute(i).name() +" - ");
            }
        }
        indices += (_trainingData.classIndex() + 1);

        //System.out.println(" Num Selected Features: " + features.size());

        _numFeatures.add(features.size());
        _features.add(features);

        Instances n_Train = DataUtils.reduce(_trainingData,selected);

        Instances aux = new Instances(_trainingData,0);
        aux.add(instance);

        Instance n_Test = DataUtils.reduce(aux,selected).firstInstance();

        // Use the classifier to produces the result

        if (!(_cls instanceof NaiveBayesLazy))
            _cls.buildClassifier(n_Train);

        double[] result = _cls.distributionForInstance(n_Test);

        //_hierarchy.print(n_Train);

        //System.out.println("result: " + result);

        return result;
    }

    protected double classifyWithReductionRetrain(Instance instance, boolean[] selected) throws Exception {
        // Reduces the dataset and employ the base classifier
        String indices = "";
        ArrayList<Integer> features = new ArrayList<Integer>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                features.add(i);
                indices += (i + 1) + ",";
                //System.out.print(instance.attribute(i).name() +" - ");
            }
        }
        indices += (_trainingData.classIndex() + 1);

        //System.out.println(" Num Selected Features: " + features.size());

        _numFeatures.add(features.size());
        _features.add(features);

        Instances n_Train = DataUtils.reduce(_trainingData,selected);

        Instances aux = new Instances(_trainingData,0);
        aux.add(instance);

        Instance n_Test = DataUtils.reduce(aux,selected).firstInstance();

        // Use the classifier to produces the result

        _cls.buildClassifier(n_Train);

        double result = _cls.classifyInstance(n_Test);

        //System.out.println("result: " + result + "-"+ n_Test.classValue());

        return result;
    }

    protected double classifyWithReduction(Instance instance, Instances training, boolean[] selected) throws Exception {
        // Reduces the dataset and employ the base classifier
        String indices = "";
        ArrayList<Integer> features = new ArrayList<Integer>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                features.add(i);
                indices += (i + 1) + ",";
                //System.out.print(instance.attribute(i).name() +" - ");
            }
        }
        indices += (training.classIndex() + 1);

        //System.out.println(" Num Selected Features: " + features.size());

        _numFeatures.add(features.size());
        _features.add(features);

        Instances n_Train = DataUtils.reduce(training,selected);

        Instances aux = new Instances(training,0);
        aux.add(instance);

        Instance n_Test = DataUtils.reduce(aux,selected).firstInstance();

        // Use the classifier to produces the result

        //if (!(_cls instanceof NaiveBayesLazy))
        _cls.buildClassifier(n_Train);

        double result = _cls.classifyInstance(n_Test);
        //_hierarchy.print(n_Train);
        //System.out.println("result: " + result + " - " + n_Test.classValue());

        return result;
    }
}
