package ivory.regression.sigir2013.moses;

import ivory.core.eval.Qrels;
import ivory.regression.GroundTruth;
import ivory.regression.GroundTruth.Metric;
import ivory.smrf.retrieval.Accumulator;
import ivory.sqe.retrieval.Constants;
import ivory.sqe.retrieval.QueryEngine;
import ivory.sqe.retrieval.RunQueryEngine;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import junit.framework.JUnit4TestAdapter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Test;
import com.google.common.collect.Maps;
import edu.umd.cloud9.collection.DocnoMapping;
import edu.umd.cloud9.io.map.HMapSFW;

public class EnZh_NTCIR8 {
  private QueryEngine qe;
  private static String PATH = "en-zh.ntcir8";
  private static String LANGUAGE = "zh";
  private static int numTopics = 73;
  
  private static float expectedTokenMAP = 0.1484f;
  private static Map<Integer,float[]> expectedMAPs = new HashMap<Integer,float[]>();
  
  private static Map<Integer, String[]> grammar_AP = new HashMap<Integer, String[]>();
  private static Map<Integer, String[]> Nbest_AP = new HashMap<Integer, String[]>();
  private static Map<Integer, String[]> Onebest_AP = new HashMap<Integer, String[]>();
  private static Map<Integer, String[]> Interp_AP = new HashMap<Integer, String[]>();
  
