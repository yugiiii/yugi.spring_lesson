package com.queue.common.converter;

public class NumberConverter {

	// 10進数から62進数に変換
	public static String toBase62String(final long value) {
		long val = value;

		StringBuilder sb = new StringBuilder(7);
		while (val > 0) {
			int mod = (int) (val % 62);
			if (mod < 10) {
				// 数字
				sb.append(mod);
			} else if (mod < 36) {
				// 英小文字 a = 97
				// mod = mod - 10 + 97
				mod += 87;
				sb.append((char) mod);
			} else {
				// 英大文字 A = 65
				// mod = mod - 36 + 65
				mod += 29;
				sb.append((char) mod);
			}
			val = val / 62;
		}

		return new String(sb.reverse());
	}

	// 62進数から10進数に変換
	public static long fromBase62String(final String value) {
		long longValue = 0;

		for (int i = 0; i < value.length(); i++) {
			int order = value.length() - i - 1;
			char c = value.charAt(i);

			int digit = 0;
			if (c >= '0' && c <= '9') {
				digit = (c - '0');
			} else if (c >= 'a' && c <= 'z') {
				digit = (c - 'a' + 10);
			} else if (c >= 'A' && c <= 'Z') {
				digit = (c - 'A' + 36);
			}

			longValue += digit * Math.pow(62, order);
		}

		return longValue;
	}
}