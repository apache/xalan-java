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
package org.apache.xalan.templates;

import org.w3c.dom.Node;
import java.util.Vector;
import java.util.StringTokenizer;
import org.apache.xalan.utils.StringBufferPool;
import org.apache.xalan.utils.FastStringBuffer;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.utils.PrefixResolver;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.processor.StylesheetHandler;

/**
 * <meta name="usage" content="advanced"/>
 * Class to hold an Attribute Value Template.
 */
public class AVT implements java.io.Serializable
{
  /**
   * If the AVT is not complex, just hold the simple string.
   */
  private String m_simpleString = null;
  
  /**
   * If the AVT is complex, hold a Vector of AVTParts.
   */
  private Vector m_parts = null;
  
  /**
   * The name of the attribute.
   */
  private String m_rawName;
  
  /**
   * Get the raw name of the attribute, with the prefix unprocessed.
   * MADE PUBLIC 9/5/2000 to support compilation experiment
   */
  public String getRawName()
  {
    return m_rawName;
  }
  
  /**
   * The name of the attribute.
   */
  private String m_name;
  
  /**
   * Get the local name of the attribute.
   * MADE PUBLIC 9/5/2000 to support compilation experiment
   */
  public String getName()
  {
    return m_name;
  }

  /**
   * The name of the attribute.
   */
  private String m_uri;
  
  /**
   * Get the namespace URI of the attribute.
   * MADE PUBLIC 9/5/2000 to support compilation experiment
   */
  public String getURI()
  {
    return m_uri;
  }

  /**
   * Construct an AVT by parsing the string, and either 
   * constructing a vector of AVTParts, or simply hold 
   * on to the string if the AVT is simple.
   */
  public AVT(StylesheetHandler handler, 
             String uri, 
             String name,
             String rawName,
             String stringedValue)
    throws org.xml.sax.SAXException
  {
    m_uri = uri;
    m_name = name;
    m_rawName = rawName;
    StringTokenizer tokenizer = new StringTokenizer(stringedValue, "{}\"\'", true);
    int nTokens = tokenizer.countTokens();
    if(nTokens < 2)
    {
      m_simpleString = stringedValue; // then do the simple thing
    }
    else
    {
      FastStringBuffer buffer = StringBufferPool.get();
      FastStringBuffer exprBuffer = StringBufferPool.get();
      try
      {
        m_parts = new Vector(nTokens+1);
        String t = null; // base token
        String lookahead= null; // next token
        String error = null; // if non-null, break from loop
        while(tokenizer.hasMoreTokens())
        {
          if( lookahead != null )
          {
            t = lookahead;
            lookahead = null;
          }
          else t = tokenizer.nextToken();
          
          if(t.length() == 1)
          {
            switch(t.charAt(0))
            {
            case('\"'):
            case('\''):
              {
                // just keep on going, since we're not in an attribute template
                buffer.append(t);
                break;
              }
            case('{'):
              {
                // Attribute Value Template start
                lookahead = tokenizer.nextToken();
                if(lookahead.equals("{"))
                {
                  // Double curlys mean escape to show curly
                  buffer.append(lookahead);
                  lookahead = null;
                  break; // from switch
                }
                /*
                else if(lookahead.equals("\"") || lookahead.equals("\'"))
                {
                // Error. Expressions can not begin with quotes.
                error = "Expressions can not begin with quotes.";
                break; // from switch
                }
                */
                else
                {
                  if(buffer.length() > 0)
                  {
                    m_parts.addElement(new AVTPartSimple(buffer.toString()));
                    buffer.setLength(0);
                  }

                  exprBuffer.setLength(0);
                  while(null != lookahead)
                  {
                    if(lookahead.length() == 1)
                    {
                      switch(lookahead.charAt(0))
                      {
                      case '\'':
                      case '\"':
                        {
                          // String start
                          exprBuffer.append(lookahead);
                          String quote = lookahead;
                          // Consume stuff 'till next quote
                          lookahead = tokenizer.nextToken();
                          while(!lookahead.equals(quote))
                          {
                            exprBuffer.append(lookahead);
                            lookahead = tokenizer.nextToken();
                          }
                          exprBuffer.append(lookahead);
                          lookahead = tokenizer.nextToken();
                          break;
                        }
                      case '{':
                        {
                          // What's another curly doing here?
                          error = XSLMessages.createMessage(XSLTErrorResources.ER_NO_CURLYBRACE, null); //"Error: Can not have \"{\" within expression.";
                          break;
                        }
                      case '}':
                        {
                          // Proper close of attribute template.
                          // Evaluate the expression.
                          buffer.setLength(0);
                          
                          XPath xpath = handler.createXPath(exprBuffer.toString());
                          m_parts.addElement(new AVTPartXPath(xpath));
                          
                          lookahead = null; // breaks out of inner while loop
                          break;
                        }
                      default:
                        {
                          // part of the template stuff, just add it.
                          exprBuffer.append(lookahead);
                          lookahead = tokenizer.nextToken();
                        }
                      } // end inner switch
                    } // end if lookahead length == 1
                    else
                    {
                      // part of the template stuff, just add it.
                      exprBuffer.append(lookahead);
                      lookahead = tokenizer.nextToken();
                    }
                  } // end while(!lookahead.equals("}"))
                  if(error != null)
                  {
                    break; // from inner while loop
                  }
                }
                break;
              }
            case('}'):
              {
                lookahead = tokenizer.nextToken();
                if(lookahead.equals("}"))
                {
                  // Double curlys mean escape to show curly
                  buffer.append(lookahead);
                  lookahead = null; // swallow
                }
                else
                {
                  // Illegal, I think...
                  handler.warn(XSLTErrorResources.WG_FOUND_CURLYBRACE, null); //"Found \"}\" but no attribute template open!");
                  buffer.append("}");
                  // leave the lookahead to be processed by the next round.
                }
                break;
              }
            default:
              {
                // Anything else just add to string.
                buffer.append(t);
              }
            } // end switch t
          } // end if length == 1
          else
          {
            // Anything else just add to string.
            buffer.append(t);
          }
          if(null != error)
          {
            handler.warn(XSLTErrorResources.WG_ATTR_TEMPLATE, new Object[] {error}); //"Attr Template, "+error);
            break;
          }
        } // end while(tokenizer.hasMoreTokens())
        
        if(buffer.length() > 0)
        {
          m_parts.addElement(new AVTPartSimple(buffer.toString()));
          buffer.setLength(0);
        }
      }
      finally
      {
        StringBufferPool.free(buffer);
        StringBufferPool.free(exprBuffer);
      }
      
    } // end else nTokens > 1
    
    if(null == m_parts && (null == m_simpleString))
    {
      // Error?
      m_simpleString = "";
    }
  }
  
