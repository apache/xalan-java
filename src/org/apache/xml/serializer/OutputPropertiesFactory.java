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
package org.apache.xml.serializer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import javax.xml.transform.OutputKeys;

import org.apache.xml.res.XMLErrorResources;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.Constants;
import org.apache.xml.utils.WrappedRuntimeException;

/**
 * This class acts as a factory to generate properties for the given output type
 * ("xml", "text", "html")..
 */
public class OutputPropertiesFactory
{
    //************************************************************
    //*  PUBLIC CONSTANTS
    //************************************************************
    /** Built-in extensions namespace, reexpressed in {namespaceURI} syntax
     * suitable for prepending to a localname to produce a "universal
     * name".
     */
    public static final String S_BUILTIN_EXTENSIONS_UNIVERSAL =
        "{" + Constants.S_BUILTIN_EXTENSIONS_URL + "}";

    // Some special Xalan keys.

    /** The number of whitespaces to indent by, if indent="yes". */
    public static final String S_KEY_INDENT_AMOUNT =
        S_BUILTIN_EXTENSIONS_UNIVERSAL + "indent-amount";

    /**
     * Fully qualified name of class with a default constructor that
     *  implements the ContentHandler interface, where the result tree events
     *  will be sent to.
     */
    public static final String S_KEY_CONTENT_HANDLER =
        S_BUILTIN_EXTENSIONS_UNIVERSAL + "content-handler";

    /** File name of file that specifies character to entity reference mappings. */
    public static final String S_KEY_ENTITIES =
        S_BUILTIN_EXTENSIONS_UNIVERSAL + "entities";

    /** Use a value of "yes" if the href values for HTML serialization should
     *  use %xx escaping. */
    public static final String S_USE_URL_ESCAPING =
        S_BUILTIN_EXTENSIONS_UNIVERSAL + "use-url-escaping";

    /** Use a value of "yes" if the META tag should be omitted where it would
     *  otherwise be supplied.
     */
    public static final String S_OMIT_META_TAG =
        S_BUILTIN_EXTENSIONS_UNIVERSAL + "omit-meta-tag";

    /**
     * The old built-in extension namespace
     */
    public static final String S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL =
        "{" + Constants.S_BUILTIN_OLD_EXTENSIONS_URL + "}";

    /**
     * The length of the old built-in extension namespace
     */
    public static final int S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL_LEN =
        S_BUILTIN_OLD_EXTENSIONS_UNIVERSAL.length();

    //************************************************************
    //*  PRIVATE CONSTANTS
    //************************************************************

    private static final String S_XSLT_PREFIX = "xslt.output.";
    private static final int S_XSLT_PREFIX_LEN = S_XSLT_PREFIX.length();
    private static final String S_XALAN_PREFIX = "org.apache.xslt.";
    private static final int S_XALAN_PREFIX_LEN = S_XALAN_PREFIX.length();

    /** a zero length Class array used in loadPropertiesFile() */
    private static final Class[] NO_CLASSES = new Class[0];
    /** Synchronization object for lazy initialization of the above tables. */
    private static Integer m_synch_object = new Integer(1);
    /** a zero length Object array used in loadPropertiesFile() */
    private static final Object[] NO_OBJS = new Object[0];

    /** the directory in which the various method property files are located */
    private static final String PROP_DIR = "org/apache/xml/serializer/";
    /** property file for default XML properties */
    private static final String PROP_FILE_XML = "output_xml.properties";
    /** property file for default TEXT properties */
    private static final String PROP_FILE_TEXT = "output_text.properties";
    /** property file for default HTML properties */
    private static final String PROP_FILE_HTML = "output_html.properties";
    /** property file for default UNKNOWN (Either XML or HTML, to be determined later) properties */
    private static final String PROP_FILE_UNKNOWN = "output_unknown.properties";

    //************************************************************
    //*  PRIVATE STATIC FIELDS
    //************************************************************

    /** The default properties of all output files. */
    private static Properties m_xml_properties = null;

    /** The default properties when method="html". */
    private static Properties m_html_properties = null;

    /** The default properties when method="text". */
    private static Properties m_text_properties = null;

    /** The properties when method="" for the "unknown" wrapper */
    private static Properties m_unknown_properties = null;

