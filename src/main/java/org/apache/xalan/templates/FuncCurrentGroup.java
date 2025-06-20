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
package org.apache.xalan.templates;

import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of an XSLT 3.0 function current-group().
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncCurrentGroup extends Function
{

   private static final long serialVersionUID = -9147780878956514961L;

   /**
     * Execute the function. The function must return a valid object.
     * 
     * @param xctxt The current execution context.
     * @return a valid XObject.
     *
     * @throws javax.xml.transform.TransformerException
   */
   public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
   {
       XMLNodeCursorImpl currentGroupNodes = null;
       
       SourceLocator srcLocator = xctxt.getSAXLocator();
       
       TransformerImpl transformer = (TransformerImpl) xctxt.getOwnerObject();                            
       ElemTemplateElement currElemTemplateElement = transformer.getCurrentElement();       
      
       List<Integer> groupNodesDtmHandles = currElemTemplateElement.getGroupNodesDtmHandles();
       while ((groupNodesDtmHandles == null) && (currElemTemplateElement != null)) {
           currElemTemplateElement = currElemTemplateElement.getParentElem();
           if (currElemTemplateElement != null) {
             groupNodesDtmHandles = currElemTemplateElement.getGroupNodesDtmHandles();
           }
       }
       
       if (groupNodesDtmHandles != null) {
          currentGroupNodes = new XMLNodeCursorImpl(groupNodesDtmHandles, xctxt);
       }
       else {
    	  throw new javax.xml.transform.TransformerException("XTDE1061 : The fn:current-group function call couldn't "
    	  		                                                                                     + "determine a group of XDM items.", srcLocator);  
       }
              
       return currentGroupNodes;
   }

   @Override
   public void fixupVariables(Vector vars, int globalsSize) {
       // NO OP    
   }
  
}