  /**
   * Get the AVT as the original string.
   */
  public String getSimpleString()
  {
    if(null != m_simpleString)
    {
      return m_simpleString;
    }
    else if(null != m_parts)
    {
      FastStringBuffer buf = StringBufferPool.get();
      String s;
      try
      {
        buf.setLength(0);
        int n = m_parts.size();
        for(int i = 0; i < n; i++)
        {
          AVTPart part = (AVTPart)m_parts.elementAt(i);
          buf.append(part.getSimpleString());
        }
        s = buf.toString();
      }
      finally
      {
        StringBufferPool.free(buf);
      }
      return s;
    }
    else
    {
      return "";
    }
  }
  
  /**
   * Evaluate the AVT and return a String.
   * @param context The current source tree context.
   * @param nsNode The current namespace context (stylesheet tree context).
   * @param NodeList The current Context Node List.
   */
  public String evaluate(XPathContext xctxt, Node context, 
                          org.apache.xalan.utils.PrefixResolver nsNode)
    throws org.xml.sax.SAXException
  {
    FastStringBuffer buf = StringBufferPool.get();
    try
    {
      if(null != m_simpleString)
      {
        return m_simpleString;
      }
      else if(null != m_parts)
      {
        buf.setLength(0);
        int n = m_parts.size();
        for(int i = 0; i < n; i++)
        {
          AVTPart part = (AVTPart)m_parts.elementAt(i);
          part.evaluate(xctxt, buf, context, nsNode);
        }
        return buf.toString();
      }
      else
      {
        return "";
      }
    }
    finally
    {
      StringBufferPool.free(buf);
    }
  }

  /** Test whether the AVT is insensitive to the context in which
   *  it is being evaluated. This is intended to facilitate 
   *  compilation of templates, by allowing simple AVTs to be 
   *  converted back into strings.
   *  
   *  Currently the only case we recognize is simple strings.
   * ADDED 9/5/2000 to support compilation experiment
   */
  public boolean isContextInsensitive()
  {
    return null != m_simpleString;
  }

}
