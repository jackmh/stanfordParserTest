package config;

import java.io.File;

public class config {
	
	public static boolean __DEBUG__ = false;
	public static boolean __WriteIntoFileFlag__ = true;
	public static boolean __ANALYSISFLAG__ = true;
	
	public static String BaseDIR = "./proteinPI";
	public static String srcPubmedText = "/home/jack/Workspaces/expPPI/pubmedtextData";
	public static String DstDIR = "/home/jack/Workspaces/recognizedPPI";
	public static String allRecognitionPPIFname = "/home/jack/Workspaces/pubmedPPIRecognition.result";
	
	
	public static String srcDataDir = "/home/jack/Workspaces/expPPI/resultData";
	public static String pubmed2GenesIntFname = srcDataDir + File.separator + "pubmedId2Gene_201108.intm";
}
