/**
 * @(#) SQLErrorDocument.java
 *
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
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 * if any, must include the following acknowledgment:
 * "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself,
 * if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 * not be used to endorse or promote products derived from this
 * software without prior written permission. For written
 * permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 * nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
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
 *
 */

package org.apache.xalan.lib.sql;

import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.w3c.dom.NodeList;
import java.sql.ResultSet;
import org.apache.xml.dtm.*;
import org.apache.xml.dtm.ref.*;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.apache.xml.dtm.ref.DTMDefaultBaseIterators;
import org.xml.sax.ext.*;
import org.xml.sax.*;
import org.apache.xml.utils.*;

import java.sql.SQLException;

/**
 * The SQL Document is the main controlling class the executesa SQL Query
 */
public class SQLErrorDocument extends DTMDefaultBaseIterators
{
  /**
   */
  public SQLErrorDocument(SQLException error )
  {
    super(null, null, 0, null, null, true);
  }


  /**
   */
  public SQLErrorDocument(Exception error )
  {
    super(null, null, 0, null, null, true);
  }


  /**
   * @param parm1
   * @return
   */
  protected int getNextNodeIdentity( int parm1 )
  {
    return 0;
  }

  /**
   * @param parm1
   * @param parm2
   * @param parm3
   * @return
   */
  public int getAttributeNode( int parm1, String parm2, String parm3 )
  {
    return 0;
  }

  /**
   * @param parm1
   * @return
   */
  public String getLocalName( int parm1 )
  {
    return "";
  }

  /**
   * @param parm1
   * @return
   */
  public String getNodeName( int parm1 )
  {
    return "";
  }

  /**
   * @param parm1
   * @return
   */
  public int getElementById( String parm1 )
  {
    return 0;
  }

  /**
   * @return
   */
  public DeclHandler getDeclHandler( )
  {
    return null;
  }

  /**
   * @return
   */
  public ErrorHandler getErrorHandler( )
  {
    return null;
  }

  /**
   * @return
   */
  public String getDocumentTypeDeclarationSystemIdentifier( )
  {
    return null;
  }

  /**
   * @return
   */
  protected int getNumberOfNodes( )
  {
    return 0;
  }


  /**
   * @param parm1
   * @return
   */
  public String getNodeValue( int parm1 )
  {
    return "";
  }

  /**
   * @param parm1
   * @return
   */
  public boolean isAttributeSpecified( int parm1 )
  {
    return false;
  }

  /**
   * @param parm1
   * @return
   */
  public String getUnparsedEntityURI( String parm1 )
  {
    return "";
  }

  /**
   * @return
   */
  public DTDHandler getDTDHandler( )
  {
    return null;
  }

  /**
   * @param parm1
   * @return
   */
  public String getPrefix( int parm1 )
  {
    return "";
  }

  /**
   * @return
   */
  public EntityResolver getEntityResolver( )
  {
    return null;
  }

  /**
   * @return
   */
  public String getDocumentTypeDeclarationPublicIdentifier( )
  {
    return "";
  }

  /**
   * @return
   */
  protected boolean nextNode( )
  {
    return false;
  }

  /**
   * @return
   */
  public LexicalHandler getLexicalHandler( )
  {
    return null;
  }

  /**
   * @param parm1
   * @return
   */
  public XMLString getStringValue( int parm1 )
  {
    return null;
  }

  /**
   * @return
   */
  public boolean needsTwoThreads( )
  {
    return false;
  }

  /**
   * @return
   */
  public ContentHandler getContentHandler( )
  {
    return null;
  }

  /**
   * @param parm1
   * @param parm2
   * @return
   * @throws org.xml.sax.SAXException
   */
  public void dispatchToEvents( int parm1, ContentHandler parm2 )throws org.xml.sax.SAXException
  {
    return;
  }

  /**
   * @param parm1
   * @return
   */
  public String getNamespaceURI( int parm1 )
  {
    return "";
  }

  /**
   * @param parm1
   * @param parm2
   * @param parm3
   * @return
   * @throws org.xml.sax.SAXException
   */
  public void dispatchCharactersEvents( int parm1, ContentHandler parm2, boolean parm3 )throws org.xml.sax.SAXException
  {
    return;
  }


  /**
   * For the moment all the run time properties are ignored by this
   * class.
   *
   * @param property a <code>String</code> value
   * @param value an <code>Object</code> value
   */
  public void setProperty(String property, Object value)
  {
  }
  
  /**
   * No source information is available for DOM2DTM, so return
   * <code>null</code> here.
   *
   * @param node an <code>int</code> value
   * @return null
   */
  public javax.xml.transform.SourceLocator getSourceLocatorFor(int node)
  {
    return null;
  }


}
