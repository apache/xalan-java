/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package servlet;

import java.net.MalformedURLException;
import javax.servlet.*;
import javax.servlet.http.*;

/*****************************************************************************************************
 * 
 * ApplyXSLTProperties contains operational parameters for ApplyXSLT based 
 * on program defaults and configuration.  
 * <p>This class is also used to return values for request-time parameters.</p>
 *
 * @author Spencer Shepard (sshepard@us.ibm.com)
 * @author R. Adam King (rak@us.ibm.com)
 * @author Tom Rowe (trowe@us.ibm.com)
 *
 *****************************************************************************************************/

public class ApplyXSLTProperties {

    /**
      * Program default for parameter "URL"
      */
    private final String DEFAULT_URL;

    /**
      * Program default for parameter "xslURL"
      */
    private final String DEFAULT_xslURL;
    
    /**
      * Program default for parameter "debug"
      */
    private final boolean DEFAULT_debug;

    /**
      * Program default for parameter "noConflictWarnings"
      */
    private final boolean DEFAULT_noCW;
    
    /**
      * Constructor to use program defaults.
      */
    public ApplyXSLTProperties() 
    {
	DEFAULT_URL = null;
	DEFAULT_xslURL = null;
	DEFAULT_debug = false;
	DEFAULT_noCW = false;
    }

    /**
      * Constructor to use to override program defaults.
      * @param config Servlet configuration
      */
    public ApplyXSLTProperties(ServletConfig config)
    {
	String xm = config.getInitParameter("URL"),
	       xu = config.getInitParameter("xslURL"),
	       db = config.getInitParameter("debug"),
	       cw = config.getInitParameter("noConflictWarnings");
	       
	if (xm != null) DEFAULT_URL = xm;
	else DEFAULT_URL = null;
	if (xu != null) DEFAULT_xslURL = xu;
	else DEFAULT_xslURL = null;
	if (db != null) DEFAULT_debug = new Boolean(db).booleanValue();
	else DEFAULT_debug = false;
	if (cw != null) DEFAULT_noCW = new Boolean(cw).booleanValue();
	else DEFAULT_noCW = false;
    }
   
    /**
      * Given a parameter name, returns the HTTP request's String value; 
      * if not present in request, returns default String value.
      * @param request Request to check for default override
      * @param param Name of the parameter
      * @return String value of named parameter
      */
    public String getRequestParmString(HttpServletRequest request, String param)
    {
	if (request != null) { 
	    String[] paramVals = request.getParameterValues(param); 
	    if (paramVals != null) 
		return paramVals[0];
	}
	return null;
    }

    /**
      * Returns the current setting for "URL".
      * @param request Request to check for parameter value
      * @return String value for "URL"
      * @exception MalformedURLException Will not be thrown
      */
    public String getXMLurl(HttpServletRequest request)
    throws MalformedURLException
    {
	String temp = getRequestParmString(request, "URL");
	if (temp != null)
	    return temp;
	return DEFAULT_URL;
    }     
    
    /**
      * Returns the current setting for "xslURL".
      * @param request Request to check for parameter value
      * @return String value for "xslURL"
      * @exception MalformedURLException Will not be thrown
      */
    public String getXSLurl(HttpServletRequest request)
    throws MalformedURLException
    {  
	String temp = getRequestParmString(request, "xslURL");
	if (temp != null)
	    return temp;
	return DEFAULT_xslURL;
    }
    
    /**
      * Returns the current setting for "debug".
      * @param request Request to check for parameter value
      * @return Boolean value for "debug"
      */
    public boolean isDebug(HttpServletRequest request)
    {
	String temp = getRequestParmString(request, "debug");
	if (temp != null)
	    return new Boolean(temp).booleanValue();
	return DEFAULT_debug;
    }

    /**
      * Returns the current setting for "noConflictWarnings".
      * @param request Request to check for parameter value
      * @return Boolean value for "noConflictWarnings"
      */
    boolean isNoCW(HttpServletRequest request)
    {
	String temp = getRequestParmString(request, "noConflictWarnings");
	if (temp != null)
	    return new Boolean(temp).booleanValue();
	return DEFAULT_noCW;
    }    
}