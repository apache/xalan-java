/*
 * Created on 28 sept. 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.xpath.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.xml.NamespaceContext;
import org.apache.xpath.expression.StaticContext;

/**
 * Implements default {@link org.apache.xpath.expression.StaticContext}.
 * 
 * <p>The attributes of the default static context are defined as following:
 * <ul>
 * <li>XPath 1.0 compatibility mode: false</li>
 * <li>In-scope namespaces:
 * <ul>
 * <li>xs is bound to http://www.w3.org/2001/XMLSchema</li>
 * <li>xsi is bound to http://www.w3.org/2001/XMLSchema-instance</li>
 * <li>fn is bound to http://www.w3.org/2003/05/xpath-functions</li>
 * </ul>
 * </li>
 * <li>Default element/type namespace: set to null</li>
 * <li>Default function namespace: set to http://www.w3.org/2003/05/xpath-functions</li>
 * <li>In-scope schema definitions: empty set</li>
 * <li>In-scope variables: empty set</li>
 * <li>In-scope functions: set of XPath functions</li>
 * <li>In-scope collations: empty set</li>
 * <li>Default collation: set to null</li>
 * <li>Base URI: set to null</li>
 * <li>Statically-known documents: empty set</li>
 * <li>Statically-known collections: empty set</li>
 * </ul>
 * </p> 
 * @author <a href="mailto:villard@us.ibm.com">Lionel Villard</a>
 * @version $Id$
 */
public class DefaultStaticContext implements StaticContext, NamespaceContext
{
	static final public String XML_SCHEMA_URI = "http://www.w3.org/2001/XMLSchema";
	static final public String XML_SCHEMA_INSTANCE_URI =
		"http://www.w3.org/2001/XMLSchema-instance";
	static final public String XPATH_FUNCTIONS_URI =
		"http://www.w3.org/2003/05/xpath-functions";

	static private final Map NS2PREFIX; 

	// Static
	
	static
	{
		NS2PREFIX = new HashMap(3);
		NS2PREFIX.put(XML_SCHEMA_INSTANCE_URI, "xsi");
		NS2PREFIX.put(XML_SCHEMA_URI, "xs");
		NS2PREFIX.put(XPATH_FUNCTIONS_URI, "fn");
	}

	// Implements StaticContext

	public boolean getCompatibilityMode()
	{
		return false;
	}

	public NamespaceContext getNamespaces()
	{
		return this;
	}

	public String getDefaultNamepaceForElement()
	{		
		return XML_SCHEMA_INSTANCE_URI;
	}

	public String getDefaultNamepaceforFunction()
	{
		return XPATH_FUNCTIONS_URI;
	}

	public void getSchemaDefs()
	{
	}

	public Set getVariables()
	{	
		return null;
	}

	public void getFunctions()
	{
	}

	public void getCollations()
	{
	}

	public void getDefaultCollation()
	{
	}

	public String getBaseURI()
	{	
		return null;
	}

	// Implements NamespaceContext

	public String getNamespaceURI(String prefix)
	{
		if ("xs".equals(prefix))
		{
			return XML_SCHEMA_URI;
		}
		else if ("xsi".equals(prefix))
		{
			return XML_SCHEMA_INSTANCE_URI;
		}
		else if ("fn".equals(prefix))
		{
			return XPATH_FUNCTIONS_URI;
		}
		else
		{
			return null;
		}
	}

	public String getPrefix(String namespaceURI)
	{
		return (String) NS2PREFIX.get(namespaceURI);		
	}
	
	public Iterator getPrefixes(String namespaceURI)
	{
		// TODO:	
		return null;
	}
	
	// Inner classes
	
	
	

}
