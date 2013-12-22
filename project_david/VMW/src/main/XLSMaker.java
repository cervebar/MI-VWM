package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class XLSMaker {
	public static void createFromInfo(){
		ArrayList<ArrayList<String>> arr = new ArrayList<>();
		arr.add(Main.XLSHeaders());
		
		File f = new File("results");
		for(File ifile : f.listFiles()){
			if(ifile.getName().endsWith(".info")){
				System.out.println(ifile.getName());
				try {
					arr.add(fileToArray(ifile));
				} catch (IOException ex){
					System.out.println("InfoFile not found:" + ifile.getName());
				}
			}
		}
		exportToXLS(arr);
	}
	
	public static void exportToXLS(ArrayList<ArrayList<String>> values){
		try {
			FileWriter fstream = new FileWriter("results/vysledek.xls");
			BufferedWriter out = new BufferedWriter(fstream);
			for(ArrayList<String> arr : values){
				for(String s : arr){
					out.write(s + "\t");
				}
				out.write("\n");
			}
			out.close();
		} catch (Exception ex){
			System.out.println("Error while writing info:" );
			ex.printStackTrace();
		}
	}
	
	
	private static ArrayList<String> fileToArray(File f) throws IOException{
		ArrayList<String> vals = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(f.getAbsolutePath()));
		vals.add(f.getName());
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	        	vals.add(line.substring(line.indexOf(":")+2));
	            line = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
		return vals;
		
	}
}