  public static void initialize() {
    expectedMAPs.put(0, new float[]{0.1647f, 0.1457f, 0.1624f, 0.1786f});   // "one2none" -> grammar,1best,10best,interp
    expectedMAPs.put(1, new float[]{0.1507f, 0.1549f, 0.1629f, 0.1753f});   // "one2one" -> grammar,1best,10best,interp
    expectedMAPs.put(2, new float[]{0.1559f, 0.15f, 0.1688f, 0.1774f});   // "one2many" -> grammar,1best,10best,interp
    grammar_AP.put(0, new String[] {
        "78", "0.2268","77", "0.2833","35", "0.0025","36", "0.0037","33", "0.3593","39", "0.024","38", "0.0","43", "0.0673","42", "0.2807","41", "0.0844","40", "0.0","82", "0.2387","83", "0.125","80", "0.0923","87", "0.2687","84", "0.3897","85", "0.1472","67", "0.1674","66", "0.0071","69", "0.0","68", "0.6848","23", "0.1406","26", "0.0138","28", "0.4743","29", "0.1019","2", "0.0678","30", "0.1548","6", "0.0235","32", "0.2258","5", "0.0016","70", "0.02","71", "0.4932","9", "0.1003","72", "0.3698","73", "0.0015","74", "0.059","75", "0.0023","76", "0.1204","59", "0.0367","58", "0.1107","57", "0.2241","19", "0.2735","56", "0.1247","18", "1.0E-4","15", "0.2153","16", "0.0622","12", "0.0545","64", "0.3344","65", "0.4672","62", "0.4913","63", "0.1856","99", "0.4383","61", "0.0313","100", "0.102","98", "0.4284","49", "0.0","97", "0.0088","48", "0.002","96", "0.0383","95", "0.0067","94", "0.1036","45", "0.065","93", "0.1538","44", "0.3571","92", "0.2314","47", "0.0","91", "0.6417","46", "0.0442","90", "0.474","51", "0.0","52", "0.1534","53", "0.0","54", "0.3374"
    });
    grammar_AP.put(1, new String[] {
        "78", "0.2521","77", "0.254","35", "0.002","36", "0.0035","33", "0.3444","39", "0.0244","38", "0.0","43", "0.083","42", "0.2373","41", "0.062","40", "1.0E-4","82", "0.2404","83", "0.1241","80", "0.0877","87", "0.2532","84", "0.4002","85", "0.1138","67", "0.1604","66", "0.0064","69", "0.0","68", "0.6812","23", "0.1318","26", "0.0314","28", "0.4595","29", "0.0577","2", "0.136","30", "0.3204","6", "0.0077","32", "0.1972","5", "0.0015","70", "0.0148","71", "0.4915","9", "0.1254","72", "0.3808","73", "0.0015","74", "0.0349","75", "0.0026","76", "0.115","59", "0.0605","58", "0.1007","57", "0.2159","19", "0.1566","56", "0.1227","18", "3.0E-4","15", "0.0641","16", "0.0697","12", "0.0539","64", "0.381","65", "0.4601","62", "0.4805","63", "0.004","99", "0.4633","61", "0.044","100", "0.1025","98", "0.4092","49", "0.0","97", "0.0194","48", "0.0016","96", "0.0337","95", "0.0029","94", "0.0587","45", "0.0853","93", "0.1789","44", "0.0878","92", "0.2366","47", "0.0","91", "0.6598","46", "0.0465","90", "0.0719","51", "0.0","52", "0.1534","53", "0.0","54", "0.3376",
    });
    grammar_AP.put(2, new String[] {
        "78", "0.285","77", "0.2825","35", "0.0026","36", "0.0036","33", "0.3451","39", "0.0244","38", "0.0","43", "0.0685","42", "0.2809","41", "0.0776","40", "0.0","82", "0.2331","83", "0.1257","80", "0.0977","87", "0.2645","84", "0.3899","85", "0.0976","67", "0.1771","66", "0.0081","69", "0.0","68", "0.6822","23", "0.145","26", "0.0153","28", "0.4532","29", "0.0554","2", "0.0494","30", "0.053","6", "0.01","32", "0.1533","5", "0.0017","70", "0.0285","71", "0.4933","9", "0.1807","72", "0.3629","73", "0.0014","74", "0.0587","75", "0.0023","76", "0.1711","59", "0.0374","58", "0.1118","57", "0.2112","19", "0.2735","56", "0.1285","18", "2.0E-4","15", "0.3116","16", "0.0735","12", "0.0542","64", "0.3494","65", "0.4686","62", "0.4797","63", "0.04","99", "0.431","61", "0.0306","100", "0.1024","98", "0.4018","49", "0.0","97", "0.0116","48", "0.0021","96", "0.036","95", "0.0056","94", "0.1052","45", "0.0949","93", "0.1563","44", "0.3376","92", "0.2337","47", "0.0","91", "0.6176","46", "0.045","90", "0.031","51", "0.0","52", "0.1534","53", "0.0","54", "0.3638",
    });
    Nbest_AP.put(0, new String[] {
        "78", "0.1939","77", "0.2594","35", "0.0019","36", "0.0022","33", "0.3507","39", "0.1125","38", "0.0","43", "0.0586","42", "0.1634","41", "0.225","40", "1.0E-4","82", "0.2835","83", "0.1208","80", "0.0453","87", "0.259","84", "0.3408","85", "0.0809","67", "0.1099","66", "0.0137","69", "0.0","68", "0.7284","23", "0.1503","26", "0.0","28", "0.4829","29", "0.0564","2", "0.119","30", "0.5261","6", "0.0051","32", "0.4065","5", "0.0","70", "0.0052","71", "0.5278","9", "0.1028","72", "0.1549","73", "0.0294","74", "0.0718","75", "0.0037","76", "0.0352","59", "0.0193","58", "0.0843","57", "0.221","19", "0.238","56", "0.0893","18", "0.0301","15", "0.5072","16", "0.0451","12", "0.052","64", "0.4703","65", "0.4118","62", "0.4401","63", "0.0013","99", "0.349","61", "0.0168","100", "0.0067","98", "0.435","49", "0.0","97", "0.0016","48", "2.0E-4","96", "0.0305","95", "0.0","94", "0.5248","45", "0.2663","93", "0.0373","44", "0.3197","92", "0.2045","47", "0.0","91", "0.6821","46", "0.1407","90", "0.0","51", "0.0","52", "0.1534","53", "0.0","54", "0.0489",
    });
    Nbest_AP.put(1, new String[] {
        "78", "0.1939","77", "0.2478","35", "0.0019","36", "0.0022","33", "0.3489","39", "0.1125","38", "0.0","43", "0.0586","42", "0.1634","41", "0.225","40", "1.0E-4","82", "0.2835","83", "0.1208","80", "0.0453","87", "0.2586","84", "0.3408","85", "0.0809","67", "0.1099","66", "0.0137","69", "0.0","68", "0.7284","23", "0.1503","26", "0.0","28", "0.4829","29", "0.0564","2", "0.119","30", "0.4551","6", "0.0053","32", "0.4065","5", "0.0","70", "0.0052","71", "0.5278","9", "0.1028","72", "0.1549","73", "0.0294","74", "0.0718","75", "0.0037","76", "0.0327","59", "0.0193","58", "0.0843","57", "0.221","19", "0.238","56", "0.0893","18", "0.0301","15", "0.5072","16", "0.0417","12", "0.052","64", "0.4703","65", "0.4118","62", "0.4401","63", "0.0035","99", "0.349","61", "0.0168","100", "0.0067","98", "0.435","49", "0.0","97", "0.0016","48", "2.0E-4","96", "0.0305","95", "0.0","94", "0.5248","45", "0.2663","93", "0.0373","44", "0.3197","92", "0.2045","47", "0.0","91", "0.6995","46", "0.1407","90", "0.1092","51", "0.0","52", "0.1534","53", "0.0","54", "0.0489",
    });
    Nbest_AP.put(2, new String[] {
        "78", "0.1939","77", "0.2562","35", "0.0019","36", "0.0022","33", "0.3498","39", "0.1125","38", "0.0","43", "0.0586","42", "0.1634","41", "0.225","40", "1.0E-4","82", "0.2835","83", "0.1208","80", "0.0453","87", "0.2572","84", "0.3408","85", "0.0809","67", "0.1099","66", "0.0137","69", "0.0","68", "0.7284","23", "0.1503","26", "0.0","28", "0.4829","29", "0.0564","2", "0.119","30", "0.5326","6", "0.005","32", "0.4065","5", "0.0","70", "0.0052","71", "0.5278","9", "0.1028","72", "0.1549","73", "0.0294","74", "0.0718","75", "0.0037","76", "0.0428","59", "0.0193","58", "0.0843","57", "0.221","19", "0.238","56", "0.0893","18", "0.0301","15", "0.5072","16", "0.0451","12", "0.052","64", "0.4703","65", "0.4118","62", "0.4401","63", "0.0088","99", "0.349","61", "0.0168","100", "0.0067","98", "0.435","49", "0.0","97", "0.0016","48", "2.0E-4","96", "0.0305","95", "0.0","94", "0.5248","45", "0.2663","93", "0.0373","44", "0.3197","92", "0.2045","47", "0.0","91", "0.6488","46", "0.1407","90", "0.4868","51", "0.0","52", "0.1534","53", "0.0","54", "0.0489",
    });
    Onebest_AP.put(0, new String[] {
        "78", "0.194","77", "0.2578","35", "0.0016","36", "0.0016","33", "0.313","39", "0.1222","38", "0.0","43", "0.0573","42", "0.1508","41", "0.2359","40", "1.0E-4","82", "0.2507","83", "0.1208","80", "0.0453","87", "0.0017","84", "0.2594","85", "0.1141","67", "0.1099","66", "0.0137","69", "0.0","68", "0.7284","23", "0.1516","26", "0.0","28", "0.5354","29", "0.011","2", "0.0685","30", "0.0742","6", "0.0054","32", "0.4273","5", "0.0","70", "0.0161","71", "0.4909","9", "0.0959","72", "0.1549","73", "3.0E-4","74", "0.072","75", "0.005","76", "0.0223","59", "0.0211","58", "0.0843","57", "0.297","19", "0.2208","56", "0.0873","18", "0.0301","15", "0.5066","16", "0.1087","12", "0.0439","64", "0.5828","65", "0.3836","62", "0.4127","63", "8.0E-4","99", "0.2133","61", "0.0053","100", "0.0064","98", "0.3904","49", "0.0","97", "0.001","48", "0.0","96", "0.0298","95", "0.0","94", "0.4194","45", "0.3404","93", "0.0946","44", "0.1842","92", "0.2045","47", "0.0","91", "0.5491","46", "0.1402","90", "0.0","51", "0.0","52", "0.1534","53", "0.0","54", "0.0128",
    });
    Onebest_AP.put(1, new String[] {
        "78", "0.194","77", "0.2578","35", "0.0016","36", "0.0016","33", "0.313","39", "0.1222","38", "0.0","43", "0.0573","42", "0.1508","41", "0.2359","40", "1.0E-4","82", "0.2507","83", "0.1208","80", "0.0453","87", "0.0017","84", "0.2594","85", "0.1141","67", "0.1099","66", "0.0137","69", "0.0","68", "0.7284","23", "0.1516","26", "0.0","28", "0.5354","29", "0.011","2", "0.0685","30", "0.2713","6", "9.0E-4","32", "0.4273","5", "0.0","70", "0.0161","71", "0.4909","9", "0.0959","72", "0.1549","73", "3.0E-4","74", "0.072","75", "0.005","76", "0.0401","59", "0.0211","58", "0.0843","57", "0.297","19", "0.2208","56", "0.0873","18", "0.0301","15", "0.5066","16", "0.0515","12", "0.0439","64", "0.5828","65", "0.3836","62", "0.4127","63", "0.01","99", "0.2133","61", "0.0053","100", "0.0064","98", "0.3904","49", "0.0","97", "0.001","48", "0.0","96", "0.0298","95", "0.0","94", "0.4194","45", "0.3404","93", "0.0946","44", "0.1842","92", "0.2045","47", "0.0","91", "0.5583","46", "0.1402","90", "0.5008","51", "0.0","52", "0.1534","53", "0.0","54", "0.0128",
    });
    Onebest_AP.put(2, new String[] {
        "78", "0.194","77", "0.2578","35", "0.0016","36", "0.0016","33", "0.313","39", "0.1222","38", "0.0","43", "0.0573","42", "0.1508","41", "0.2359","40", "1.0E-4","82", "0.2507","83", "0.1208","80", "0.0453","87", "0.0017","84", "0.2594","85", "0.1141","67", "0.1099","66", "0.0137","69", "0.0","68", "0.7284","23", "0.1516","26", "0.0","28", "0.5354","29", "0.011","2", "0.0685","30", "0.2713","6", "9.0E-4","32", "0.4273","5", "0.0","70", "0.0161","71", "0.4909","9", "0.0959","72", "0.1549","73", "3.0E-4","74", "0.072","75", "0.005","76", "0.0571","59", "0.0211","58", "0.0843","57", "0.297","19", "0.2208","56", "0.0873","18", "0.0301","15", "0.5066","16", "0.0493","12", "0.0439","64", "0.5828","65", "0.3836","62", "0.4127","63", "0.0213","99", "0.2133","61", "0.0053","100", "0.0064","98", "0.3904","49", "0.0","97", "0.001","48", "0.0","96", "0.0298","95", "0.0","94", "0.4194","45", "0.3404","93", "0.0946","44", "0.1842","92", "0.2045","47", "0.0","91", "0.148","46", "0.1402","90", "0.5265","51", "0.0","52", "0.1534","53", "0.0","54", "0.0128",
    });
    Interp_AP.put(0, new String[] {
        "78", "0.2718","77", "0.237","35", "0.0023","36", "0.003","33", "0.3589","39", "0.0733","38", "0.0","43", "0.0632","42", "0.2166","41", "0.1512","40", "0.0","82", "0.306","83", "0.1201","80", "0.0925","87", "0.2821","84", "0.3849","85", "0.1178","67", "0.136","66", "0.0085","69", "0.0","68", "0.6863","23", "0.1559","26", "0.0049","28", "0.4827","29", "0.1044","2", "0.1904","30", "0.3805","6", "0.0171","32", "0.3675","5", "0.0025","70", "0.0298","71", "0.5121","9", "0.1153","72", "0.3689","73", "0.0039","74", "0.0694","75", "0.0033","76", "0.089","59", "0.0317","58", "0.1001","57", "0.2078","19", "0.2689","56", "0.1321","18", "0.0366","15", "0.3869","16", "0.0519","12", "0.0546","64", "0.444","65", "0.4482","62", "0.4895","63", "0.0089","99", "0.4194","61", "0.0539","100", "0.0511","98", "0.3937","49", "0.0","97", "0.0061","48", "5.0E-4","96", "0.0331","95", "8.0E-4","94", "0.468","45", "0.2849","93", "0.098","44", "0.2971","92", "0.2177","47", "0.0","91", "0.671","46", "0.2192","90", "0.1807","51", "0.0","52", "0.1495","53", "0.1198","54", "0.301",
    });
    Interp_AP.put(1, new String[] {
        "78", "0.2572","77", "0.2317","35", "0.0023","36", "0.0029","33", "0.3569","39", "0.071","38", "0.0","43", "0.0675","42", "0.201","41", "0.1442","40", "0.0","82", "0.3083","83", "0.1208","80", "0.0921","87", "0.2823","84", "0.3931","85", "0.1192","67", "0.1349","66", "0.009","69", "0.0","68", "0.6801","23", "0.1546","26", "0.008","28", "0.4865","29", "0.1029","2", "0.2122","30", "0.3718","6", "0.0066","32", "0.36","5", "0.0025","70", "0.0296","71", "0.5133","9", "0.126","72", "0.3715","73", "0.0045","74", "0.0662","75", "0.0031","76", "0.0861","59", "0.0457","58", "0.1017","57", "0.208","19", "0.2258","56", "0.1278","18", "0.0362","15", "0.3452","16", "0.0455","12", "0.0544","64", "0.4507","65", "0.4448","62", "0.4837","63", "0.0056","99", "0.492","61", "0.0386","100", "0.0413","98", "0.3806","49", "0.0","97", "0.0076","48", "4.0E-4","96", "0.0322","95", "6.0E-4","94", "0.4635","45", "0.297","93", "0.1067","44", "0.267","92", "0.2171","47", "0.0","91", "0.6852","46", "0.2154","90", "0.0389","51", "0.0","52", "0.1495","53", "0.1198","54", "0.286",
    });
    Interp_AP.put(2, new String[] {
        "78", "0.2738","77", "0.234","35", "0.0023","36", "0.0031","33", "0.3541","39", "0.0728","38", "0.0","43", "0.0633","42", "0.2172","41", "0.1445","40", "0.0","82", "0.3056","83", "0.1201","80", "0.094","87", "0.2833","84", "0.3851","85", "0.1116","67", "0.1368","66", "0.0094","69", "0.0","68", "0.6846","23", "0.1557","26", "0.0048","28", "0.4786","29", "0.0957","2", "0.1949","30", "0.377","6", "0.0096","32", "0.3649","5", "0.0025","70", "0.0296","71", "0.5123","9", "0.1333","72", "0.3678","73", "0.0039","74", "0.0689","75", "0.0033","76", "0.1059","59", "0.0317","58", "0.1009","57", "0.1731","19", "0.2689","56", "0.1332","18", "0.0366","15", "0.4058","16", "0.0561","12", "0.0546","64", "0.4555","65", "0.448","62", "0.4855","63", "0.0112","99", "0.4144","61", "0.0529","100", "0.0412","98", "0.3986","49", "0.0","97", "0.0061","48", "5.0E-4","96", "0.0337","95", "8.0E-4","94", "0.4656","45", "0.298","93", "0.0981","44", "0.2955","92", "0.2185","47", "0.0","91", "0.6529","46", "0.2191","90", "0.1005","51", "0.0","52", "0.1495","53", "0.1198","54", "0.318",
    });
  }

