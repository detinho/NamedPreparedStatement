package br.com.detinho;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class MapListTest {

	@Test
	public void createsANewMapList() {
		new MapList<String, String>();
	}
	
	@Test
	public void whenGetAnInexistentKeyReturnAnEmptyList() {
		MapList<String,String> mapList = new MapList<String, String>();
		
		assertEquals(Collections.emptyList(), mapList.get("ANYKEY"));
	}
	
	@Test
	public void putASingleValue() {
		MapList<String,String> mapList = new MapList<String, String>();
		mapList.put("KEY", "VALUE");
		
		assertEquals(Arrays.asList("VALUE"), mapList.get("KEY"));
	}
	
	@Test
	public void putMultipleValues() {
		MapList<String,String> mapList = new MapList<String, String>();
		mapList.put("KEY", "VALUE1");
		mapList.put("KEY", "VALUE2");
		mapList.put("OTHERKEY", "VALUE3");
		
		assertEquals(Arrays.asList("VALUE1", "VALUE2"), mapList.get("KEY"));
		assertEquals(Arrays.asList("VALUE3"), mapList.get("OTHERKEY"));
	}

}
