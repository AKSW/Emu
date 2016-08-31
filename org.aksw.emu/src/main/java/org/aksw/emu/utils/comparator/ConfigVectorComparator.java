package org.aksw.emu.utils.comparator;

import java.util.Comparator;

import org.aksw.emu.config.ConfigVector;

public class ConfigVectorComparator implements Comparator<ConfigVector> {

	@Override
	public int compare(ConfigVector o1, ConfigVector o2) {
		return Double.valueOf(o2.getScore()).compareTo(o1.getScore());
	}



}
