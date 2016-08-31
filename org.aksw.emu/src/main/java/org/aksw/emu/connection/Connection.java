package org.aksw.emu.connection;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;


public interface Connection {
	
	public Model executeConstruct(String query);
	public Model executeDescribe(String query);
	public Boolean executeAsk(String query);
	public ResultSet executeSelect(String query);
	
	public void executeUpdate(String update);

	void init(String endpoint, String user, String pwd);
	
}
