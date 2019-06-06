package iesi.gateway.filters;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class CustomFilter extends ZuulFilter {
	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 10;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		final RequestContext ctx = RequestContext.getCurrentContext();
		final HttpServletResponse response = ctx.getResponse();
		if (HttpStatus.BAD_REQUEST.value() == ctx.getResponse().getStatus()) {
			try {
				response.sendError(400, "Mismatch");
			} catch (final IOException e) {
				e.printStackTrace();
			}
			;
		}
		return null;
	}
}