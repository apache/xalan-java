/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
package org.apache.xpath.operations;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;


/**
 * This is a "smart" variable reference that is used in situations where 
 * an absolute path is optimized into a variable reference, but may 
 * be used in some situations where the document context may have changed.
 * For instance, in select="document(doc/@href)//name[//salary &gt; 7250]", the 
 * root in the predicate will be different for each node in the set.  While 
 * this is easy to detect statically in this case, in other cases static 
 * detection would be very hard or impossible.  So, this class does a dynamic check 
 * to make sure the document context of the referenced variable is the same as 
 * the current document context, and, if it is not, execute the referenced variable's 
 * expression with the current context instead.
 */
public class VariableSafeAbsRef extends Variable
{
	
  /**
   * Dereference the variable, and return the reference value.  Note that lazy 
   * evaluation will occur.  If a variable within scope is not found, a warning 
   * will be sent to the error listener, and an empty nodeset will be returned.
   *
   *
   * @param xctxt The runtime execution context.
   *
   * @return The evaluated variable, or an empty nodeset if not found.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt, boolean destructiveOK) 
  	throws javax.xml.transform.TransformerException
  {
  	XNodeSet xns = (XNodeSet)super.execute(xctxt, destructiveOK);
  	DTMManager dtmMgr = xctxt.getDTMManager();
  	int context = xctxt.getContextNode();
  	if(dtmMgr.getDTM(xns.getRoot()).getDocument() != 
  	   dtmMgr.getDTM(context).getDocument())
  	{
  		Expression expr = (Expression)xns.getContainedIter();
  		xns = (XNodeSet)expr.asIterator(xctxt, context);
  	}
  	return xns;
  }

}

