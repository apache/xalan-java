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
// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package org.apache.trax;

/**
 * This is a SAX ContentHandler that may be used to process SAX 
 * events into an Templates objects.  This is an abstract class 
 * instead of an interface, so it can be a ContentHandler object, 
 * for passing into the JAXP SAXParser interface.
 * 
 * <h3>Open issues:</h3>
 * <dl>
 *    <dt><h4>Should Processor derive from org.xml.sax.ContentHandler?</h4></dt>
 *    <dd>Instead of requesting an object from the Processor class, should 
 *        the Processor class simply derive from org.xml.sax.ContentHandler?</dd>
 *    <dt><h4>ContentHandler vs. ContentHandler</h4></dt>
 *    <dd>I don't think I would use ContentHandler at all, except that JAXP uses it.  
 *        Maybe we should go back to using ContentHandler?</dd>
 * </dl>
 */
public interface TemplatesBuilder extends org.xml.sax.ContentHandler
{
  /**
   * When this object is used as a ContentHandler or DocumentHandler, it  
   * creates a Templates object, which the caller can get once 
   * the SAX events have been completed.
   * @return The stylesheet object that was created during 
   * the SAX event process, or null if no stylesheet has 
   * been created.
   *
   * @version Alpha
   * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
   */
  public Templates getTemplates() 
    throws TransformException;
  
  /**
   * Set the base ID (URL or system ID) for the stylesheet 
   * created by this builder.  This must be set in order to 
   * resolve relative URLs in the stylesheet.
   * @param baseID Base URL for this stylesheet.
   */
  public void setBaseID(String baseID);
}
