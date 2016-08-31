package org.aksw.emu.hillclimbing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.aksw.emu.config.ConfigDefinition;
import org.aksw.emu.config.ConfigVector;
import org.aksw.emu.config.EmuConfig;
import org.aksw.emu.config.Var;
import org.aksw.emu.exception.EmuException;
import org.aksw.emu.score.ScoreCalculator;
import org.aksw.emu.utils.comparator.ConfigVectorComparator;
import org.aksw.iguana.benchmark.Benchmark;
import org.aksw.iguana.utils.ShellProcessor;
import org.apache.commons.lang.ArrayUtils;
import org.xml.sax.SAXException;

public class EmuHillClimber {

	
	public enum RecombineStrategy{
		Alternating, MostOften
	}

	private static final String CANCEL_AFTER_RUNS = "org.aksw.emu.cancel.run";
	private static final String IGUANA_CONFIG = "org.aksw.emu.iguana.config";
	private static final String METRIC = "org.aksw.emu.scorer.metric";
	private static final String CANCEL_AFTER_MUTATIONS = "org.aksw.emu.cancel.mutations";
	private static final String START_MINUTES = "org.aksw.emu.minutes";
	private String metric="";
	private List<Integer> tested = new ArrayList<Integer>();
	private PrintWriter pw;
	private int n=5;
	private int cancel_mutations=10;
	
