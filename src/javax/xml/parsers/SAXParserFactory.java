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


package javax.xml.parsers;

import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import java.util.Properties;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Defines a factory API that enables applications to configure and
 * obtain a SAX based parser to parse XML documents. 
 *
 * @since JAXP 1.0
 * @version 1.0
 * @author Rajiv Mordani
 * @author James Davidson
 */

public abstract class SAXParserFactory {
    /** The default property name according to the JAXP spec */
    private static final String defaultPropName =
        "javax.xml.parsers.SAXParserFactory";

    private boolean validating = false;
    private boolean namespaceAware= false;
    
    protected SAXParserFactory () {
    
    }

    /**
     * Obtain a new instance of a <code>SAXParserFactory</code>. This
     * static method creates a new factory instance 
     * This method uses the following ordered lookup procedure to determine
     * the <code>SAXParserFactory</code> implementation class to
     * load:
     * <ul>
     * <li>
     * Use the <code>javax.xml.parsers.SAXParserFactory</code> system
     * property.
     * </li>
     * <li>
     * Use the JAVA_HOME(the parent directory where jdk is
     * installed)/lib/jaxp.properties for a property file that contains the
     * name of the implementation class keyed on the same value as the
     * system property defined above.
     * </li>
     * <li>
     * Use the Services API (as detailed in teh JAR specification), if
     * available, to determine the classname. The Services API will look
     * for a classname in the file
     * <code>META-INF/services/javax.xml.parsers.SAXParserFactory</code>
     * in jars available to the runtime.
     * </li>
     * <li>
     * Platform default <code>SAXParserFactory</code> instance.
     * </li>
     * </ul>
     *
     * Once an application has obtained a reference to a
     * <code>SAXParserFactory</code> it can use the factory to
     * configure and obtain parser instances.
     *
     * @exception FactoryConfigurationError if the implementation is
     * not available or cannot be instantiated.
     */

    public static SAXParserFactory newInstance() {
        String factoryImplName = findFactory(defaultPropName,
                                             "org.apache.crimson.jaxp.SAXParserFactoryImpl");
        // the default can be removed after services are tested well enough
        
        if (factoryImplName == null) {
            throw new FactoryConfigurationError(
                "No default implementation found");
        }

        SAXParserFactory factoryImpl = null;
        try {
            Class clazz = Class.forName(factoryImplName);
            factoryImpl = (SAXParserFactory)clazz.newInstance();
        } catch  (ClassNotFoundException cnfe) {
            throw new FactoryConfigurationError(cnfe);
        } catch (IllegalAccessException iae) {
            throw new FactoryConfigurationError(iae);
        } catch (InstantiationException ie) {
            throw new FactoryConfigurationError(ie);
        }
        return factoryImpl;
    }
    
    /**
     * Creates a new instance of a SAXParser using the currently
     * configured factory parameters.
     *
     * @exception ParserConfigurationException if a parser cannot
     * be created which satisfies the requested configuration.
     */
    
    public abstract SAXParser newSAXParser()
        throws ParserConfigurationException, SAXException;

    
    /**
     * Specifies that the parser produced by this code will
     * provide support for XML namespaces. By default the value of this is set 
     * to <code>false</code>
     */
    
    public void setNamespaceAware(boolean awareness) 
    {
        this.namespaceAware = awareness;
    }

    /**
     * Specifies that the parser produced by this code will
     * validate documents as they are parsed. By default the value of this is 
     * set to <code>false</code>
     */
    
    public void setValidating(boolean validating) 
    {
        this.validating = validating;
    }

    /**
     * Indicates whether or not the factory is configured to produce
     * parsers which are namespace aware.
     */
    
    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    /**
     * Indicates whether or not the factory is configured to produce
     * parsers which validate the XML content during parse.
     */
    
    public boolean isValidating() {
        return validating;
    }

