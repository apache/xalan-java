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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.trace.PrintTraceListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

public class Trace
{	
  public static void main (String[] args)
	  throws java.io.IOException, 
			 TransformerException, TransformerConfigurationException,
			 java.util.TooManyListenersException, 
			 org.xml.sax.SAXException			 
  {
    // Set up a PrintTraceListener object to print to a file.
    java.io.FileWriter fw = new java.io.FileWriter("events.log");  
    java.io.PrintWriter pw = new java.io.PrintWriter(fw, true);
    PrintTraceListener ptl = new PrintTraceListener(pw);

    // Print information as each node is 'executed' in the stylesheet.
    ptl.m_traceElements = true;
    // Print information after each result-tree generation event.
    ptl.m_traceGeneration = true;
    // Print information after each selection event.
    ptl.m_traceSelection = true;
    // Print information whenever a template is invoked.
    ptl.m_traceTemplates = true;

    // Set up the transformation    
   	TransformerFactory tFactory = TransformerFactory.newInstance();
    Transformer transformer = tFactory.newTransformer(new StreamSource("foo.xsl"));

    // Cast the Transformer object to TransformerImpl.
    if (transformer instanceof TransformerImpl) 
	  {
      TransformerImpl transformerImpl = (TransformerImpl)transformer;
      // Register the TraceListener with a TraceManager associated 
      // with the TransformerImpl.
      TraceManager trMgr = transformerImpl.getTraceManager();
      trMgr.addTraceListener(ptl);
                     
      // Perform the transformation --printing information to
      // the events log during the process.
      transformer.transform
                         ( new StreamSource("foo.xml"), 
                           new StreamResult(new java.io.FileWriter("foo.out")) );
    }
    // Close the PrintWriter and FileWriter.
    pw.close();
    fw.close();
   	System.out.println("**The output is in foo.out; the log is in events.log ****");	
    
  }
}