package org.aksw.emu.config;

public class Var {

	private String name;
	private String value;
	
	public Var(String value, String name){
		this.value=value;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Var){
			return ((Var)o).getValue().equals(this.value);
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString(){
		return this.value+"("+this.name+")";
	}
	
	@Override
	public int hashCode(){
		return this.value.hashCode();
	}
	
	
}
