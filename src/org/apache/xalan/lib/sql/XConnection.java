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
  
  public XConnection(String driver, String dbURL, String user, String password)
   {
    super();
    init(driver, dbURL, user, password, null, 3);
  }
  
  /**
   * Create an XConnection object with a connection protocol
   * @param driver JDBC driver of the form foo.bar.Driver.
   * @param dbURL database URL of the form jdbc:subprotocol:subname.
   * @param protocol list of string tag/value connection arguments,
   * normally including at least "user" and "password".
   */
  public XConnection(String driver, String dbURL, Element protocolElem)
  {
    super();	
    init(driver, dbURL, null, null, protocolElem, 2);
  }  
  /**
   * Initialize.
   */
  private void init(String driver, String dbURL, String user, 
					String password,  Element protocolElem, int getConnectionArgs)
  {
    m_driver = driver;
    m_dbURL = dbURL;
	m_user = user;
	m_password = password;
	
	if(protocolElem == null)
	  m_protocol = null;
	else
	{
	  m_protocol = new Properties();
	  NamedNodeMap atts = protocolElem.getAttributes();
      for (int i = 0; i < atts.getLength(); i++)
      {	  
        m_protocol.put(atts.item(i).getNodeName(), atts.item(i).getNodeValue());
      }
	}	
	connect(driver, dbURL, user, password, m_protocol, getConnectionArgs);
  }
  
  /**
   * Connect to the JDBC database. 
   * @param driver Database url of the form jdbc:subprotocol:subname .
   * @param protocol List of arbitrary string tag/value pairs as 
   * connection arguments; normally at least a "user" and "password" 
   * property should be included.
   */
  public void connect(String driver, String dbURL, String user,
					  String password, Properties protocol, int getConnectionArgs) 
  {    
    try 
    {      
      // The driver is installed by loading its class.
      Class.forName(driver).newInstance();

      // Use the appropriate getConnection() method.
	  switch(getConnectionArgs)
	  {
	    case 1:		 
	    m_connection = DriverManager.getConnection(dbURL);
		break;
	    case 2:
		m_connection = DriverManager.getConnection(dbURL, protocol);
		break;
        case 3:
		m_connection = DriverManager.getConnection(dbURL, user, password);
	 }
      /*
      We could also turn autocommit off by putting
      ;autocommit=false on the URL.
      */
      m_connection.setAutoCommit(false);
      
	  DatabaseMetaData dma = m_connection.getMetaData ();

      if(DEBUG)
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
  */
  public NodeIterator query(String queryString)
    throws SQLException
  {
    // TODO: These need to be pooled.
    return new XStatement(this, queryString);
  }
  
  /*
   * Close the connection to the data source.
   */
   public void close()
    throws SQLException
  {
    if(DEBUG)
      System.out.println("Entering XConnection.close");
    if(null != m_connection)
    {
      m_connection.close();
      m_connection = null;
    }
    if(DEBUG)
      System.out.println("Exiting XConnection.close");
  }
}
