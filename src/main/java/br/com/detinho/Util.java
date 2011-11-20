package br.com.detinho;

public class Util {
	
	private Util() {}
	
	public static boolean isValidChar(char ch) {
		return Character.isJavaIdentifierPart(ch);
	}
}