    /**
     * Creates an empty OutputProperties with the defaults specified by
     * a property file.  The method argument is used to construct a string of
     * the form output_[method].properties (for instance, output_html.properties).
     * The output_xml.properties file is always used as the base.
     * <p>At the moment, anything other than 'text', 'xml', and 'html', will
     * use the output_xml.properties file.</p>
     *
     * @param   method non-null reference to method name.
     *
     * @return Properties object that holds the defaults for the given method.
     */
    static public Properties getDefaultMethodProperties(String method)
    {
        String fileName = null;
        Properties defaultProperties = null;
        // According to this article : Double-check locking does not work
        // http://www.javaworld.com/javaworld/jw-02-2001/jw-0209-toolbox.html
        try
        {
            synchronized (m_synch_object)
            {
                if (null == m_xml_properties) // double check
                {
                    fileName = PROP_FILE_XML;
                    m_xml_properties = loadPropertiesFile(fileName, null);
                }
            }

            if (method.equals(Method.XML))
            {
                defaultProperties = m_xml_properties;
            }
            else if (method.equals(Method.HTML))
            {
                if (null == m_html_properties) // double check
                {
                    fileName = PROP_FILE_HTML;
                    m_html_properties =
                        loadPropertiesFile(fileName, m_xml_properties);
                }

                defaultProperties = m_html_properties;
            }
            else if (method.equals(Method.TEXT))
            {
                if (null == m_text_properties) // double check
                {
                    fileName = PROP_FILE_TEXT;
                    m_text_properties =
                        loadPropertiesFile(fileName, m_xml_properties);
                    if (null
                        == m_text_properties.getProperty(OutputKeys.ENCODING))
                    {
                        String mimeEncoding = Encodings.getMimeEncoding(null);
                        m_text_properties.put(
                            OutputKeys.ENCODING,
                            mimeEncoding);
                    }
                }

                defaultProperties = m_text_properties;
            }
            else if (method.equals(org.apache.xml.serializer.Method.UNKNOWN))
            {
                if (null == m_unknown_properties) // double check
                {
                    fileName = PROP_FILE_UNKNOWN;
                    m_unknown_properties =
                        loadPropertiesFile(fileName, m_xml_properties);
                }
                defaultProperties = m_unknown_properties;
            }
            else
            {

                // TODO: Calculate res file from name.
                defaultProperties = m_xml_properties;
            }
        }
        catch (IOException ioe)
        {
            throw new WrappedRuntimeException(
                XMLMessages.createXMLMessage(
                    XMLErrorResources.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
                    new Object[] { fileName, method }),
                ioe);
        }

        return defaultProperties;
    }

