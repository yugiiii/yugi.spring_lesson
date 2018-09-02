package com.queue.common.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListUtils<T> {
	
	/**
	 * Listのデータをマップから取得する
	 * @param map
	 * @param key
	 * @param class_
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(Map<Object, Object> map, String key, Class<T> class_) {
		List<T> list = new ArrayList<T>();
		// keyが持っているか確認する
		if (!map.containsKey(key)) {
			return list;
		}
		
		if (ListUtils.<T>checkCast(map.get(key), class_)) {
			list = (List<T>) map.get(key);
		}
		return list;
	}

	/**
	 * キャストできるかチェックをする
	 * @param o
	 * @param class_
	 * @return
	 */
	public static <T> Boolean checkCast(Object o, Class<T> class_) {
		if (o instanceof List<?>) {
			if (!((List<?>) o).isEmpty() && ((List<?>) o).get(0).getClass() == class_) {
				return true;
			}
		}
		// 型が目的のものと一致をしていない場合
		return false;
	}
}