package br.com.detinho;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreparedStatement {

	private final String statement;

	enum STATES {NORMAL, PARAMETER};
	private STATES state;
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private Map<String, List<Integer>> parameterPositions = new HashMap<String, List<Integer>>();

	public PreparedStatement(String statement) {
		this.statement = statement;
	}
	
	public String parsedSql() {
		String parsedStatement = "";
		int actualIndex = 0;
		int actualPosition = 1;
		
		state = STATES.NORMAL;
		
		String parameterName = "";
		while (actualIndex < statement.length()) {
			final char actualChar = statement.charAt(actualIndex);
						
			if (state == STATES.PARAMETER) {
				if (Character.isJavaIdentifierPart(actualChar))
					parameterName += actualChar;
				
				if (!(Character.isJavaIdentifierPart(actualChar)) || actualIndex == statement.length()-1) {
					parameterName = parameterName.trim();
					
					parsedStatement += "?";
					if (!parameterName.endsWith(String.valueOf(actualChar)))
						parsedStatement += actualChar;

					parameters.put(parameterName, null);
					putPosition(parameterName, actualPosition);
					actualPosition++;
					state = STATES.NORMAL;
					parameterName = "";
				}
			} else {
				if (actualChar == ':') {
					state = STATES.PARAMETER;
				} else {
					parsedStatement += actualChar;
				}
			}
			
			actualIndex++;
		}
		
		return parsedStatement;
	}
	
	private void putPosition(String parameter, Integer position) {
		List<Integer> positions = parameterPositions.get(parameter);
		if (positions == null) {
			positions = new ArrayList<Integer>();
			parameterPositions.put(parameter, positions);
		}
		positions.add(position);
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

	public List<Integer> getParameterPositions(String parameter) {
		return parameterPositions.get(parameter);
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

}
