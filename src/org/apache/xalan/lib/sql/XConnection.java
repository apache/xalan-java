package org.apache.xalan.lib.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.w3c.dom.traversal.NodeIterator;

/**
 * This is an extension for XSLT that allows a stylesheet to 
 * access JDBC data.
 */
public class XConnection
{ 
  private static final boolean DEBUG = false;

  /**
   * A database url of the form jdbc:subprotocol:subname.
   */
  public String m_driver;
  
  /**
   * A list of arbitrary string tag/value pairs as connection 
   * arguments; normally at least a "user" and "password" 
   * property should be included.
   */
  public String m_protocol;
  
  /**
   * The JDBC connection.
   */
  public Connection m_connection = null;
    
  /**
   * Create a SimpleNodeLocator object.
   */
  public XConnection(String driver, String protocol)
  {
    super();
    init(driver, protocol);
  }
  
  /**
   * Initialize.
   */
  private void init(String driver, String protocol)
  {
    m_driver = driver;
    m_protocol = protocol;
    connect(driver, protocol);
  }
  
  /**
   * Connect to the JDBC database. 
   * @param driver Database url of the form jdbc:subprotocol:subname .
   * @param protocol List of arbitrary string tag/value pairs as 
   * connection arguments; normally at least a "user" and "password" 
   * property should be included.
   */
  public void connect(String driver, String protocol) 
  {    
    try 
    {      
      // The driver is installed by loading its class.
      Class.forName(driver).newInstance();

      m_connection = DriverManager.getConnection(protocol);
      
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
  
  public NodeIterator query(String queryString)
    throws SQLException
  {
    // TODO: These need to be pooled.
    return new XStatement(this, queryString);
  }
  
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
