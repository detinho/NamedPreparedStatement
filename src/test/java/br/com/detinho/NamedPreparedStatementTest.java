package br.com.detinho;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

public class NamedPreparedStatementTest {

	@Test
	public void createANewNamedPreparedStatement() {
		new PreparedStatement("SELECT * FROM TABLE_NAME");
	}

	@Test
	public void createANewNamedPreparedStatementASingleParameter() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID = ?";
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID");
		assertEquals(parsedSql, stmt.parsedSql());
	}

	@Test
	public void createANamedPreparedStatementWithTwoParameters() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID = ? OR NAME = ?";
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID OR NAME = :NAME");
		assertEquals(parsedSql, stmt.parsedSql());
	}

	@Test
	public void setTheNamedParameter() {
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID");
		stmt.setInteger("ID", 1);
		assertEquals(new Integer(1), stmt.getInteger("ID"));
	}

	@Test
	public void setTwoNamedParameters() {
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID OR  NAME = :NAME");
		stmt.setInteger("ID", 1);
		stmt.setString("NAME", "Marcos");

		assertEquals(new Integer(1), stmt.getInteger("ID"));
		assertEquals("Marcos", stmt.getString("NAME"));
	}

	@Test
	public void useTheSameParameterTwice() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE COL1 = ? OR COL2 = ? OR COL3 = ?";
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE COL1 = :PARAM1 OR COL2 = :PARAM2 OR COL3 = :PARAM1");
		assertEquals(parsedSql, stmt.parsedSql());

		assertEquals(Arrays.asList(1, 3), stmt.getParameterPositions("PARAM1"));
		assertEquals(Arrays.asList(2), stmt.getParameterPositions("PARAM2"));
	}

	@Test
	@Ignore
	public void useACollectionParameter() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID IN (?,?,?,?)";
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID IN (:IDS)");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		
		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(1, 2, 3, 4), stmt.getParameterPositions("IDS"));
	}

}
