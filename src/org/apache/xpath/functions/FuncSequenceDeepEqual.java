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
//import org.w3c.dom.traversal.NodeIterator;
import org.apache.xml.utils.DateTimeObj;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XSequence;
import org.apache.xpath.objects.XSequenceImpl;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XNodeSequenceSingleton;
import org.apache.xpath.objects.XBoolean;
import java.util.Comparator;
import org.apache.xml.dtm.XType;
import org.apache.xpath.parser.regexp.*;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.XPathException;

import java.text.Collator;
import java.net.URL;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the xs:matches() function.
 */
public class FuncSequenceDeepEqual extends Function3Args
{

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
  	XSequence seq1 = m_arg0.execute(xctxt).xseq();
  	XSequence seq2 = m_arg1.execute(xctxt).xseq();
  	
  	if (seq1.getLength() != seq2.getLength())
  	  return new XBoolean(false);	
  	
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

	XObject item1, item2;
	  
	while((item1 = seq1.next()) != null)  	
	{
	    boolean found = false;
	    int type = item1.getType();
	    item2 = seq2.next();
	    if(type == XType.NODE)
	    {
	      if(item1 instanceof XNodeSequenceSingleton)
	      {
	        XNodeSequenceSingleton xnss = (XNodeSequenceSingleton)item1;
	        
	        if (type == item2.getType())
	        {
	          if (!xnss.deepEquals((XNodeSequenceSingleton)item2)) 
	            return new XBoolean(false);
	        }
	      }
	    }
	    else
	    {
	      if (collator == null)
	      {
	        if (!item1.equals(item2))
              return new XBoolean(false);
	      }
	      else
	      {
	        if (collator.equals(item1.str(), item2.str()))
  	          return new XBoolean(false);
  	      }    	      
  	    }
	  }
	  return new XBoolean(true);
    
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
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("twoorthree", null));
  }
  
  
  /** Return the number of children the node has. */
  public int exprGetNumChildren()
  {
  	return (m_arg2 == null) ?  2 :  3;
  }
}
