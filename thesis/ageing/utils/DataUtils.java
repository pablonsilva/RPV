package thesis.ageing.utils;

import thesis.ageing.data.build.BuildDatabase;
import thesis.ageing.data.build.DataType;
import thesis.ageing.data.build.GOStructureType;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.expressionlanguage.weka.InstancesHelper;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class is responsible for all data related methods that might be used across the entire project.
 *
 * @author pablonsilva
 * @version 20151203
 */
public class DataUtils {
    /**
     * This method load a weka.Instances dataset given the path where the arff file is saved.
     *
     * @param path for the arff file
     * @return weka.core.Instances file with a loaded dataset.
     */
    public static Instances loadDataSet(String path) {
        Instances data = null;

        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            data = new Instances(br);

            fr.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    /**
     * @param data
     * @return
     */
    public static double degreeOfImbalance(Instances data) {
        double[] v = DataUtils.numInstancesPerClass(data);
        if (v[0] > v[1]) {
            return 1 - (v[1] / v[0]);
        } else {
            return 1 - (v[0] / v[1]);
        }
    }

    /**
     * This method load a weka.Instances dataset given the path where the arff file is saved.
     *
     * @param path for the arff file
     * @return weka.core.Instances file with a loaded dataset.
     */
    public static Instances loadDataSetWithoutSupportAttributes(String path) {
        Instances data = null;

        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            data = new Instances(br);

            int index = -1;
            if (data.attribute("protein") != null) {
                index = data.attribute("protein").index();
                data.deleteAttributeAt(index);
            }
            if (data.attribute("entrez") != null) {
                index = data.attribute("entrez").index();
                data.deleteAttributeAt(index);
            }

            if (data.attribute("symbol") != null) {
                index = data.attribute("symbol").index();
                data.deleteAttributeAt(index);
            }
            if (data.attribute("GO:0008150") != null) {
                index = data.attribute("GO:0008150").index();
                data.deleteAttributeAt(index);
            }
            if (data.attribute("GO:0003674") != null) {
                index = data.attribute("GO:0003674").index();
                data.deleteAttributeAt(index);
            }
            if (data.attribute("GO:0005575") != null) {
                index = data.attribute("GO:0005575").index();
                data.deleteAttributeAt(index);
            }
            if (data.attribute("ROOT") != null) {
                index = data.attribute("ROOT").index();
                data.deleteAttributeAt(index);
            }

            fr.close();
            br.close();
        } catch (
                IOException e)

        {
            e.printStackTrace();
        }
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    /**
     * @param data
     * @return
     */
    public static double[] numInstancesPerClass(Instances data) {
        double[] v = new double[2];
        v[0] = 0;
        v[1] = 0;

        for (int i = 0; i < data.numInstances(); i++) {
            if (data.instance(i).classValue() == 1)
                v[0] += 1;
            else
                v[1] += 1;
        }

        return v;
    }

    /**
     * @param data
     * @param type
     * @return
     */
    public static String getAgeingDatasetGO(DataType data, GOStructureType type) {
        String path = "/Users/pablonsilva/Dropbox/Compartilhada/Doutorado/Biology of Ageing/Data/GenAgev17/gene_ontology_mix/Threshold/3/";
        //String path = "/Users/pablonsilva/Dropbox/Compartilhada/Doutorado/Biology of Ageing/Data/GenAgev18/mix_ontology/Threshold/3/";
        String name = data + "-" + type.toString().replace("_", "+");

        String pathData = path + name + "-threshold-3.arff";

        return pathData;
    }

    public static String getPPI(DataType data) {
        //String path = "/Users/pablonsilva/Dropbox/Compartilhada/Doutorado/Biology of Ageing/Data/BasesGO-PPI/";
        String path = "/Users/pablonsilva/Desktop/BasesGO-PPI2/score-900/";

        String name = data + "-".replace("_", "+");

        String pathData = path + name + "threshold-3.arff";

        return pathData;
    }

    public static String getGOPPI(DataType data, GOStructureType type) {
        String path = "/Users/Users/pablonsilva/Google Drive/Doutorado/Biology of Ageing/Data/PPI/PPIs - STRING/base_go_ppi_string/800/Threshold/";

        String name = data + "-" + type.toString().replace("_", "+");

        String pathData = path + name + "-threshold-3.arff";

        return pathData;
    }

    public static String getDataset(DataType data) {

        String path3 = "/Users/pablonsilva/Dropbox/Compartilhada/Doutorado/Biology of Ageing/Data/Bases_paper_shsel/Threshold/3/";
        String path = "/Users/pablonsilva/Dropbox/Compartilhada/Doutorado/Biology of Ageing/Data/Bases_paper_shsel/";
        String name = data.name();
        String pathData = "";
        if (!name.contains("LDD"))
            pathData = path3 + name + "-threshold-3.arff";
        else
            pathData = path + name + ".arff";

        return pathData;
    }

    public static Instances reduce(Instances data, boolean[] subset) throws Exception {
        String indices = "";
        for (int i = 0; i < subset.length; i++) {
            if (subset[i]) {
                indices += (i + 1) + ",";
            }
        }
        indices += (data.classIndex() + 1);

        Instances tData = null;
        Remove rmv = new Remove();
        rmv.setInvertSelection(true);
        rmv.setAttributeIndices(indices);
        rmv.setInputFormat(data);

        tData = Filter.useFilter(data, rmv);
        tData.setClassIndex(tData.numAttributes() - 1);

        return tData;
    }

    public static int getNumberOfNonPPIFeatures(Instances data)
    {
        int number_of_non_ppi = 0;
        for(int i = 0 ; i < data.numAttributes();i++)
        {
            if(!data.attribute(i).name().startsWith("6239.") &&
                    !data.attribute(i).name().startsWith("7227.") &&
                    !data.attribute(i).name().startsWith("10090.") &&
                    !data.attribute(i).name().startsWith("4932."))
            {
                number_of_non_ppi++;
            }
        }
        return number_of_non_ppi;
    }

    public static int getNumberOfGOTerms(Instances data)
    {
        int number_of_go = 0;
        for(int i = 0 ; i < data.numAttributes();i++)
        {
            if(data.attribute(i).name().contains("GO:"))
            {
                number_of_go++;
            }
        }
        return number_of_go;
    }

    public static Instances transformData(Instances d, double threshold, boolean t)
    {
        Instances data = new Instances(d);
        String aux_file = data.toString();

        Random r = new Random();

        String aux_name = d.relationName() + "-" + Math.abs(r.nextInt()) + "-";

        Instances data_new = null;
        FileUtils.removeFile(Config.getOutputPath() + aux_name + "aux_data.arff");

        FileUtils.saveFile(aux_file,Config.getOutputPath() + aux_name + "aux_data.arff");

        StringBuilder sb_newFile = new StringBuilder();

        try {
            FileReader fr = new FileReader(Config.getOutputPath() + aux_name + "aux_data.arff");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            boolean ini = false;
            while(line != null)
            {
                if(ini)
                {
                    StringBuilder sb_newLine = new StringBuilder();
                    String[] line_break = line.split(",");
                    for(int i = 0 ; i < line_break.length;i++)
                    {
                        if(i == line_break.length-1)
                        {
                            //class
                            sb_newLine.append(line_break[i]);
                        }
                        else if(i < DataUtils.getNumberOfGOTerms(data))
                        {
                            // GO TERMS
                            sb_newLine.append(line_break[i] + ",");
                        }
                        else {
                            // PPI
                            double val = 0;

                            try {
                                val = Double.parseDouble(line_break[i]);
                                if (val >= threshold) {
                                    sb_newLine.append("'1',");
                                } else {
                                    sb_newLine.append("'0',");
                                }
                            }catch (Exception e){
                                //System.out.println(line_break[i] + " - " + val);
                                sb_newLine.append("'0',");
                            }
                        }
                    }
                    sb_newFile.append(sb_newLine.toString() + "\n");
                }
                else {
                    String aux = line;
                    if(line.contains("numeric"))
                        aux = line.replace("numeric","{'1','0'}");
                    sb_newFile.append(aux + "\n");

                    if (line.contains("@data")) {
                        ini = true;
                    }
                }
                line = br.readLine();
            }

            fr.close();
            br.close();

            FileUtils.removeFile(Config.getOutputPath() + aux_name + "aux_data.arff");
            FileUtils.saveFile(sb_newFile.toString(),Config.getOutputPath() + aux_name + "aux_data.arff");

            data_new = DataUtils.loadDataSet(Config.getOutputPath() + aux_name + "aux_data.arff");

            FileUtils.removeFile(Config.getOutputPath() + aux_name + "aux_data.arff");

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(t)
            data_new = DataUtils.thresholdDataPPI(data_new,3);

        return data_new;
    }

    /***
     * Aplica o threshold
     *
     * @param data
     * @param threshold
     * @return
     */
    private static Instances thresholdFeatures(Instances data, int threshold) {
        Instances dataAux = new Instances(data);
        dataAux.setClassIndex(data.numAttributes() - 1);
        int[] a = numGOTerms(data);

        for (int i = data.numAttributes() - 2; i > 0; i--) {
            //System.out.println(data.attribute(i).name() + " - " + a[i]);
            if (a[i] < threshold) {
                //System.out.println("Removed");
                dataAux.deleteAttributeAt(i);
            }
        }


        // Se uma instancia nao tiver PPIs deve-se configura os atributos como missing-values
        for (int j = data.numInstances() - 1; j >= 0; j--) {
            int numPPIs = 0;
            for (int i = data.numAttributes() - 2; i > 0; i--) {
                if (!data.attribute(i).name().contains("GO:")) {
                    numPPIs++;
                }
            }
            if (numPPIs == 0) {
                for (int i = 1; i < data.numAttributes() - 2; i++) {
                    if (!dataAux.attribute(i).name().contains("GO:"))
                        dataAux.instance(j).setValue(i, Double.NaN);
                }
            }
        }

        return dataAux;
    }

    /**
     * Numero de vezes que cada GO term aparece na base.
     *
     * @param data
     * @return
     */
    private static int[] numGOTerms(Instances data) {
        int[] numTotal = new int[data.numAttributes()];

        for (int i = 0; i < data.numInstances(); i++) {
            double[] vec = data.instance(i).toDoubleArray();
            for (int j = 0; j < vec.length; j++)
                if (!Double.isNaN(vec[j]))
                    numTotal[j] += vec[j];
        }

        return numTotal;
    }



    public static Instances removeFeaturesFromSecondBasedOnTheFirstDataset(Instances firstData, Instances secondData)
    {
        ArrayList<String> _featuresToKeep = new ArrayList<>();

        Instances newData = new Instances(secondData);

        for(int i = 0 ; i < firstData.numAttributes();i++)
        {
            _featuresToKeep.add(firstData.attribute(i).name());
        }

        for(int i = 0 ; i < secondData.numAttributes();i++)
        {
            if(!_featuresToKeep.contains(secondData.attribute(i).name()))
            {
                int index = newData.attribute(secondData.attribute(i).name()).index();
                newData.deleteAttributeAt(index);
            }
        }

        return newData;
    }


    public static int numberOfAttributesWithLessPostiveValuesThanAGivenThreshold(Instances dataset, int threshold)
    {
        int[] num = numberOfInstancesWithPositiveValuesForEachAtt(dataset);
        int count = 0;

        for(int i = 0; i < num.length; i++)
        {
            if(num[i] < threshold)
            {
                count++;
            }
        }

        return count;
    }

    public static int[] numberOfInstancesWithPositiveValuesForEachAtt(Instances dataset)
    {
        int[] values = new int[dataset.numAttributes()];

        for(int i = 0; i < dataset.numAttributes();i++)
        {
            values[i] = numberOfInstancesWithPositiveValues(dataset,i);
        }

        return values;
    }

    public static int numberOfInstancesWithPositiveValues(Instances data, int attribute_index)
    {
        int count = 0;
        for(int i = 0 ; i < data.numInstances(); i++)
        {
            Instance inst = data.get(i);
            if(inst.attribute(attribute_index).isNumeric()) {
                if (inst.value(attribute_index) > 0) {
                    count++;
                }
            }else
            {
                if (inst.stringValue(attribute_index).equalsIgnoreCase("1")) {
                    count++;
                }
            }
        }
        return count;
    }

    public static void createPartitions(String path, int seed)
    {
        int nFolds = 10;
        File aDirectory = new File(path);

        // get a listing of all files in the directory
        String[] filesInDir = aDirectory.list();

        for ( int i=0; i<filesInDir.length; i++ ) {
            System.out.println("Arquivo: " + filesInDir[i]);
            if (filesInDir[i].contains(".arff")) {
                System.out.println("eh Arff");

                Instances data = DataUtils.loadDataSet(path + filesInDir[i]);

                if (!FileUtils.directoryExists(path +
                        data.relationName().replace(" ", ""))) {

                    // create seeded number generator
                    Random rand = new Random(seed);
                    // create copy of original data
                    Instances randData = new Instances(data);
                    // randomize data with number generator
                    randData.randomize(rand);
                    randData.stratify(nFolds);

                    for (int c = 0; c < 10; c++) {
                        System.out.println("10 particoes " + (c + 1) + "/10");

                        Instances train = randData.trainCV(nFolds, c);
                        Instances test = randData.testCV(nFolds, c);

                        FileUtils.createDir(path +
                                data.relationName().replace(" ", ""));

                        FileUtils.createDir(path +
                                data.relationName().replace(" ", "") +
                                "/train/");
                        FileUtils.createDir(path +
                                data.relationName().replace(" ", "") +
                                "/test/");

                        FileUtils.saveFile(train.toString(), path +
                                data.relationName().replace(" ", "") +
                                "/train/"
                                + "train-" + c + ".arff", false);
                        FileUtils.saveFile(test.toString(), path +
                                data.relationName().replace(" ", "") +
                                "/test/"
                                + "test-" + c + ".arff", false);

                        Instances trainCopy = new Instances(train);
                        trainCopy.randomize(rand);
                        trainCopy.stratify(5);
                        System.out.println("Particoes Internas - fold " + (c + 1));
                        for (int h = 0; h < 5; h++) {

                            System.out.println("5 particoes " + (h + 1) + "/5");

                            Instances train_part = trainCopy.trainCV(5, h);
                            Instances test_part = trainCopy.testCV(5, h);

                            FileUtils.createDir(path +
                                    data.relationName().replace(" ", "") +
                                    "/train/internal/");
                            FileUtils.createDir(path +
                                    data.relationName().replace(" ", "") +
                                    "/train/internal/" + c + "");
                            FileUtils.createDir(path +
                                    data.relationName().replace(" ", "") +
                                    "/train/internal/" + c + "/5cv/");

                            FileUtils.saveFile(train_part.toString(), path +
                                    data.relationName().replace(" ", "") +
                                    "/train/internal/" + c + "/5cv/"
                                    + "train-internal-" + h + ".arff", false);
                            FileUtils.saveFile(test_part.toString(), path +
                                    data.relationName().replace(" ", "") +
                                    "/train/internal/" + c + "/5cv/"
                                    + "test-internal-" + h + ".arff", false);

                        }
                        System.out.println("Fim");
                    }
                }
            }
        }
    }

    public static Instances removeGOTerms(Instances data)
    {
        Instances d = new Instances(data);

        StringBuilder sb = new StringBuilder();

        for(int a = 0; a < d.numAttributes();a++)
        {
            String att_name = d.attribute(a).name();

            if(!att_name.contains("GO:"))
            {
                sb.append((data.attribute(att_name).index()+1) + ",");
            }
        }
        sb.deleteCharAt(sb.length()-1);

        Remove rmv = new Remove();
        Instances newData = null;

        try {
            rmv.setAttributeIndices(sb.toString());
            rmv.setInvertSelection(true);
            rmv.setInputFormat(data);

            newData = Filter.useFilter(data, rmv);
        }catch (Exception e)
        {

        }

        newData.setRelationName(data.relationName().replace("GO_",""));

        return newData;
    }

    public static Instances thresholdDataPPI(Instances data, int threshold)
    {
        Instances d = new Instances(data);

        int[] vet = DataUtils.numberOfInstancesWithPositiveValuesForEachAtt(data);

        StringBuilder sb = new StringBuilder();

        for(int a = 0; a < vet.length;a++)
        {
            String att_name = d.attribute(a).name();

            if((att_name.contains("GO:") || att_name.contains(".")) && (vet[a] < threshold))
            {
                sb.append((data.attribute(att_name).index()+1) + ",");
            }
        }
        if(sb.length() > 0)
            sb.deleteCharAt(sb.length()-1);

        Remove rmv = new Remove();
        Instances newData = null;

        try {
            rmv.setAttributeIndices(sb.toString());
            rmv.setInvertSelection(false);
            rmv.setInputFormat(data);

            newData = Filter.useFilter(data, rmv);
        }catch (Exception e)
        {

        }

        newData.setRelationName(data.relationName());

        return newData;
    }
}
