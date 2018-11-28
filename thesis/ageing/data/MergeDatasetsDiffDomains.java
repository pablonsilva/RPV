package thesis.ageing.data;

import thesis.ageing.utils.DataUtils;
import thesis.ageing.utils.FileUtils;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by pablonsilva on 09/06/2017.
 */
public class MergeDatasetsDiffDomains
{
    private String _ppi;
    private String _go;
    private String _motifs;
    private String _kegg;

    public MergeDatasetsDiffDomains()
    {
        _ppi = "";
        _go = "";
        _motifs = "";
        _kegg = "";
    }

    public void saveFileWithentrezIDs()
    {
        ArrayList<String> proteins = new ArrayList<>();

        String dataPath = "/Users/pablonsilva/Google Drive/Doutorado/Biology of Ageing/Data/GenAgev17/Base-gene_ontology_com_id/threshold/3/";

        String[] base = {"CE","DM","MM","SC"};
        String[] hier = {"BP","CC","MF","BP+CC", "BP+MF", "CC+MF", "BP+CC+MF"};

        for(int i = 0 ; i < base.length; i++)
        {
            for(int j = 0 ; j < hier.length;j++)
            {
                String d = dataPath + base[i] + "-" + hier[j] + "-threshold-3.arff";

                Instances data = DataUtils.loadDataSet(d);

                for(Instance inst : data)
                {
                    if(!proteins.contains(inst.stringValue(0)))
                        proteins.add(inst.stringValue(0));
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for(String s : proteins)
            sb.append(s + "\n");
        FileUtils.saveFile(sb.toString(),"/Users/pablonsilva/Desktop/protiens.txt");
    }

    public static void main(String[] args)
    {

    }
}
