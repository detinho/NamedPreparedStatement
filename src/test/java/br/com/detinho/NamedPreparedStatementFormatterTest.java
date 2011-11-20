package br.com.detinho;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class NamedPreparedStatementFormatterTest {

	@Test
	public void createANewNamedPreparedStatement() {
		new PreparedStatementFormatter("SELECT * FROM TABLE_NAME");
	}
	
	@Test
	public void checkTheParsedSqlWithoutParameters() {
		String sql = "SELECT * FROM TABLE_NAME";
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(sql);

		stmt.parse();
		assertEquals(sql, stmt.parsedSql());
	}
	
	@Test(expected=IllegalStateException.class)
	public void beforeFormatTheFinalStatementCheckIfAllParametersAreSet() {
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID");
		stmt.parse();
		stmt.parsedSql();		
	}

	@Test
	public void createANewNamedPreparedStatementASingleParameter() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID = ?";
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID");
		stmt.setInteger("ID", 1);

		stmt.parse();
		assertEquals(parsedSql, stmt.parsedSql());
	}

	@Test
	public void createANamedPreparedStatementWithTwoParameters() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID = ? OR NAME = ?";
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID OR NAME = :NAME");
		
		stmt.setInteger("ID", 1);
		stmt.setString("NAME", "Marcos");		

		stmt.parse();
		assertEquals(parsedSql, stmt.parsedSql());
	}

	@Test
	public void setTheNamedParameter() {
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID");
		
		stmt.setInteger("ID", 1);
		
		assertEquals(new Integer(1), stmt.getInteger("ID"));
	}

	@Test
	public void setTwoNamedParameters() {
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID OR  NAME = :NAME");
		stmt.setInteger("ID", 1);
		stmt.setString("NAME", "Marcos");

		assertEquals(new Integer(1), stmt.getInteger("ID"));
		assertEquals("Marcos", stmt.getString("NAME"));
	}

	@Test
	public void useTheSameParameterTwice() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE COL1 = ? OR COL2 = ? OR COL3 = ?";

		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE COL1 = :PARAM1 OR COL2 = :PARAM2 OR COL3 = :PARAM1");
		
		stmt.setInteger("PARAM1", 1);
		stmt.setString("PARAM2", "Marcos");
		stmt.parse();
		
		assertEquals(parsedSql, stmt.parsedSql());
	}
	
	@Test
	public void verifyTheIndexesOfTheSimpleParameters() {
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE COL1 = :PARAM1 OR COL2 = :PARAM2 OR COL3 = :PARAM1");
		
		stmt.setInteger("PARAM1", 1);
		stmt.setString("PARAM2", "Marcos");
		stmt.parse();
		
		stmt.parsedSql();
		assertEquals(Arrays.asList(1, 3), stmt.getParameterIndexes("PARAM1"));
		assertEquals(Arrays.asList(2), stmt.getParameterIndexes("PARAM2"));
	}

	@Test
	public void useACollectionParameter() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID IN (?,?,?,?)";
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE ID IN (:IDS)");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		stmt.parse();
		
		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(1, 2, 3, 4), stmt.getParameterIndexes("IDS"));
	}
	
	@Test
	public void useTwoCollectionParameters() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID IN (?,?,?,?) OR ID IN (?,?,?)";
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE ID IN (:IDS) OR ID IN (:OTHER_IDS)");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		stmt.setCollection("OTHER_IDS", Arrays.asList("5", "6", "7"));
		stmt.parse();
		
		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(1, 2, 3, 4), stmt.getParameterIndexes("IDS"));
		assertEquals(Arrays.asList(5, 6, 7), stmt.getParameterIndexes("OTHER_IDS"));
	}
	
	@Test
	public void useACollectionParameterAndASimpleParameter() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID IN (?,?,?,?) AND AGE >= ?";
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE ID IN (:IDS) AND AGE >= :AGE");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		stmt.setInteger("AGE", 10);
		stmt.parse();
		
		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(1, 2, 3, 4), stmt.getParameterIndexes("IDS"));
		assertEquals(Arrays.asList(5), stmt.getParameterIndexes("AGE"));
	}
	
	@Test
	public void useASimpleParameterAndACollectionParameter() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE AGE >= ? AND ID IN (?,?,?,?)";
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE AGE >= :AGE AND ID IN (:IDS)");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		stmt.setInteger("AGE", 10);
		stmt.parse();
		
		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(2, 3, 4, 5), stmt.getParameterIndexes("IDS"));
		assertEquals(Arrays.asList(1), stmt.getParameterIndexes("AGE"));		
	}
	
	@Test
	public void useTheSameCollectionParameterTwice() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID IN (?,?,?,?) OR NAME = ? OR ID IN (?,?,?,?)";
		PreparedStatementFormatter stmt = new PreparedStatementFormatter(
				"SELECT * FROM TABLE_NAME WHERE ID IN (:IDS) OR NAME = :NAME OR ID IN (:IDS)");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		stmt.setString("NAME", "MARCOS");
		stmt.parse();

		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(1, 2, 3, 4, 6, 7, 8, 9), stmt.getParameterIndexes("IDS"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkIfAParameterExistsBeforeSet() {
		PreparedStatementFormatter stmt = new PreparedStatementFormatter("SELECT * FROM TBL");
		stmt.setCollection("ANYTHING", Collections.emptyList());
	}
}
