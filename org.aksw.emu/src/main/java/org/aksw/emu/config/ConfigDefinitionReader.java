package org.aksw.emu.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.aksw.emu.exception.EmuException;
import org.xml.sax.SAXException;

public class ConfigDefinitionReader {

	private static final String CONFIG_FILE_KEY = "ConfigFile";
	private static final String VARIABLES_KEY = "vars";
	private static final String OUTPUT_KEY = "Output";

	
	public static ConfigDefinition readConfigDef(String fileName) throws EmuException{
		
		Properties p = new Properties();
		ConfigDefinition cdef = null;
		
		try(FileInputStream fis = new FileInputStream(fileName)){
			p.load(fis);
			String file = p.getProperty(CONFIG_FILE_KEY);
			String output = p.getProperty(OUTPUT_KEY);
			cdef = new ConfigDefinition(file, output);
			String[] vars = p.getProperty(VARIABLES_KEY).split(",\\s*");
			for(String var : vars){
				cdef.setVars(var, 
						Integer.valueOf(p.getProperty(var+".start")), 
						p.getProperty(var+".line"), 
						p.getProperty(var+".values").split(",\\s*"));
			}
		}
		catch(NullPointerException | IOException | ParserConfigurationException | SAXException e){
			e.printStackTrace();
			throw new EmuException(e.getMessage());
			
		}
		
		return cdef;
	}
}
