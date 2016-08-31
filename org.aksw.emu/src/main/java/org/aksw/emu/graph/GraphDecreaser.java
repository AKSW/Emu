package org.aksw.emu.graph;

import org.aksw.emu.exception.EmuException;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;

/**
 * Interface to decrease a Graph 
 * 
 * @author tortugaattack
 *
 */
public interface GraphDecreaser {

	public enum Type{
		RDF, TURTLE, NTRIPLE
	};
	
	/**
	 * Get a Graph by querying an endpoint
	 * 
	 * @param endpoint
	 */
	public void init(String endpoint) throws EmuException;
	
	/**
	 * Get a Graph by an RDF, TURTLE, NTRIPLE File
	 * 
	 * @param file
	 * @param type
	 */
	public void init(String file, String type) throws EmuException;
	
	/**
	 * Get a graph by Model
	 * 
	 * @param model
	 * @throws EmuException 
	 */
	public void init(Model model) throws EmuException;

	/**
	 * Get a Graph by Jena Graph
	 * @param graph
	 * @throws EmuException 
	 */
	public void init(Graph graph) throws EmuException;
	
	public void decreaseGraphToFile(String output, String type) throws EmuException;
	
	public void decreaseGraphToEndpoint(String endpoint) throws EmuException;

	void init() throws EmuException;

	void init(String endpoint, String user, String pwd);
	
}
