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
 * 
 * <p>An object of this class can not be used concurrently over
 * multiple threads.</p>
 * 
 * <p>A Transformer may be used multiple times.</p>
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 * @since JAXP 1.1
 */
public abstract class Transformer
{

  /**
   * Process the source tree to the output result.
   * @param xmlSource  The input for the source tree.
   * @param outputTarget The output source target.
   *
   * @throws TransformerException
   */
  public abstract void transform(Source xmlSource, Result outputTarget)
    throws TransformerException;

  /**
   * Add a parameter for the transformation.
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
   * @return A parameter that has been set with setParameter 
   * or setParameters,
   * *not* all the xsl:params on the stylesheet (which require 
   * a transformation Source to be evaluated).
   */
  public abstract Object getParameter(String name);

  /**
   * Set a bag of parameters for the transformation. Note that 
   * these will not be additive, they will replace the existing
   * set of parameters.
   *
   * @param name The name of the parameter,
   *             which may have a namespace URI.
   * @param value The value object.  This can be any valid Java object
   * -- it's up to the processor to provide the proper
   * coersion to the object, or simply pass it on for use
   * in extensions.
   */
  public abstract void setParameters(Properties params);

  /**
   * Set an object that will be used to resolve URIs used in
   * document(), etc.
   * 
   * @param resolver An object that implements the URIResolver interface,
   * or null.
   */
  public abstract void setURIResolver(URIResolver resolver);

  /**
   * Set the output properties for the transformation.  These
   * properties will override properties set in the templates
   * with xsl:output.
   *
   * <p>If argument to this function is null, any properties
   * previously set will be removed.</p>
   *
   * @param oformat A set of output properties that will be
   * used to override any of the same properties in effect
   * for the transformation.
   */
  public abstract void setOutputProperties(Properties oformat);

  /**
   * Get a copy of the output properties for the transformation.  These
   * properties will override properties set in the templates
   * with xsl:output.
   * 
   * <p>Note that mutation of the Properties object returned will not 
   * effect the properties that the transformation contains.</p>
   *
   * @returns A copy of the set of output properties in effect
   * for the next transformation.
   */
  public abstract Properties getOutputProperties();

  /**
   * Method setProperty
   *
   * @param name
   * @param value
   *
   * @throws TransformerConfigurationException
   */
  public abstract void setOutputProperty(String name, String value)
    throws TransformerException;

  /**
   * Method getProperty
   *
   * @param name
   *
   * @return
   *
   * @throws TransformerConfigurationException
   */
  public abstract String getOutputProperty(String name)
    throws TransformerException;
}
