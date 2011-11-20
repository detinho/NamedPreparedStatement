package br.com.detinho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.detinho.Util.isValidChar;

public class StatementParser {

	private final String statement;
	
	private Map<String, Object> parameters = new HashMap<String, Object>();
	private List<Position> parameterPositions = new ArrayList<Position>();

	private int actualIndex;
	private int startPosition;
	private int endPosition;
	
	enum STATES {NORMAL, PARAMETER, SINGLE_QUOTE, DOUBLE_QUOTE};
	private STATES state;

	public StatementParser(String statement) {
		this.statement = statement;
		
		actualIndex = 0;
		resetStateToNormal();
	}

	private void resetStateToNormal() {
		startPosition = -1;
		endPosition = -1;
		state = STATES.NORMAL;
	}

	public Map<String, Object> getParametersFound() {
		return parameters;
	}

	public List<Position> getParameterPositions() {
		return parameterPositions;
	}

	public void parse() {
		String parameterName = "";
		while (actualIndex < statement.length()) {
			final char actualChar = statement.charAt(actualIndex);
						
			if (state == STATES.PARAMETER) {
				if (isValidChar(actualChar))
					parameterName += actualChar;
				
				if (!(isValidChar(actualChar)) || lastChar()) {
					parameterName = parameterName.trim();
					
					endPosition = actualIndex;
					parameters.put(parameterName, null);
					putPosition(parameterName, startPosition, endPosition);
					
					parameterName = "";
					resetStateToNormal();
				}
			} else if (actualChar == ':')
				mayChangeToParameterState();
			
			else if (actualChar == '\'' && state != STATES.DOUBLE_QUOTE)
				mayChangeToSingleQuoteState();
			
			else if (actualChar == '\"' && state != STATES.SINGLE_QUOTE)
				mayChangeToDoubleQuoteState();				
			
			actualIndex++;
		}		
	}
	
	private void mayChangeToParameterState() {
		if (state == STATES.NORMAL) {
			state = STATES.PARAMETER;
			startPosition = actualIndex;
		}
	}
	
	private void mayChangeToSingleQuoteState() {
		if (state == STATES.SINGLE_QUOTE)
			state = STATES.NORMAL;
		else
			state = STATES.SINGLE_QUOTE;
	}

	private void mayChangeToDoubleQuoteState() {
		if (state == STATES.DOUBLE_QUOTE)
			state = STATES.NORMAL;
		else
			state = STATES.DOUBLE_QUOTE;
	}

	private boolean lastChar() {
		return actualIndex == statement.length()-1;
	}
	
	private void putPosition(String parameterName, int startPosition,
			int endPosition) {
		Position position = Position.make(parameterName, startPosition, endPosition);
		parameterPositions.add(position);
	}


}
