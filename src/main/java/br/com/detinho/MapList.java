package br.com.detinho;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapList<K, V> {

	private Map<K, List<V>> mapList = new HashMap<K, List<V>>();
	
	public List<V> get(K key) {
		List<V> value = mapList.get(key);
		if (value == null)
			return Collections.emptyList();
		else
			return value;
	}

	public void put(K key, V value) {
		List<V> list = mapList.get(key);
		if (list == null) {
			list = new ArrayList<V>();
			mapList.put(key, list);
		}
		list.add(value);
	}

}
