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
import java.util.Vector;
import java.util.StringTokenizer;


import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.apache.xalan.stree.ElementImpl;

/**
 * <p>
 * The XConnection Object is the main (currently the only) interface
 * into the SQL Extensions from the Transformer. It is initiated by
 * supplying connection information and is used by instructing it
 * to produce a JDBC Query.
 * </p>
 * </p>
 *  * The XConnection Object provides several methods of connecting to a
 * JDBC Source. There are two major connection methods, one where the
 * actual connection is established outside the scope of the trasformer
 * {@link org.apache.xalan.lib.sql.XConnectionPoolManager} and the other
 * where the Connection information is supplied as part or either the
 * XSLT Stylesheet or the XML Document.
 *</p>
 * <p>
 * If the Connection is part of an external connection, the connection is
 * identified just by its connection pool name. In this mode, the connection
 * pool manager will be interogated for the connection pool instance and
 * a new connection is pulled from there.
 * </p>
 * <p>
 * When the Connection information is supplied from the Stylesheet or Document,
 * the minimal amount of information that needs to be supplied is the JDBC
 * class or driver and the URL to connect to that Driver.
 * </p>
 * </p>
 * Top maintain backward compatibility with previous versions of the extension,
 * supplying connection information in the constructor is still supported.
 * </p>
 * <p>
 * The new method is to supply the connection information via the connect
 * method.
 * </p>
 * <p>
 * Once a connection is established, a Database query must be performed to
 * have data to work with. This can be accomplished in one of two way.
 * </p>
 * <p>
 * The straight query; the query method allow a simple query to be excuted.
 * This methode qucikly becomes cumbersome when parts of the query are being
 * created dynamically as part of the data but is easiest to implement for a
 * simple static query.
 * </p>
 * <p>
 * The parameter based query; the pquery method allows the Stylesheet designer
 * to create a simple query with place markers for the dynamic components.
 * Form there the parameters are added in order. The pquery uses the JDBC
 * prepated statement to do its work. Parmateres also allow the Stylesheet
 * designer to specify the parameter type.
 * </p>
 */
public class XConnection
{

  /**
   * Flag for DEBUG mode
   */
  private static final boolean DEBUG = false;


  /**
   * The JDBC connection.
   */
  public Connection m_connection = null;

  /**
   * Reference to the ConnectionPool used
   */
  private ConnectionPool  m_ConnectionPool = null;
  private String          m_ConnectionPoolName;

  private boolean         m_IsDefaultPool = false;
  private boolean         m_DefaultPoolingEnabled = false;


  /**
   * Let's keep a copy of the ConnectionPoolMgr in
   * alive here so we are keeping the static pool alive
   *
   * We will also use this Pool Manager to register our default pools.
   *
   */

   private XConnectionPoolManager m_PoolMgr = new XConnectionPoolManager();

  /**
   * For PreparedStatements, we need a place to
   * to store the parameters in a vector.
   */
   private Vector        m_ParameterList = new Vector();

  /**
   * Determine if we will build an in-memory copy of the SQL Data
   */
  private boolean      m_ShouldCacheNodes = true;

  // The original constructors will be kept around for backwards
  // compatibility. Future Stylesheets should use the approaite
  // connect method to receive full error information.
  //
  public XConnection (String ConnPoolName)
  {
    connect(ConnPoolName);
  }

  public XConnection(String driver, String dbURL)
  {
    connect(driver, dbURL);
  }

  public XConnection(NodeList list)
  {
    connect(list);
  }

  public XConnection(String driver, String dbURL, String user,
                     String password)
  {
    connect(driver, dbURL, user, password);
  }

  public XConnection(String driver, String dbURL, Element protocolElem)
  {
    connect(driver, dbURL, protocolElem);
  }