	public ConfigVector execute(ConfigDefinition cdef, RecombineStrategy combine) throws EmuException{

		try {
			pw = new PrintWriter("scores.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ShellProcessor.setWaitForIt(10000);
		int cancel = EmuConfig.getInstance().getInt(CANCEL_AFTER_RUNS);
		cancel_mutations = EmuConfig.getInstance().getInt(CANCEL_AFTER_MUTATIONS);
		String[] metricArray = EmuConfig.getInstance().getStringArray(METRIC);
		for(int i=0;i<metricArray.length-1;i++){
			metric+=metricArray[i]+", ";
		}
		
		 metric+=metricArray[metricArray.length-1];
		List<ConfigVector> cvecs = getInitialConstellations(cdef);
		
		calculateScores(cvecs, cdef);
				
		sort(cvecs);
		
		int i=0;
		ConfigVector oldCvec = cvecs.get(0);
		do{
			//TODO rewrite so recombineSafe gives back n+1 cvecs
			List<ConfigVector> recombined = recombineSafe(cvecs, cdef, combine);

			calculateScores(recombined, cdef);
			int size = cvecs.size();
			cvecs.addAll(recombined);
			sort(cvecs);
			cvecs = cvecs.subList(0, size);
			if(cvecs.get(0).equals(oldCvec)){
				i++;
				System.out.println("run without change "+i);
			}
			else{
				oldCvec=cvecs.get(0);
				i=1;
				System.out.println("run with change "+i);
			}
		}while(i<cancel);
		pw.close();
		return cvecs.get(0);
	}
	
	
		public List<ConfigVector> getInitialConstellations(ConfigDefinition cdef){
		List<ConfigVector> ret = new LinkedList<ConfigVector>();
		for(int i=0; i<cdef.size()+1;i++){
			ConfigVector cvec = new ConfigVector();
			Random rand = new Random();
			rand.setSeed(System.currentTimeMillis());
			//For each var in cdef
			for(int j=0;j<cdef.size();j++){
				String id =  cdef.getNameFromIndex(j);
				int k=0;
				if(j==i){
					k=rand.nextInt(cdef.getPossibleVars(cdef.getNameFromIndex(j)).size()-1)+1;
				}
				Var var = new Var(cdef.getPossibleVars(id).get(k).getValue(), id);
				cvec.setVar(j, var);
			}
			ret.add(cvec);
		}
		return ret;
	}
	
	public void calculateScores(List<ConfigVector> cvecs, ConfigDefinition cdef) throws EmuException{
		for(ConfigVector cvec: cvecs){
			if(cvec == null){
				continue;
			}
			if(cvec.getScore()!=0){
				System.out.println("Cvec: "+cvec+" Already tested");
				continue;
			}
			if(tested.contains(cvec.getVars().hashCode())){
				System.out.println("Cvec: "+cvec+" Already tested");
				continue;
			}
			double score=0;
			String folder="results_0"+File.separator+"org.aksw.iguana.testcases.StressTestcase1.0";
			
			//TODO calculate n
			n = EmuConfig.getInstance().getInt(START_MINUTES);
			int timeLimit=60000*n;
			cdef.saveConfig(cvec);
			
			
			try {
				//REPLACE IGUANA CONFIGS TIMELIMIT VAR
				Path path = Paths.get(EmuConfig.getInstance().getString(IGUANA_CONFIG));
				Path pathO = Paths.get(EmuConfig.getInstance().getString(IGUANA_CONFIG)+"copy.xml");
				Charset charset = StandardCharsets.UTF_8;

				String content = new String(Files.readAllBytes(path), charset);
				content = content.replaceAll("%timeLimit", timeLimit+"");
				Files.write(pathO, content.getBytes(charset));
				
				Benchmark.execute(pathO.toFile().getAbsolutePath());
				tested.add(cvec.getVars().hashCode());
			} catch (ParserConfigurationException | SAXException | IOException e) {
				throw new EmuException(e.toString());
			}
			
			score = ScoreCalculator.getScore(folder, metric);
			cvec.setScore(score);
			System.out.println("cvec:"+cvec+" \nscore: "+score);
			pw.println(cvec.toString().replace("\n","\t")+" : "+score);
			pw.flush();
		}
	}
	
	private void sort(List<ConfigVector> cvecs){
		Collections.sort(cvecs, new ConfigVectorComparator());
		for(ConfigVector cvec : cvecs)
			System.out.println(cvec+" : "+cvec.getScore());
	}
	
	
	
	public ConfigVector mutateSafe(ConfigVector old, ConfigDefinition cdef, List<ConfigVector> cvecs){
		ConfigVector ret, old_c = old;
		int i=0;
		
		do{
			ret = mutate(old_c, cdef);
			i++;
			System.out.println("mutate try: "+i);
			old_c = ret;
		}while(checkCVContainCached(cvecs, ret)&&cancel_mutations>=i);
		if(cancel_mutations<i){
			System.out.println("Mutation does not work");
			return null;
		}
		return ret;
			
	}
	
	private boolean checkCVContainCached(List<ConfigVector> cvecs, ConfigVector cvec){
//		for(ConfigVector cv : cvecs){
//			if(cv.equals(cvec))
//				return true;
//		}
		if(tested.contains(cvec.getVars().hashCode()))
			return true;
		return false;
		
	}

	
	
	public ConfigVector mutate(ConfigVector old, ConfigDefinition cdef){
		ConfigVector cvec = new ConfigVector();
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		int mutateIndex = rand.nextInt(old.getVars().size());
		for(int i=0; i<old.getVars().size() ;i++){
			if(i==mutateIndex){
				List<Var> possibleVars = new ArrayList<Var>(cdef.getPossibleVars(old.getVar(i).getName()));
				possibleVars.remove(old.getVar(i));				
				cvec.setVar(i, possibleVars.get(rand.nextInt(possibleVars.size())));
			}
			else{
				cvec.setVar(i, old.getVar(i));
			}
		}
		
		return cvec;
	}
	
	public List<ConfigVector> recombineSafe(List<ConfigVector> cvecs, ConfigDefinition cdef, RecombineStrategy recombine){
		List<ConfigVector> ret = new LinkedList<ConfigVector>();

		for(int i=0; i<cvecs.size();i++){
			ConfigVector cvec;
			if(i==cvecs.size()-1){
				cvec = recombineTwo(cvecs.get(i), cvecs.get(0));				
			}
			else{
				cvec = recombineTwo(cvecs.get(i), cvecs.get(i+1));
			}
			if(checkCVContainCached(cvecs, cvec)||ret.contains(cvec)){
				cvec = mutateSafe(cvec, cdef, cvecs);
			}
			if(cvec!=null){
				ret.add(cvec);
			}	
		}
		return ret;
	}
	
	public ConfigVector recombine(List<ConfigVector> cvecs, RecombineStrategy recombine){
		switch(recombine){
		case Alternating:
			return recombineAlternating(cvecs);
		case MostOften:
			return recombineMostOften(cvecs);
		default:
			return recombineMostOften(cvecs);
		}
	}
	
	public ConfigVector recombineTwo(ConfigVector cvec1, ConfigVector cvec2){
		ConfigVector newCvec = new ConfigVector();
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		for(int i=0;i<cvec1.getVars().size();i++){
			int r = rand.nextInt(2);
			if(r==0){
				newCvec.getVars().add(cvec1.getVar(i));
			}
			else{
				newCvec.getVars().add(cvec2.getVar(i));
			}
		}
		return newCvec;
	}
	
	public ConfigVector recombineAlternating(List<ConfigVector> cvecs){
		ConfigVector cvec = new ConfigVector();
		for(int i=0; i<cvecs.size()-1;i++){
			ConfigVector old = cvecs.get(i);
			cvec.setVar(i, old.getVar(i));
		}
		return cvec;
	}

	public ConfigVector recombineMostOften(List<ConfigVector> cvecs) {
		ConfigVector cvec = new ConfigVector();
		for(int i=0; i<cvecs.get(0).getVars().size();i++){
			Map<Var, Integer> scoreTable = new HashMap<Var, Integer>();
			//Adding a factor to the score so cvecs with a higher score have an higher impact
			int j=cvecs.size()-1;
			for(int k=0; k < cvecs.size(); k++){
				ConfigVector old = cvecs.get(k);
				Var v = old.getVar(i);
				if(scoreTable.containsKey(v)){
					scoreTable.put(v, scoreTable.get(v)+j);
				}
				else{
					scoreTable.put(v, j);
				}
				j--;
			}
			Var bestScoreVar=null;
			double bestScore=0;
			for(Var v: scoreTable.keySet()){
				if(scoreTable.get(v)>bestScore){
					bestScoreVar=v;
					bestScore = scoreTable.get(v);
				}
			}
			cvec.setVar(i, bestScoreVar);
		}
		return cvec;
	}
	
	
}
