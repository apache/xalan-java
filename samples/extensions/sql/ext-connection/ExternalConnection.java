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
 * 4. The names "XSLT4J" and "Apache Software Foundation" must
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
// Imported TraX classes
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import org.apache.xalan.lib.sql.DefaultConnectionPool;
import org.apache.xalan.lib.sql.ConnectionPoolManager;


// Imported java classes
import java.io.StringReader;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *  Use the TraX interface to perform a transformation in the simplest manner possible
 *  (3 statements).
 */
public class ExternalConnection
{
	public static void main(String[] args)
    throws TransformerException, TransformerConfigurationException,
           FileNotFoundException, IOException
  {

  // Create a connection to the database server
  // Up the connection pool count for testing
  DefaultConnectionPool cp = new DefaultConnectionPool();
  cp.setDriver("org.enhydra.instantdb.jdbc.idbDriver");
  cp.setURL("jdbc:idb:../../instantdb/sample.prp");
  //cp.setUser("sa");
  //cp.setPassword("");
  cp.setMinConnections(10);
  cp.setPoolEnabled(true);

  // Now let's register our connection pool so we can use
  // in a stylesheet
  ConnectionPoolManager pm = new ConnectionPoolManager();
  pm.registerPool("extpool", cp);


  // Use the static TransformerFactory.newInstance() method to instantiate
  // a TransformerFactory. The javax.xml.transform.TransformerFactory
  // system property setting determines the actual class to instantiate --
  // org.apache.xalan.transformer.TransformerImpl.
	TransformerFactory tFactory = TransformerFactory.newInstance();

	// Use the TransformerFactory to instantiate a Transformer that will work with
	// the stylesheet you specify. This method call also processes the stylesheet
  // into a compiled Templates object.
	Transformer transformer = tFactory.newTransformer(
        new StreamSource("dbtest.xsl"));

	// For this transformation, all the required information is in the stylesheet, so generate 
  // a minimal XML source document for the input.
  // Note: the command-line processor (org.apache.xalan.xslt.Process) uses this strategy when 
  // the user does not provide an -IN parameter.
  StringReader reader =
              new StringReader("<?xml version=\"1.0\"?> <doc/>");

  // Use the Transformer to apply the associated Templates object to an XML document
	// and write the output to a file.
	transformer.transform(
        new StreamSource(reader),
        new StreamResult(new FileOutputStream("dbtest-out.html")));

	System.out.println("************* The result is in dbtest-out.html *************");
  
  cp.setPoolEnabled(false);
  }
}
