package br.com.detinho;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class NamedPreparedStatementParserTest {

	@Test
	public void createANewNamedPreparedStatement() {
		new PreparedStatementParser("SELECT * FROM TABLE_NAME");
	}
	
	@Test
	public void checkTheParsedSqlWithoutParameters() {
		String sql = "SELECT * FROM TABLE_NAME";
		PreparedStatementParser stmt = new PreparedStatementParser(sql);

		stmt.parse();
		assertEquals(sql, stmt.parsedSql());
	}
	
	@Test(expected=IllegalStateException.class)
	public void beforeFormatTheFinalStatementCheckIfAllParametersAreSet() {
		PreparedStatementParser stmt = new PreparedStatementParser(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID");
		stmt.parse();
		stmt.parsedSql();		
	}

	@Test
	public void createANewNamedPreparedStatementASingleParameter() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID = ?";
		PreparedStatementParser stmt = new PreparedStatementParser(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID");
		stmt.setInteger("ID", 1);

		stmt.parse();
		assertEquals(parsedSql, stmt.parsedSql());
	}

	@Test
	public void createANamedPreparedStatementWithTwoParameters() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID = ? OR NAME = ?";
		PreparedStatementParser stmt = new PreparedStatementParser(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID OR NAME = :NAME");
		
		stmt.setInteger("ID", 1);
		stmt.setString("NAME", "Marcos");		

		stmt.parse();
		assertEquals(parsedSql, stmt.parsedSql());
	}

	@Test
	public void setTheNamedParameter() {
		PreparedStatementParser stmt = new PreparedStatementParser(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID");
		
		stmt.setInteger("ID", 1);
		
		assertEquals(new Integer(1), stmt.getInteger("ID"));
	}

	@Test
	public void setTwoNamedParameters() {
		PreparedStatementParser stmt = new PreparedStatementParser(
				"SELECT * FROM TABLE_NAME WHERE ID = :ID OR  NAME = :NAME");
		stmt.setInteger("ID", 1);
		stmt.setString("NAME", "Marcos");

		assertEquals(new Integer(1), stmt.getInteger("ID"));
		assertEquals("Marcos", stmt.getString("NAME"));
	}

	@Test
	public void useTheSameParameterTwice() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE COL1 = ? OR COL2 = ? OR COL3 = ?";

		PreparedStatementParser stmt = new PreparedStatementParser(
				"SELECT * FROM TABLE_NAME WHERE COL1 = :PARAM1 OR COL2 = :PARAM2 OR COL3 = :PARAM1");
		
		stmt.setInteger("PARAM1", 1);
		stmt.setString("PARAM2", "Marcos");
		stmt.parse();
		
		assertEquals(parsedSql, stmt.parsedSql());
	}
	
	@Test
	public void useTheSameParameterTwiceAndCheckParameterPositions() {
		List<Position> paramList = Arrays.asList(Position.make("", 38, 45), Position.make("", 56, 63), Position.make("", 74, 80));

		PreparedStatementParser stmt = new PreparedStatementParser(
				"SELECT * FROM TABLE_NAME WHERE COL1 = :PARAM1 OR COL2 = :PARAM2 OR COL3 = :PARAM1");
		
		stmt.setInteger("PARAM1", 1);
		stmt.setString("PARAM2", "Marcos");
		stmt.parse();
		
		assertEquals(paramList, stmt.getParameterPositions());
	}
	
	@Test
	public void verifyTheIndexesOfTheSimpleParameters() {
		PreparedStatementParser stmt = new PreparedStatementParser(
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
		PreparedStatementParser stmt = new PreparedStatementParser(
				"SELECT * FROM TABLE_NAME WHERE ID IN (:IDS)");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		stmt.parse();
		
		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(1, 2, 3, 4), stmt.getParameterIndexes("IDS"));
	}
	
	@Test
	public void useTwoCollectionParameters() {
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID IN (?,?,?,?) OR ID IN (?,?,?)";
		PreparedStatementParser stmt = new PreparedStatementParser(
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
		PreparedStatementParser stmt = new PreparedStatementParser(
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
		PreparedStatementParser stmt = new PreparedStatementParser(
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
		String parsedSql = "SELECT * FROM TABLE_NAME WHERE ID IN (?,?,?,?) OR ID IN (?,?,?,?)";
		PreparedStatementParser stmt = new PreparedStatementParser(
				"SELECT * FROM TABLE_NAME WHERE ID IN (:IDS) OR ID IN (:IDS)");
		stmt.setCollection("IDS", Arrays.asList("1", "2", "3", "4"));
		stmt.parse();

		assertEquals(parsedSql, stmt.parsedSql());
		assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8), stmt.getParameterIndexes("IDS"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void checkIfAParameterExistsBeforeSet() {
		PreparedStatementParser stmt = new PreparedStatementParser("SELECT * FROM TBL");
		stmt.setCollection("ANYTHING", Collections.emptyList());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void ignoreAnythingInsideSingleQuotes() {
		PreparedStatementParser stmt = new PreparedStatementParser("SELECT * FROM TBL WHERE NAME = ':NAME'");
		stmt.setString("NAME", "VALUE");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void ignoreAnyThingInsideDoubleQuotes() {
		PreparedStatementParser stmt = new PreparedStatementParser("SELECT * FROM TBL WHERE NAME = \":NAME\"");
		stmt.setString("NAME", "VALUE");		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void ignoreDoubleQuotesInsideSingleQuotes() {
		String sql = "SELECT * FROM TBL WHERE NAME = '\"'";
		PreparedStatementParser stmt = new PreparedStatementParser(sql);
		
		stmt.setString("NAME", "VALUE");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void ignoreSingleQuotesInsideDoubleQuotes() {
		String sql = "SELECT * FROM TBL WHERE NAME = \"'\"";
		PreparedStatementParser stmt = new PreparedStatementParser(sql);
		
		stmt.setString("NAME", "VALUE");
	}
	
	@Test
	public void ignoreDoubleQuotesInsideSingleQuotesAndCheckTheFinalSql() {
		String parsedSql = "SELECT * FROM TBL WHERE NAME = '\"' AND AGE >= ?";
		PreparedStatementParser stmt = 
				new PreparedStatementParser("SELECT * FROM TBL WHERE NAME = '\"' AND AGE >= :AGE");
		stmt.setInteger("AGE", 10);
		stmt.parse();
		
		assertEquals(parsedSql, stmt.parsedSql());
	}
	
	@Test
	public void ignoreSingleQuotesInsideDoubleQuotesAndCheckTheFinalSql() {
		String parsedSql = "SELECT * FROM TBL WHERE NAME = \"'\" AND AGE >= ?";
		PreparedStatementParser stmt = 
				new PreparedStatementParser("SELECT * FROM TBL WHERE NAME = \"'\" AND AGE >= :AGE");
		stmt.setInteger("AGE", 10);
		stmt.parse();		
		
		assertEquals(parsedSql, stmt.parsedSql());
	}
	
	@Test
	public void verifyTheUnterminatedSingleQuoteCaseParameterFirst() {
		String parsedSql = "SELECT * FROM TBL WHERE AGE = ? AND NAME = 'TEST";
		PreparedStatementParser stmt = 
				new PreparedStatementParser("SELECT * FROM TBL WHERE AGE = :AGE AND NAME = 'TEST");
		stmt.setInteger("AGE", 10);
		
		stmt.parse();
		assertEquals(parsedSql, stmt.parsedSql());
	}
	
	@Test
	public void verifyTheUnterminatedDoubleQuoteCaseParameterFirst() {
		String parsedSql = "SELECT * FROM TBL WHERE AGE = ? AND NAME = \"TEST";
		PreparedStatementParser stmt = 
				new PreparedStatementParser("SELECT * FROM TBL WHERE AGE = :AGE AND NAME = \"TEST");
		stmt.setInteger("AGE", 10);
		
		stmt.parse();		
		assertEquals(parsedSql, stmt.parsedSql());
	}
}
