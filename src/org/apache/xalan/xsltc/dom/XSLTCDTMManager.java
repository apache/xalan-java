/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.ref.DTMDefaultBase;
import org.apache.xml.dtm.DTMException;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xml.res.XMLErrorResources;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xalan.xsltc.trax.DOM2SAX;

import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

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
     * This static method creates a new factory instance.
     * The current implementation just returns a new XSLTCDTMManager instance.
     *
     * %REVISIT% Do we need the factory lookup mechanism for class loading here?
     * Factory lookup will add a lot of complexity and also has a performance hit.
     * There is currently no need to do it unless it is proved to be useful.
     */
    public static XSLTCDTMManager newInstance()
    {
        XSLTCDTMManager factoryImpl = new XSLTCDTMManager();
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
        return getDTM(source, unique, whiteSpaceFilter, incremental,
		      doIndexing, false, 0, true, false);
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
     * @param buildIdIndex true if the id index table should be built.
     * 
     * @return a non-null DTM reference.
     */
    public DTM getDTM(Source source, boolean unique,
                      DTMWSFilter whiteSpaceFilter, boolean incremental,
                      boolean doIndexing, boolean buildIdIndex)
    {
        return getDTM(source, unique, whiteSpaceFilter, incremental,
		      doIndexing, false, 0, buildIdIndex, false);
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
     * @param buildIdIndex true if the id index table should be built.
     * @param newNameTable true if we want to use a separate ExpandedNameTable
     *                     for this DTM.
     * 
     * @return a non-null DTM reference.
     */
  public DTM getDTM(Source source, boolean unique,
		    DTMWSFilter whiteSpaceFilter, boolean incremental,
		    boolean doIndexing, boolean buildIdIndex,
		    boolean newNameTable)
  {
    return getDTM(source, unique, whiteSpaceFilter, incremental,
		  doIndexing, false, 0, buildIdIndex, newNameTable);
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
     * @param buildIdIndex true if the id index table should be built.
     *
     * @return a non-null DTM reference.
     */
    public DTM getDTM(Source source, boolean unique,
                      DTMWSFilter whiteSpaceFilter, boolean incremental,
                      boolean doIndexing, boolean hasUserReader, int size,
                      boolean buildIdIndex)
    {
      return getDTM(source, unique, whiteSpaceFilter, incremental,
                    doIndexing, hasUserReader, size,
                    buildIdIndex, false);
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
     * @param buildIdIndex true if the id index table should be built.
     * @param newNameTable true if we want to use a separate ExpandedNameTable
     *                     for this DTM.
     *
     * @return a non-null DTM reference.
     */
  public DTM getDTM(Source source, boolean unique,
		    DTMWSFilter whiteSpaceFilter, boolean incremental,
		    boolean doIndexing, boolean hasUserReader, int size,
		    boolean buildIdIndex, boolean newNameTable)
  {
        if(DEBUG && null != source) {
            System.out.println("Starting "+
			 (unique ? "UNIQUE" : "shared")+
			 " source: "+source.getSystemId());
        }

        int dtmPos = getFirstFreeDTMID();
        int documentID = dtmPos << IDENT_DTM_NODE_BITS;

        if ((null != source) && source instanceof DOMSource)
        {
            final DOMSource domsrc = (DOMSource) source;
            final org.w3c.dom.Node node = domsrc.getNode();
            final DOM2SAX dom2sax = new DOM2SAX(node);
      
            SAXImpl dtm;

            if (size <= 0) {
                dtm = new SAXImpl(this, source, documentID,
                                  whiteSpaceFilter, null, doIndexing, 
                                  DTMDefaultBase.DEFAULT_BLOCKSIZE,
                                  buildIdIndex, newNameTable);
            } else {
                dtm = new SAXImpl(this, source, documentID,
                                  whiteSpaceFilter, null, doIndexing, 
                                  size, buildIdIndex, newNameTable);
            }
      
            dtm.setDocumentURI(source.getSystemId());

            addDTM(dtm, dtmPos, 0);
      
            dom2sax.setContentHandler(dtm);
      
            try {
                dom2sax.parse();
            }
            catch (RuntimeException re) {
                throw re;
            }
            catch (Exception e) {
                throw new org.apache.xml.utils.WrappedRuntimeException(e);
            }
      
            return dtm;
        }
        else
        {
            boolean isSAXSource = (null != source)
                                  ? (source instanceof SAXSource) : true;
            boolean isStreamSource = (null != source)
                                  ? (source instanceof StreamSource) : false;

            if (isSAXSource || isStreamSource) {
                XMLReader reader;
                InputSource xmlSource;

                if (null == source) {
                    xmlSource = null;
                    reader = null;
                    hasUserReader = false;  // Make sure the user didn't lie
                }
                else {
                    reader = getXMLReader(source);
                    xmlSource = SAXSource.sourceToInputSource(source);

                    String urlOfSource = xmlSource.getSystemId();

                    if (null != urlOfSource) {
                        try {
                            urlOfSource = SystemIDResolver.getAbsoluteURI(urlOfSource);
                        }
                        catch (Exception e) {
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
			              null, doIndexing, 
			              DTMDefaultBase.DEFAULT_BLOCKSIZE,
			              buildIdIndex, newNameTable);
                } else {
                    dtm = new SAXImpl(this, source, documentID, whiteSpaceFilter,
			    null, doIndexing, size, buildIdIndex, newNameTable);
                }

                // Go ahead and add the DTM to the lookup table.  This needs to be
                // done before any parsing occurs. Note offset 0, since we've just
                // created a new DTM.
                addDTM(dtm, dtmPos, 0);

                if (null == reader) {
                    // Then the user will construct it themselves.
                    return dtm;
                }

                reader.setContentHandler(dtm.getBuilder());
                
                if (!hasUserReader || null == reader.getDTDHandler()) {
                    reader.setDTDHandler(dtm);
                }
                
                if(!hasUserReader || null == reader.getErrorHandler()) {
                    reader.setErrorHandler(dtm);
                }

                try {
                    reader.setProperty("http://xml.org/sax/properties/lexical-handler", dtm);
                }
                catch (SAXNotRecognizedException e){}
                catch (SAXNotSupportedException e){}

                try {
                    reader.parse(xmlSource);
                }
                catch (RuntimeException re) {
                    throw re;
                }
                catch (Exception e) {
                    throw new org.apache.xml.utils.WrappedRuntimeException(e);
                }

                if (DUMPTREE) {
                    System.out.println("Dumping SAX2DOM");
                    dtm.dumpDTM(System.err);
                }

                return dtm;
            }
            else {
                // It should have been handled by a derived class or the caller
                // made a mistake.
                throw new DTMException(XMLMessages.createXMLMessage(XMLErrorResources.ER_NOT_SUPPORTED, new Object[]{source}));
            }
        }
    }
}
