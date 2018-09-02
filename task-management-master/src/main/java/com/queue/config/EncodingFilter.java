package com.queue.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * OncePerRequestFilterはリクエストごとに1回の実行を保証するための基本クラスをディスパッチします。
 * EncodingFilterはRequestごとに一度だけ行う処理をまとめています。
 * 
 * @author nagataryou
 *
 */
public class EncodingFilter extends OncePerRequestFilter {
	
	/*
	 * このメソッドが呼び出される前に、このフィルタの全てのBeanプロパティが設定されます。
	 */
	@Override
	protected void initFilterBean() throws ServletException {
	}
	
	/*
	 * 1つの要求スレッドで一度だけ呼び出されることが保証されている
	 * (non-Javadoc)
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain
			) throws IOException, ServletException {
				request.setCharacterEncoding("UTF-8");
				chain.doFilter(request, response);
			}
	
	/*
	 * フィルタのシャットダウンを行います。
	 * (non-Javadoc)
	 * @see org.springframework.web.filter.GenericFilterBean#destroy()
	 */
	@Override
	public void destroy() {
		
	}
}