  private static String[] baseline_token_AP = new String[] {
    "78", "0.2855","77", "0.1152","35", "0.0021","36", "0.0028","33", "0.3539","39", "0.0542","38", "0.0","43", "0.072","42", "0.2187","41", "0.0713","40", "0.0","82", "0.1729","83", "0.1171","80", "0.0968","87", "0.2273","84", "0.3616","85", "0.0601","67", "0.1404","66", "0.0118","69", "0.0","68", "0.6598","23", "0.1505","26", "0.0056","28", "0.4382","29", "0.1725","2", "0.1554","30", "0.1201","6", "0.0103","32", "0.1379","5", "0.0024","70", "0.0015","71", "0.4966","9", "0.1322","72", "0.3776","73", "7.0E-4","74", "0.0384","75", "0.0041","76", "0.3721","59", "0.0329","58", "0.0955","57", "0.1211","19", "0.2073","56", "0.1257","18", "0.0","15", "0.2478","16", "0.0552","12", "0.0476","64", "0.4298","65", "0.4463","62", "0.4367","63", "0.0063","99", "0.3902","61", "0.0107","100", "0.1015","98", "0.3636","49", "0.0","97", "0.0062","48", "2.0E-4","96", "0.0306","95", "7.0E-4","94", "0.1813","45", "0.117","93", "0.1338","44", "0.026","92", "0.212","47", "0.0","91", "0.6703","46", "0.2168","90", "0.0233","51", "0.0","52", "0.1413","53", "0.1198","54", "0.1984",
  };
  
