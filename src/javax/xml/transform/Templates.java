/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
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
 * 4. The name "Apache Software Foundation" must not be used to endorse or
 *    promote products derived from this software without prior written
 *    permission. For written permission, please contact apache@apache.org.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package javax.xml.transform;

import java.util.Properties;

import javax.xml.transform.TransformerException;


/**
 * An object that implements this interface is the runtime representation of processed
 * transformation instructions.
 *
 * <p>Templates must be threadsafe for a given instance
 * over multiple threads running concurrently, and may
 * be used multiple times in a given session.</p>
 */
public interface Templates {

    /**
     * Create a new transformation context for this Templates object.
     *
     * @return A valid non-null instance of a Transformer.
     *
     * @throws TransformerConfigurationException if a Transformer can not be created.
     */
    Transformer newTransformer() throws TransformerConfigurationException;

    /**
     * Get the static properties for xsl:output.  The object returned will
     * be a clone of the internal values. Accordingly, it can be mutated
     * without mutating the Templates object, and then handed in to
     * {@link javax.xml.transform.Transformer#setOutputProperties}.
     *
     * <p>The properties returned should contain properties set by the stylesheet,
     * and these properties are "defaulted" by default properties specified by
     * <a href="http://www.w3.org/TR/xslt#output">section 16 of the
     * XSL Transformations (XSLT) W3C Recommendation</a>.  The properties that
     * were specifically set by the stylesheet should be in the base
     * Properties list, while the XSLT default properties that were not
     * specifically set should be in the "default" Properties list.  Thus,
     * getOutputProperties().getProperty(String key) will obtain any
     * property in that was set by the stylesheet, <em>or</em> the default
     * properties, while
     * getOutputProperties().get(String key) will only retrieve properties
     * that were explicitly set in the stylesheet.</p>
     *
     * <p>For XSLT,
     * <a href="http://www.w3.org/TR/xslt#attribute-value-templates">Attribute
     * Value Templates</a> attribute values will
     * be returned unexpanded (since there is no context at this point).  The
     * namespace prefixes inside Attribute Value Templates will be unexpanded,
     * so that they remain valid XPath values.  (For XSLT 1.0, this is not
     * a problem since Attribute Value Templates are not allowed for xsl:output
     * attributes.  However, the will be allowed in versions after 1.1.)</p>
     *
     * @return A Properties object, never null.
     */
    Properties getOutputProperties();
}
