package fit.ctu.cz.vwm.business.extract;

import java.util.ArrayList;


public class EConfig {
	public ArrayList<String> config;
	public ArrayList<Integer> vals;
	
	public EConfig(){
		this.config = new ArrayList<>();
		this.vals = new ArrayList<>();
	}
	public void add(String s, int i){
		this.config.add(s);
		this.vals.add(i);
	}
	public void add(String s){
		this.add(s,1);
	}
}
