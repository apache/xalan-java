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
package servlet;

import java.io.*;
import org.xml.sax.*;
import org.apache.xml.utils.DefaultErrorHandler;

/*****************************************************************************************************
 * ApplyXSLTListener provides a buffered listener essential for capturing, and then subsequently
 * reporting, XML and XSL processor messages which may be of use in debugging XML+XSL processed at
 * the server.
 *
 * @author Spencer Shepard (sshepard@us.ibm.com)
 * @author R. Adam King (rak@us.ibm.com)
 * @author Tom Rowe (trowe@us.ibm.com)
 *
 *****************************************************************************************************/

public class ApplyXSLTListener extends DefaultErrorHandler implements ErrorHandler
{

    /**
      * Output stream
      */
    private ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    /**
      * Buffered output stream
      */
    public PrintWriter out = null;

    /**
      * Constructor.
      */
    public ApplyXSLTListener()
    {
      out = new PrintWriter(new BufferedOutputStream(outStream), true);
    }

    /**
      * Receive notification of a warning.
      *
      * @param spe The warning information encapsulated in a SAX parse exception.
      */
    public void warning(SAXParseException spe)
    {
	out.println("Parser Warning: " + spe.getMessage());
    }

    /**
      * Receive notification of a recoverable error.
      *
      * @param spe The error information encapsulated in a SAX parse exception.
      */
    public void error(SAXParseException spe)
    {
	out.println("Parser Error: " + spe.getMessage());
    }

    /**
      * Receive notification of a non-recoverable error.
      *
      * @param spe The error information encapsulated in a SAX parse exception.
      * @exception SAXException Always thrown
      */
    public void fatalError(SAXParseException spe)
    throws SAXException
    {
	out.println("Parser Fatal Error: " + spe.getMessage());
	throw spe;
    }

    /**
      * Returns the buffered processing message(s).
      * @return Buffered processing message(s)
      */
    public String getMessage()
    {
	return outStream.toString();
    }
}

