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

// Imported JAXP classes
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;

// SAX import
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

// Imported java.io classes
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileWriter;

/* Use JAXP SAXParser to parse 1 .xml file or all the .xml files in a directory.
 * Takes 1 or 2 command-line arguments:
 *   Argument 1 (required) is a file name or directory name.
 *   Argument 2 (optional) is a log file name. If ommitted, messages are written to screen.
 */
public class Validate
{
  static int numXMLFiles = 0;
  static int numValidFiles = 0;
  static int numInvalidFiles = 0;
  static int numFilesMissingDoctype = 0;
  static int numMalformedFiles = 0;
  static boolean useLogFile = false;
  static StringBuffer buff = new StringBuffer();

  public static void main(String[] args)
    throws FileNotFoundException, IOException, ParserConfigurationException, SAXException
  {
    if (args.length == 0 || args.length > 2)
    {
      System.out.println("\nEnter 'java validate -help' for information about running Validate");
      return;
    }
    if (args[0].toLowerCase().equals("-help"))
    { 
      String sep = "\n====================================================\n";
      String a = "Validate uses Xerces to parse the xml files in the directory you specify or the individual xml file you specify. The parser validates each document (checks that it conforms to its DOCTYPE).\n";
      String b = "Each xml file should contain a DOCTYPE declaration.\n\n";
      String c = "Validate takes 1 or 2 arguments:\n";
      String d = " Argument 1 specifies a directory or an individual xml file.\n";
      String e = " Argument 2 specifies a log file. If you include this argument, Validate appends messages to this file. If you do not, Validate writes messages to the screen.\n";
      System.out.println(sep+a+b+c+d+e+sep);
      return;
    }    
    try
    {
      Validate v = new Validate();
      v.validate(args);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }    
  }
  
  void validate(String[] args)
    throws FileNotFoundException, IOException, ParserConfigurationException, SAXException
  {
    File dir = new File(args[0]);
   
    // User may include a 2nd argument for the log file. 
    useLogFile = (args.length == 2);
   
    if (dir.isFile()) // Just checking one file.
    {
      parse(null,args[0]);
    }
    else if (dir.isDirectory())  // Checking the contents of a directory.
    {
      // Only interested in .xml files.
      XMLFileFilter filter = new XMLFileFilter();
      String [] files = dir.list(filter);
      for (int i = 0; i <files.length; i++)
      { 
        parse(dir.toString(),files[i]); // All the work is done here.
        
        if (!useLogFile) 
        // Write messages to screen after parsing each file.
        {
          System.out.print(buff.toString());
          buff = new StringBuffer();
        }
      }
    }
    else // Command-line argument is no good!
    {
      System.out.println(args[0] + " not found!");
      return;
    }
    // Provide user with a summary.
    buff.append("================SUMMARY=============================\n");
    if (numXMLFiles > 1)
      buff.append("Parsed " + numXMLFiles + " .xml files in " +  args[0] + ".\n");
    if (numValidFiles > 1)
      buff.append( numValidFiles + " files are valid.\n");
    else if (numValidFiles == 1)
      buff.append( numValidFiles + " file is valid.\n");
    if (numInvalidFiles > 1)
      buff.append(numInvalidFiles + " files are not valid.\n");
    else if (numInvalidFiles == 1)
      buff.append( numInvalidFiles + " file is not valid.\n");
    if (numMalformedFiles > 1)
      buff.append(numMalformedFiles + " files are not well-formed.\n");
    else if (numMalformedFiles == 1)
      buff.append( numMalformedFiles + " file is not well-formed.\n");
    if (numFilesMissingDoctype > 1)
      buff.append(numFilesMissingDoctype + " files do not contain a DOCTYPE declaration.\n");
    else if (numFilesMissingDoctype == 1)
      buff.append(numFilesMissingDoctype + " file does not contain a DOCTYPE declaration.\n");
     
    if (!useLogFile)
      System.out.print(buff.toString());
    else
    {
      // If log file exists, append.
      FileWriter writer = new FileWriter(args[1], true);
      writer.write(new java.util.Date().toString()+ "\n");
      writer.write(buff.toString());
      writer.close();     
      System.out.println("Done with validation. See " + args[1] + ".");
    }
  }
  
  // Parse each XML file.
  void parse(String dir, String filename)
     throws FileNotFoundException, IOException, ParserConfigurationException, SAXException
  {
    try 
    {
      File f = new File(dir, filename);
      StringBuffer errorBuff = new StringBuffer();
      InputSource input = new InputSource(new FileInputStream(f));
      // Set systemID so parser can find the dtd with a relative URL in the source document.
      input.setSystemId(f.toString());
      SAXParserFactory spfact = SAXParserFactory.newInstance();
       
      spfact.setValidating(true);
      spfact.setNamespaceAware(true);
            
      SAXParser parser = spfact.newSAXParser();
      XMLReader reader = parser.getXMLReader();
      
      //Instantiate inner-class error and lexical handler.
      Handler handler = new Handler(filename, errorBuff);
      reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
      parser.parse(input, handler);

      if (handler.containsDTD && !handler.errorOrWarning) // valid
      {
        buff.append("VALID " + filename +"\n");
        numValidFiles++;
      }
      else if (handler.containsDTD) // not valid
      {
        buff.append ("NOT VALID " + filename + "\n");
        buff.append(errorBuff.toString());
        numInvalidFiles++;
      }
      else // no DOCTYPE to use for validation
      {
        buff.append("NO DOCTYPE DECLARATION " + filename + "\n");
        numFilesMissingDoctype++;
      }
    }
    catch (Exception e) // Serious problem!
    {
      buff.append("NOT WELL-FORMED " + filename + ". " + e.getMessage() + "\n");
      numMalformedFiles++;
    }
    finally
    {
      numXMLFiles++;
    }
  }
  // Inner classes
  
  // Only interested in parsing .xml files.
  class XMLFileFilter implements FilenameFilter
  {
    public boolean accept(File dir, String fileName)
    {
      return fileName.toLowerCase().endsWith(".xml") && new File(dir.toString(),fileName).isFile();
    }
  }
  
  // Catch any errors or warnings, and verify presence of doctype statement.
  class Handler extends DefaultHandler implements LexicalHandler
  {
    boolean errorOrWarning;
    boolean containsDTD;
    String sourceFile;
    StringBuffer errorBuff;  
  
    Handler(String sourceFile, StringBuffer errorBuff)
    {
      super();
      this.sourceFile = sourceFile;
      this.errorBuff = errorBuff;
      errorOrWarning = false;
      containsDTD = false;
    }
    
    public void error(SAXParseException exc)
    {
      errorBuff.append(sourceFile + " Error: " + exc.getMessage()+ "\n");    
      errorOrWarning = true;
    }
    public void warning(SAXParseException exc)
    {
      errorBuff.append(sourceFile + " Warning:" + exc.getMessage()+ "\n");   
      errorOrWarning = true;
    }

    // LexicalHandler methods; all no-op except startDTD().
    
    // Set containsDTD to true when startDTD event occurs.
    public void startDTD (String name, String publicId, String systemId)
	    throws SAXException
    {
      containsDTD = true;
    }
 
    public void endDTD () throws SAXException
    {}  
  
    public void startEntity (String name) throws SAXException
    {}
  
    public void endEntity (String name) throws SAXException
    {}
  
    public void startCDATA () throws SAXException
    {}
  
    public void endCDATA () throws SAXException
    {}
  
    public void comment (char ch[], int start, int length) throws SAXException
    {}
  }
}