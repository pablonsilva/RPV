package thesis.ageing.utils;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by pablonsilva on 11/2/16.
 */
public class Config {

    private static String _dataGoPath;
    private static String _get_path_hierarchy_raw;
    private static String _get_path_hierarchy_mix;
    private static String _getHierarchyPath;
    private static String _getGOPPIPath;
    private static String _getGOPPICompletePath;
    private static String _getGOPPIUncertainPath;
    private static String _getGOPPIThresholds;

    private static String _getMixDataPath;
    private static String _getOutputPath;
    private static int _getNumThreads;
    private static int _type;

    static
    {
        _type = 0;

        if(_type == 0) { //MacOS
            String base = "/Users/pablonsilva/Google Drive/Doutorado/Biology of Ageing/Data/";
            _dataGoPath = base + "GenAgev17/Base-gene_ontology_com_id/threshold/3/";
            _get_path_hierarchy_raw = base + "GO/biological process/go-basic.obo 08102015.txt";
        }
    }

    public static int getType()
    {
        return _type;
    }

    public static String getGOPPIThresholds() { return _getGOPPIThresholds;}

    public static String getDataGOPath() {
         String path = "./properties.txt";

        if (FileUtils.fileExist(path)) {
            FileReader fr = null;
            try {
                fr = new FileReader(path);
                BufferedReader br = new BufferedReader(fr);

                String line = br.readLine();
                    while (line != null) {
                        line = br.readLine();
                        if (line != null) {
                            if (line.contains("Path_Data_GO=")) {
                                String[] split = line.split("=");
                                _dataGoPath = split[1];
                            }
                        }
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return _dataGoPath;
    }

    public static String get_path_hierarchy_raw() {
        String path = "./properties.txt";

        //System.out.println(path);

        if (FileUtils.fileExist(path)) {
            FileReader fr = null;
            try {
                fr = new FileReader(path);
                BufferedReader br = new BufferedReader(fr);

                String line = br.readLine();
                while (line != null) {
                    line = br.readLine();
                    if (line != null) {
                        if (line.contains("Path_Hierarchy_Raw=")) {
                            String[] split = line.split("=");
                            _get_path_hierarchy_raw = split[1];
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return _get_path_hierarchy_raw;
    }
}