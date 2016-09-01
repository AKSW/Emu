package org.aksw.emu;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.aksw.emu.config.ConfigDefinition;
import org.aksw.emu.config.ConfigDefinitionReader;
import org.aksw.emu.config.ConfigVector;
import org.aksw.emu.config.EmuConfig;
import org.aksw.emu.exception.EmuException;
import org.aksw.emu.hillclimbing.EmuHillClimber;
import org.aksw.emu.hillclimbing.EmuHillClimber.RecombineStrategy;

public class Main {


	private static final String RECOMBINE = "org.aksw.emu.recombine";

	public static void main(String[] args) throws EmuException, IOException{
		EmuConfig.setPropertiesFile(args[0]);
		ConfigDefinition cdef = ConfigDefinitionReader.readConfigDef(args[1]);
		EmuHillClimber climber = new EmuHillClimber();
		ConfigVector best =climber.execute(cdef, 
				RecombineStrategy.valueOf(EmuConfig.getInstance().getString(RECOMBINE)));
		System.out.println(best);
		File f = new File("best-config");
		f.createNewFile();
		PrintWriter pw = new PrintWriter(f);
		pw.println(best);
		pw.close();
	}
}
	
