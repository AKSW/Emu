package org.aksw.emu.score;

import org.junit.Test;

public class MetricTest {

	
	@Test
	public void test(){
		String metric = "ADD(SUB(AVG(QPS(Sum, 0, 16, 16)), NQTL(Mean, 0, 16, 16)), NQTL(Mean, 0, 16, 1))";
		metric="ADD(NQTL(Sum, 0,1,0), NQTL(Sum, 0, 2,0))";
		System.out.println(ScoreMetric.buildTree("", metric));
		
	}
}
