package org.aksw.emu.config;

import java.io.File;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmuConfig {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(EmuConfig.class);

//    private static final String DEFAULT_EMU_PROPERTIES_FILE_NAME = "emu.properties";
    public static final String EMU_VERSION_PROPERTY_NAME = "org.aksw.emu.Version";

    private static Configuration instance = null;

	private static String propertiesFile="emu.properties";

    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new CompositeConfiguration();
            loadAdditionalProperties(propertiesFile);
        }
        return instance;
    }

    public static synchronized void loadAdditionalProperties(String fileName) {
        try {
            ((CompositeConfiguration) getInstance()).addConfiguration(new PropertiesConfiguration(new File(propertiesFile)));
        } catch (ConfigurationException e) {
            LOGGER.error("Couldnt load Properties from the properties file (\"" + fileName
                    + "\"). This GERBIL instance won't work as expected.", e);
        }
    }

    public static String getEmuVersion() {
        return getInstance().getString(EMU_VERSION_PROPERTY_NAME);
    }

	public static void setPropertiesFile(String string) {
		propertiesFile = string;
	}
}
