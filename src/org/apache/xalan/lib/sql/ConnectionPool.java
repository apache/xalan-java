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

import java.util.Properties;
import java.lang.String;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *<p>
 * This interface specification is used to define how Xalan will
 * interact with a Connection Pool. To adapt a third party connection
 * pool, either implement this interface as part of that pool or create
 * a wrapper class that will provide the translation.
 * </p>
 *
 */


public interface ConnectionPool
{
  /**
   * The Pool can be Enabled and Disabled. Disabling the pool
   * closes all the outstanding Unused connections and any new
   * connections will be closed upon release.
   *
   */
  public void     enablePool();
  public void     disablePool();
  public boolean  isEnabled();

  /**
   * The Driver and URL are the only required parmeters.
   */
  public void     setDriver(String d);
  public void     setURL(String url);

  /**
   * Start downsizeing the pool, this usally happens right after the
   * pool has been marked as Inactive and we are removing connections
   * that are not currently inuse.
   */
  public void     freeUnused();


  /**
   * Provide an indicator to the PoolManager when the Pool can be removed
   * from the Pool Table.
   */
  public boolean  hasActiveConnections();

  /**
   * The rest of the protocol parameters can eiter be passed in as
   * just Username and Password or as a property collection. If the
   * property collection is used, then the sperate username and password
   * may be ignored, it is up to the wrapper implementation to handle
   * the situation. If the connection information changes while after the
   * pool has been established, the wrapper implementation should ignore
   * the change and throw an error.
   *
   */
  public void     setPassword(String p);
  public void     setUser(String u);
  public void     setProtocol(Properties p);

  /**
   * Set tne minimum number of connections that are to be maintained in the
   * pool.
   */
  public void     setMinConnections(int n);

  /**
   * Test to see if the connection info is valid to make a real connection
   * to the database. This method may cause the pool to be crated and filled
   * with min connections.
   *
   */
  public boolean  testConnection();

  /**
   * Retrive a database connection from the pool
   *
   */
   public Connection getConnection() throws SQLException;

   /**
    * Return a connection to the pool, the connection may be closed if the
    * pool is inactive or has exceeded the max number of free connections
    *
    */
  public void releaseConnection(Connection con) throws SQLException;

}