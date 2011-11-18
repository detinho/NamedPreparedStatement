package br.com.detinho;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreparedStatementParser {

	private final String statement;
	private String parsedStatement = "";

	enum STATES {NORMAL, PARAMETER};
	private STATES state;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private Map<String, List<Position>> parameterPositions = new HashMap<String, List<Position>>();
	private List<Position> orderedParameterPositions = new ArrayList<Position>();
	private Map<String, List<Integer>> parameterIndexes = new HashMap<String, List<Integer>>();

	public PreparedStatementParser(String statement) {
		this.statement = statement;
		parseStatement();
	}
	
	public String parsedSql() {
		checkParametersAreSet();
		generateFinalStatement();
		
		return parsedStatement;
	}
	
	private void checkParametersAreSet() {
		for (String parameterName : parameters.keySet())
			if (parameters.get(parameterName) == null)
				throw new IllegalStateException("The parameter " + parameterName + " is not set.");
	}
	
	private void generateFinalStatement() {
		parsedStatement = "";
		int currentStatementIndex = 1;
		int startIndex = 0;
		int lastPositionEnd = -1;
		for (Position position : orderedParameterPositions) {
			parsedStatement += statement.substring(startIndex, position.getStart());
			if (parameters.get(position.getName()) instanceof Collection<?>) {
				Collection<?> tempCollection = (Collection<?>)parameters.get(position.getName());
				parsedStatement += repeatWithCommas("?", tempCollection.size());
				
				for (int i = 0; i < tempCollection.size(); i++)
					putParameterIndex(position.getName(), currentStatementIndex++);
				
			} else {
				parsedStatement += "?";
				putParameterIndex(position.getName(), currentStatementIndex++);
			}
			startIndex = position.getEnd();
			lastPositionEnd = position.getEnd();
		}
		
		if (lastPositionEnd != -1) {
			String lastPartOfStatement = statement.substring(lastPositionEnd, statement.length());
			if (!lastPartOfStatement.isEmpty() && !Character.isJavaIdentifierPart(lastPartOfStatement.charAt(0)))
				parsedStatement += lastPartOfStatement;
		}
	}

	private void putParameterIndex(String parameterName, int currentStatementIndex) {
		List<Integer> ilist = parameterIndexes.get(parameterName);
		if (ilist == null) {
			ilist = new ArrayList<Integer>();
			parameterIndexes.put(parameterName, ilist);
		}
		ilist.add(currentStatementIndex);
	}

	public void parseStatement() {
		int actualIndex = 0;
		
		int startPosition = -1;
		int endPosition = -1;
		
		state = STATES.NORMAL;
		
		String parameterName = "";
		while (actualIndex < statement.length()) {
			final char actualChar = statement.charAt(actualIndex);
						
			if (state == STATES.PARAMETER) {
				if (Character.isJavaIdentifierPart(actualChar))
					parameterName += actualChar;
				
				if (!(Character.isJavaIdentifierPart(actualChar)) || actualIndex == statement.length()-1) {
					parameterName = parameterName.trim();
					
					if (!parameterName.endsWith(String.valueOf(actualChar)))
						parsedStatement += actualChar;

					endPosition = actualIndex;
					parameters.put(parameterName, null);
					putPosition(parameterName, startPosition, endPosition);
					state = STATES.NORMAL;
					parameterName = "";
					
					startPosition = -1;
					endPosition = -1;
				}
			} else {
				if (actualChar == ':') {
					state = STATES.PARAMETER;
					startPosition = actualIndex;
				}
			}
			
			actualIndex++;
		}
	}
	
	private void putPosition(String parameterName, int startPosition,
			int endPosition) {
		List<Position> plist = parameterPositions.get(parameterName);
		if (plist == null) {
			plist = new ArrayList<Position>();
			parameterPositions.put(parameterName, plist);
		}
		Position position = Position.make(parameterName, startPosition, endPosition);
		
		plist.add(position);
		orderedParameterPositions.add(position);
		
	}

	public void setInteger(String parameter, Integer value) {
		parameters.put(parameter, value);
	}

	public Integer getInteger(String parameter) {
		return (Integer)parameters.get(parameter);
	}

	public void setString(String parameter, String value) {
		parameters.put(parameter, value);
	}

	public String getString(String parameter) {
		return (String)parameters.get(parameter);
	}

	public void setCollection(String parameter, Collection<? extends Object> collection) {
		parameters.put(parameter, collection);
	}
	
	private String repeatWithCommas(String str, int times) {
		String result = "";
		for (int count = 1; count <= times; count++) {
			result += str;
			if (count != times)
				result += ",";
		}
		return result;
	}

	public List<Position> getParameterPositions(String parameterName) {
		return parameterPositions.get(parameterName);
	}

	public List<Integer> getParameterIndexes(String parameterName) {
		return parameterIndexes.get(parameterName);
	}
}
