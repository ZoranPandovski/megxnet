package net.megx.security.filter.http;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.megx.security.auth.web.WebUtils;

public class DummyFilter implements Filter{

	@Override
	public void destroy() {
		System.out.println("Filter destroy");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		System.out.println("Filter start");
	}

}
