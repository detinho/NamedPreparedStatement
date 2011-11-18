package br.com.detinho;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class NamedPreparedStatementTest {

	@Test
	public void createANewNamedPreparedStatement() {
		new PreparedStatement("SELECT * FROM TABLE_NAME");
	}
	
	@Test(expected=IllegalStateException.class)
	public void beforeFormatTheFinalStatementCheckIfAllParametersAreSet() {
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID");
		stmt.parsedSql();		
	}

	@Test
	public void createANewNamedPreparedStatementASingleParameter() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID = ?";
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID");
		stmt.setInteger("ID", 1);

		assertEquals(parsedSql, stmt.parsedSql());
	}

	@Test
	public void createANamedPreparedStatementWithTwoParameters() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID = ? OR NAME = ?";
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID OR NAME = :NAME");
		
		stmt.setInteger("ID", 1);
		stmt.setString("NAME", "Marcos");		
		
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
		List<Position> param1PairList = Arrays.asList(Position.make("", 38, 45), Position.make("", 74, 80));
		List<Position> param2PairList = Arrays.asList(Position.make("", 56, 63));

		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE COL1 = :PARAM1 OR COL2 = :PARAM2 OR COL3 = :PARAM1");
		
		stmt.setInteger("PARAM1", 1);
		stmt.setString("PARAM2", "Marcos");		
		
		assertEquals(param1PairList, stmt.getParameterPositions("PARAM1"));
		assertEquals(param2PairList, stmt.getParameterPositions("PARAM2"));
		assertEquals(parsedSql, stmt.parsedSql());
	}
	
	@Test
	public void verifyTheIndexesOfTheSimpleParameters() {
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE COL1 = :PARAM1 OR COL2 = :PARAM2 OR COL3 = :PARAM1");
		
		stmt.setInteger("PARAM1", 1);
		stmt.setString("PARAM2", "Marcos");

		stmt.parsedSql();
		assertEquals(Arrays.asList(1, 3), stmt.getParameterIndexes("PARAM1"));
		assertEquals(Arrays.asList(2), stmt.getParameterIndexes("PARAM2"));
	}

	@Test
	public void useACollectionParameter() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID IN (?,?,?,?)";
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID IN (:IDS)");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		
		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(1, 2, 3, 4), stmt.getParameterIndexes("IDS"));
	}
	
	@Test
	public void useTwoCollectionParameters() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID IN (?,?,?,?) OR ID IN (?,?,?)";
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID IN (:IDS) OR ID IN (:OTHER_IDS)");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		stmt.setCollection("OTHER_IDS", Arrays.asList("5", "6", "7"));
		
		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(1, 2, 3, 4), stmt.getParameterIndexes("IDS"));
		assertEquals(Arrays.asList(5, 6, 7), stmt.getParameterIndexes("OTHER_IDS"));
	}
	
	@Test
	public void useACollectionParameterAndASimpleParameter() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID IN (?,?,?,?) AND AGE >= ?";
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID IN (:IDS) AND AGE >= :AGE");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		stmt.setInteger("AGE", 10);
		
		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(1, 2, 3, 4), stmt.getParameterIndexes("IDS"));
		assertEquals(Arrays.asList(5), stmt.getParameterIndexes("AGE"));
	}
	
	@Test
	public void useASimpleParameterAndACollectionParameter() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE AGE >= ? AND ID IN (?,?,?,?)";
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE AGE >= :AGE AND ID IN (:IDS)");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		stmt.setInteger("AGE", 10);
		
		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(2, 3, 4, 5), stmt.getParameterIndexes("IDS"));
		assertEquals(Arrays.asList(1), stmt.getParameterIndexes("AGE"));		
	}
	
	@Test
	public void useTheSameCollectionParameterTwice() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID IN (?,?,?,?) OR ID IN (?,?,?,?)";
		PreparedStatement stmt = new PreparedStatement(
				"SELECT * FROM TABLE_NAME WHERE ID IN (:IDS) OR ID IN (:IDS)");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		
		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8), stmt.getParameterIndexes("IDS"));
	}

}
