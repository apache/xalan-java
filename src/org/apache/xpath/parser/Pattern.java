/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
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
package org.apache.xpath.parser;

import java.util.Vector;

import org.apache.xerces.impl.xpath.XPath.Axis;
import org.apache.xpath.Expression;
import org.apache.xpath.patterns.StepPattern;

/**
 * This is an expression node that only exists for construction 
 * purposes. 
 */
public class Pattern extends NonExecutableExpression
{
  public Pattern(XPath parser)
  {
  	super(parser);
  }

  public void jjtAddChild(Node n, int i) 
  {
    if(null == m_exprs)
    	m_exprs  = new Vector();
  	if(i >= m_exprs.size())
  	{
  		m_exprs.setSize(i+1);
  	}
  	int invertedPos = (m_exprs.size()-1)-i;
  	if(0 == invertedPos)
  	{
//  		if(n instanceof StepPattern)
//  		{
//  		 	StepPattern spat = (StepPattern)n;
//  		 	spat.setAxis(org.apache.xml.dtm.Axis.SELF);
//  		}
    	m_exprs.setElementAt(n, invertedPos);
  	}
  	else if(n instanceof SlashOrSlashSlash)
  	{
  		m_exprs.setElementAt(n, invertedPos);
  	}
  	else
  	{
  		int prevNodePos = invertedPos-1;
  		Node prevNode = (Node)m_exprs.elementAt(prevNodePos);
  		int whichAxis;
  		if(prevNode instanceof SlashOrSlashSlash)
  		{
  			m_exprs.removeElementAt(prevNodePos);
  			whichAxis = ((SlashOrSlashSlash)prevNode).getisSlashSlash() ? 
  					org.apache.xml.xdm.Axis.ANCESTOR : org.apache.xml.xdm.Axis.PARENT;
  					
	  		if(n instanceof StepPattern)
	  		{
	  			StepPattern spat = (StepPattern)n;
	  			spat.setAxis(whichAxis);
	  		}
  		}
  			
  		Node head = (Node)m_exprs.elementAt(0);
  		if(head instanceof StepPattern)
  		{
  			StepPattern headPat = (StepPattern)head;
  			StepPattern tail = headPat.getRelativePathPattern();
  			while(null != tail)
  			{
  					headPat = tail;
  					tail = tail.getRelativePathPattern();
  			}
  			headPat.setExpression((Expression)n);
  		}
  			
  	}
  }

  public void jjtClose() 
  {
  	if(jjtGetNumChildren() > 0)
  	{
  		((StepPattern)jjtGetChild(0)).calcScore();
  		int i = 4; // debugger breakpoint
  	}
  }

}