  /**
   *
   * Create an XConnection using the name of an existing Connection Pool
   * @param <code>String poolName</code>, name of the existing pool
   * to pull connections from.
   *
   */
  public NodeIterator connect(String ConnPoolName)
  {
    try
    {
      if ((m_ConnectionPool != null) && (m_IsDefaultPool))
        m_PoolMgr.removePool(m_ConnectionPoolName);

      m_ConnectionPool = m_PoolMgr.getPool(ConnPoolName);
      m_ConnectionPoolName = ConnPoolName;

      m_connection = m_ConnectionPool.getConnection();
    }
    catch(SQLException e)
    {
      SQLExtensionError err = new SQLExtensionError(e);
      return err;
    }

    return null;
  }

  /**
   * Create an XConnection object with just a driver and database URL.
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   */

  public NodeIterator connect(String driver, String dbURL)
  {
    try
    {
      init(driver, dbURL, new Properties() );
    }
    catch(SQLException e)
    {
      SQLExtensionError err = new SQLExtensionError(e);
      return err;
    }
    catch (Exception e)
    {
      ExtensionError err = new ExtensionError(e);
      return err;
    }

    return null;

  }

  public NodeIterator connect(Element protocolElem)
  {
    try
    {
      initFromElement(protocolElem);
    }
    catch(SQLException e)
    {
      SQLExtensionError err = new SQLExtensionError(e);
      return err;
    }
    catch (Exception e)
    {
      ExtensionError err = new ExtensionError(e);
      return err;
    }

    return null;

  }

  public NodeIterator connect(NodeList list)
  {
    try
    {
      initFromElement( (Element) list.item(0) );
    }
    catch(SQLException e)
    {
      SQLExtensionError err = new SQLExtensionError(e);
      return err;
    }
    catch (Exception e)
    {
      ExtensionError err = new ExtensionError(e);
      return err;
    }

    return null;
  }

  /**
   * Create an XConnection object with user ID and password.
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   * @param user user ID.
   * @param password connection password.
   */
  public NodeIterator connect(String driver, String dbURL, String user,
                     String password)
  {
    try
    {
      Properties prop = new Properties();
      prop.put("user", user);
      prop.put("password", password);

      init(driver, dbURL, prop);
    }
    catch(SQLException e)
    {
      SQLExtensionError err = new SQLExtensionError(e);
      return err;
    }
    catch (Exception e)
    {
      ExtensionError err = new ExtensionError(e);
      return err;
    }

    return null;
  }


  /**
   * Create an XConnection object with a connection protocol
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   * @param protocolElem list of string tag/value connection arguments,
   * normally including at least "user" and "password".
   */
  public NodeIterator connect(String driver, String dbURL, Element protocolElem)
  {
    try
    {
      Properties prop = new Properties();

      NamedNodeMap atts = protocolElem.getAttributes();

      for (int i = 0; i < atts.getLength(); i++)
      {
        prop.put(atts.item(i).getNodeName(), atts.item(i).getNodeValue());
      }

      init(driver, dbURL, prop);
    }
    catch(SQLException e)
    {
      SQLExtensionError err = new SQLExtensionError(e);
      return err;
    }
    catch (Exception e)
    {
      ExtensionError err = new ExtensionError(e);
      return err;
    }

    return null;
  }


