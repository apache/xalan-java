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
package org.apache.xpath.functions;

//import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathException;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the Concat() function.
 */
public class FuncCompare extends FunctionMultiArgs
{
  /**
   * Execute the xf:compare() function.  The function must return
   * a valid object.
   * If a third argument is provided, it should be taken as an anyURI
   * naming a collation under which the comparisons are to be performed.
   * If the implementation does not support the collation, an
   * "Unsupported collation" error should be raised. The default is to
   * use whatever was specified in the Static Context; if that
   * wasn't set, standard Unicode codepoint collation
   * (http://www.w3.org/2002/08/query-operators/collation/codepoint) 
   * should be applied.
   * 
   * @param xctxt The current execution context.
   * @return Fortran-style string comparison of the first 
   * two arguments (-1 if arg0<arg1, 0 if equal, +1 if arg0>arg1).
   * If either is EMPTY, the result is EMPTY.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {	
	String val1=m_arg0.execute(xctxt).str();
    String val2=m_arg1.execute(xctxt).str();

	java.text.Collator collator=null;
	if(null != m_arg2)
	{
		String collation=m_arg2.execute(xctxt).str();
		
		// We can handle this special case...
		if(collation!=null && 
			collation.equals(xctxt.getDefaultCollation()))
			collator=xctxt.getDefaultCollator();

		// But I currently have no clue what to do with others.
		// %REVIEW%
		else
		{
			// This should probably be error rather than 
			// exception -- %REVIEW%
			throw new XPathException(XSLMessages.createXPATHMessage(
      			XPATHErrorResources.ER_CANNOT_FIND_COLLATOR,
		      	new Object[]{collation}
	    	  	) );
		}
	}
	else // Unspecified, take the static default
		collator=xctxt.getDefaultCollator();

	int result;
	if(collator==null)
	{
		// If never set, use default Java ion?
		// %REVIEW%
		result=val1.compareTo(val2);
	}
	else
	{	
		result=collator.compare(val1,val2);			
	}
	
	// Java methods may try to return more info than we need
	if(result!=0)
		result=(result>0) ? 1 : -1;
	
	return new org.apache.xpath.objects.XInteger(result);
  }

  /**
   * Check that the number of arguments passed to this function is correct.
   *
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
    if (argNum < 2 || argNum > 3)
      reportWrongNumberArgs();
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("gtone", null));
  }
}