    /**
     *
     * Sets the particular feature in the underlying implementation of 
     * org.xml.sax.XMLReader.
     * A list of the core features and properties can be found at 
     * <a href="http://www.megginson.com/SAX/Java/features.html"> http://www.megginson.com/SAX/Java/features.html </a>
     *
     * @param name The name of the feature to be set.
     * @param value The value of the feature to be set.
     * @exception SAXNotRecognizedException When the underlying XMLReader does 
     *            not recognize the property name.
     *
     * @exception SAXNotSupportedException When the underlying XMLReader 
     *            recognizes the property name but doesn't support the
     *            property.
     *
     * @see org.xml.sax.XMLReader#setFeature
     */
    public abstract void setFeature(String name, boolean value)
        throws ParserConfigurationException, SAXNotRecognizedException,
                    SAXNotSupportedException;

    /**
     *
     * returns the particular property requested for in the underlying 
     * implementation of org.xml.sax.XMLReader.
     *
     * @param name The name of the property to be retrieved.
     * @return Value of the requested property.
     *
     * @exception SAXNotRecognizedException When the underlying XMLReader does 
     *            not recognize the property name.
     *
     * @exception SAXNotSupportedException When the underlying XMLReader 
     *            recognizes the property name but doesn't support the
     *            property.
     *
     * @see org.xml.sax.XMLReader#getProperty
     */
    public abstract boolean getFeature(String name)
        throws ParserConfigurationException, SAXNotRecognizedException,
                    SAXNotSupportedException;


    // -------------------- private methods --------------------
    // This code is duplicated in all factories.
    // Keep it in sync or move it to a common place 
    // Because it's small probably it's easier to keep it here
    /** Avoid reading all the files when the findFactory
        method is called the second time ( cache the result of
        finding the default impl )
    */
    private static String foundFactory=null;

    /** Temp debug code - this will be removed after we test everything
     */
    private static final boolean debug=
        System.getProperty( "jaxp.debug" ) != null;

    /** Private implementation method - will find the implementation
        class in the specified order.
        @param factoryId   Name of the factory interface
        @param xmlProperties Name of the properties file based on JAVA/lib
        @param defaultFactory Default implementation, if nothing else is found
    */
    private static String findFactory(String factoryId,
                                      String defaultFactory)
    {
        if( foundFactory!=null)
            return foundFactory;
        
        // Use the system property first
        try {
            foundFactory =
                System.getProperty( factoryId );
            if( foundFactory!=null) {
                if( debug ) 
                    System.err.println("JAXP: found system property" +
                                       foundFactory );
                return foundFactory;
            }
            
        }catch (SecurityException se) {
        }

        // try to read from $java.home/lib/jaxp.properties
        try {
            String javah=System.getProperty( "java.home" );
            String configFile = javah + File.separator +
                "lib" + File.separator + "jaxp.properties";
            File f=new File( configFile );
            if( f.exists()) {
                Properties props=new Properties();
                props.load( new FileInputStream(f));
                foundFactory=props.getProperty( factoryId );
                if( debug )
                    System.err.println("JAXP: found java.home property " +
                                       foundFactory );
                if(foundFactory!=null )
                    return foundFactory;
            }
        } catch(Exception ex ) {
            if( debug ) ex.printStackTrace();
        }

        String serviceId = "META-INF/services/" + factoryId;
        // try to find services in CLASSPATH
        try {
            ClassLoader cl=SAXParserFactory.class.getClassLoader();
            InputStream is=null;
            if( cl == null ) {
                is=ClassLoader.getSystemResourceAsStream( serviceId );
            } else {
                is=cl.getResourceAsStream( serviceId );
            }
            
            if( is!=null ) {
                if( debug )
                    System.err.println("JAXP: found  " +
                                       serviceId);
                BufferedReader rd=new BufferedReader( new
                    InputStreamReader(is));
                
                foundFactory=rd.readLine();
                rd.close();

                if( debug )
                    System.err.println("JAXP: loaded from services: " +
                                       foundFactory );
                if( foundFactory != null &&
                    !  "".equals( foundFactory) ) {
                    return foundFactory;
                }
            }
        } catch( Exception ex ) {
            if( debug ) ex.printStackTrace();
        }

        return defaultFactory;
    }
}