  /**
   *
   * Allow the database connection information to be sepcified in
   * the XML tree. The connection information could also be
   * externally originated and passed in as an XSL Parameter.
   *
   * The required XML Format is as follows.
   *
   * // A document fragment is needed to specify the connection information
   * // the top tag name is not specific for this code, we are only interested
   * // in the tags inside.
   * <DBINFO-TAG>
   *
   * // Specify the driver name for this connection pool
   *  <dbdriver>drivername</dbdriver>
   *
   * // Specify the URL for the driver in this connection pool
   *  <dburl>url</dburl>
   *
   * // Specify the password for this connection pool
   *  <password>password</password>
   *
   * // Specify the username for this connection pool
   *  <user>username</user>
   *
   *  // You can add extra protocol items including the User Name & Password
   *  // with the protocol tag. For each extra protocol item, add a new element
   *  // where the name of the item is specified as the name attribute and
   *  // and its value as the elements value.
   *  <protocol name="name of value">value</protocol>
   *
   * </DBINFO-TAG>
   *
   */
  private void initFromElement(Element e)
    throws SQLException
  {

    Properties prop = new Properties();
    String driver = "";
    String dbURL = "";
    Node n = e.getFirstChild();

    if (null == n) return; // really need to throw an error

    do
    {
      String nName = n.getNodeName();

      if (nName.equalsIgnoreCase("dbdriver"))
      {
        driver = "";
        Node n1 = n.getFirstChild();
        if (null != n1)
        {
          driver = n1.getNodeValue();
        }
      }

      if (nName.equalsIgnoreCase("dburl"))
      {
        dbURL = "";
        Node n1 = n.getFirstChild();
        if (null != n1)
        {
          dbURL = n1.getNodeValue();
        }
      }

      if (nName.equalsIgnoreCase("password"))
      {
        String s = "";
        Node n1 = n.getFirstChild();
        if (null != n1)
        {
          s = n1.getNodeValue();
        }
        prop.put("password", s);
      }

      if (nName.equalsIgnoreCase("user"))
      {
        String s = "";
        Node n1 = n.getFirstChild();
        if (null != n1)
        {
          s = n1.getNodeValue();
        }
        prop.put("user", s);
      }

      if (nName.equalsIgnoreCase("protocol"))
      {
        String Name = "";

        NamedNodeMap attrs = n.getAttributes();
        Node n1 = attrs.getNamedItem("name");
        if (null != n1)
        {
          String s = "";
          Name = n1.getNodeValue();

          Node n2 = n.getFirstChild();
          if (null != n2) s = n2.getNodeValue();

          prop.put(Name, s);
        }
      }

    } while ( (n = n.getNextSibling()) != null);

    init(driver, dbURL, prop);
  }



  /**
   * Initilize is being called because we did not have an
   * existing Connection Pool, so let's see if we created one
   * already or lets create one ourselves.
   *
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   * @param Properties list of string tag/value connection arguments,
   * normally including at least "user" and "password".
   * @param getConnectionArgs Connection arguments
   */
  private void init(String driver, String dbURL, Properties prop)
    throws SQLException
  {
    if (DEBUG)
      System.out.println("XConnection, Connection Init");

    String user = prop.getProperty("user");
    if (user == null) user = "";

    String passwd = prop.getProperty("password");
    if (passwd == null) passwd = "";


    String poolName = driver + dbURL + user + passwd;
    ConnectionPool cpool = m_PoolMgr.getPool(poolName);

    // We are limited to only one Default Connection Pool
    // at a time.
    // If we are creating a new Default Pool, release the first
    // One so it is not lost when we close the Stylesheet
    if (
        (m_ConnectionPool != null) &&
        (m_IsDefaultPool) &&
        (cpool != m_ConnectionPool))
    {
      m_PoolMgr.removePool(m_ConnectionPoolName);
    }

    if (cpool == null)
    {

      if (DEBUG)
      {
        System.out.println("XConnection, Creating Connection");
        System.out.println(" Driver  :" + driver);
        System.out.println(" URL     :" + dbURL);
        System.out.println(" user    :" + user);
        System.out.println(" passwd  :" + passwd);
      }


      DefaultConnectionPool defpool = new DefaultConnectionPool();

      if ((DEBUG) && (defpool == null))
        System.out.println("Failed to Create a Default Connection Pool");

      defpool.setDriver(driver);
      defpool.setURL(dbURL);
      defpool.setProtocol(prop);


      // Only enable pooling in the default pool if we are explicatly
      // told too.
      if (m_DefaultPoolingEnabled) defpool.enablePool();

      m_PoolMgr.registerPool(poolName, defpool);

      m_ConnectionPool = defpool;
      m_ConnectionPoolName = poolName;
    }
    else
    {
      if (DEBUG)
        System.out.println("Default Connection already existed");

      m_ConnectionPool = cpool;
      m_ConnectionPoolName = poolName;
    }

    m_ConnectionPool.testConnection();

    m_connection = m_ConnectionPool.getConnection();
  }


