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
package org.apache.xalan.trace;

import java.io.*;

import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;

import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.templates.Constants;
import org.apache.xpath.axes.ContextNodeList;

/**
 * <meta name="usage" content="advanced"/>
 * Implementation of the TraceListener interface that
 * prints each event to standard out as it occurs.
 *
 * @see org.apache.xalan.trace.TracerEvent
 */
public class PrintTraceListener implements TraceListener
{

  /**
   * Construct a trace listener.
   *
   * @param pw PrintWriter to use for tracing events
   */
  public PrintTraceListener(java.io.PrintWriter pw)
  {
    m_pw = pw;
  }

  /**
   * The print writer where the events should be written.
   */
  java.io.PrintWriter m_pw;

  /**
   * This needs to be set to true if the listener is to print an event whenever a template is invoked.
   */
  public boolean m_traceTemplates = false;

  /**
   * Set to true if the listener is to print events that occur as each node is 'executed' in the stylesheet.
   */
  public boolean m_traceElements = false;

  /**
   * Set to true if the listener is to print information after each result-tree generation event.
   */
  public boolean m_traceGeneration = false;

  /**
   * Set to true if the listener is to print information after each selection event.
   */
  public boolean m_traceSelection = false;

  /**
   * Print information about a TracerEvent.
   *
   * @param ev the trace event.
   */
  public void trace(TracerEvent ev)
  {

    switch (ev.m_styleNode.getXSLToken())
    {
    case Constants.ELEMNAME_TEXTLITERALRESULT :
      if (m_traceElements)
      {
        m_pw.print("Line #" + ev.m_styleNode.getLineNumber() + ", "
                   + "Column #" + ev.m_styleNode.getColumnNumber() + " -- "
                   + ev.m_styleNode.getNodeName() + ": ");

        ElemTextLiteral etl = (ElemTextLiteral) ev.m_styleNode;
        String chars = new String(etl.getChars(), 0, etl.getChars().length);

        m_pw.println("    " + chars.trim());
      }
      break;
    case Constants.ELEMNAME_TEMPLATE :
      if (m_traceTemplates || m_traceElements)
      {
        ElemTemplate et = (ElemTemplate) ev.m_styleNode;

        m_pw.print("Line #" + et.getLineNumber() + ", " + "Column #"
                   + et.getColumnNumber() + ": " + et.getNodeName() + " ");

        if (null != et.getMatch())
        {
          m_pw.print("match='" + et.getMatch().getPatternString() + "' ");
        }

        if (null != et.getName())
        {
          m_pw.print("name='" + et.getName() + "' ");
        }

        m_pw.println();
      }
      break;
    default :
      if (m_traceElements)
      {
        m_pw.println("Line #" + ev.m_styleNode.getLineNumber() + ", "
                     + "Column #" + ev.m_styleNode.getColumnNumber() + ": "
                     + ev.m_styleNode.getNodeName());
      }
    }
  }

  /**
   * Method that is called just after a select attribute has been evaluated.
   *
   * @param ev the generate event.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public void selected(SelectionEvent ev) throws javax.xml.transform.TransformerException
  {

    if (m_traceSelection)
    {
      ElemTemplateElement ete = (ElemTemplateElement) ev.m_styleNode;

      if (ev.m_styleNode.getLineNumber() == 0)
      {

        // You may not have line numbers if the selection is occuring from a
        // default template.
        ElemTemplateElement parent =
          (ElemTemplateElement) ete.getParentNode();

        if (parent == ete.getStylesheetRoot().getDefaultRootRule())
        {
          m_pw.print("(default root rule) ");
        }
        else if (parent == ete.getStylesheetRoot().getDefaultTextRule())
        {
          m_pw.print("(default text rule) ");
        }
        else if (parent == ete.getStylesheetRoot().getDefaultRule())
        {
          m_pw.print("(default rule) ");
        }

        m_pw.print(ete.getNodeName() + ", " + ev.m_attributeName + "='"
                   + ev.m_xpath.getPatternString() + "': ");
      }
      else
      {
        m_pw.print("Line #" + ev.m_styleNode.getLineNumber() + ", "
                   + "Column #" + ev.m_styleNode.getColumnNumber() + ": "
                   + ete.getNodeName() + ", " + ev.m_attributeName + "='"
                   + ev.m_xpath.getPatternString() + "': ");
      }

      if (ev.m_selection.getType() == ev.m_selection.CLASS_NODESET)
      {
        m_pw.println();

        NodeIterator nl = ev.m_selection.nodeset();
        if(nl instanceof ContextNodeList)
        {
          try
          {
            nl = ((ContextNodeList)nl).cloneWithReset();
          }
          catch(CloneNotSupportedException cnse)
          {
            m_pw.println("     [Can't trace nodelist because it it threw a CloneNotSupportedException]");
            return;
          }
          Node pos = nl.nextNode();

          if (null == pos)
          {
            m_pw.println("     [empty node list]");
          }
          else
          {
            while (null != pos)
            {
              m_pw.println("     " + pos);

              pos = nl.nextNode();
            }
          }
        }
        else
        {
            m_pw.println("     [Can't trace nodelist because it isn't a ContextNodeList]");
        }
      }
      else
      {
        m_pw.println(ev.m_selection.str());
      }
    }
  }

  /**
   * Print information about a Generate event.
   *
   * @param ev the trace event.
   */
  public void generated(GenerateEvent ev)
  {

    if (m_traceGeneration)
    {
      switch (ev.m_eventtype)
      {
      case GenerateEvent.EVENTTYPE_STARTDOCUMENT :
        m_pw.println("STARTDOCUMENT");
        break;
      case GenerateEvent.EVENTTYPE_ENDDOCUMENT :
        m_pw.println("ENDDOCUMENT");
        break;
      case GenerateEvent.EVENTTYPE_STARTELEMENT :
        m_pw.println("STARTELEMENT: " + ev.m_name);
        break;
      case GenerateEvent.EVENTTYPE_ENDELEMENT :
        m_pw.println("ENDELEMENT: " + ev.m_name);
        break;
      case GenerateEvent.EVENTTYPE_CHARACTERS :
      {
        String chars = new String(ev.m_characters, ev.m_start, ev.m_length);

        m_pw.println("CHARACTERS: " + chars);
      }
      break;
      case GenerateEvent.EVENTTYPE_CDATA :
      {
        String chars = new String(ev.m_characters, ev.m_start, ev.m_length);

        m_pw.println("CDATA: " + chars);
      }
      break;
      case GenerateEvent.EVENTTYPE_COMMENT :
        m_pw.println("COMMENT: " + ev.m_data);
        break;
      case GenerateEvent.EVENTTYPE_PI :
        m_pw.println("PI: " + ev.m_name + ", " + ev.m_data);
        break;
      case GenerateEvent.EVENTTYPE_ENTITYREF :
        m_pw.println("ENTITYREF: " + ev.m_name);
        break;
      case GenerateEvent.EVENTTYPE_IGNORABLEWHITESPACE :
        m_pw.println("IGNORABLEWHITESPACE");
        break;
      }
    }
  }
}
