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
package javax.xml.transform.sax;

import javax.xml.transform.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.XMLFilter;

/**
 * Interface SAXTransformerFactory
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public abstract class SAXTransformerFactory extends TransformerFactory
{

  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result, based on the transformation
   * instructions specified by the argument.
   *
   * @param src The source of the transformation instructions.
   *
   *
   * @return TransformerHandler ready to transform SAX events.
   * @throws TransformerConfigurationException
   */
  public abstract TransformerHandler newTransformerHandler(Source src)
    throws TransformerConfigurationException;

  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result.
   *
   * @param src The source of the transformation instructions.
   *
   *
   * NEEDSDOC ($objectName$) @return
   * @throws TransformerConfigurationException
   */
  public abstract TransformerHandler newTransformerHandler()
    throws TransformerConfigurationException;

  /**
   * Get a TemplatesHandler object that can process SAX
   * ContentHandler events into a Templates object.
   *
   * @param src The source of the transformation instructions.
   *
   *
   * NEEDSDOC ($objectName$) @return
   * @throws TransformerConfigurationException
   */
  public abstract TemplatesHandler newTemplatesHandler()
    throws TransformerConfigurationException;

  /**
   * Create an XMLFilter that uses the given source as the
   * transformation instructions.
   *
   * @param src The source of the transformation instructions.
   *
   * @return An XMLFilter object, or null if this feature is not supported.
   */
  public abstract XMLFilter newXMLFilter(Source src);

  /**
   * Get InputSource specification(s) that are associated with the
   * given document specified in the source param,
   * via the xml-stylesheet processing instruction
   * (see http://www.w3.org/TR/xml-stylesheet/), and that matches
   * the given criteria.  Note that it is possible to return several stylesheets
   * that match the criteria, in which case they are applied as if they were
   * a list of imports or cascades.
   *
   * @param source
   * @param media The media attribute to be matched.  May be null, in which
   *              case the prefered templates will be used (i.e. alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   * @returns An array of InputSources that can be passed to processMultiple method.
   *
   * @return A Source object suitable for passing to the TransformerFactory.
   *
   * @throws TransformerConfigurationException
   */
  public abstract Source getAssociatedStylesheet(
    Source source, String media, String title, String charset)
      throws TransformerConfigurationException;
  
  /** NEEDSDOC Field errorHandler          */
  ErrorHandler errorHandler;

  /**
   * Allow an application to register an error event handler.
   *
   * <p>If the application does not register an error handler, all
   * error events reported by the SAX parser will be silently
   * ignored; however, normal processing may not continue.  It is
   * highly recommended that all SAX applications implement an
   * error handler to avoid unexpected bugs.</p>
   *
   * <p>Applications may register a new or different handler in the
   * middle of a parse, and the SAX parser must begin using the new
   * handler immediately.</p>
   *
   * @param handler The error handler.
   * @exception java.lang.NullPointerException If the handler
   *            argument is null.
   * @see #getErrorHandler
   */
  public void setErrorHandler(ErrorHandler handler)
  {

    if (handler == null)
    {
      throw new NullPointerException("Null error handler");
    }

    errorHandler = handler;
  }

  /**
   * Return the current error handler.
   *
   * @return The current error handler, or null if none
   *         has been registered.
   * @see #setErrorHandler
   */
  public ErrorHandler getErrorHandler()
  {
    return errorHandler;
  }

  /** NEEDSDOC Field entityResolver          */
  private EntityResolver entityResolver;

  /**
   * Allow an application to register an entity resolver.
   *
   * <p>If the application does not register an entity resolver,
   * the XMLReader will perform its own default resolution.</p>
   *
   * <p>Applications may register a new or different resolver in the
   * middle of a parse, and the SAX parser must begin using the new
   * resolver immediately.</p>
   *
   * @param resolver The entity resolver.
   * @exception java.lang.NullPointerException If the resolver
   *            argument is null.
   * @see #getEntityResolver
   */
  public void setEntityResolver(EntityResolver resolver)
  {
    entityResolver = resolver;
  }

  /**
   * Return the current entity resolver.
   *
   * @return The current entity resolver, or null if none
   *         has been registered.
   * @see #setEntityResolver
   */
  public EntityResolver getEntityResolver()
  {
    return entityResolver;
  }

}