  /**
   * Execute a query statement by instantiating an
   * {@link org.apache.xalan.lib.sql.XStatement XStatement}
   * object. The XStatement executes the query, and uses the result set
   * to create a {@link org.apache.xalan.lib.sql.RowSet RowSet},
   * a row-set element.
   *
   * @param queryString the SQL query.
   * @return XStatement implements NodeIterator.
   */
  public NodeIterator query(String queryString)
  {
    try
    {
      return new XStatement(this, queryString, m_ShouldCacheNodes);
    }
    catch(SQLException e)
    {
      if (DEBUG)
      {
        System.out.println("SQL Exception in Query");
        e.printStackTrace();
      }

      SQLExtensionError err = new SQLExtensionError(e);
      return err;
    }
    catch (Exception e)
    {
      if (DEBUG)
      {
        System.out.println("Exception in Query");
        e.printStackTrace();
      }

      ExtensionError err = new ExtensionError(e);
      return err;
    }

  }

  /**
   * Execute a parameterized query statement by instantiating an
   * {@link org.apache.xalan.lib.sql.XStatement XStatement}
   * object. The XStatement executes the query, and uses the result set
   * to create a {@link org.apache.xalan.lib.sql.RowSet RowSet},
   * a row-set element.
   *
   * @param queryString the SQL query.
   * @return XStatement implements NodeIterator.
   */
  public NodeIterator pquery(String queryString)
  {
    try
    {
      return new XStatement(
        this, queryString,
        m_ParameterList, m_ShouldCacheNodes);
    }
    catch(SQLException e)
    {
      SQLExtensionError err = new SQLExtensionError(e);
      return err;
    }
    catch (Exception e)
    {
      ExtensionError err = new ExtensionError(e);
      return err;
    }


  }


  /**
   * Execute a parameterized query statement by instantiating an
   * {@link org.apache.xalan.lib.sql.XStatement XStatement}
   * object. The XStatement executes the query, and uses the result set
   * to create a {@link org.apache.xalan.lib.sql.RowSet RowSet},
   * a row-set element.
   * This method allows for the user to pass in a comma seperated
   * String that represents a list of parameter types. If supplied
   * the parameter types will be used to overload the current types
   * in the current parameter list.
   *
   * @param queryString the SQL query.
   * @return XStatement implements NodeIterator.
   */
  public NodeIterator pquery(String queryString, String typeInfo)
  {
    try
    {
      int indx = 0;
      QueryParameter param = null;

      // Parse up the parameter types that were defined
      // with the query
      StringTokenizer plist = new StringTokenizer(typeInfo);

      // Override the existing type that is stored in the
      // parameter list. If there are more types than parameters
      // ignore for now, a more meaningfull error should occur
      // when the actual query is executed.
      while (plist.hasMoreTokens())
      {
        String value = plist.nextToken();
        param = (QueryParameter) m_ParameterList.elementAt(indx);
        if ( null != param )
        {
          param.setType(value);
        }
      }

      return new XStatement(
        this, queryString,
        m_ParameterList, m_ShouldCacheNodes);
    }
    catch(SQLException e)
    {
      SQLExtensionError err = new SQLExtensionError(e);
      return err;
    }
    catch (Exception e)
    {
      ExtensionError err = new ExtensionError(e);
      return err;
    }


  }

  /**
   * Add an untyped value to the parameter list.
   */
  public void addParameter(String value)
  {
    addParameterWithType(value, null);
  }

  /**
   * Add a typed parameter to the parameter list.
   */
  public void addParameterWithType(String value, String Type)
  {
    m_ParameterList.addElement( new QueryParameter(value, Type) );
  }


  /**
   * Add a single parameter to the parameter list
   * formatted as an Element
   */
  public void addParameterFromElement(Element e)
  {
    NamedNodeMap attrs = e.getAttributes();
    Node Type = attrs.getNamedItem("type");
    Node n1  = e.getFirstChild();
    if (null != n1)
    {
      String value = n1.getNodeValue();
      if (value == null) value = "";
      m_ParameterList.addElement( new QueryParameter(value, Type.getNodeValue()) );
    }
  }


