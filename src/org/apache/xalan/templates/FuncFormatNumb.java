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
package org.apache.xalan.templates;

import java.util.Vector;

import org.apache.xalan.utils.QName;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.XPath;
import org.apache.xpath.Expression;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import org.apache.xalan.utils.SAXSourceLocator;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the FormatNumber() function.
 */
public class FuncFormatNumb extends Function3Args
{

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws org.xml.sax.SAXException
   */
  public XObject execute(XPathContext xctxt) throws org.xml.sax.SAXException
  {

    // A bit of an ugly hack to get our context.
    ElemTemplateElement templElem =
      (ElemTemplateElement) xctxt.getNamespaceContext();
    StylesheetComposed ss = templElem.getStylesheetComposed();
    java.text.DecimalFormat formatter = null;
    java.text.DecimalFormatSymbols dfs = null;
    double num = getArg0().execute(xctxt).num();
    String patternStr = getArg1().execute(xctxt).str();

    // TODO: what should be the behavior here??
    if (patternStr.indexOf(0x00A4) > 0)
      ss.error(XSLTErrorResources.ER_CURRENCY_SIGN_ILLEGAL);  // currency sign not allowed

    // this third argument is not a locale name. It is the name of a
    // decimal-format declared in the stylesheet!(xsl:decimal-format
    try
    {
      Expression arg2Expr = getArg2();

      if (null != arg2Expr)
      {
        String dfName = arg2Expr.execute(xctxt).str();
        QName qname = new QName(dfName, xctxt.getNamespaceContext());

        dfs = ss.getDecimalFormatComposed(qname);

        if (null == dfs)
        {
          warn(xctxt, XSLTErrorResources.WG_NO_DECIMALFORMAT_DECLARATION,
               new Object[]{ dfName });  //"not found!!!

          //formatter = new java.text.DecimalFormat(patternStr);
        }
        else
        {

          //formatter = new java.text.DecimalFormat(patternStr, dfs);
          formatter = new java.text.DecimalFormat();

          formatter.setDecimalFormatSymbols(dfs);
          formatter.applyLocalizedPattern(patternStr);
        }
      }

      //else
      if (null == formatter)
      {

        // look for a possible default decimal-format
        if (ss.getDecimalFormatCount() > 0)
          dfs = ss.getDecimalFormatComposed(new QName(""));

        if (dfs != null)
        {
          formatter = new java.text.DecimalFormat();

          formatter.setDecimalFormatSymbols(dfs);
          formatter.applyLocalizedPattern(patternStr);
        }
        else
        {
          dfs = new java.text.DecimalFormatSymbols();

          dfs.setInfinity(Constants.ATTRVAL_INFINITY);
          dfs.setNaN(Constants.ATTRVAL_NAN);

          formatter = new java.text.DecimalFormat();

          formatter.setDecimalFormatSymbols(dfs);

          if (null != patternStr)
            formatter.applyLocalizedPattern(patternStr);
        }
      }

      return new XString(formatter.format(num));
    }
    catch (Exception iae)
    {
      templElem.error(XSLTErrorResources.ER_MALFORMED_FORMAT_STRING,
                      new Object[]{ patternStr });

      return XString.EMPTYSTRING;

      //throw new XSLProcessorException(iae);
    }
  }

  /**
   * Warn the user of a problem.
   *
   * NEEDSDOC @param xctxt
   * NEEDSDOC @param msg
   * NEEDSDOC @param args
   * @exception XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws org.xml.sax.SAXException
   */
  public void warn(XPathContext xctxt, int msg, Object args[])
          throws org.xml.sax.SAXException
  {

    String formattedMsg = XSLMessages.createWarning(msg, args);
    ErrorHandler errHandler = xctxt.getPrimaryReader().getErrorHandler();

    errHandler.warning(new SAXParseException(formattedMsg,
                                             (SAXSourceLocator)xctxt.getSAXLocator()));
  }

  /**
   * NEEDSDOC Method checkNumberArgs 
   *
   *
   * NEEDSDOC @param argNum
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
    if ((argNum > 3) || (argNum < 2))
      throw new WrongNumberArgsException("3");
  }
}
