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
package org.apache.xpath.objects;

import java.io.PrintStream;
import java.net.URL;

import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.XType;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.VariableComposeState;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.parser.Node;
import org.apache.xpath.parser.Token;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * The responsibility of enclosing_type is to .
 * 
 * Created Jul 18, 2002
 * @author sboag
 */
public class XSequenceSingleton extends XObject implements XSequence
{
	XObject m_xobject;
	
  int m_pos = 0;

  /**
   * Constructor for XSequenceSingleton.
   * @param obj
   */
  public XSequenceSingleton(XObject obj)
  {
    m_xobject=obj;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#getTypes()
   */
  public int getTypes()
  {
    return m_xobject.getType();
  }

  /**
   * @see org.apache.xml.dtm.XSequence#next()
   */
  public XObject next()
  {
    if(m_pos == 0)
    {
      m_pos++;
      return this;
    }
    else
      return null;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#previous()
   */
  public XObject previous()
  {
    if(m_pos == 1)
    {
      m_pos--;
      return this;
    }
    else
      return null;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#getCurrent()
   */
  public XObject getCurrent()
  {
    if(m_pos == 0)
    {
      return this;
    }
    else
      return null;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#isFresh()
   */
  public boolean isFresh()
  {
    return (m_pos == 0);
  }

  /**
   * At this level, assume that the contained type is always an 
   * atomic type.
   * @see org.apache.xml.dtm.XSequence#getTypeNS()
   */
  public String getTypeNS()
  {
    return XType.XMLSCHEMA_DATATYPE_NAMESPACE;
  }

  /**
   * At this level, assume that the contained type is always an 
   * atomic type.
   * @see org.apache.xml.dtm.XSequence#getTypeLocalName()
   */
  public String getTypeLocalName()
  {
    if(m_pos != 0)
      return null;
    // For now, I'm going to do this ssslllloooowwww.
    return XType.getLocalNameFromType(m_xobject.getType());
  }

  /**
   * @see org.apache.xml.dtm.XSequence#isSchemaType(String, String)
   */
  public boolean isSchemaType(String namespace, String localname)
  {
    String uri = getTypeNS();
    String thisname = getTypeLocalName();
    if(null == namespace)
      return false;
    return uri.equals(uri) && thisname.equals(localname);
  }

  /**
   * @see org.apache.xml.dtm.XSequence#setShouldCache(boolean)
   */
  public void setShouldCacheNodes(boolean b)
  {
  }

  /**
   * @see org.apache.xml.dtm.XSequence#getIsRandomAccess()
   */
  public boolean getIsRandomAccess()
  {
    return true; // as far as it goes.
  }

  /**
   * @see org.apache.xml.dtm.XSequence#isMutable()
   */
  public boolean isMutable()
  {
    return false;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#getCurrentPos()
   */
  public int getCurrentPos()
  {
    return m_pos;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#setCurrentPos(int)
   */
  public void setCurrentPos(int i)
  {
    m_pos = i;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#getLength()
   */
  public int getLength()
  {
    return (null == m_xobject) ? 0 : 1;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#isSingletonOrEmpty()
   */
  public boolean isSingletonOrEmpty()
  {
    return true;
  }

  /**
   * @see org.apache.xpath.objects.XObject#getType()
   */
  public int getType()
  {
    if(m_pos != 0)
      return XType.SEQ;
    return m_xobject.getType();
  }

  /**
   * @see org.apache.xml.dtm.XSequence#isPureNodeSequence()
   */
  public boolean isPureNodeSequence()
  {
    return false;
  }

  /**
   * @see java.lang.Object#clone()
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  /**
   * @see org.apache.xpath.objects.XObject#allowDetachToRelease(boolean)
   */
  public void allowDetachToRelease(boolean allowRelease)
  {
    m_xobject.allowDetachToRelease(allowRelease);
  }

  /**
   * @see org.apache.xpath.objects.XObject#appendToFsb(FastStringBuffer)
   */
  public void appendToFsb(FastStringBuffer fsb)
  {
    m_xobject.appendToFsb(fsb);
  }

  /**
   * @see org.apache.xpath.objects.XObject#bool()
   */
  public boolean bool() throws TransformerException
  {
    return m_xobject.bool();
  }

  /**
   * @see org.apache.xpath.objects.XObject#boolWithSideEffects()
   */
  public boolean boolWithSideEffects() throws TransformerException
  {
    return m_xobject.boolWithSideEffects();
  }

  /**
   * @see org.apache.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
    m_xobject.callVisitors(owner, visitor);
  }

  /**
   * @see org.apache.xpath.objects.XObject#castToType(int, XPathContext)
   */
  public Object castToType(int t, XPathContext support)
    throws TransformerException
  {
    return m_xobject.castToType(t, support);
  }

  /**
   * @see org.apache.xpath.Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
    return m_xobject.deepEquals(expr);
  }

  /**
   * @see org.apache.xpath.objects.XObject#destruct()
   */
  public void destruct()
  {
    m_xobject.destruct();
  }

  /**
   * @see org.apache.xpath.objects.XObject#detach()
   */
  public void detach()
  {
    m_xobject.detach();
  }

  /**
   * @see org.apache.xpath.objects.XObject#dispatchCharactersEvents(ContentHandler)
   */
  public void dispatchCharactersEvents(ContentHandler ch) throws SAXException
  {
    m_xobject.dispatchCharactersEvents(ch);
  }

  /**
   * @see org.apache.xpath.objects.XObject#equals(XObject)
   */
  public boolean equals(XObject obj2)
  {
    return m_xobject.equals(obj2);
  }

  /**
   * @see org.apache.xpath.objects.XObject#error(int, Object[])
   */
  protected void error(int msg, Object[] args) throws TransformerException
  {
    m_xobject.error(msg, args);
  }

  /**
   * @see org.apache.xpath.objects.XObject#error(int)
   */
  protected void error(int msg) throws TransformerException
  {
    m_xobject.error(msg);
  }

  /**
   * @see org.apache.xpath.Expression#execute(XPathContext)
   */
  public XObject execute(XPathContext xctxt) throws TransformerException
  {
    return m_xobject.execute(xctxt);
  }

  /**
   * @see org.apache.xpath.Expression#fixupVariables(VariableComposeState)
   */
  public void fixupVariables(VariableComposeState vcs)
  {
    m_xobject.fixupVariables(vcs);
  }

  /**
   * @see org.apache.xpath.objects.XObject#getFresh()
   */
  public XObject getFresh()
  {
    return m_xobject.getFresh();
  }

  /**
   * @see org.apache.xpath.objects.XObject#getTypeString()
   */
  public String getTypeString()
  {
    return m_xobject.getTypeString();
  }

  /**
   * @see org.apache.xpath.objects.XObject#greaterThan(XObject)
   */
  public boolean greaterThan(XObject obj2) throws TransformerException
  {
    return m_xobject.greaterThan(obj2);
  }

  /**
   * @see org.apache.xpath.objects.XObject#greaterThanOrEqual(XObject)
   */
  public boolean greaterThanOrEqual(XObject obj2) throws TransformerException
  {
    return m_xobject.greaterThanOrEqual(obj2);
  }

  /**
   * @see org.apache.xpath.parser.SimpleNode#isPathExprReduced()
   */
  public boolean isPathExprReduced()
  {
    return m_xobject.isPathExprReduced();
  }

  /**
   * @see org.apache.xpath.objects.XObject#iter()
   */
  public DTMIterator iter() throws TransformerException
  {
    return m_xobject.iter();
  }

  /**
   * @see org.apache.xpath.objects.XObject#lessThan(XObject)
   */
  public boolean lessThan(XObject obj2) throws TransformerException
  {
    return m_xobject.lessThan(obj2);
  }

  /**
   * @see org.apache.xpath.objects.XObject#lessThanOrEqual(XObject)
   */
  public boolean lessThanOrEqual(XObject obj2) throws TransformerException
  {
    return m_xobject.lessThanOrEqual(obj2);
  }

  /**
   * @see org.apache.xpath.objects.XObject#mutableNodeset()
   */
  public NodeSetDTM mutableNodeset() throws TransformerException
  {
    return m_xobject.mutableNodeset();
  }

  /**
   * @see org.apache.xpath.objects.XObject#nodelist()
   */
  public NodeList nodelist() throws TransformerException
  {
    return m_xobject.nodelist();
  }

  /**
   * @see org.apache.xpath.objects.XObject#nodeset()
   */
  public NodeIterator nodeset() throws TransformerException
  {
    return m_xobject.nodeset();
  }

  /**
   * @see org.apache.xpath.objects.XObject#notEquals(XObject)
   */
  public boolean notEquals(XObject obj2) throws TransformerException
  {
    return m_xobject.notEquals(obj2);
  }

  /**
   * @see org.apache.xpath.objects.XObject#num()
   */
  public double num() throws TransformerException
  {
    return m_xobject.num();
  }

  /**
   * @see org.apache.xpath.objects.XObject#numWithSideEffects()
   */
  public double numWithSideEffects() throws TransformerException
  {
    return m_xobject.numWithSideEffects();
  }

  /**
   * @see org.apache.xpath.objects.XObject#object()
   */
  public Object object()
  {
    return m_xobject.object();
  }

  /**
   * @see org.apache.xpath.objects.XObject#reset()
   */
  public void reset()
  {
    m_xobject.reset();
  }

  /**
   * @see org.apache.xpath.objects.XObject#rtf()
   */
  public int rtf()
  {
    return m_xobject.rtf();
  }

  /**
   * @see org.apache.xpath.objects.XObject#rtf(XPathContext)
   */
  public int rtf(XPathContext support)
  {
    return m_xobject.rtf(support);
  }

  /**
   * @see org.apache.xpath.objects.XObject#rtree()
   */
  public DocumentFragment rtree()
  {
    return m_xobject.rtree();
  }

  /**
   * @see org.apache.xpath.objects.XObject#rtree(XPathContext)
   */
  public DocumentFragment rtree(XPathContext support)
  {
    return m_xobject.rtree(support);
  }

  /**
   * @see org.apache.xpath.objects.XObject#str()
   */
  public String str()
  {
    return m_xobject.str();
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return m_xobject.toString();
  }

  /**
   * @see org.apache.xpath.objects.XObject#xseq()
   */
  public XSequence xseq()
  {
    return m_xobject.xseq();
  }

  /**
   * @see org.apache.xpath.objects.XObject#xstr()
   */
  public XMLString xstr()
  {
    return m_xobject.xstr();
  }

  /**
   * @see org.apache.xpath.Expression#asIterator(XPathContext, int)
   */
  public DTMIterator asIterator(XPathContext xctxt, int contextNode)
    throws TransformerException
  {
    return m_xobject.asIterator(xctxt, contextNode);
  }

  /**
   * @see org.apache.xpath.Expression#asIteratorRaw(XPathContext, int)
   */
  public DTMIterator asIteratorRaw(XPathContext xctxt, int contextNode)
    throws TransformerException
  {
    return m_xobject.asIteratorRaw(xctxt, contextNode);
  }

  /**
   * @see org.apache.xpath.Expression#asNode(XPathContext)
   */
  public int asNode(XPathContext xctxt) throws TransformerException
  {
    return m_xobject.asNode(xctxt);
  }

  /**
   * @see org.apache.xpath.Expression#assertion(boolean, String)
   */
  public void assertion(boolean b, String msg)
  {
    m_xobject.assertion(b, msg);
  }

  /**
   * @see org.apache.xpath.Expression#bool(XPathContext)
   */
  public boolean bool(XPathContext xctxt) throws TransformerException
  {
    return m_xobject.bool(xctxt);
  }

  /**
   * @see org.apache.xpath.Expression#canTraverseOutsideSubtree()
   */
  public boolean canTraverseOutsideSubtree()
  {
    return m_xobject.canTraverseOutsideSubtree();
  }

  /**
   * @see org.apache.xpath.Expression#cloneDeep()
   */
  public Object cloneDeep() throws CloneNotSupportedException
  {
    return m_xobject.cloneDeep();
  }

  /**
   * @see org.apache.xpath.Expression#error(XPathContext, int, Object[])
   */
  public void error(XPathContext xctxt, int msg, Object[] args)
    throws TransformerException
  {
    m_xobject.error(xctxt, msg, args);
  }

  /**
   * @see org.apache.xpath.Expression#execute(XPathContext, boolean)
   */
  public XObject execute(XPathContext xctxt, boolean destructiveOK)
    throws TransformerException
  {
    return m_xobject.execute(xctxt, destructiveOK);
  }

  /**
   * @see org.apache.xpath.Expression#execute(XPathContext, int, DTM, int)
   */
  public XObject execute(
    XPathContext xctxt,
    int currentNode,
    DTM dtm,
    int expType)
    throws TransformerException
  {
    return m_xobject.execute(xctxt, currentNode, dtm, expType);
  }

  /**
   * @see org.apache.xpath.Expression#execute(XPathContext, int)
   */
  public XObject execute(XPathContext xctxt, int currentNode)
    throws TransformerException
  {
    return m_xobject.execute(xctxt, currentNode);
  }

  /**
   * @see org.apache.xpath.Expression#executeCharsToContentHandler(XPathContext, ContentHandler)
   */
  public void executeCharsToContentHandler(
    XPathContext xctxt,
    ContentHandler handler)
    throws TransformerException, SAXException
  {
    m_xobject.executeCharsToContentHandler(xctxt, handler);
  }

  /**
   * @see org.apache.xpath.ExpressionNode#exprAddChild(ExpressionNode, int)
   */
  public void exprAddChild(ExpressionNode n, int i)
  {
    m_xobject.exprAddChild(n, i);
  }

  /**
   * @see org.apache.xpath.ExpressionNode#exprGetChild(int)
   */
  public ExpressionNode exprGetChild(int i)
  {
    return m_xobject.exprGetChild(i);
  }

  /**
   * @see org.apache.xpath.ExpressionNode#exprGetNumChildren()
   */
  public int exprGetNumChildren()
  {
    return m_xobject.exprGetNumChildren();
  }

  /**
   * @see org.apache.xpath.ExpressionNode#exprGetParent()
   */
  public ExpressionNode exprGetParent()
  {
    return m_xobject.exprGetParent();
  }

  /**
   * @see org.apache.xpath.ExpressionNode#exprSetParent(ExpressionNode)
   */
  public void exprSetParent(ExpressionNode n)
  {
    m_xobject.exprSetParent(n);
  }

  /**
   * @see javax.xml.transform.SourceLocator#getColumnNumber()
   */
  public int getColumnNumber()
  {
    return m_xobject.getColumnNumber();
  }

  /**
   * @see org.apache.xpath.Expression#getExpressionOwner()
   */
  public ExpressionNode getExpressionOwner()
  {
    return m_xobject.getExpressionOwner();
  }

  /**
   * @see javax.xml.transform.SourceLocator#getLineNumber()
   */
  public int getLineNumber()
  {
    return m_xobject.getLineNumber();
  }

  /**
   * @see javax.xml.transform.SourceLocator#getPublicId()
   */
  public String getPublicId()
  {
    return m_xobject.getPublicId();
  }

  /**
   * @see javax.xml.transform.SourceLocator#getSystemId()
   */
  public String getSystemId()
  {
    return m_xobject.getSystemId();
  }

  /**
   * @see org.apache.xpath.Expression#isNodesetExpr()
   */
  public boolean isNodesetExpr()
  {
    return m_xobject.isNodesetExpr();
  }

  /**
   * @see org.apache.xpath.Expression#isStableNumber()
   */
  public boolean isStableNumber()
  {
    return m_xobject.isStableNumber();
  }

  /**
   * @see org.apache.xpath.Expression#num(XPathContext)
   */
  public double num(XPathContext xctxt) throws TransformerException
  {
    return m_xobject.num(xctxt);
  }

  /**
   * @see org.apache.xpath.Expression#warn(XPathContext, int, Object[])
   */
  public void warn(XPathContext xctxt, int msg, Object[] args)
    throws TransformerException
  {
    m_xobject.warn(xctxt, msg, args);
  }

  /**
   * @see org.apache.xpath.Expression#xstr(XPathContext)
   */
  public XMLString xstr(XPathContext xctxt) throws TransformerException
  {
    return m_xobject.xstr(xctxt);
  }

  /**
   * @see org.apache.xpath.parser.SimpleNode#dump(String, PrintStream)
   */
  public void dump(String prefix, PrintStream ps)
  {
    m_xobject.dump(prefix, ps);
  }

  /**
   * @see org.apache.xpath.parser.SimpleNode#dump(String)
   */
  public void dump(String prefix)
  {
    m_xobject.dump(prefix);
  }

  /**
   * @see org.apache.xpath.parser.SimpleNode#toString(String)
   */
  public String toString(String prefix)
  {
    return m_xobject.toString(prefix);
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return m_xobject.hashCode();
  }

  /**
   * @see java.lang.Object#equals(Object)
   */
  public boolean equals(Object arg0)
  {
    return m_xobject.equals(arg0);
  }

}