  public EnZh_NTCIR8() {
    super();
    qe = new QueryEngine();
  }

  @Test
  public void runRegressions() throws Exception {
    initialize();
    runRegression(0);   // "one2none"
    runRegression(1);   // "one2one"
    runRegression(2);   // "one2many"
    verifyAllResults(qe.getModels(), qe.getAllResults(), qe.getDocnoMapping(),
        new Qrels("data/" + PATH + "/qrels." + PATH + ".txt"));
  }

  public void runRegression(int heuristic) throws Exception {
    /////// baseline-token
    Configuration conf = RunQueryEngine.parseArgs(new String[] {
        "--xml", "data/" + PATH + "/run_en-" + LANGUAGE + ".token.xml",
        "--queries_path", "data/" + PATH + "/moses/title_en-" + LANGUAGE + "-trans10-filtered.xml", "--one2many", heuristic + "", "--is_stemming", "--is_doc_stemmed", 
        "--unknown", "data/" + PATH + "/moses/10.unk"});
    FileSystem fs = FileSystem.getLocal(conf);

    conf.setBoolean(Constants.Quiet, true);
        
    // no need to repeat token-based case for other heuristics
    if (heuristic == 0) {
      qe.init(conf, fs);
      qe.runQueries(conf);
    }
    /////// 1-best

    conf = RunQueryEngine.parseArgs(new String[] {
        "--xml", "data/" + PATH + "/run_en-" + LANGUAGE + ".1best.xml",
        "--queries_path", "data/" + PATH + "/moses/title_en-" + LANGUAGE + "-trans1-filtered.xml", "--one2many", heuristic + "", "--is_stemming", "--is_doc_stemmed",
        "--unknown", "data/" + PATH + "/moses/1.unk"});

    qe.init(conf, fs);
    qe.runQueries(conf);

    /////// grammar

    conf = RunQueryEngine.parseArgs(new String[] {
        "--xml", "data/" + PATH + "/run_en-" + LANGUAGE + ".grammar.xml",
        "--queries_path", "data/" + PATH + "/moses/title_en-" + LANGUAGE + "-trans10-filtered.xml", "--one2many", heuristic + "", "--is_stemming", "--is_doc_stemmed",
        "--unknown", "data/" + PATH + "/moses/10.unk"});

    qe.init(conf, fs);
    qe.runQueries(conf);

    /////// 10-best

    conf = RunQueryEngine.parseArgs(new String[] {
        "--xml", "data/" + PATH + "/run_en-" + LANGUAGE + ".10best.xml",
        "--queries_path", "data/" + PATH + "/moses/title_en-" + LANGUAGE + "-trans10-filtered.xml", "--one2many", heuristic + "", "--is_stemming", "--is_doc_stemmed",
        "--unknown", "data/" + PATH + "/moses/10.unk"});

    qe.init(conf, fs);
    qe.runQueries(conf);

    /////// interp

    conf = RunQueryEngine.parseArgs(new String[] {
        "--xml", "data/"+ PATH + "/run_en-" + LANGUAGE + ".interp.xml",
        "--queries_path", "data/" + PATH + "/moses/title_en-" + LANGUAGE + "-trans10-filtered.xml", "--one2many", heuristic + "", "--is_stemming", "--is_doc_stemmed",
        "--unknown", "data/" + PATH + "/moses/10.unk"});

    qe.init(conf, fs);
    qe.runQueries(conf);
  }

