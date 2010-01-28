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

package com.eviware.soapui.impl.wadl.support;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;

import com.eviware.soapui.impl.rest.RestService;
import com.eviware.soapui.impl.support.definition.DefinitionLoader;
import com.eviware.soapui.impl.support.definition.support.XmlSchemaBasedInterfaceDefinition;
import net.java.dev.wadl.x2009.x02.ApplicationDocument;

public class WadlInterfaceDefinition extends XmlSchemaBasedInterfaceDefinition<RestService>
{
	private ApplicationDocument applicationDocument;
	private Logger log = Logger.getLogger( WadlInterfaceDefinition.class );

	public WadlInterfaceDefinition( RestService iface )
	{
		super( iface );
	}

	public WadlInterfaceDefinition load( DefinitionLoader loader ) throws Exception
	{
		XmlObject obj = loader.loadXmlObject( loader.getBaseURI(), null );
		applicationDocument = ( ApplicationDocument )obj.changeType( ApplicationDocument.type );

		if( !loader.isAborted() )
		{
			super.loadSchemaTypes( loader );
		}
		else
			throw new Exception( "Loading of WADL from [" + loader.getBaseURI() + "] was aborted" );

		return this;
	}

	public String getTargetNamespace()
	{
		return null;
	}

	public ApplicationDocument.Application getApplication()
	{
		return applicationDocument.getApplication();
	}
}