package org.aksw.emu.score;

import org.aksw.emu.exception.EmuException;

public class ScoreCalculator {

	public static double getScore(String folder, String metric) throws EmuException{
		return ScoreMetric.buildTree(folder, metric).get();
	}
	
}
