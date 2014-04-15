package org.dt;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import org.eclipse.jetty.servlets.GzipFilter;


@WebFilter(urlPatterns = "/*", initParams = { @WebInitParam(name = "mimeTypes", value = "text/html,application/json,application/javascript,text/javascript") })
public class CompressionFiter extends GzipFilter {

}