package org.aksw.emu.config;

import java.util.LinkedList;
import java.util.List;

public class ConfigVector{

	private List<Var> vars = new LinkedList<Var>();
	private double score=0;
	
	public Var getVar(int i) {
		return vars.get(i);
	}

	public void setVar(int i, Var var) {
		if(vars.size()<=i){
			vars.add(var);
		}
		else{
			vars.set(i, var);
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof ConfigVector){
			ConfigVector test = (ConfigVector) o;
			if(test.size()!=this.size()) {
				return false;
			}
			for(int i=0; i<vars.size();i++){
				if(!test.getVar(i).equals(vars.get(i))){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	

	private int size() {
		return vars.size();
	}

	public Double getScore() {
		return score;
	}

	public void setScore(double score2) {
		this.score = score2;
	}

	public List<Var> getVars() {
		return vars;
	}
	
	@Override
	public String toString(){
		return vars.toString();
	}

}
