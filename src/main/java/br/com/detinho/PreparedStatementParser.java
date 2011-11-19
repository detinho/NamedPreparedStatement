package br.com.detinho;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreparedStatementParser {

	private final String statement;
	private String parsedStatement = "";

	enum STATES {NORMAL, PARAMETER, SINGLE_QUOTE, DOUBLE_QUOTE};
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<Position> parameterPositions = new ArrayList<Position>();
	private MapList<String, Integer> parameterIndexes = new MapList<String, Integer>();

	public PreparedStatementParser(String statement) {
		this.statement = statement;
		parseStatement();
	}

	public String parsedSql() {
		return parsedStatement;
	}
	
	public List<Integer> getParameterIndexes(String parameterName) {
		return parameterIndexes.get(parameterName);
	}
	
	public List<Position> getParameterPositions() {
		return parameterPositions;
	}
	
	public void parse() {
		checkParametersAreSet();
		generateFinalStatement();
		defineParameterIndexes();
	}
	
	
	private void checkParametersAreSet() {
		for (String parameterName : parameters.keySet())
			if (parameters.get(parameterName) == null)
				throw new IllegalStateException("The parameter " + parameterName + " is not set.");
	}
	
	private void generateFinalStatement() {
		if (thereAreNoParameters()) {
			parsedStatement = statement;
			return;
		}
		
		parsedStatement = "";
		int startIndex = 0;
		int lastPositionEnd = -1;
		
		for (Position position : parameterPositions) {
			parsedStatement += statement.substring(startIndex, position.getStart());
			if (parameters.get(position.getName()) instanceof Collection<?>) {
				Collection<?> tempCollection = (Collection<?>)parameters.get(position.getName());
				parsedStatement += repeatWithCommas("?", tempCollection.size());
			} else {
				parsedStatement += "?";
			}
			startIndex = position.getEnd();
			lastPositionEnd = position.getEnd();
		}
		
		if (lastPositionEnd != -1) {
			String lastPartOfStatement = statement.substring(lastPositionEnd, statement.length());
			if (!lastPartOfStatement.isEmpty() && !isValidChar(lastPartOfStatement.charAt(0)))
				parsedStatement += lastPartOfStatement;
		}
	}
	
	private void defineParameterIndexes() {
		if (thereAreNoParameters()) {
			return;
		}
		
		int currentStatementIndex = 1;
		
		for (Position position : parameterPositions) {
			if (parameters.get(position.getName()) instanceof Collection<?>) {
				Collection<?> tempCollection = (Collection<?>)parameters.get(position.getName());
				
				for (int i = 0; i < tempCollection.size(); i++)
					parameterIndexes.put(position.getName(), currentStatementIndex++);
				
			} else {
				parameterIndexes.put(position.getName(), currentStatementIndex++);
			}
		}
	}

	private boolean thereAreNoParameters() {
		return parameterPositions.isEmpty();
	}
	
	private boolean isValidChar(char ch) {
		return Character.isJavaIdentifierPart(ch);
	}

	private void parseStatement() {
		int actualIndex = 0;
		
		int startPosition = -1;
		int endPosition = -1;
		
		STATES state = STATES.NORMAL;
		
		String parameterName = "";
		while (actualIndex < statement.length()) {
			final char actualChar = statement.charAt(actualIndex);
						
			if (state == STATES.PARAMETER) {
				if (isValidChar(actualChar))
					parameterName += actualChar;
				
				if (!(isValidChar(actualChar)) || actualIndex == statement.length()-1) {
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
			} else if (actualChar == ':') {
				if (state == STATES.NORMAL) {
					state = STATES.PARAMETER;
					startPosition = actualIndex;
				}
			} else if (actualChar == '\'' && state != STATES.DOUBLE_QUOTE) {
				if (state == STATES.SINGLE_QUOTE)
					state = STATES.NORMAL;
				else
					state = STATES.SINGLE_QUOTE;
			} else if (actualChar == '\"' && state != STATES.SINGLE_QUOTE) {
				if (state == STATES.DOUBLE_QUOTE)
					state = STATES.NORMAL;
				else
					state = STATES.DOUBLE_QUOTE;				
			}
			
			actualIndex++;
		}
	}
	
	private void putPosition(String parameterName, int startPosition,
			int endPosition) {
		Position position = Position.make(parameterName, startPosition, endPosition);
		parameterPositions.add(position);
	}

	public void setInteger(String parameter, Integer value) {
		checkIfParameterExists(parameter);
		parameters.put(parameter, value);
	}

	public Integer getInteger(String parameter) {
		return (Integer)parameters.get(parameter);
	}

	public void setString(String parameter, String value) {
		checkIfParameterExists(parameter);
		parameters.put(parameter, value);
	}

	public String getString(String parameter) {
		return (String)parameters.get(parameter);
	}

	public void setCollection(String parameter, Collection<? extends Object> collection) {
		checkIfParameterExists(parameter);
		parameters.put(parameter, collection);
	}
	
	private void checkIfParameterExists(String parameter) {
		if (!parameters.containsKey(parameter))
			throw new IllegalArgumentException("Parameter " + parameter + " does not exists.");
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
}
