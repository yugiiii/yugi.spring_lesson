package com.queue.app.dto.base;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

public class BaseDto implements Serializable {

	private static final long serialVersionUID = 3775954162244791234L;
	private static final Gson gson = new Gson();
	
	public BaseDto fromJson(String json) {
		return gson.fromJson(snakeToCamel(json), this.getClass());
	}
	
	
	public static final String snakeToCamel(final String snake) {
        if (StringUtils.isEmpty(snake)) {
            return snake;
        }
        final StringBuilder sb = new StringBuilder(snake.length() + snake.length());
        for (int i = 0; i < snake.length(); i++) {
            final char c = snake.charAt(i);
            if (c == '_') {
                sb.append((i + 1) < snake.length() ? Character.toUpperCase(snake.charAt(++i)) : "");
            } else {
            	// valueの方も小文字になってしまうので、一旦はコメントアウト
                // sb.append(sb.length() == 0 ? Character.toUpperCase(c) : Character.toLowerCase(c));
            	sb.append(c);
            }
        }
        return sb.toString();
    }

}
