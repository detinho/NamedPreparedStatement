package br.com.detinho;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class StatementParserTest {

	@Test
	public void noParametersFound() {
		StatementParser parser = new StatementParser("SELECT * FROM TBL");
		parser.parse();
		
		Map<String, Object> parametersFound = parser.getParametersFound();
		List<Position> parameterPositions = parser.getParameterPositions();
		
		assertEquals(Collections.emptyMap(), parametersFound);
		assertEquals(Collections.emptyList(), parameterPositions);
	}
	
	@Test
	public void oneParameterFound() {
		Map<String, Object> parametersFound = new HashMap<String, Object>();
		parametersFound.put("ID", null);
		
		List<Position> parameterPositions = Arrays.asList(Position.make("ID", 29, 31));

		StatementParser parser = new StatementParser("SELECT * FROM TBL WHERE ID = :ID");
		parser.parse();
		
		assertEquals(parametersFound, parser.getParametersFound());
		assertEquals(parameterPositions, parser.getParameterPositions());
	}
	
	@Test
	public void useTheSameParameterTwiceAndCheckParameterPositions() {
		List<Position> paramList = Arrays.asList(Position.make("", 38, 45), Position.make("", 56, 63), Position.make("", 74, 80));

		StatementParser parser = new StatementParser("SELECT * FROM TABLE_NAME WHERE COL1 = :PARAM1 OR COL2 = :PARAM2 OR COL3 = :PARAM1");
		parser.parse();
		
		assertEquals(paramList, parser.getParameterPositions());
	}

	@Test
	public void ignoreAnythingInsideSingleQuotes() {
		String statement = "SELECT * FROM TBL WHERE NAME = ':NAME'";
		StatementParser parser = new StatementParser(statement);
		parser.parse();
		
		assertEquals(Collections.emptyMap(), parser.getParametersFound());
	}
	
	@Test
	public void ignoreAnyThingInsideDoubleQuotes() {
		StatementParser parser = new StatementParser("SELECT * FROM TBL WHERE NAME = \":NAME\"");
		parser.parse();
		
		assertEquals(Collections.emptyMap(), parser.getParametersFound());
	}
	
	@Test
	public void ignoreDoubleQuotesInsideSingleQuotes() {
		StatementParser parser = new StatementParser("SELECT * FROM TBL WHERE NAME = \":NAME\"");
		parser.parse();
		
		assertEquals(Collections.emptyMap(), parser.getParametersFound());
	}
	
	@Test
	public void ignoreSingleQuotesInsideDoubleQuotes() {
		StatementParser parser = new StatementParser("");
		parser.parse();
		
		assertEquals(Collections.emptyMap(), parser.getParametersFound());
	}
	
	@Test
	public void ignoreDoubleQuotesInsideSingleQuotesAndCheckTheParameters() {
		Map<String, Object> parametersFound = new HashMap<String, Object>();
		parametersFound.put("AGE", null);
		List<Position> positions = Arrays.asList(Position.make("AGE", 46, 49));
		
		StatementParser parser = new StatementParser("SELECT * FROM TBL WHERE NAME = '\"' AND AGE >= :AGE");
		parser.parse();

		assertEquals(parametersFound, parser.getParametersFound());
		assertEquals(positions, parser.getParameterPositions());
	}
	
	@Test
	public void ignoreSingleQuotesInsideDoubleQuotesAndCheckTheParameters() {
		Map<String, Object> parametersFound = new HashMap<String, Object>();
		parametersFound.put("AGE", null);
		List<Position> positions = Arrays.asList(Position.make("AGE", 46, 49));
		
		StatementParser parser = new StatementParser("SELECT * FROM TBL WHERE NAME = \"'\" AND AGE >= :AGE");
		parser.parse();

		assertEquals(parametersFound, parser.getParametersFound());
		assertEquals(positions, parser.getParameterPositions());
	}
	
	@Test
	public void verifyTheUnterminatedSingleQuoteCaseParameterFirst() {
		Map<String, Object> parametersFound = new HashMap<String, Object>();
		parametersFound.put("AGE", null);
		List<Position> positions = Arrays.asList(Position.make("AGE", 30, 34));
		
		StatementParser parser = new StatementParser("SELECT * FROM TBL WHERE AGE = :AGE AND NAME = 'TEST");
		parser.parse();

		assertEquals(parametersFound, parser.getParametersFound());
		assertEquals(positions, parser.getParameterPositions());
	}
	
	@Test
	public void verifyTheUnterminatedDoubleQuoteCaseParameterFirst() {
		Map<String, Object> parametersFound = new HashMap<String, Object>();
		parametersFound.put("AGE", null);
		List<Position> positions = Arrays.asList(Position.make("AGE", 30, 34));
		
		StatementParser parser = new StatementParser("SELECT * FROM TBL WHERE AGE = :AGE AND NAME = \"TEST");
		parser.parse();

		assertEquals(parametersFound, parser.getParametersFound());
		assertEquals(positions, parser.getParameterPositions());
	}	
	
}
