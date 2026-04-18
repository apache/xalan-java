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
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.StringUtil;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of XPath 3.1 function fn:unparsed-text-available.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncUnparsedTextAvailable extends Function2Args {
  
  private static final long serialVersionUID = -1511121613068142606L;
  
  /**
   * The function fn:unparsed-text-available fetches the string value from
   * url, what the functions fn:unparsed-text and fn:unparsed-text-lines
   * also have to do. After a successful call to function fn:unparsed-text-available, 
   * the file content result are cached within memory that can improve performance of 
   * further calls to functions fn:unparsed-text or/and fn:unparsed-text-lines for the 
   * same url, within the same XSL stylesheet.
   * 
   * XSLT 3.0 specification suggests implementations to possibly do this.
   */
  public static Map<String, String> CACHE_RESULT_MAP = null;

  /**
   * Class constructor.
   */
  public FuncUnparsedTextAvailable() {
	  m_defined_arity = new Short[] { 1, 2 };  
  }

  /**
   * Evaluate the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context
   * @return A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {      
      XObject result = null;
      
      SourceLocator srcLocator = xctxt.getSAXLocator();
      
      XObject arg0Result = m_arg0.execute(xctxt);
      
      if ((arg0Result instanceof ResultSequence) && (((ResultSequence)arg0Result).size() == 0)) {
    	 // fn:unparsed-text-available function call's first argument is an empty sequence     	  
    	 return new XSBoolean(false); 
      }
        
      String hrefStrVal = XslTransformEvaluationHelper.getStrVal(arg0Result);
      
      String encodingStr = null;
        
      if (m_arg1 != null) {
         XObject arg1Result = m_arg1.execute(xctxt);
         encodingStr = XslTransformEvaluationHelper.getStrVal(arg1Result);
         if (!("utf-8".equalsIgnoreCase(encodingStr) || "utf-16".equalsIgnoreCase(encodingStr))) {
        	return new XSBoolean(false);    
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
              
          String urlStrContents = StringUtil.getStringContentFromUrl(resolvedArg0Url);

          String resultStr = null;
          if (encodingStr != null) {
        	  resultStr = new String(urlStrContents.getBytes(), encodingStr.toUpperCase());              
          }
          else {
        	  resultStr = urlStrContents;  
          }
          
          if (resultStr != null) {
        	  result = new XSBoolean(true);
        	  
        	  if (CACHE_RESULT_MAP == null) {
        		 CACHE_RESULT_MAP = new HashMap<String, String>(); 
        	  }
        	  
        	  CACHE_RESULT_MAP.put(resolvedArg0Url.toString(), resultStr);
          }
          else {
        	  result = new XSBoolean(false);
          }
      }
      catch (URISyntaxException ex) {
    	  return new XSBoolean(false);  
      }
      catch (MalformedURLException ex) {
    	  return new XSBoolean(false);
      }
      catch (IOException ex) {
    	  return new XSBoolean(false);
      }
      catch (Exception ex) {
    	  return new XSBoolean(false);
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
  
  public static String getCachedResult(String resolvedUrlStr) {
	  return CACHE_RESULT_MAP.get(resolvedUrlStr);
  }
  
}
