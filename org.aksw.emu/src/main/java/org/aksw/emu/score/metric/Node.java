package org.aksw.emu.score.metric;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.aksw.emu.exception.EmuException;
import org.aksw.iguana.utils.ResultReader;
import org.aksw.iguana.utils.ResultSet;

public class Node {

	
	public enum Ops{
		ADD, SUB, MULTIPLY, DIV, MIN, MAX, SUM, AVG, NQTL
	}
	
	public enum ResultMetric{
		No_of_Queries, Queries_Per_Second, Queries_Totaltime, Failed, Succeded;

		public static ResultMetric getShort(String op2) {
			switch(op2.toUpperCase()){
			case "NQTL":
				return No_of_Queries;
			case "QPS":
				return Queries_Per_Second;
			case "TOTALTIME":
				return Queries_Totaltime;
			case "FAILED":
				return Failed;
			case "SUCCEDED":
				return Succeded;
			}
			return null;
		}
	}
	
	public enum ResultMetricCalc{
		Sum, Mean
	}
	
	private Node parent = null;
	private int state = 0;
	private boolean isLeave = true;
	private Ops nodeOp;
	private String folder;
	private Integer tID, sparql, update;
	private ResultMetric metric;
	private ResultMetricCalc metricCalc;
	
	private List<Node> childs = new LinkedList<Node>();
	
	public Node(String folder, Ops nodeOp){
		this.folder=folder;
		this.nodeOp=nodeOp;
	}
	
	public Node(String folder, Ops nodeOp, Integer tID, Integer sparql, Integer update, ResultMetric metric, ResultMetricCalc metricCalc){
		this.folder=folder;
		this.nodeOp=nodeOp;
		this.tID=tID;
		this.sparql=sparql;
		this.update=update;
		this.metric=metric;
		this.metricCalc=metricCalc;
	}
	
	public void addChildren(Node n){
		childs.add(n);
		n.setParent(this);
		isLeave = false;
	}
	
	private void setParent(Node node) {
		this.parent = node;
	}
	
	public Node getParent(){
		return parent;
	}

	public double get() throws EmuException{
		if(isLeave){
			ResultSet res = getRightResultSet();
			switch(nodeOp){
			case MIN:
				return getMIN(res);
			case MAX:
				return getMAX(res); 
			case NQTL:
				return Double.valueOf(res.getTable().get(0).get(1).toString());
			case AVG:
				return getAVG(res);
			case SUM:
				return getSUM(res);
			default:
				break;
			}
		}
		else{
			switch(nodeOp){
			case ADD:
				return childs.get(0).get()+childs.get(1).get();
			case SUB:
				return childs.get(0).get()-childs.get(1).get();
			case MULTIPLY:
				return childs.get(0).get()*childs.get(1).get();
			case DIV:
				return childs.get(0).get()/childs.get(1).get();
			default:
				break;
			}
		}
		return 0;
	}

	private ResultSet getRightResultSet() throws EmuException {
		StringBuilder folder = new StringBuilder(this.folder);
		folder.append(File.separator).append(tID).append(File.separator).append(sparql).append(File.separator).append(update);
		folder.append(File.separator).append("calculated").append(File.separator);
		for(File f : new File(folder.toString()).listFiles()){
			
			if(f.getName().startsWith(metric.name()) 
					&& f.getName().endsWith(metricCalc.name()+".csv")
					&& !f.getName().contains("UPDATE")){
				try {
					return ResultReader.readFile(f);
				} catch (IOException e) {
					throw new EmuException(e.getMessage());
				}
			}
		}
		
		return null;
	}

	public Ops getNodeOp() {
		return nodeOp;
	}

	public void setNodeOp(Ops nodeOp) {
		this.nodeOp = nodeOp;
	}
	
	private double getAVG(ResultSet res){
		double ret=0.0;
		List<Object> row = res.getTable().get(0);
		for(int i=1;i<row.size();i++){
			ret+=Double.valueOf(row.get(i).toString());
		}
		return ret/(row.size()-1);
	}
	
	private double getSUM(ResultSet res){
		double ret=0.0;
		List<Object> row = res.getTable().get(0);
		for(int i=1;i<row.size();i++){
			ret+=Double.valueOf(row.get(i).toString());
		}
		return ret;
	}
	
	private double getMIN(ResultSet res){
		double ret=0.0;
		List<Object> row = res.getTable().get(0);
		ret=Double.valueOf(row.get(1).toString());
		for(int i=2;i<row.size();i++){
			if(Double.valueOf(row.get(i).toString()) < ret){
				ret = Double.valueOf(row.get(i).toString());
			}
		}
		return ret;
	}
	
	private double getMAX(ResultSet res){
		double ret=0.0;
		List<Object> row = res.getTable().get(0);
		ret=Double.valueOf(row.get(1).toString());
		for(int i=2;i<row.size();i++){
			if(ret < Double.valueOf(row.get(i).toString())){
				ret = Double.valueOf(row.get(i).toString());
			}
		}
		return ret;
	}
	
	@Override
	public String toString() {
		String ret = "";
		for(int i=0;i<state;i++){
			ret+="\t";
		}
		ret+="- "+this.nodeOp.name();
		if(this.childs.isEmpty()){
			ret+="("+tID+", "+sparql+", "+update+")";
		}
		else{
			ret+= "\n"+childs.get(0).toString(state+1)+"\n";
			ret+= childs.get(1).toString(state+1);
		}
		return ret;
	}
	
	public String toString(int state) {
		String ret = "";
		for(int i=0;i<state;i++){
			ret+="  ";
		}
		ret+="- "+this.nodeOp.name();
		if(this.childs.isEmpty()){
			ret+="("+tID+", "+sparql+", "+update+")";
		}
		else{
			ret+= "\n"+childs.get(0).toString(state+1)+"\n";
			ret+= childs.get(1).toString(state+1);
		}
		return ret;
	}
}
