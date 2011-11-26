package br.com.detinho;

public class StubbedParamInfo {

	private final Object value;
	private final Class<?> cls;

	public StubbedParamInfo(Object fvalue, Class<?> cls) {
		this.value = fvalue;
		this.cls = cls;
		
	}

	public Object getValue() {
		return value;
	}
	
	public Class<?> getParamClass() {
		return cls;
	}

}
