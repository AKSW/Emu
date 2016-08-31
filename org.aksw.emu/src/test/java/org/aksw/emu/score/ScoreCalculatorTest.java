package org.aksw.emu.score;

import static org.junit.Assert.*;

import org.aksw.emu.exception.EmuException;
import org.junit.Test;

public class ScoreCalculatorTest {

	@Test
	public void test() throws EmuException{
		String metric = "AVG(QPS(Sum, 0,1,0))";
		double res= ScoreCalculator.getScore("src/test/resources/results", metric);
		System.out.println(res);
		assertEquals(res, 4.0,0.0);
		
		metric = "ADD(AVG(QPS(Sum, 0,1,0)), NQTL(Sum,0,1,0))";
		res= ScoreCalculator.getScore("src/test/resources/results", metric);
//		System.out.println(res);
		assertEquals(res, 44.0, 0.0);
		
		metric = "ADD(AVG(QPS(Sum, 0,1,0)), SUB(NQTL(Sum,0,1,0), NQTL(Sum, 0,1,16)))";
		res= ScoreCalculator.getScore("src/test/resources/results", metric);
//		System.out.println(res);
		assertEquals(res, 34.0, 0.0);		
		
		metric = "ADD(AVG(QPS(Sum, 0,1,0)), MULTIPLY(NQTL(Sum,0,1,0), NQTL(Sum, 0,1,16)))";
		res= ScoreCalculator.getScore("src/test/resources/results", metric);
//		System.out.println(res);
		assertEquals(res, 404.0, 0.0);
		
		metric = "ADD(AVG(QPS(Sum, 0,1,0)), DIV(NQTL(Sum,0,1,0), NQTL(Sum, 0,1,16)))";
		res= ScoreCalculator.getScore("src/test/resources/results", metric);
//		System.out.println(res);
		assertEquals(res, 8.0, 0.0);
	}
	
}
