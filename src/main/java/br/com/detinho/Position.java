package br.com.detinho;

public final class Position {

	private final String name;
	private final int start;
	private final int end;

	public static Position make(String name, int start, int end) {
		return new Position(name, start, end);
	}
	
	private Position(String name, int start, int end) {
		this.name = name;
		this.start = start;
		this.end = end;
	}
	
	public String getName() {
		return name;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	@Override
	public boolean equals(Object other) {
		if (! (other instanceof Position)) return false;
		
		Position otherPosition = (Position)other;
		return otherPosition.start == start && otherPosition.end == end;
	}
	
	@Override
	public int hashCode() {
		return 37 * (start + end);
	}
	
	@Override
	public String toString() {
		return "[" + start + ":" + end + "]";
	}
	
}