  /**
   * Add a section of parameters to the Parameter List
   * Do each element from the list
   */
  public void addParameterFromElement(NodeList nl)
  {
    //
    // Each child of the NodeList represents a node
    // match from the select= statment. Process each
    // of them as a seperate list.
    // The XML Format is as follows
    //
    // <START-TAG>
    //   <TAG1 type="int">value</TAG1>
    //   <TAGA type="int">value</TAGA>
    //   <TAG2 type="string">value</TAG2>
    // </START-TAG>
    //
    // The XSL to process this is formatted as follows
    // <xsl:param name="plist" select="//START-TAG" />
    // <sql:addParameter( $plist );
    //
    int count = nl.getLength();
    for (int x=0; x<count; x++)
    {
      addParameters( (Element) nl.item(x));
    }
  }

  private void addParameters(Element elem)
  {
    //
    // Process all of the Child Elements
    // The format is as follows
    //
    //<TAG type ="typeid">value</TAG>
    //<TAG1 type ="typeid">value</TAG1>
    //<TAGA type ="typeid">value</TAGA>
    //
    // The name of the Node is not important just is value
    // and if it contains a type attribute

    Node n = elem.getFirstChild();

    if (null == n) return;

    do
    {
      if (n.getNodeType() == Node.ELEMENT_NODE)
      {
        NamedNodeMap attrs = n.getAttributes();
        Node Type = attrs.getNamedItem("type");
        String TypeStr;

        if (Type == null) TypeStr = "string";
        else TypeStr = Type.getNodeValue();

        Node n1  = n.getFirstChild();
        if (null != n1)
        {
          String value = n1.getNodeValue();
          if (value == null) value = "";


          m_ParameterList.addElement(
            new QueryParameter(value, TypeStr) );
        }
      }
    } while ( (n = n.getNextSibling()) != null);
  }


  /**
   *
   * There is a problem with some JDBC drivers when a Connection
   * is open and the JVM shutsdown. If there is a problem, there
   * is no way to control the currently open connections in the
   * pool. So for the default connection pool, the actuall pooling
   * mechinsm is disabled by default. The Stylesheet designer can
   * re-enabled pooling to take advantage of connection pools.
   * The connection pool can even be disabled which will close all
   * outstanding connections.
   *
   *
   */
  public void enableDefaultConnectionPool()
  {

    if (DEBUG)
      System.out.println("Enabling Default Connection Pool");

    m_DefaultPoolingEnabled = true;

    if (m_ConnectionPool == null) return;
    if (!m_IsDefaultPool) return;

    m_ConnectionPool.enablePool();

  }

  /**
   * See enableDefaultConnectionPool
   */
  public void disableDefaultConnectionPool()
  {
    if (DEBUG)
      System.out.println("Disabling Default Connection Pool");

    m_DefaultPoolingEnabled = false;

    if (m_ConnectionPool == null) return;
    if (!m_IsDefaultPool) return;

    m_ConnectionPool.disablePool();
  }

  /**
   *
   * Clear the parameter list for the Prameter based
   * queries. Allows for XConnection reuse.
   *
   */
  public void clearParameters()
  {
    m_ParameterList.removeAllElements();
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
      if (null != m_ConnectionPool)
      {
        m_ConnectionPool.releaseConnection(m_connection);

      }
      else
      {
        // something is wrong here, we have a connection
        // but no controlling pool, close it anyway the
        // error will show up as an excpeion elsewhere
        m_connection.close();
      }
    }

    m_connection = null;

    if (DEBUG)
      System.out.println("Exiting XConnection.close");
  }

  public void enableCacheNodes()
  {
    m_ShouldCacheNodes = true;
  }

  public void disableCacheNodes()
  {
    m_ShouldCacheNodes = false;
  }

  protected void finalize()
  {
    if (DEBUG) System.out.println("In XConnection, finalize");
  }
}