    /**
     * Load the properties file from a resource stream.  If a
     * key name such as "org.apache.xslt.xxx", fix up the start of
     * string to be a curly namespace.  If a key name starts with
     * "xslt.output.xxx", clip off "xslt.output.".  If a key name *or* a
     * key value is discovered, check for \u003a in the text, and
     * fix it up to be ":", since earlier versions of the JDK do not
     * handle the escape sequence (at least in key names).
     *
     * @param resourceName non-null reference to resource name.
     * @param defaults Default properties, which may be null.
     */
    static private Properties loadPropertiesFile(
        final String resourceName,
        Properties defaults)
        throws IOException
    {

        // This static method should eventually be moved to a thread-specific class
        // so that we can cache the ContextClassLoader and bottleneck all properties file
        // loading throughout Xalan.

        Properties props = new Properties(defaults);

        InputStream is = null;
        BufferedInputStream bis = null;
        Class accessControllerClass = null;

        try
        {
            try
            {
                try
                {

                    // This Class was introduced in JDK 1.2. With the re-architecture of
                    // security mechanism ( starting in JDK 1.2 ), we have option of
                    // giving privileges to certain part of code using doPrivileged block.
                    // In JDK1.1.X applications won't be having security manager and if
                    // there is security manager ( in applets ), code need to be signed
                    // and trusted for having access to resources.

                    accessControllerClass =
                        Class.forName("java.security.AccessController");

                    // If we are here means user is using JDK >= 1.2.
                    // Using doPrivileged to be able to read property file without opening
                    // up secured container permissions like J2EE container

                    is =
                        (
                            InputStream) java
                                .security
                                .AccessController
                                .doPrivileged(
                                    new java
                                    .security
                                    .PrivilegedAction()
                    {

                        public Object run()
                        {
                            try
                            {
                                java.lang.reflect.Method getCCL =
                                    Thread.class.getMethod(
                                        "getContextClassLoader",
                                        NO_CLASSES);
                                if (getCCL != null)
                                {
                                    ClassLoader contextClassLoader =
                                        (ClassLoader) getCCL.invoke(
                                            Thread.currentThread(),
                                            NO_OBJS);
                                    return (
                                        contextClassLoader.getResourceAsStream(
                                            PROP_DIR + resourceName));
                                }
                            }
                            catch (Exception e)
                            {
                            }

                            return null;

                        }
                    });
                }
                catch (ClassNotFoundException e)
                {
                    //User may be using older JDK ( JDK <1.2 ). Allow him/her to use it.
                    // But don't try to use doPrivileged
                    try
                    {
                        java.lang.reflect.Method getCCL =
                            Thread.class.getMethod(
                                "getContextClassLoader",
                                NO_CLASSES);
                        if (getCCL != null)
                        {
                            ClassLoader contextClassLoader =
                                (ClassLoader) getCCL.invoke(
                                    Thread.currentThread(),
                                    NO_OBJS);
                            is =
                                contextClassLoader.getResourceAsStream(
                                    PROP_DIR + resourceName);
                        }
                    }
                    catch (Exception exception)
                    {
                    }
                }
            }
            catch (Exception e)
            {
            }

            if (is == null)
            {
                if (accessControllerClass != null)
                {
                    is =
                        (
                            InputStream) java
                                .security
                                .AccessController
                                .doPrivileged(
                                    new java
                                    .security
                                    .PrivilegedAction()
                    {
                        public Object run()
                        {
                            return OutputPropertiesFactory
                                .class
                                .getResourceAsStream(
                                resourceName);
                        }
                    });
                }
                else
                {
                    // User may be using older JDK ( JDK < 1.2 )
                    is =
                        OutputPropertiesFactory.class.getResourceAsStream(
                            resourceName);
                }
            }

            bis = new BufferedInputStream(is);
            props.load(bis);
        }
        catch (IOException ioe)
        {
            if (defaults == null)
            {
                throw ioe;
            }
            else
            {
                throw new WrappedRuntimeException(
                    XMLMessages.createXMLMessage(
                        XMLErrorResources.ER_COULD_NOT_LOAD_RESOURCE,
                        new Object[] { resourceName }),
                    ioe);
                //"Could not load '"+resourceName+"' (check CLASSPATH), now using just the defaults ", ioe);
            }
        }
        catch (SecurityException se)
        {
            // Repeat IOException handling for sandbox/applet case -sc
            if (defaults == null)
            {
                throw se;
            }
            else
            {
                throw new WrappedRuntimeException(
                    XMLMessages.createXMLMessage(
                        XMLErrorResources.ER_COULD_NOT_LOAD_RESOURCE,
                        new Object[] { resourceName }),
                    se);
                //"Could not load '"+resourceName+"' (check CLASSPATH, applet security), now using just the defaults ", se);
            }
        }
        finally
        {
            if (bis != null)
            {
                bis.close();
            }
            if (is != null)
            {
                is.close();
            }
        }

        // Note that we're working at the HashTable level here,
        // and not at the Properties level!  This is important
        // because we don't want to modify the default properties.
        // NB: If fixupPropertyString ends up changing the property
        // name or value, we need to remove the old key and re-add
        // with the new key and value.  However, then our Enumeration
        // could lose its place in the HashTable.  So, we first
        // clone the HashTable and enumerate over that since the
        // clone will not change.  When we migrate to Collections,
        // this code should be revisited and cleaned up to use
        // an Iterator which may (or may not) alleviate the need for
        // the clone.  Many thanks to Padraig O'hIceadha
        // <padraig@gradient.ie> for finding this problem.  Bugzilla 2000.

        Enumeration keys = ((Properties) props.clone()).keys();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            // Now check if the given key was specified as a
            // System property. If so, the system property
            // overides the default value in the propery file.
            String value = null;
            try
            {
                value = System.getProperty(key);
            }
            catch (SecurityException se)
            {
                // No-op for sandbox/applet case, leave null -sc
            }
            if (value == null)
                value = (String) props.get(key);

            String newKey = fixupPropertyString(key, true);
            String newValue = null;
            try
            {
                newValue = System.getProperty(newKey);
            }
            catch (SecurityException se)
            {
                // No-op for sandbox/applet case, leave null -sc
            }
            if (newValue == null)
                newValue = fixupPropertyString(value, false);
            else
                newValue = fixupPropertyString(newValue, false);

            if (key != newKey || value != newValue)
            {
                props.remove(key);
                props.put(newKey, newValue);
            }

        }

        return props;
    }

    /**
     * Fix up a string in an output properties file according to
     * the rules of {@link #loadPropertiesFile}.
     *
     * @param s non-null reference to string that may need to be fixed up.
     * @return A new string if fixup occured, otherwise the s argument.
     */
    static private String fixupPropertyString(String s, boolean doClipping)
    {
        int index;
        if (doClipping && s.startsWith(S_XSLT_PREFIX))
        {
            s = s.substring(S_XSLT_PREFIX_LEN);
        }
        if (s.startsWith(S_XALAN_PREFIX))
        {
            s =
                S_BUILTIN_EXTENSIONS_UNIVERSAL
                    + s.substring(S_XALAN_PREFIX_LEN);
        }
        if ((index = s.indexOf("\\u003a")) > 0)
        {
            String temp = s.substring(index + 6);
            s = s.substring(0, index) + ":" + temp;

        }
        return s;
    }

}
