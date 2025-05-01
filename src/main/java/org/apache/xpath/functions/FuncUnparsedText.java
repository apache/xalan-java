/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.functions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Implementation of the unparsed-text() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncUnparsedText extends Function2Args {
  
  private static final long serialVersionUID = 1760543978696171233L;

  /**
   * Execute the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {      
      XObject result = null;
      
      SourceLocator srcLocator = xctxt.getSAXLocator();
      
      XObject arg0Result = m_arg0.execute(xctxt);
        
      String hrefStrVal = XslTransformEvaluationHelper.getStrVal(arg0Result);
      
      String encodingStr = null;
        
      if (m_arg1 != null) {
         XObject arg1Result = m_arg1.execute(xctxt);
         encodingStr = XslTransformEvaluationHelper.getStrVal(arg1Result);
         if (!("utf-8".equalsIgnoreCase(encodingStr) || "utf-16".equalsIgnoreCase(encodingStr))) {
             throw new javax.xml.transform.TransformerException("FOUT1190 : The value of the 'encoding' argument "
                                                                                    + "is not a valid encoding name. Allowed encoding names are "
                                                                                    + "UTF-8 and UTF-16.", srcLocator);    
         }
      }
                                                         
      // If the first argument is a relative uri reference, then 
      // resolve that relative uri with base uri of the stylesheet.
      
      URL resolvedArg0Url = null;

      try {
          URI arg0Uri = new URI(hrefStrVal);
          
          if (arg0Uri.isAbsolute()) {
        	  resolvedArg0Url = new URL(hrefStrVal); 
          }
          else {
        	  String stylesheetSystemId = null;

        	  if (srcLocator != null) {
        		  stylesheetSystemId = srcLocator.getSystemId();
        	  }
        	  else {
        		  ExpressionNode expressionNode = getExpressionOwner();
        		  stylesheetSystemId = expressionNode.getSystemId(); 
        	  }

        	  if (stylesheetSystemId != null) {
        		  URI resolvedUriArg = (new URI(stylesheetSystemId)).resolve(hrefStrVal);
        		  resolvedArg0Url = resolvedUriArg.toURL();
        	  }
        	  else {
        		  resolvedArg0Url = new URL(hrefStrVal);
        	  }
          }
              
          String urlStrContents = XslTransformEvaluationHelper.getStringContentFromUrl(resolvedArg0Url);

          String resultStr = null;
          if (encodingStr != null) {
        	  resultStr = new String(urlStrContents.getBytes(), encodingStr.toUpperCase());              
          }
          else {
        	  resultStr = urlStrContents;  
          }

          result = new XString(resultStr);
      }
      catch (URISyntaxException ex) {
          throw new javax.xml.transform.TransformerException("FODC0005 : The uri '" + hrefStrVal + "' is not a valid absolute uri, "
                                                                                              + "or cannot be resolved to an absolute uri.", srcLocator);  
      }
      catch (MalformedURLException ex) {
          throw new javax.xml.transform.TransformerException("FODC0005 : The uri '" + hrefStrVal + "' is not a valid absolute uri, "
                                                                                              + "or cannot be resolved to an absolute uri.", srcLocator);
      }
      catch (IOException ex) {
          throw new javax.xml.transform.TransformerException("FODC0002 : The data from uri '" + hrefStrVal + "' cannot be "
                                                                                                                     + "retrieved.", srcLocator);
      }
        
      return result;
      
  }
  
  

  /**
   * Check that the number of arguments passed to this function is correct.
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
     if (argNum > 2) {
        reportWrongNumberArgs();
     }
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                              XPATHErrorResources.ER_ONE_OR_TWO, null)); //"1 or 2"
  }
  
}
