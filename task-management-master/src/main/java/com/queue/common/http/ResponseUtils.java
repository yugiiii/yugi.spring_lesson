package com.queue.common.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class ResponseUtils {
	private ResponseUtils() {
		
	}
	
	/**
     * レスポンスを返します。
     * 
     * @return レスポンス
     */
    public static HttpServletResponse getResponse() {
    		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse(); // アプリケーションのどこにいても現在のレスポンスオブジェクトを取得できる
    		return response;
    }


    /**
     * レスポンスにテキストを書き込みます。
     * 
     * @param text
     *            テキスト
     */
    public static void write(String text) {
        write(text, null, null);
    }

    /**
     * レスポンスにテキストを書き込みます。
     * 
     * @param text
     *            テキスト
     * @param contentType
     *            コンテントタイプ。 デフォルトはtext/plain。
     */
    public static void write(String text, String contentType) {
        write(text, contentType, null);
    }

    /**
     * レスポンスにテキストを書き込みます。
     * 
     * @param text
     *            テキスト
     * @param contentType
     *            コンテントタイプ。 デフォルトはtext/plain。
     * @param encoding
     *            エンコーディング。 指定しなかった場合は、リクエストのcharsetEncodingが設定される。
     *            リクエストのcharsetEncodingも指定がない場合は、UTF-8。
     */
    public static void write(String text, String contentType, String encoding) {
        if (contentType == null) {
            contentType = "text/plain";
        }
        if (encoding == null) {
            encoding = RequestUtils.getRequest().getCharacterEncoding();
            if (encoding == null) {
                encoding = "UTF-8";
            }
        }
        HttpServletResponse response = getResponse();
        response.setContentType(contentType + "; charset=" + encoding);
        try {
            PrintWriter out = null;
            try {
                out = new PrintWriter(new OutputStreamWriter(response
                        .getOutputStream(), encoding));
                out.print(text);
            } finally {
                if (out != null) {
                    out.close(); // HttpServletStreamをcloseする => ここで接続を閉じる
                }
            }
        } catch (UnsupportedEncodingException e) {
        		e.printStackTrace();
        } catch (IOException e) {
        		e.printStackTrace();
        }
    }
}
