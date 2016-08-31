package org.aksw.emu.connection;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;

public class SPARQLConnection implements Connection {

	private String endpoint;
	private String user;
	private String pwd;

	@Override
	public void init(String endpoint, String user, String pwd) {
		this.endpoint = endpoint;
		this.user = user;
		this.pwd = pwd;
	}

	@Override
	public void executeUpdate(String update) {
		// TODO
	}

	@Override
	public Model executeConstruct(String query) {
		Query q = QueryFactory.create(query);
		try (QueryExecution qexec = QueryExecutionFactory.createServiceRequest(
				endpoint, q)) {
			return qexec.execConstruct();
		}
	}

	@Override
	public Model executeDescribe(String query) {
		Query q = QueryFactory.create(query);
		try (QueryExecution qexec = QueryExecutionFactory.createServiceRequest(
				endpoint, q)) {
			return qexec.execDescribe();
		}
	}

	@Override
	public Boolean executeAsk(String query) {
		Query q = QueryFactory.create(query);
		try (QueryExecution qexec = QueryExecutionFactory.createServiceRequest(
				endpoint, q)) {
			return qexec.execAsk();
		}
	}

	@Override
	public ResultSet executeSelect(String query) {
		Query q = QueryFactory.create(query);
		try (QueryExecution qexec = QueryExecutionFactory.createServiceRequest(
				endpoint, q)) {
			ResultSet results = qexec.execSelect();
			results = ResultSetFactory.copyResults(results);
			return results;
		}
	}

}
