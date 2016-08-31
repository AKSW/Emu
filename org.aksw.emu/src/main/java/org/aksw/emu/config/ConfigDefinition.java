package org.aksw.emu.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.aksw.emu.exception.EmuException;
import org.xml.sax.SAXException;

public class ConfigDefinition {
	
	private Map<String, List<Var>> vars = new HashMap<String, List<Var>>();
	private Map<String, Object[]> config = new HashMap<String, Object[]>();
	private List<String> configLines;
	private List<String> index = new LinkedList<String>();
	private String output;
	
	
	public ConfigDefinition(String fileName, String output) throws ParserConfigurationException, SAXException, IOException{
		this(new File(fileName), output);
	}
	
	public ConfigDefinition(File file, String output) throws ParserConfigurationException, SAXException, IOException{
		configLines = new LinkedList<String>();
		FileReader freader = new FileReader(file);
		BufferedReader reader = new BufferedReader(freader);
		String line="";
		while((line=reader.readLine())!=null)
			configLines.add(line);
		freader.close();
		reader.close();
		this.output=output;
	}

	public int size() {
		return vars.size();
	}

	public List<Var> getPossibleVars(String name) {
		return vars.get(name);
	}
	
	public void setVars(String name, int start, String lines, String[] values){
		List<Var> vars = new LinkedList<Var>();
		for(String value : values){
			Var v = new Var(value, name);
			vars.add(v);
		}
		this.vars.put(name, vars);
		Object[] o = new Object[2];
		o[0] = start-1;
		o[1] = lines;
		this.config.put(name, o);
		this.index.add(name);
	}
	
	public Set<String> getVariableNames(){
		return vars.keySet();
	}
	
	public int getIndexFromName(String name){
		return index.indexOf(name);
	}
	
	public String getNameFromIndex(int i){
		return index.get(i);
	}
	
	public void saveConfig(ConfigVector cvec) throws EmuException{
		//make copy
		List<String> clCopy = new LinkedList<String>(configLines);
		
		for(Var v : cvec.getVars()){
			String key = v.getName();
			//Exchange original Lines with calculated Lines
			Object[] currentLine = config.get(key);
			int start = (int)currentLine[0];
			String line = currentLine[1].toString();
			String[] lines = line.split("\n");
			int i=start;
			for(String _line : lines){
				_line = _line.replace("%s", v.getValue());
				clCopy.set(i++, _line);
			}
			
		}
		try(PrintWriter pw = new PrintWriter(output)){
			for(String line : clCopy){
				pw.println(line);				
			}
		} catch (IOException e) {
			throw new EmuException(e);
		}
	}


}
