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
package javax.xml.transform;

import java.util.Properties;

/**
 * This object represents a Transformer, which can transform a
 * source tree into a result tree.
 *
 * <p>An instance of this class can be obtained from the <code>
 * TransformerFactory.newTransformer</code> method. Once an instance
 * of this class is obtained, XML can be processed from a variety
 * of sources with the output from the transform being written
 * to a variety of sinks.</p>
 * <p>An object of this class can not be used concurrently over
 * multiple threads.</p>
 * <p>A Transformer may be used multiple times.  Parameters and 
 * output properties are preserved across transformations.</p>
 */
public abstract class Transformer
{

  /**
   * Process the source tree to the output result.
   * @param xmlSource  The input for the source tree.
   * @param outputTarget The output source target.
   *
   * @throws TransformerException If an unrecoverable error occurs 
   * during the course of the transformation.
   */
  public abstract void transform(Source xmlSource, Result outputTarget)
    throws TransformerException;

  /**
   * Add a parameter for the transformation.
   * 
   * <p>In order to pass namespaced names, the name can be passed as 
   * a two-part string, with
   * the first part being the URL, the delimiter being the '{' for the start of the
   * URI and '}' signifies the end, with the local name following. If the qname has
   * a null URL, then the String object will only contain the local name. An
   * application can safely check for a non-null URI by testing to see if the first
   * character of the name is a '{' character.</p> 
   * <p>For example, if a URI and local name were obtained from an element
   * defined with &lt;xyz:foo xmlns:xyz="http://xyz.foo.com/yada/baz.html"/&gt;,
   * then the TrAX QName would be "{http://xyz.foo.com/yada/baz.html}foo". Note that
   * no prefix is used.</p>
   *
   * @param name The name of the parameter,
   *             which may have a namespace URI.
   * @param value The value object.  This can be any valid Java object
   * -- it's up to the processor to provide the proper
   * coersion to the object, or simply pass it on for use
   * in extensions.
   */
  public abstract void setParameter(String name, Object value);
    
  /**
   * Get a parameter that was explicitly set with setParameter 
   * or setParameters.
   * 
   * <p>This does not return objects from default values of 
   * parameters, since, among other reasons, these require a 
   * node context to be evaluated, and thus can not be resolved 
   * until the transformation is underway.</p>
   *
   * @return A parameter that has been set with setParameter 
   * or setParameters.
   */
  public abstract Object getParameter(String name);

  /**
   * Set an object that will be used to resolve URIs used in
   * document(), etc.
   * 
   * @param resolver An object that implements the URIResolver interface,
   * or null.
   */
  public abstract void setURIResolver(URIResolver resolver);

  /**
   * Get an object that will be used to resolve URIs used in
   * document(), etc.
   * 
   * @return An object that implements the URIResolver interface,
   * or null.
   */
  public abstract URIResolver getURIResolver();

  /**
   * Set the output properties for the transformation.  These
   * properties will override properties set in the templates
   * with xsl:output.
   *
   * <p>If argument to this function is null, any properties
   * previously set will be removed.</p>
   * 
   * <p>In order to pass namespaced names, the name can be passed as 
   * a two-part string, with
   * the first part being the URL, the delimiter being the '{' for the start of the
   * URI and '}' signifies the end, with the local name following. If the qname has
   * a null URL, then the String object will only contain the local name. An
   * application can safely check for a non-null URI by testing to see if the first
   * character of the name is a '{' character.</p> 
   * <p>For example, if a URI and local name were obtained from an element
   * defined with &lt;xyz:foo xmlns:xyz="http://xyz.foo.com/yada/baz.html"/&gt;,
   * then the TrAX QName would be "{http://xyz.foo.com/yada/baz.html}foo". Note that
   * no prefix is used.</p>
   * 
   * <p>If a given property is not supported, it will be silently ignored.</p>
   *
   * @param oformat A set of output properties that will be
   * used to override any of the same properties in effect
   * for the transformation.
   * 
   * @see javax.xml.transform.OutputKeys
   * @see java.util.Properties
   * 
   * @throws IllegalArgumentException if any of the argument keys are not 
   * recognized and are not namespace qualified.
   */
  public abstract void setOutputProperties(Properties oformat)
    throws IllegalArgumentException;

  /**
   * Get a copy of the output properties for the transformation.
   * 
   * <p>The properties should contain a set of layered properties.  The 
   * first "layer" will contain the properties that were set with 
   * setOutputProperties and setOutputProperty.  Subsequent layers 
   * contain the properties set in the stylesheet and the 
   * default properties for the transformation type.
   * There is no guarantee on how the layers are structured passed the 
   * first layer.  Thus, getOutputProperties().getProperty(String key) will obtain any 
   * property in effect for the stylesheet, while 
   * getOutputProperties().get(String key) will only retrieve properties 
   * that were explicitly set with setOutputProperties and setOutputProperty.</p>
   * 
   * <p>Note that mutation of the Properties object returned will not 
   * effect the properties that the transformation contains.</p>
   *
   * @returns A copy of the set of output properties in effect
   * for the next transformation.
   * 
   * @see javax.xml.transform.OutputKeys
   * @see java.util.Properties
   */
  public abstract Properties getOutputProperties();

  /**
   * Set an output property that will be in effect for the 
   * transformation.
   * 
   * <p>In order to pass namespaced names, the name can be passed as 
   * a two-part string, with
   * the first part being the URL, the delimiter being the '{' for the start of the
   * URI and '}' signifies the end, with the local name following. If the qname has
   * a null URL, then the String object will only contain the local name. An
   * application can safely check for a non-null URI by testing to see if the first
   * character of the name is a '{' character.</p> 
   * <p>For example, if a URI and local name were obtained from an element
   * defined with &lt;xyz:foo xmlns:xyz="http://xyz.foo.com/yada/baz.html"/&gt;,
   * then the TrAX QName would be "{http://xyz.foo.com/yada/baz.html}foo". Note that
   * no prefix is used.</p>
   * 
   * @param name A non-null String that specifies an output 
   * property name, which may be namespace qualified.
   * @param value The non-null string value of the output property.
   *
   * @throws IllegalArgumentException If the property is not supported, and is 
   * not qualified with a namespace.
   * 
   * @see javax.xml.transform.OutputKeys
   */
  public abstract void setOutputProperty(String name, String value)
    throws IllegalArgumentException;

  /**
   * Get an output property that is in effect for the 
   * transformation.  The property specified may be a property 
   * that was set with setOutputProperty, or it may be a 
   * property specified in the stylesheet.
   *
   * @param name A non-null String that specifies an output 
   * property name, which may be namespace qualified.
   *
   * @return The string value of the output property, or null 
   * if no property was found.
   *
   * @throws IllegalArgumentException If the property is not supported.
   * 
   * @see javax.xml.transform.OutputKeys
   */
  public abstract String getOutputProperty(String name)
    throws IllegalArgumentException;
  
  /**
   * Set the error event listener in effect for the transformation.
   *
   * @param listener The new error listener.
   * @throws IllegalArgumentException if 
   */
  public abstract void setErrorListener (ErrorListener listener)
    throws IllegalArgumentException;

  /**
   * Get the error event handler in effect for the transformation.
   *
   * @return The current error handler, which should never be null.
   */
  public abstract ErrorListener getErrorListener ();

}
