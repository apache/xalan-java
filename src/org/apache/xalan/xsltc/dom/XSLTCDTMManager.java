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
package org.apache.xalan.xsltc.dom;

import org.apache.xml.dtm.*;

import java.util.Vector;

// JAXP 1.1
import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;

// Apache XML Utilities
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.dtm.ref.dom2dtm.DOM2DTM;
import org.apache.xml.dtm.ref.sax2dtm.SAX2DTM;
import org.apache.xml.dtm.ref.*;

// W3C DOM
import org.w3c.dom.Document;
import org.w3c.dom.Node;

// SAX2
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;

/**
 * The default implementation for the DTMManager.
 */
public class XSLTCDTMManager extends DTMManagerDefault
{
	
	/** The default class name to use as the manager. */
  private static String defaultClassName =
    "org.apache.xalan.xsltc.dom.XSLTCDTMManager";

  /** Set this to true if you want a dump of the DTM after creation */
  private static final boolean DUMPTREE = false;
  
  /** Set this to true if you want basic diagnostics */
  private static final boolean DEBUG = false;


  /**
   * Constructor DTMManagerDefault
   *
   */
  public XSLTCDTMManager()
  {
    super();
  } 
  
  /**
   * Obtain a new instance of a <code>DTMManager</code>.
   * This static method creates a new factory instance
   * This method uses the following ordered lookup procedure to determine
   * the <code>DTMManager</code> implementation class to
   * load:
   * <ul>
   * <li>
   * Use the <code>org.apache.xml.dtm.DTMManager</code> system
   * property.
   * </li>
   * <li>
   * Use the JAVA_HOME(the parent directory where jdk is
   * installed)/lib/jaxp.properties for a property file that contains the
   * name of the implementation class keyed on the same value as the
   * system property defined above.
   * </li>
   * <li>
   * Use the Services API (as detailed in the JAR specification), if
   * available, to determine the classname. The Services API will look
   * for a classname in the file
   * <code>META-INF/services/javax.xml.parsers.DTMManager</code>
   * in jars available to the runtime.
   * </li>
   * <li>
   * Use the default <code>DTMManager</code> classname, which is
   * <code>org.apache.xml.dtm.ref.DTMManagerDefault</code>.
   * </li>
   * </ul>
   *
   * Once an application has obtained a reference to a <code>
   * DTMManager</code> it can use the factory to configure
   * and obtain parser instances.
   *
   * @return new DTMManager instance, never null.
   *
   * @throws DTMConfigurationException
   * if the implementation is not available or cannot be instantiated.
   */
  public static DTMManager newInstance(XMLStringFactory xsf) 
           throws DTMConfigurationException
  {

    String classname =  defaultClassName;

    if (classname == null)
    {
      throw new DTMConfigurationException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_DEFAULT_IMPL, null)); //"No default implementation found");
    }

    DTMManager factoryImpl;

    try
    {
      Class clazz = Class.forName(classname);

      factoryImpl = (DTMManager) clazz.newInstance();
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new DTMConfigurationException(cnfe);
    }
    catch (IllegalAccessException iae)
    {
      throw new DTMConfigurationException(iae);
    }
    catch (InstantiationException ie)
    {
      throw new DTMConfigurationException(ie);
    }
    factoryImpl.setXMLStringFactory(xsf);

    return factoryImpl;
  } 

  /**
   * Get an instance of a DTM, loaded with the content from the
   * specified source.  If the unique flag is true, a new instance will
   * always be returned.  Otherwise it is up to the DTMManager to return a
   * new instance or an instance that it already created and may be being used
   * by someone else.
   * (I think more parameters will need to be added for error handling, and
   * entity resolution).
   *
   * @param source the specification of the source object.
   * @param unique true if the returned DTM must be unique, probably because it
   * is going to be mutated.
   * @param whiteSpaceFilter Enables filtering of whitespace nodes, and may
   *                         be null.
   * @param incremental true if the DTM should be built incrementally, if
   *                    possible.
   * @param doIndexing true if the caller considers it worth it to use
   *                   indexing schemes.
   *
   * @return a non-null DTM reference.
   */
  public DTM getDTM(Source source, boolean unique,
                    DTMWSFilter whiteSpaceFilter, boolean incremental,
                    boolean doIndexing)
  {
    return getDTM(source, unique, whiteSpaceFilter, incremental, doIndexing,
                  false, 0);
  }
  
  /**
   * Get an instance of a DTM, loaded with the content from the
   * specified source.  If the unique flag is true, a new instance will
   * always be returned.  Otherwise it is up to the DTMManager to return a
   * new instance or an instance that it already created and may be being used
   * by someone else.
   * (I think more parameters will need to be added for error handling, and
   * entity resolution).
   *
   * @param source the specification of the source object.
   * @param unique true if the returned DTM must be unique, probably because it
   * is going to be mutated.
   * @param whiteSpaceFilter Enables filtering of whitespace nodes, and may
   *                         be null.
   * @param incremental true if the DTM should be built incrementally, if
   *                    possible.
   * @param doIndexing true if the caller considers it worth it to use
   *                   indexing schemes.
   * @param hasUserReader true if <code>source</code> is a
   *                      <code>SAXSource</code> object that has an
   *                      <code>XMLReader</code>, that was specified by the
   *                      user.
   *
   * @return a non-null DTM reference.
   */
  public DTM getDTM(Source source, boolean unique,
                    DTMWSFilter whiteSpaceFilter, boolean incremental,
                    boolean doIndexing, boolean hasUserReader)
  {
    return getDTM(source, unique, whiteSpaceFilter, incremental, doIndexing,
                  hasUserReader, 0);
  }

  /**
   * Get an instance of a DTM, loaded with the content from the
   * specified source.  If the unique flag is true, a new instance will
   * always be returned.  Otherwise it is up to the DTMManager to return a
   * new instance or an instance that it already created and may be being used
   * by someone else.
   * (I think more parameters will need to be added for error handling, and
   * entity resolution).
   *
   * @param source the specification of the source object.
   * @param unique true if the returned DTM must be unique, probably because it
   * is going to be mutated.
   * @param whiteSpaceFilter Enables filtering of whitespace nodes, and may
   *                         be null.
   * @param incremental true if the DTM should be built incrementally, if
   *                    possible.
   * @param doIndexing true if the caller considers it worth it to use
   *                   indexing schemes.
   * @param hasUserReader true if <code>source</code> is a
   *                      <code>SAXSource</code> object that has an
   *                      <code>XMLReader</code>, that was specified by the
   *                      user.
   * @param size  Specifies initial size of tables that represent the DTM
   *
   * @return a non-null DTM reference.
   */
  public DTM getDTM(Source source, boolean unique,
                    DTMWSFilter whiteSpaceFilter, boolean incremental,
                    boolean doIndexing, boolean hasUserReader, int size)
  {

    if(DEBUG && null != source)
      System.out.println("Starting "+
			 (unique ? "UNIQUE" : "shared")+
			 " source: "+source.getSystemId()
			 );

    XMLStringFactory xstringFactory = m_xsf;
    int dtmPos = getFirstFreeDTMID();
    int documentID = dtmPos << IDENT_DTM_NODE_BITS;

    if ((null != source) && source instanceof DOMSource)
    {
      DOMImpl dtm;

      if (size <= 0) {
        dtm = new DOMImpl(this, (DOMSource) source, documentID,
                          whiteSpaceFilter, xstringFactory, doIndexing);
      } else {
        dtm = new DOMImpl(this, (DOMSource) source, documentID,
                          whiteSpaceFilter, xstringFactory, doIndexing, size);
      }

      addDTM(dtm, dtmPos);

      dtm.createMappings();

//      if (DUMPTREE)
//      {
//        dtm.dumpDTM();
//      }

      return dtm;
    }
    else
    {
      boolean isSAXSource = (null != source)
                            ? (source instanceof SAXSource) : true;
      boolean isStreamSource = (null != source)
                               ? (source instanceof StreamSource) : false;

      if (isSAXSource || isStreamSource)
      {
        XMLReader reader;
        InputSource xmlSource;

        if (null == source)
        {
          xmlSource = null;
          reader = null;
          hasUserReader = false;  // Make sure the user didn't lie
        }
        else
        {
          reader = getXMLReader(source);
          xmlSource = SAXSource.sourceToInputSource(source);

          String urlOfSource = xmlSource.getSystemId();

          if (null != urlOfSource)
          {
            try
            {
              urlOfSource = SystemIDResolver.getAbsoluteURI(urlOfSource);
            }
            catch (Exception e)
            {

              // %REVIEW% Is there a better way to send a warning?
              System.err.println("Can not absolutize URL: " + urlOfSource);
            }

            xmlSource.setSystemId(urlOfSource);
          }
        }

        // Create the basic SAX2DTM.
        SAXImpl dtm;
        if (size <= 0) {
          dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter,
                            xstringFactory, doIndexing);
        } else {
          dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter,
                            xstringFactory, doIndexing, size);
        }

        // Go ahead and add the DTM to the lookup table.  This needs to be
        // done before any parsing occurs. Note offset 0, since we've just
        // created a new DTM.
        addDTM(dtm, dtmPos, 0);

        boolean haveXercesParser =
          (null != reader)
          && (reader.getClass().getName().equals("org.apache.xerces.parsers.SAXParser") );
	
        if (haveXercesParser)
          incremental = true;  // No matter what.  %REVIEW%

        // If the reader is null, but they still requested an incremental build,
        // then we still want to set up the IncrementalSAXSource stuff.
        if (this.m_incremental && incremental)
        {
          IncrementalSAXSource coParser=null;

          if (haveXercesParser)
          {
            // IncrementalSAXSource_Xerces to avoid threading.
            // System.out.println("Using IncrementalSAXSource_Xerces to avoid threading");
            try {
              // should be ok, it's in the same package - no need for thread class loader,
              // AND theoretically no need for reflection...
              // Class c=Class.forName( "org.apache.xml.dtm.ref.IncrementalSAXSource_Xerces" );
              // coParser=(IncrementalSAXSource)c.newInstance();
              coParser=org.apache.xml.dtm.ref.IncrementalSAXSource_Xerces
                                             .createIncrementalSAXSource();
            }  catch( Exception ex ) {
              ex.printStackTrace();
              coParser=null;
            }
          }

          if( coParser==null ) {
            // Create a IncrementalSAXSource that will run on the secondary thread.
            if (null == reader)
              coParser = new IncrementalSAXSource_Filter();
            else
	    {
	      IncrementalSAXSource_Filter filter=new IncrementalSAXSource_Filter();
	      filter.setXMLReader(reader);
	      coParser=filter;
	    }

          }

          // Have the DTM set itself up as the IncrementalSAXSource's listener.
          dtm.setIncrementalSAXSource(coParser);

          if (null == xmlSource)
          {

            // Then the user will construct it themselves.
            return dtm;
          }

          if(!hasUserReader || null == reader.getDTDHandler()) {
            reader.setDTDHandler(dtm);
          }
          if(!hasUserReader || null == reader.getErrorHandler()) {
            reader.setErrorHandler(dtm);
          }

          try
          {

	    // Launch parsing coroutine.  Launches a second thread,
	    // if we're using IncrementalSAXSource.filter().
            coParser.startParse(xmlSource);
          }
          catch (RuntimeException re)
          {

            dtm.clearCoRoutine();

            throw re;
          }
          catch (Exception e)
          {

            dtm.clearCoRoutine();

            throw new org.apache.xml.utils.WrappedRuntimeException(e);
          }
        }
        else
        {
          if (null == reader)
          {

            // Then the user will construct it themselves.
            return dtm;
          }

          // not incremental
          reader.setContentHandler(dtm.getBuilder());
          if(!hasUserReader || null == reader.getDTDHandler())
            reader.setDTDHandler(dtm);
          if(!hasUserReader || null == reader.getErrorHandler())
            reader.setErrorHandler(dtm);

          try
          {
            reader.setProperty(
              "http://xml.org/sax/properties/lexical-handler", dtm);
          }
          catch (SAXNotRecognizedException e){}
          catch (SAXNotSupportedException e){}

          try
          {
            reader.parse(xmlSource);
          }
          catch (RuntimeException re)
          {

            dtm.clearCoRoutine();

            throw re;
          }
          catch (Exception e)
          {

            dtm.clearCoRoutine();

            throw new org.apache.xml.utils.WrappedRuntimeException(e);
          }
        }

        if (DUMPTREE)
        {
          System.out.println("Dumping SAX2DOM");
          dtm.dumpDTM(System.err);
        }

        return dtm;
      }
      else
      {

        // It should have been handled by a derived class or the caller
        // made a mistake.
        throw new DTMException(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUPPORTED, new Object[]{source})); //"Not supported: " + source);
      }
    }
  }  

}
