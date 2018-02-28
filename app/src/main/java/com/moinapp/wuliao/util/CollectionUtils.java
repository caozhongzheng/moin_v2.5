package com.moinapp.wuliao.util;

import java.util.Collection;

public class CollectionUtils {
	public static boolean isNotEmpty(Collection<?> coll) {
		return (coll != null && !coll.isEmpty());
	}

	public static boolean isEmpty(Collection<?> coll) {
		return (coll == null || coll.isEmpty());
	}
}
