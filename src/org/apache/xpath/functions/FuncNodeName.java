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
package org.apache.xpath.functions;

import org.apache.xpath.res.XPATHErrorResources;

//import org.w3c.dom.Node;
//import org.w3c.dom.traversal.NodeIterator;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;

import java.util.Vector;

import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XSequence;
import org.apache.xpath.objects.XExpandedQName;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the xf:node-name function, returning a QName.
 * 
 * @author Joe Kesselman
 * @since Aug 27, 2002
 */
public class FuncNodeName extends FunctionDef1Arg
{
  /**
   * Execute the xf:node-name function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return An expanded QName containing the node's namespace URI and
   * localname, or 
   * XObject.EMPTY if the input wasn't a node that can have such
   * a name.
   * %BUG% We don't yet know what the behavior of an expanded QName
   * should be, so we don't yet have a "proper" XObject for it.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    int context = getArg0AsNode(xctxt);
    XExpandedQName qn=null;
    
    if(DTM.NULL != context)
    {
	    DTM dtm = xctxt.getDTM(context);
    	switch (dtm.getNodeType(context))
	    {
    		case DTM.ATTRIBUTE_NODE:
    		case DTM.ELEMENT_NODE:
    			qn=new XExpandedQName(dtm.getNamespaceURI(context),
    				dtm.getLocalName(context));
    			break;
    		default:
    			// leave it unknown? SHOULD NOT ARISE!
    			break;
	    }
    }

	// %BUG% %REVIEW% THIS IS WRONG. We probably want a real XQName
	// XObject to solve this properly. Problem is, "expanded QName"
	// is underspecified in the spec; not knowing its intended lexical
	// representation or behavior when manipulated keeps me from
	// implementing it. (For example: If inserted as text, is it
	// supposed to turn back into a Qualified Name? If so, using whose
	// definition of the prefix?)
	//
	// For now, this compiles and passes the primitive smoketest...
	// but it is *NOT* correct.

	if(qn==null)
		return XSequence.EMPTY;
	else
		return qn;
  }
}
