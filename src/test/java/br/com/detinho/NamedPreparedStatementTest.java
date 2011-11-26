package br.com.detinho;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.Before;
import org.junit.Test;

public class NamedPreparedStatementTest {

	private Connection conn;

	@Before
	public void setUp() {
		conn = new ConnectionStub();
	}
	
	@Test
	public void createANewNamedPreparedStatement() {
		NamedPreparedStatement stmt = 
				NamedPreparedStatement.prepareStatement(conn, "SELECT * FROM TBL WHERE ID = :ID");
		assertNotNull(stmt);
	}
	
	@Test
	public void setsTheValueOfAnIntegerScalarParameter() {
//		int paramIdIndex = 1;
//		NamedPreparedStatement stmt = 
//				NamedPreparedStatement.prepareStatement(conn, "SELECT * FROM TBL WHERE ID = :ID");
//		stmt.setInt("ID", 1);
//		
//		PreparedStatementStub generatedStmt = (PreparedStatementStub) stmt.getGeneratedStatement();
//		assertParameterSet(generatedStmt, paramIdIndex, 1, Integer.class);
	}

}