  public static void verifyAllResults(Set<String> models,
      Map<String, Map<String, Accumulator[]>> results, DocnoMapping mapping, Qrels qrels) {

    Map<String, GroundTruth> g = Maps.newHashMap();

    g.put("en-" + LANGUAGE + ".token_10-0-100-100_0", new GroundTruth(Metric.AP, numTopics, baseline_token_AP, expectedTokenMAP));

    for (int heuristic=0; heuristic <= 2; heuristic++) {
      g.put("en-" + LANGUAGE + ".grammar_10-0-0-100_" + heuristic, new GroundTruth(Metric.AP, numTopics, grammar_AP.get(heuristic), expectedMAPs.get(heuristic)[0]));
      g.put("en-" + LANGUAGE + ".1best_1-0-0-100_" + heuristic, new GroundTruth(Metric.AP, numTopics, Onebest_AP.get(heuristic), expectedMAPs.get(heuristic)[1]));
      g.put("en-" + LANGUAGE + ".10best_10-100-0-100_" + heuristic, new GroundTruth(Metric.AP, numTopics, Nbest_AP.get(heuristic), expectedMAPs.get(heuristic)[2]));
      g.put("en-" + LANGUAGE + ".interp_10-30-30-100_" + heuristic, new GroundTruth(Metric.AP, numTopics, Interp_AP.get(heuristic), expectedMAPs.get(heuristic)[3]));
    }

    for (String model : models) {
      System.err.println("Verifying results of model \"" + model + "\"");

      GroundTruth groundTruth = g.get(model); 
      Map<String, Accumulator[]> result = results.get(model);
      groundTruth.verify(result, mapping, qrels);

      System.err.println("Done!");
    }
  }
  
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(EnZh_NTCIR8.class);
  }

  public static void main(String[] args) {
    initialize();

    HMapSFW tenbestAPMap = array2Map(Nbest_AP.get(2));
    HMapSFW onebestAPMap = array2Map(Onebest_AP.get(1));
    HMapSFW grammarAPMap = array2Map(grammar_AP.get(0));
    HMapSFW tokenAPMap = array2Map(baseline_token_AP);
    
    System.out.println("10best: improved=" + countNumberOfImprovedTopics(tokenAPMap, tenbestAPMap) + ", negligible=" + countNumberOfNegligibleTopics(tokenAPMap, tenbestAPMap));
    System.out.println("Grammar: improved=" + countNumberOfImprovedTopics(tokenAPMap, grammarAPMap) + ", negligible=" + countNumberOfNegligibleTopics(tokenAPMap, grammarAPMap));
    System.out.println("1best: improved=" + countNumberOfImprovedTopics(tokenAPMap, onebestAPMap) + ", negligible=" + countNumberOfNegligibleTopics(tokenAPMap, onebestAPMap));
  }

  private static int countNumberOfImprovedTopics(HMapSFW tokenAPMap, HMapSFW gridAPMap) {
    int cnt = 0;
    for (String key : tokenAPMap.keySet()) {
      float difference = gridAPMap.get(key) - tokenAPMap.get(key); 
      if ( difference > 0.001 ) {
        cnt++;
      }
    }
    return cnt;
  }

  private static int countNumberOfNegligibleTopics(HMapSFW tokenAPMap, HMapSFW gridAPMap) {
    int cnt = 0;
    for (String key : tokenAPMap.keySet()) {
      float difference = gridAPMap.get(key) - tokenAPMap.get(key); 
      if ( difference > -0.001 && difference < 0.001 ) {
        cnt++;
      }
    }
    return cnt;
  }

  private static HMapSFW array2Map(String[] array) {
    HMapSFW map = new HMapSFW();
    for ( int i = 0; i < array.length; i += 2 ) {
      map.put(array[i], Float.parseFloat(array[i+1]));
    }
    return map;
  }

}
