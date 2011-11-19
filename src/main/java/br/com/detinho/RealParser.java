package br.com.detinho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.detinho.PreparedStatementParser.STATES;

public class RealParser {

	private final String statement;
	
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<Position> parameterPositions = new ArrayList<Position>();

	public RealParser(String statement) {
		this.statement = statement;
	}

	public Map<String, Object> getParametersFound() {
		return parameters;
	}

	public List<Position> getParameterPositions() {
		return parameterPositions;
	}

	public void parse() {
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
	
	private boolean isValidChar(char ch) {
		return Character.isJavaIdentifierPart(ch);
	}

	private void putPosition(String parameterName, int startPosition,
			int endPosition) {
		Position position = Position.make(parameterName, startPosition, endPosition);
		parameterPositions.add(position);
	}


}
