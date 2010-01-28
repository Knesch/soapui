/*
 *  soapUI, copyright (C) 2004-2010 eviware.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.impl.wsdl.submit;

import java.util.HashMap;
import java.util.Map;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.wsdl.submit.filters.EndpointRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.EndpointStrategyRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.HttpAuthenticationRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.HttpCompressionRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.HttpProxyRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.HttpRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.HttpSettingsRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.PostPackagingRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.PropertyExpansionRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.RemoveEmptyContentRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.RestRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.SoapHeadersRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.StripWhitespacesRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.WsaRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.WsdlPackagingRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.HttpPackagingResponseFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.WsrmRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.WssAuthenticationRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.filters.WssRequestFilter;
import com.eviware.soapui.impl.wsdl.submit.transports.http.HttpClientRequestTransport;
import com.eviware.soapui.impl.wsdl.submit.transports.jms.HermesJmsRequestTransport;
import com.eviware.soapui.model.iface.SubmitContext;

/**
 * Registry of available transports, currently hard-coded but should be
 * configurable in the future.
 * 
 * @author Ole.Matzura
 */

public class RequestTransportRegistry
{
	public static final String HTTP = "http";
	public static final String HTTPS = "https";
	public static final String JMS = "jms";

	private static Map<String, RequestTransport> transports = new HashMap<String, RequestTransport>();

	static
	{
		HttpClientRequestTransport httpTransport = new HttpClientRequestTransport();
 		HermesJmsRequestTransport jmsTransport = new HermesJmsRequestTransport();
	
		
		httpTransport.addRequestFilter( new EndpointRequestFilter() );
		httpTransport.addRequestFilter( new HttpSettingsRequestFilter() );
		httpTransport.addRequestFilter( new HttpRequestFilter() );
		httpTransport.addRequestFilter( new RestRequestFilter() );
		httpTransport.addRequestFilter( new SoapHeadersRequestFilter() );
		httpTransport.addRequestFilter( new HttpProxyRequestFilter() );
		httpTransport.addRequestFilter( new HttpAuthenticationRequestFilter() );
		httpTransport.addRequestFilter( new WssAuthenticationRequestFilter() );
		httpTransport.addRequestFilter( new PropertyExpansionRequestFilter() );
		httpTransport.addRequestFilter( new RemoveEmptyContentRequestFilter() );
		httpTransport.addRequestFilter( new StripWhitespacesRequestFilter() );
		httpTransport.addRequestFilter( new EndpointStrategyRequestFilter() );
		httpTransport.addRequestFilter( new WsaRequestFilter() );
		httpTransport.addRequestFilter( new WsrmRequestFilter() );
		httpTransport.addRequestFilter( new WssRequestFilter() );

		for( RequestFilter filter : SoapUI.getListenerRegistry().getListeners( RequestFilter.class ) )
		{
			httpTransport.addRequestFilter( filter );
		}

		httpTransport.addRequestFilter( new WsdlPackagingRequestFilter() );
		httpTransport.addRequestFilter( new HttpCompressionRequestFilter() );
		httpTransport.addRequestFilter( new HttpPackagingResponseFilter() );
		httpTransport.addRequestFilter( new PostPackagingRequestFilter() );

		transports.put( HTTP, httpTransport );
		transports.put( HTTPS, httpTransport );
		transports.put(JMS, jmsTransport);
	}

	public static RequestTransport getTransport( String endpoint, SubmitContext submitContext )
			throws MissingTransportException, CannotResolveJmsTypeException
	{
		int ix = endpoint.indexOf( "://" );
		if( ix == -1 )
			throw new MissingTransportException( "Missing protocol in endpoint [" + endpoint + "]" );

		String protocol = endpoint.substring( 0, ix ).toLowerCase();
		
		RequestTransport transport = transports.get( protocol );

		if( transport == null )
			throw new MissingTransportException( "Missing transport for protocol [" + protocol + "]" );

		return transport;
	}


	public static void addTransport( String key, RequestTransport rt )
	{
		transports.put( key, rt );
	}

	public static class MissingTransportException extends Exception
	{
		public MissingTransportException( String msg )
		{
			super(msg);
		}
	}
	
	public static class CannotResolveJmsTypeException extends Exception
	{
		public CannotResolveJmsTypeException( String msg )
		{
			super(msg);
		}
	}
}
