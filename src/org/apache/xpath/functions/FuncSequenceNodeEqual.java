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
import org.apache.xml.dtm.DTM;
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
public class FuncSequenceNodeEqual extends Function2Args
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
  	
  	if (seq1.getLength() == 0 || seq2.getLength() == 0)
  	return XSequence.EMPTY;
  	
  	if (seq1.getLength() != seq2.getLength())
  	  return new XBoolean(false);	
  	
  	

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
	        XNodeSequenceSingleton xnss1 = (XNodeSequenceSingleton)item1;
	        XNodeSequenceSingleton xnss2 = (XNodeSequenceSingleton)item2;
	        
	        if (type == item2.getType())
	        {
	          DTM dtm1 = xnss1.getDTM();
              DTM dtm2 = xnss2.getDTM();
            int node1 = xnss1.getNodeHandle();
            int node2 = xnss2.getNodeHandle();
            
            String uri1 = null;
	        if(!dtm1.getLocalName(node1).equals(dtm2.getLocalName(node2))
	           || !((uri1 = dtm1.getNamespaceURI(node1)) == null ?
	    dtm2.getNamespaceURI(node2) == null :
	    uri1.equals(dtm2.getNamespaceURI(node2))) 
	        //(!dtm1.getNodeName(node1).equals(dtm2.getNodeName(node2))
	          || (!xnss1.deepEquals(xnss2))) 
	            return new XBoolean(false);
	        }
	      }
	    }
	  }
	  return new XBoolean(true);
    
  }
}
