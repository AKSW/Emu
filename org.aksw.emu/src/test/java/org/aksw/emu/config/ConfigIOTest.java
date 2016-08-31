package org.aksw.emu.config;

import org.aksw.emu.exception.EmuException;
import org.junit.Test;

public class ConfigIOTest {

	
	@Test
	public void test() throws EmuException{
		ConfigDefinition cdef = ConfigDefinitionReader.readConfigDef("src/test/resources/exampleCdef.properties");
		ConfigVector cvec = new ConfigVector();
		Var v1 = new Var("NumberOfBuffers=10000\nMaxDirtyBuffers=6000", "var1");
		Var v2 = new Var("MaxClientConnection=16", "var2");
		
		cvec.setVar(0, v1);
		cvec.setVar(1, v2);
		cdef.saveConfig(cvec);
		//TODO read test.txt and assert
		
		//TODO delete test.txt
	}
}
