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
public class FuncDeepEqual extends Function3Args
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
    
    if((item1 = seq1.next()) != null &&(item2 = seq2.next()) != null )  	
    {
      int type = item1.getType();
      if(type == XType.NODE)
      {
        if(item1 instanceof XNodeSequenceSingleton)
        {
          XNodeSequenceSingleton xnss1 = (XNodeSequenceSingleton)item1;
          XNodeSequenceSingleton xnss2 = (XNodeSequenceSingleton)item2;
          //%Review% Do I really need 2 dtms??
          DTM dtm1 = xnss1.getDTM();
          DTM dtm2 = xnss2.getDTM();
          int node1 = xnss1.getNodeHandle();
         int node2 = xnss2.getNodeHandle();
          
          // Make sure we start at the first (and only node)
          xnss1.reset();
          xnss2.reset(); 
          
          if (xnss1.deepEquals(xnss2))
          {
            return new XBoolean(deepEqual(node1, node2, dtm1, dtm2, collator));
          }
          else
            return new XBoolean(false);
          
        }
      }
    }
    return new XBoolean(false);
    
  }
  
  static boolean deepEqual(int node1, int node2, DTM dtm1, DTM dtm2, java.text.Collator collator)
  {
    int type = dtm1.getNodeType(node1);
    String uri1 = null;
    if (type == dtm2.getNodeType(node2)
        && dtm1.getLocalName(node1).equals(dtm2.getLocalName(node2))
	    && ((uri1 = dtm1.getNamespaceURI(node1)) == null ?
	    dtm2.getNamespaceURI(node2) == null :
	    uri1.equals(dtm2.getNamespaceURI(node2))))  
        //dtm1.getNodeName(node1).equals(dtm2.getNodeName(node2)))
    {
      switch (type)
      {
      case DTM.COMMENT_NODE:
      case DTM.TEXT_NODE:
      case DTM.NAMESPACE_NODE:
     case DTM.PROCESSING_INSTRUCTION_NODE:
     case DTM.ATTRIBUTE_NODE:
      if (collator != null)
      {
        if (collator.equals(dtm1.getNodeValue(node1), dtm2.getNodeValue(node2)))
          return true;
        else 
          return false;
      }
      else
      {
        if (dtm1.getNodeValue(node1).equals(dtm2.getNodeValue(node2)))
          return true;
        else 
          return false;
      }
      }
      
        int attrNode1 = dtm1.getFirstAttribute(node1);
        int attrNode2 = dtm2.getFirstAttribute(node2);
        
        if ((attrNode1 == DTM.NULL || attrNode2 == DTM.NULL) && attrNode2 != attrNode1)
          return false;
           
        while (attrNode1 != DTM.NULL && attrNode2 != DTM.NULL)
        {
          attrNode2 = dtm2.getAttributeNode(node2, dtm1.getNamespaceURI(attrNode1), dtm1.getNodeName(attrNode1));	             
         if (attrNode2 == DTM.NULL)
          return false; 
         if (deepEqual(attrNode1, attrNode2, dtm1, dtm2, collator))
            //collator.equals(dtm1.getNodeValue(attrNode1), dtm2.getNodeValue(node2))) 
          {	                 
            attrNode1 = dtm1.getNextAttribute(attrNode1);
            //attrNode2 = dtm2.getNextAttribute(attrNode2);
          }
         else
           break;
        }
        if (attrNode1 != DTM.NULL)
          return false;
       else
       {
         attrNode2 = dtm2.getFirstAttribute(node2);
         while (attrNode2 != DTM.NULL)
         {
           if(DTM.NULL == dtm2.getAttributeNode(node1, dtm2.getNamespaceURI(attrNode2), dtm2.getNodeName(attrNode2)))
               return false;
           else
               attrNode2 = dtm2.getNextAttribute(attrNode2);
         }       
       }
            
        
        if (dtm1.hasChildNodes(node1) && dtm2.hasChildNodes(node2))
        {
          int child1 = dtm1.getFirstChild(node1);
          int child2 = dtm2.getFirstChild(node2);
          while (true)
          {          
           short type1 = dtm1.getNodeType(child1);
           short type2 = dtm2.getNodeType(child2); 
           if((type1 == DTM.COMMENT_NODE) || 
                  (type1 == DTM.PROCESSING_INSTRUCTION_NODE))
             {
               child1 =  getNonCommentOrPI(dtm1,dtm1.getNextSibling(child1));
             }
           else if((type2  == DTM.COMMENT_NODE) || 
                  (type2 == DTM.PROCESSING_INSTRUCTION_NODE))
             {
               child2 = getNonCommentOrPI(dtm2,dtm2.getNextSibling(child2));
             }    
           else
           {
            if (deepEqual(child1, child2, dtm1, dtm2, collator))
            {
              child1 = getNonCommentOrPI(dtm1,dtm1.getNextSibling(child1));
              child2 = getNonCommentOrPI(dtm2,dtm2.getNextSibling(child2));
            }
            else
              return false;
           }
           if (child1 == DTM.NULL && DTM.NULL == child2)
             return true;
            
           if (child1 == DTM.NULL || child2 == DTM.NULL)
             return false;
        }
      }
      else if (!dtm1.hasChildNodes(node1) && !dtm2.hasChildNodes(node2))
        return true;
      else
        return false;
    }
    else
      return false;
  }
  
  static private int getNonCommentOrPI(DTM dtm, int child)
  {
    if (DTM.NULL == child)
      return child;
    short type = dtm.getNodeType(child); 
    while((type == DTM.COMMENT_NODE) || 
                  (type == DTM.PROCESSING_INSTRUCTION_NODE))
     {
        child =  dtm.getNextSibling(child);
       if ( child != DTM.NULL)
          type = dtm.getNodeType(child);
       else 
         break;
     }
    return child;
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
