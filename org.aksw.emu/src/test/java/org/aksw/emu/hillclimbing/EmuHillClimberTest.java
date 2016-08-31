package org.aksw.emu.hillclimbing;

import java.util.List;

import org.aksw.emu.config.ConfigDefinition;
import org.aksw.emu.config.ConfigDefinitionReader;
import org.aksw.emu.config.ConfigVector;
import org.aksw.emu.config.Var;
import org.aksw.emu.exception.EmuException;
import org.junit.Test;

public class EmuHillClimberTest {

	@Test
	public void test2() throws EmuException{
		EmuHillClimber climber = new EmuHillClimber();
		ConfigDefinition cdef = ConfigDefinitionReader.readConfigDef("src/test/resources/exampleCdef.properties");
		List<ConfigVector> cvecs = climber.getInitialConstellations(cdef);
		
		System.out.println(climber.mutateSafe(cvecs.get(0), cdef, cvecs));
		System.out.println(cvecs);
	}
	
	@Test
	public void test(){
		ConfigVector[] cvecs = new ConfigVector[4];
		cvecs[0] = new ConfigVector();
		cvecs[1] = new ConfigVector();
		cvecs[2] = new ConfigVector();
		cvecs[3] = new ConfigVector();
		cvecs[0].getVars().add(new Var("name1", "1"));
		cvecs[0].getVars().add(new Var("name2", "a"));
		cvecs[0].getVars().add(new Var("name3", "a1"));
		cvecs[0].setScore(50);
		cvecs[1].getVars().add(new Var("name1", "2"));
		cvecs[1].getVars().add(new Var("name2", "b"));
		cvecs[1].getVars().add(new Var("name3", "b1"));
		cvecs[1].setScore(10);
		cvecs[2].getVars().add(new Var("name1", "4"));
		cvecs[2].getVars().add(new Var("name2", "b"));
		cvecs[2].getVars().add(new Var("name3", "c1"));
		cvecs[2].setScore(8);
		cvecs[3].getVars().add(new Var("name1", "4"));
		cvecs[3].getVars().add(new Var("name2", "b"));
		cvecs[3].getVars().add(new Var("name3", "c1"));
		cvecs[3].setScore(8);
//		ConfigDefinition cdef = new ConfigDefinition();
		
		System.out.println(cvecs[3].hashCode());
		System.out.println(cvecs[2].hashCode());
//		System.out.println(climber.mutateSafe(cvecs[2], cdef, cvecs));
//		System.out.println(climber.recombineSafe(cvecs, cdef, RecombineStrategy.Alternating));
//		System.out.println(climber.recombineSafe(cvecs, cdef, RecombineStrategy.MostOften));
	}
}
