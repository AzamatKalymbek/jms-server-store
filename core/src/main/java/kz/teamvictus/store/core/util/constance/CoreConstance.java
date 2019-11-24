package kz.teamvictus.store.core.util.constance;

import java.io.File;

public class CoreConstance {
    public static String core = "C:\\PROJECTS\\COURSE\\PR_Algorithms\\src\\util\\files";
//    private static String core = "C:\\PROJECTS\\PATTERN\\PR_Algorithms\\src\\util\\files";
    public static File file = new File(core + "\\data.txt");
    public static File file2D = new File(core + "\\2d_data.txt");
    public static File output_file = new File(core + "\\output.txt");

    // Object and keys
    public static String clusterObjectsKey = "objects";
    public static String clusterCenterKey = "center";
}
