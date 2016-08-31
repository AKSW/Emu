package org.aksw.emu.score;

import org.aksw.emu.score.metric.Node;
import org.aksw.emu.score.metric.Node.ResultMetric;
import org.aksw.emu.score.metric.Node.ResultMetricCalc;

public class ScoreMetric {

	public static Node buildTree(String folder, String metric) {
		return parse(folder, metric);
	}

	private static Node parse(String folder, String metric) {

		String op = metric.substring(0, metric.indexOf("(")).trim();
		String rest = metric.substring(metric.indexOf("(") + 1,
				metric.lastIndexOf(")")).trim();
		if (op.matches("NQTL")) {
			
			ResultMetric met = ResultMetric.No_of_Queries;
			
			String[] restArr = rest.split(",");
			ResultMetricCalc calc = ResultMetricCalc.valueOf(restArr[0].trim());
			int tID = Integer.valueOf(restArr[1].trim());
			int sparql = Integer.valueOf(restArr[2].trim());
			int update = Integer.valueOf(restArr[3].trim());
			Node node = new Node(folder, Node.Ops.valueOf(op), tID, sparql,
					update, met, calc);
			return node;
		}
		if (op.matches("(AVG|SUM|MIN|MAX)")) {
			String op2 = rest.substring(0, rest.indexOf("(")).trim();
			rest = rest.substring(rest.indexOf("(") + 1,
					rest.lastIndexOf(")")).trim();
			
			ResultMetric met = ResultMetric.getShort(op2.trim());
			
			String[] restArr = rest.split(",");
			ResultMetricCalc calc = ResultMetricCalc.valueOf(restArr[0].trim());
			int tID = Integer.valueOf(restArr[1].trim());
			int sparql = Integer.valueOf(restArr[2].trim());
			int update = Integer.valueOf(restArr[3].trim());
			
			Node node = new Node(folder, Node.Ops.valueOf(op), tID, sparql,
					update, met, calc);
			return node;
		}
		Node node = new Node(folder, Node.Ops.valueOf(op));
		String[] restArr = rest.split(",");
		String child1="", child2="";
		boolean first1=true, first2=true;
		int count=0, i=0;
		
		do{
			count = getMissing(restArr[i], count);
			if(first1){
				child1+=restArr[i++].trim();
				first1=false;
			}
			else{
				child1+=","+restArr[i++].trim();
			}
		}while(count!=0);
		
		for(;i<restArr.length;i++){
			if(first2){
				child2+=restArr[i].trim();
				first2=false;
			}
			else{
				child2+=","+restArr[i].trim();
			}
		}
		
		node.addChildren(parse(folder, child1));
		node.addChildren(parse(folder, child2));
		return node;
	}
	
	public static int getMissing(String m, int initial){
		int count=initial;
		for(char c : m.toCharArray()){
			if(c=='('){
				count++;
			}
			else if(c==')'){
				count--;
			}
		}
		return count;
	}

}
