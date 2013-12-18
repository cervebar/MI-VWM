package fit.ctu.cz.vwm.business.extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import fit.ctu.cz.vwm.model.AudioDocumentMap;

public class DEscriptionExtractor implements Extractor {
	
	double afflower;
	double affhigher;
	double ulhlower;
	double ulhhigher;
	double asptlower;
	double aspthigher;
	double sctlower;
	double scthigher;
	double hsctlower;
	double hscthigher;
	double rmslower;
	double rmshigher;
	double colower;
	double cohigher;
	double scoalower;
	double scoahigher;
	double bsoalower;
	double bsoahigher;
	double bsoslower;
	double bsoshigher;

	@Override
	public void extract(File file, AudioDocumentMap aDoc) throws Exception {
		String result = "";
		String SFile;
		try {
			SFile = fileToString(file);	
		}	catch (Exception ex){
			SFile = "";
		}
		HashMap<String,String> vals = new HashMap<>();
		String [] arr = SFile.split("[|]");
		for(int i = 0; i < arr.length; i++){
			String [] val = arr[i].split("[:][ ]");
			String key = valToKey(val[0]);				
			if(key.length() > 0){
				vals.put(key,val[1]);
			}
		}
		
		// aff
		String aff="";
		afflower = 500;
		affhigher = 600; 
		aff = "affhigh";
		if(Double.parseDouble(vals.get("aff")) < afflower) aff = "afflow";
		else if(Double.parseDouble(vals.get("aff")) < affhigher) aff = "affmedium";
		result += aff;
		aDoc.put("aff", aff);
		
		// ulh
		String ulh="";
		ulhlower = 6000;
		ulhhigher = 7000; 
		ulh = "ulhhigh";
		if(Double.parseDouble(vals.get("ulh")) < ulhlower) ulh = "ulhlow";
		else if(Double.parseDouble(vals.get("ulh")) < ulhhigher) ulh = "ulhmedium";
		result += " " + ulh;
		aDoc.put("ulh", ulh);
		
		//aspt
		String aspt="";
		asptlower = 25;
		aspthigher = 30; 
		aspt = "aspthigh";
		if(Double.parseDouble(vals.get("aspt")) < asptlower) aspt = "asptlow";
		else if(Double.parseDouble(vals.get("aspt")) < aspthigher) aspt = "asptmedium";
		result += " " + aspt;
		aDoc.put("aspt", aspt);
		//sct
		String sct="";
		sctlower = 500;
		scthigher = 700; 
		sct = "scthigh";
		if(Double.parseDouble(vals.get("sct")) < sctlower) sct = "sctlow";
		else if(Double.parseDouble(vals.get("sct")) < scthigher) sct = "sctmedium";
		result += " " + sct;
		aDoc.put("sct", sct);
		//hsct
		String hsct="";
		hsctlower = 1300;
		hscthigher = 2000; 
		hsct = "hscthigh";
		if(Double.parseDouble(vals.get("hsct")) < hsctlower) hsct = "hsctlow";
		else if(Double.parseDouble(vals.get("hsct")) < hscthigher) hsct = "hsctmedium";
		result += " " + hsct;
		aDoc.put("hsct", hsct);
		//rms
		String rms="";
		rmslower = 0.05;
		rmshigher = 0.08; 
		rms = "rmshigh";
		if(Double.parseDouble(vals.get("rms")) < rmslower) rms = "rmslow";
		else if(Double.parseDouble(vals.get("rms")) < rmshigher) rms = "rmsmedium";
		result += " " + rms;
		aDoc.put("rms", rms);
		//co
		String co="";
		colower = 1600;
		cohigher = 1650; 
		co = "cohigh";
		if(Double.parseDouble(vals.get("co")) < colower) co = "colow";
		else if(Double.parseDouble(vals.get("co")) < cohigher) co = "comedium";
		result += " " + co;
		aDoc.put("co", co);
		//scoa
		String scoa="";
		scoalower = 24;
		scoahigher = 28; 
		scoa = "scoahigh";
		if(Double.parseDouble(vals.get("scoa")) < scoalower) scoa = "scoalow";
		else if(Double.parseDouble(vals.get("scoa")) < scoahigher) scoa = "scoamedium";
		result += " " + scoa;
		aDoc.put("scoa", scoa);
		//bsoa
		String bsoa="";
		bsoalower = 1500;
		bsoahigher = 2000; 
		bsoa = "bsoahigh";
		if(Double.parseDouble(vals.get("bsoa")) < bsoalower) bsoa = "bsoalow";
		else if(Double.parseDouble(vals.get("bsoa")) < bsoahigher) bsoa = "bsoamedium";
		result += " " + bsoa;
		aDoc.put("bsoa", bsoa);
		//bsos
		String bsos="";
		bsoslower = 500;
		bsoshigher = 700; 
		bsos = "bsoshigh";
		if(Double.parseDouble(vals.get("bsos")) < bsoslower) bsos = "bsoslow";
		else if(Double.parseDouble(vals.get("bsos")) < bsoshigher) bsos = "bsosmedium";
		result += " " + bsos;
		aDoc.put("bsos", bsos);
		
		
		// overall
		aDoc.put("description",result);
	}
	
	public String valToKey(String val){
		switch(val){
			case "AudioFundamentalFrequencyType": return "aff";
			case "UpperLimitOfHarmonicity": return "ulh";
			case "AudioSpectrumProjectionType": return "aspt";
			case "SpectralCentroidType": return "sct";
			case "HarmonicSpectralCentroidType" : return "hsct";
			case "RootMeanSquareOverallStandardDeviation" : return "rms";
			case "CompactnessOverallAverage": return "co";
			case "SpectralCentroidOverallAverage": return "scoa";
			case "ZeroCrossingsOverallAverage": return "zca";
			case "BeatSumOverallAverage": return "bsoa";
			case "BeatSumOverallStandardDeviation": return "bsos";
			default: return "";
		}
		
		
	}
	
	public String fileToString(File file) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String str = "";
		String line = "";
		while (true) {
			String readLine = br.readLine();
			if (readLine == null)	break;
			str += readLine + "|";
		}
		br.close();
		return str;
	}

}
