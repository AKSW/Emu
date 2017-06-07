package org.aksw.emu.heatmap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aksw.emu.config.ConfigDefinition;
import org.aksw.emu.config.ConfigDefinitionReader;
import org.aksw.emu.config.Var;
import org.aksw.emu.exception.EmuException;

public class TableCreator {

	private static List<String> columnNames = new LinkedList<String>();
	private static List<String> rowNames = new LinkedList<String>();
	
	public static void main(String args[]) throws IOException, EmuException{
		saveTable("heatmap", createTable(ConfigDefinitionReader.readConfigDef("cdef.properties"), 
				createScoreMapFromScoreFile("scores.txt")));
	}
	
	public static void saveTable(String output, List<List<Double>> table) throws IOException{
		PrintWriter pw = new PrintWriter(output+".csv");

		String row = "";
		
		for(int i=0;i<columnNames.get(0).split(", ").length;i++){
			row="";
			for(String s : rowNames.get(0).split(";")){
				row+=";";
			}
			for(String column : columnNames){
				row+=column.split(", ")[i]+"\t";
			}
			pw.println(row);
		}
		for(int i=0; i<table.size();i++){
			List<Double> values = table.get(i);
			row=rowNames.get(i)+"\t";
			for(Double d : values){
				if(d>=0){
					row+=d+"\t";
				}else{
					row+="\t";
				}
			}
			pw.println(row);
		}
		pw.close();
		pw = new PrintWriter(output+"_further-work.csv");
		PrintWriter pw2 = new PrintWriter("heatmap_mapping.txt");
		pw2.println("y, columns");
		row = "Vars\t";
		int j=1;
		for(String column : columnNames){
			row+="´"+j+"\t";
			pw2.println(j+"\t"+column.replace(",", "\t"));
			j++;
		}
		j=1;
		pw2.println("\nx, rows");
		pw.println(row);
		row="";
		for(int i=0; i<table.size();i++){
			List<Double> values = table.get(i);
			row=j+"\t";
			pw2.println(j+"\t"+rowNames.get(i));
			j++;
			for(Double d : values){
				if(d>=0){
					row+=d+"\t";
				}else{
					row+="\t";
				}
			}
			pw.println(row);
		}
		pw2.close();
		pw.close();
	}
	
	public static Map<String, Double> createScoreMapFromScoreFile(String scoreFile){
		Map<String, Double> map = new HashMap<String, Double>();
		try(BufferedReader reader = new BufferedReader(new FileReader(scoreFile))){
			String line ="";
			while(null!=(line=reader.readLine())){
				int split = line.lastIndexOf(":");
				Double score = Double.valueOf(line.substring(split+1).trim());
				split = line.lastIndexOf("]");
				String key = line.trim().substring(1, split).trim();
				map.put(key, score);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
		
	}
	
	public static List<List<Double>> createTable(ConfigDefinition cdef, Map<String, Double> score){
		List<List<Double>> ret = new LinkedList<List<Double>>();
		int size = cdef.getVariableNames().size();
		int k = size/2;
		int i=0;
		List<Double> row = new LinkedList<Double>();
		add(i, k, row, cdef, 0, "", "", score, ret);
		
		return ret;
	}

	public static List<Double> add(int deep, int split, List<Double> row, ConfigDefinition cdef, int nameIndex, String rowN, String columnN, Map<String, Double> score, List<List<Double>> ret){
		String _rowN = rowN;
		
		for(Var var : cdef.getPossibleVars(cdef.getNameFromIndex(nameIndex))){
			_rowN = rowN;
			String _columnN=columnN;
			if(deep>split){
				_columnN+=", "+var.toString().replace("\n", "\t");
			}
			else if(deep==split){
				_columnN+=var.toString().replace("\n", "\t");
			}
			else if(deep==split-1){
				_rowN+=var.toString().replace("\n", "\t");
			}
			else{
				_rowN+=var.toString().replace("\n", "\t")+", ";
			}
			if(nameIndex < cdef.getVariableNames().size()-1){
				row = add(deep+1, split, row, cdef,nameIndex+1, _rowN, _columnN, score, ret);
			}
			else{
				//finished columnName
				if(!columnNames.contains(_columnN.replace("\t", "  ")))
					columnNames.add(_columnN.replace("\t", "  "));
				Double d = score.get(_rowN+", "+_columnN);
				if(d==null)
					d=-1.;
				row.add(d);
			}
		}
		if(deep==split){
			//finished rowNames
			rowNames.add(rowN.replace("\t", "  ").replace(", ", ";"));
			rowN="";
			ret.add(row);
			row = new LinkedList<Double>();
		}
		return row;
	}
	
}
