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
import org.apache.trax.Processor; 
import org.apache.trax.Templates;
import org.apache.trax.Transformer; 
import org.apache.trax.Result;
import org.apache.trax.ProcessorException; 
import org.apache.trax.ProcessorFactoryException;
import org.apache.trax.TransformException; 


// Imported SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Imported java classes
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  Use the TraX interface to perform a transformation in the simplest manner possible
 *  (4 statements).
 */
public class SimpleTransform
{
	public static void main(String[] args)
    throws ProcessorException, ProcessorFactoryException, 
           TransformException, SAXException, IOException
  {  
    // Instantiate a stylesheet processor. The org.apache.trax.Processor newInstance()
	// method with "xslt" as the argument reads the org.apache.trax.processor.xslt 
	// system property to determine the actual class to instantiate:
	// org.apache.xalan.processor.StylesheetProcessor.
	Processor processor = Processor.newInstance("xslt");
	
	// Use the stylesheet processor to process the stylesheet (foo.xsl) and
	// return a Templates object. As implemented by StylesheetProcessor,
	// the process() method uses the Xerces SAX processor to parse the stylesheet
	// and returns a StylesheetRoot (an implementation of the org.apache.trax.Templates interface)
	// from the ContentHandler for the SAX processor. The Templates (StylesheetRoot)
	// object contains an XSLT schema and the stylesheet data.
    Templates templates = processor.process(new InputSource("foo.xsl"));
	
	// Use the Templates object to generate a Transformer object. As implemented by
	// StylesheetRoot, newTransformer() returns an instance of 
	// org.apache.xalan.transformer.Transformer, an implementation of the 
	// org.apache.trax.Transformer interface.
	Transformer transformer = templates.newTransformer();

	// Use the transformer to apply the Templates (StylesheetRoot) object to an XML document
	// (foo.xml) and write the output to a file (foo.out).
	transformer.transform(new InputSource("foo.xml"), new Result(new FileOutputStream("foo.out")));
	
	System.out.println("************* The result is in foo.out *************");
  }
}