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
package org.apache.xalan.lib.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Properties;

import org.xml.sax.AttributeList;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.traversal.NodeIterator;

/**
 * <meta name="usage" content="experimental"/>
 * An XSLT extension that allows a stylesheet to
 * access JDBC data. From the stylesheet perspective,
 * XConnection provides 3 extension functions: new(),
 * query(), and close().
 * Use new() to call one of XConnection constructors, which
 * establishes a JDBC driver connection to a data source and
 * returns an XConnection object.
 * Then use the XConnection object query() method to return a
 * result set in the form of a row-set element.
 * When you have finished working with the row-set, call the
 * XConnection object close() method to terminate the connection.
 */
public class XConnection
{

  /** Flag for DEBUG mode          */
  private static final boolean DEBUG = false;

  /**
   * A JDBC driver of the form "foo.bar.Driver".
   */
  public String m_driver;

  /**
   * A database URL of the form jdbc:subprotocol:subname.
   */
  public String m_dbURL;

  /**
   * A user ID.
   */
  public String m_user;

  /**
   * A password.
   */
  public String m_password;

  /**
   * A list of arbitrary string tag/value pairs as connection
   * arguments; normally at least a "user" and "password"
   * property should be included.
   */
  public Properties m_protocol;

  /**
   * The JDBC connection.
   */
  public Connection m_connection = null;

  /**
   * Create an XConnection object with just a driver and database URL.
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   */
  public XConnection(String driver, String dbURL)
  {

    super();

    init(driver, dbURL, null, null, null, 1);
  }

  /**
   * Create an XConnection object with user ID and password.
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   * @param user user ID.
   * @param password connection password.
   */
  public XConnection(String driver, String dbURL, String user,
                     String password)
  {

    super();

    init(driver, dbURL, user, password, null, 3);
  }

  /**
   * Create an XConnection object with a connection protocol
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   * @param protocolElem list of string tag/value connection arguments,
   * normally including at least "user" and "password".
   */
  public XConnection(String driver, String dbURL, Element protocolElem)
  {

    super();

    init(driver, dbURL, null, null, protocolElem, 2);
  }

  /**
   * Initialize.
   *
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   * @param user user ID
   * @param password connection password.
   * @param protocolElem list of string tag/value connection arguments,
   * normally including at least "user" and "password".
   * @param getConnectionArgs Connection arguments
   */
  private void init(String driver, String dbURL, String user,
                    String password, Element protocolElem,
                    int getConnectionArgs)
  {

    m_driver = driver;
    m_dbURL = dbURL;
    m_user = user;
    m_password = password;

    if (protocolElem == null)
      m_protocol = null;
    else
    {
      m_protocol = new Properties();

      NamedNodeMap atts = protocolElem.getAttributes();

      for (int i = 0; i < atts.getLength(); i++)
      {
        m_protocol.put(atts.item(i).getNodeName(),
                       atts.item(i).getNodeValue());
      }
    }

    connect(driver, dbURL, user, password, m_protocol, getConnectionArgs);
  }

  /**
   * Connect to the JDBC database.
   * @param driver Database url of the form jdbc:subprotocol:subname .
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   * @param user user ID
   * @param password connection password.
   * @param protocol List of arbitrary string tag/value pairs as
   * connection arguments; normally at least a "user" and "password"
   * property should be included.
   * @param getConnectionArgs Connection arguments
   */
  public void connect(String driver, String dbURL, String user,
                      String password, Properties protocol,
                      int getConnectionArgs)
  {

    try
    {

      // The driver is installed by loading its class.
      Class.forName(driver).newInstance();

      // Use the appropriate getConnection() method.
      switch (getConnectionArgs)
      {
      case 1 :
        m_connection = DriverManager.getConnection(dbURL);
        break;
      case 2 :
        m_connection = DriverManager.getConnection(dbURL, protocol);
        break;
      case 3 :
        m_connection = DriverManager.getConnection(dbURL, user, password);
      }

      /*
      We could also turn autocommit off by putting
      ;autocommit=false on the URL.
      */
      try
      {
        m_connection.setAutoCommit(false);
      }
      catch(java.sql.SQLException se)
      {
        // Some drivers do not support transactions
      }

      DatabaseMetaData dma = m_connection.getMetaData();

      if (DEBUG)
      {
        System.out.println("\nConnected to " + dma.getURL());
        System.out.println("Driver   " + dma.getDriverName());
        System.out.println("Version  " + dma.getDriverVersion());
        System.out.println("");
      }
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Execute a query statement by instantiating an {@link org.apache.xalan.lib.sql.XStatement XStatement}
   * object. The XStatement executes the query, and uses the result set to create a
   * {@link org.apache.xalan.lib.sql.RowSet RowSet}, a row-set element.
   * @param queryString the SQL query.
   * @return XStatement implements NodeIterator.
   *
   * @throws SQLException
   */
  public NodeIterator query(String queryString) throws SQLException
  {

    // TODO: These need to be pooled.
    return new XStatement(this, queryString);
  }

  /**
   * Close the connection to the data source.
   *
   *
   * @throws SQLException
   */
  public void close() throws SQLException
  {

    if (DEBUG)
      System.out.println("Entering XConnection.close");

    if (null != m_connection)
    {
      m_connection.close();

      m_connection = null;
    }

    if (DEBUG)
      System.out.println("Exiting XConnection.close");
  }
}
