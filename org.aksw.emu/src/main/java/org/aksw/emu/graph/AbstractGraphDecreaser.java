package org.aksw.emu.graph;

import org.aksw.emu.config.EmuConfig;
import org.aksw.emu.connection.SPARQLConnection;
import org.aksw.emu.exception.EmuException;
import org.apache.commons.configuration.Configuration;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;

public abstract class AbstractGraphDecreaser implements GraphDecreaser {

	private static final String EMU_GRAPH_BASE = "org.aksw.emu.graph";

	private static final String EMU_GRAPH_FILE = EMU_GRAPH_BASE + ".file";
	private static final String EMU_GRAPH_FILETYPE = EMU_GRAPH_BASE
			+ ".filetype";

	private static final String EMU_GRAPH_ENDPOINT = EMU_GRAPH_BASE
			+ ".endpoint";
	private static final String EMU_GRAPH_USER = EMU_GRAPH_BASE + ".user";
	private static final String EMU_GRAPH_PWD = EMU_GRAPH_BASE + ".password";

	protected String file;
	protected String endpoint;
	protected String user;
	protected String pwd;
	protected String type;

	private SPARQLConnection con;

	@Override
	public void init() throws EmuException {
		Configuration conf = EmuConfig.getInstance();
		if (conf.containsKey(EMU_GRAPH_FILE)) {
			// FILE not endpoint
			init(conf.getString(EMU_GRAPH_FILE),
					conf.getString(EMU_GRAPH_FILETYPE));
		} else if (conf.containsKey(EMU_GRAPH_ENDPOINT)) {
			// ENDPOINT not file
			if (conf.containsKey(EMU_GRAPH_USER)) {
				init(conf.getString(EMU_GRAPH_ENDPOINT),
						conf.getString(EMU_GRAPH_USER),
						conf.getString(EMU_GRAPH_PWD));
			} else {
				init(conf.getString(EMU_GRAPH_ENDPOINT));
			}
		} else {
			throw new EmuException(
					"Could not load Graph due to missing propertie in graph.properties.");
		}
	}

	public void init(String endpoint) {
		// Create connection, set endpoint
		this.endpoint = endpoint;
		this.con = new SPARQLConnection();
		this.con.init(endpoint, null, null);
	}

	@Override
	public void init(String endpoint, String user, String pwd) {
		// TODO Auto-generated method stub
		this.endpoint = endpoint;
		this.user = user;
		this.pwd = pwd;
		this.con = new SPARQLConnection();
		this.con.init(endpoint, user, pwd);
	}

	public void init(String file, String type) {
		this.file = file;
		this.type = type;
	}

	public void init(Model model) throws EmuException {
		throw new EmuException(
				"Graph decreasing by jena model is not supported yet");
	}

	public void init(Graph graph) throws EmuException {
		throw new EmuException(
				"Graph decreasing by jena graph is not supported yet");
	}

}
