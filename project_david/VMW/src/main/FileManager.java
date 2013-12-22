package main;

import java.io.File;
import java.util.ArrayList;

public class FileManager {
	public String root;
	public ArrayList<File> files;
	
	public FileManager(String root){
		this.root = root;
		this.init();
	}
	
	private void init(){
		files = new ArrayList<>();
		this.getSubfolders();
	}
	
	private void getSubfolders(){
		File f = new File(this.root);
		if(f.exists()){
			for(File sub : f.listFiles()){
				if(sub.isDirectory()){
					this.getFiles(sub);
				}
			}
		}
	}
	private void getFiles(File f){
		for(File sub : f.listFiles()){
			if(sub.isFile()){
				if(!sub.getName().endsWith(".txt") && !sub.getName().endsWith(".info") )		files.add(sub);
			}
		}
	}
	
}
