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

import java.lang.String;

import java.io.OutputStream;
import java.io.Writer;


/**
 * An object that implements this interface contains the information
 * needed to build a transformation result tree.
 */
public interface Result {

    /**
     * The name of the processing instruction that is sent if the
     * result tree disables output escaping.
     *
     * <p>Normally, result tree serialization escapes & and < (and
     * possibly other characters) when outputting text nodes.
     * This ensures that the output is well-formed XML. However,
     * it is sometimes convenient to be able to produce output that is
     * almost, but not quite well-formed XML; for example,
     * the output may include ill-formed sections that will
     * be transformed into well-formed XML by a subsequent non-XML aware
     * process. If a processing instruction is sent with this name,
     * serialization should be output without any escaping. </p>
     *
     * <p>Result DOM trees may also have PI_DISABLE_OUTPUT_ESCAPING and
     * PI_ENABLE_OUTPUT_ESCAPING inserted into the tree.</p>
     *
     * @see <a href="http://www.w3.org/TR/xslt#disable-output-escaping">disable-output-escaping in XSLT Specification</a>
     */
    public static final String PI_DISABLE_OUTPUT_ESCAPING =
        "javax.xml.transform.disable-output-escaping";

    /**
     * The name of the processing instruction that is sent
     * if the result tree enables output escaping at some point after having
     * received a PI_DISABLE_OUTPUT_ESCAPING processing instruction.
     *
     * @see <a href="http://www.w3.org/TR/xslt#disable-output-escaping">disable-output-escaping in XSLT Specification</a>
     */
    public static final String PI_ENABLE_OUTPUT_ESCAPING =
        "javax.xml.transform.enable-output-escaping";

    /**
     * Set the system identifier for this Result.
     *
     * <p>If the Result is not to be written to a file, the system identifier is optional.
     * The application may still want to provide one, however, for use in error messages
     * and warnings, or to resolve relative output identifiers.</p>
     *
     * @param systemId The system identifier as a URI string.
     */
    public void setSystemId(String systemId);

    /**
     * Get the system identifier that was set with setSystemId.
     *
     * @return The system identifier that was set with setSystemId,
     * or null if setSystemId was not called.
     */
    public String getSystemId();
}
