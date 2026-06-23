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

import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:path.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncPath extends FunctionDef1Arg
{
	
	private static final long serialVersionUID = 8461734974298609819L;

	/**
	 * Class constructor.
	 */
	public FuncPath() {
		m_defined_arity = new Short[] { 1 };
	}

	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                         An XPath context object
	 * @return                              A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		
		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		final int contextNode = xctxt.getContextNode();
		
		int nodeHandle = DTM.NULL;
		
        if (m_arg0 == null) {
        	XObject xObj0 = xctxt.getXPath3ContextItem();
        	
        	if (xObj0 != null) {
        		if (xObj0 instanceof ResultSequence) {
        			ResultSequence rSeq = (ResultSequence)xObj0;        	  
        			if (rSeq.size() == 0) {
        				result = new ResultSequence();

        				return result; 
        			}
        			else if (rSeq.size() == 1) {
        				XObject xObj = rSeq.item(0);

        				if (xObj instanceof XMLNodeCursorImpl) {
        					XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
        					if (xmlNodeCursorImpl.getLength() == 0) {
        						result = new ResultSequence();

        						return result; 
        					}
        					else {
        						nodeHandle = xmlNodeCursorImpl.asNode(xctxt);  
        					}        					
        				}
        				else {
        					throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'path' "
																		        							+ "argument is not an xdm node "
																		        							+ "reference.", srcLocator);
        				}
        			}
        			else {
        				throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'path' "
																			        						+ "argument is not an xdm node "
																			        						+ "reference.", srcLocator);  
        			}
                }
        		else if (xObj0 instanceof XMLNodeCursorImpl) {
        			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj0;
        			if (xmlNodeCursorImpl.getLength() == 0) {
        				result = new ResultSequence();

        				return result; 
        			}
        			else {
        				nodeHandle = xmlNodeCursorImpl.asNode(xctxt);  
        			}        			
        		}
        		else {
        			throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'path' "
																			                              + "argument is not an xdm node "
																			                              + "reference.", srcLocator);
        		}
        	}
        	else if (contextNode != DTM.NULL) {
        		nodeHandle = contextNode;  
        	}
        	else {
        		result = new ResultSequence();

        		return result;
        	}
        }
        else {
           XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
           
           if (xObj0 instanceof ResultSequence) {
        	  ResultSequence rSeq = (ResultSequence)xObj0;        	  
        	  if (rSeq.size() == 0) {
        		  result = new ResultSequence();

        		  return result; 
        	  }
        	  else if (rSeq.size() == 1) {
                  XObject xObj = rSeq.item(0);
                  
                  if (xObj instanceof XMLNodeCursorImpl) {
                	  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
                	  if (xmlNodeCursorImpl.getLength() == 0) {
                		  result = new ResultSequence();

                		  return result; 
                	  }
                	  else {
                		  nodeHandle = xmlNodeCursorImpl.asNode(xctxt);  
                	  }                	  
                  }
                  else {
                	  throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'path' "
																		                              + "argument is not an xdm node "
																		                              + "reference.", srcLocator);
                  }
        	  }
        	  else {
        		  throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'path' "
																			                          + "argument is not an xdm node "
																			                          + "reference.", srcLocator);  
        	  }
           }           
           else if (xObj0 instanceof XMLNodeCursorImpl) {
    		   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj0;
    		   if (xmlNodeCursorImpl.getLength() == 0) {
    			   result = new ResultSequence();

    			   return result; 
    		   }
    		   else {
    			   nodeHandle = xmlNodeCursorImpl.asNode(xctxt);  
    		   }    		   
    	   }
           else {
        	   throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'path' "
																			                         + "argument is not an xdm node "
																			                         + "reference.", srcLocator);
           }
        }
        
        if (nodeHandle != DTM.NULL) {
        	String fnPathResultStr = "";
        	
        	int nodeHandle1 = nodeHandle;
        	DTM dtm = xctxt.getDTM(nodeHandle); 

        	short nodeType = dtm.getNodeType(nodeHandle1);
        	if (nodeType == DTM.DOCUMENT_NODE) {
        	    fnPathResultStr = "/";
        	}
        	else if (nodeType == DTM.ELEMENT_NODE) {        		        		
        		fnPathResultStr = getFnPathStrElemNode(nodeHandle1, dtm);
        	}
        	else if (nodeType == DTM.ATTRIBUTE_NODE) {
        		String localName = dtm.getLocalName(nodeHandle1);        		
        		String nsUri = dtm.getNamespaceURI(nodeHandle1);
        		String attrNsQualName = null; 
        		if (nsUri != null) {
        			attrNsQualName = "@Q{" + nsUri + "}" + localName;  
        		}
        		else {
        			attrNsQualName = "@" + localName; 
        		}
        		
        		nodeHandle1 = dtm.getParent(nodeHandle1);
        		
        		fnPathResultStr = getFnPathStrElemNode(nodeHandle1, dtm);
        		
        		fnPathResultStr = fnPathResultStr + "/" + attrNsQualName; 
        	}
        	else if (nodeType == DTM.TEXT_NODE) {
        		int textNodeHandle = nodeHandle1;
        		
        		int prevSibling = dtm.getPreviousSibling(textNodeHandle);
    			int prevSiblingCount = 0;

    			while (prevSibling != DTM.NULL) {
    				if (dtm.getNodeType(prevSibling) == DTM.TEXT_NODE) {
    					prevSiblingCount++;
    				}

    				prevSibling = dtm.getPreviousSibling(prevSibling);
    			}
        		        		
        		nodeHandle1 = dtm.getParent(textNodeHandle);
        		
        		short nodeTypeParent = dtm.getNodeType(nodeHandle1);
        		if (nodeTypeParent == DTM.ELEMENT_NODE) {
        		   fnPathResultStr = getFnPathStrElemNode(nodeHandle1, dtm);
        		   if (prevSiblingCount > 0) {
        			  fnPathResultStr = fnPathResultStr + "/text()" + "[" + (prevSiblingCount + 1) + "]";  
        		   }
        		   else {
        			  fnPathResultStr = fnPathResultStr + "/text()[1]";
        		   }
        		}
        		else if (nodeTypeParent == DTM.DOCUMENT_NODE) {
        			if (prevSiblingCount > 0) {
        				fnPathResultStr = "/text()" + "[" + (prevSiblingCount + 1) + "]";  
        			}
        			else {
        				fnPathResultStr = "/text()[1]";
        			}
        		}
        	}
            else if (nodeType == DTM.COMMENT_NODE) {
                int commentNodeHandle = nodeHandle1;
        		
        		int prevSibling = dtm.getPreviousSibling(commentNodeHandle);
    			int prevSiblingCount = 0;

    			while (prevSibling != DTM.NULL) {
    				if (dtm.getNodeType(prevSibling) == DTM.TEXT_NODE) {
    					prevSiblingCount++;
    				}

    				prevSibling = dtm.getPreviousSibling(prevSibling);
    			}
        		        		
        		nodeHandle1 = dtm.getParent(commentNodeHandle);
        		
        		short nodeTypeParent = dtm.getNodeType(nodeHandle1);
        		if (nodeTypeParent == DTM.ELEMENT_NODE) {
        		   fnPathResultStr = getFnPathStrElemNode(nodeHandle1, dtm);
        		   if (prevSiblingCount > 0) {
        			  fnPathResultStr = fnPathResultStr + "/comment()" + "[" + (prevSiblingCount + 1) + "]";  
        		   }
        		   else {
        			  fnPathResultStr = fnPathResultStr + "/comment()[1]";
        		   }
        		}
        		else if (nodeTypeParent == DTM.DOCUMENT_NODE) {
        			if (prevSiblingCount > 0) {
        				fnPathResultStr = "/comment()" + "[" + (prevSiblingCount + 1) + "]";  
        			}
        			else {
        				fnPathResultStr = "/comment()[1]";
        			}
        		}
        	}
            else if (nodeType == DTM.PROCESSING_INSTRUCTION_NODE) {
            	int piNodeHandle = nodeHandle1;
            	
            	String nodeName = dtm.getNodeName(nodeHandle1);
            	
            	int prevSibling = dtm.getPreviousSibling(piNodeHandle);
    			int prevSiblingCount = 0;

    			while (prevSibling != DTM.NULL) {
    				if (dtm.getNodeType(prevSibling) == DTM.PROCESSING_INSTRUCTION_NODE) {    					
    					String nodeName2 = dtm.getNodeName(prevSibling);
    					if (nodeName2.equals(nodeName)) {
    					   prevSiblingCount++;
    					}
    				}

    				prevSibling = dtm.getPreviousSibling(prevSibling);
    			}
    			
    			nodeHandle1 = dtm.getParent(piNodeHandle);
    			
    			short nodeTypeParent = dtm.getNodeType(nodeHandle1);
        		if (nodeTypeParent == DTM.ELEMENT_NODE) {
        		   fnPathResultStr = getFnPathStrElemNode(nodeHandle1, dtm);
        		   if (prevSiblingCount > 0) {
        			  fnPathResultStr = fnPathResultStr + "/processing-instruction(" + nodeName + ")" + "[" + (prevSiblingCount + 1) + "]";  
        		   }
        		   else {
        			  fnPathResultStr = fnPathResultStr + "/processing-instruction(" + nodeName + ")[1]";
        		   }
        		}
        		else if (nodeTypeParent == DTM.DOCUMENT_NODE) {
        			if (prevSiblingCount > 0) {
        				fnPathResultStr = "/processing-instruction(" + nodeName + ")" + "[" + (prevSiblingCount + 1) + "]";  
        			}
        			else {
        				fnPathResultStr = "/processing-instruction(" + nodeName + ")[1]";
        			}
        		}
    			
        	}
            else if (nodeType == DTM.NAMESPACE_NODE) {
                int nsNodeHandle = nodeHandle1;
            	
            	String localName = dtm.getLocalName(nodeHandle1);
            	
            	nodeHandle1 = dtm.getParent(nsNodeHandle);
            	
            	short nodeTypeParent = dtm.getNodeType(nodeHandle1);
            	if (nodeTypeParent == DTM.ELEMENT_NODE) {
         		   fnPathResultStr = getFnPathStrElemNode(nodeHandle1, dtm);
         		   
         		   if ((localName != null) && !"".equals(localName)) {
         		      fnPathResultStr = fnPathResultStr + "/namespace::" + localName;
         		   }
         		   else {
         			  fnPathResultStr = fnPathResultStr + "/namespace::*[Q{http://www.w3.org/2005/xpath-functions}local-name()='']"; 
         		   }
            	}
            	else if (nodeTypeParent == DTM.NULL) {
            	   if ((localName != null) && !"".equals(localName)) {
           		      fnPathResultStr = "/namespace::" + localName;
           		   }
           		   else {
           			  fnPathResultStr = "/namespace::*[Q{http://www.w3.org/2005/xpath-functions}local-name()='']"; 
           		   }
            	}
        	}
        	
        	result = new XSString(fnPathResultStr);
        }
        else {
        	throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'path' "
                                                                                                        + "argument is not a node reference.", srcLocator);
        }

		return result;
	}

	/**
	 * Method definition, to get XPath 3.1 function fn:path
	 * result serialization for an XML element node.
	 * 
	 * @param nodeHandle                The supplied xdm node handle for an
	 *                                  XML element node.
	 * @param dtm                       Xalan-J document table model (xdm) 
	 *                                  object instance, for the supplied xdm node.
	 * @return
	 */
	private String getFnPathStrElemNode(int nodeHandle, DTM dtm) {
		
		String result = "";

		while (nodeHandle != DTM.NULL) {
			String localName = dtm.getLocalName(nodeHandle);
			String name = dtm.getNodeName(nodeHandle); 
			String nsUri = dtm.getNamespaceURI(nodeHandle);					
			int prevSibling = dtm.getPreviousSibling(nodeHandle);
			int prevSiblingCount = 0;

			while (prevSibling != DTM.NULL) {
				if (dtm.getNodeType(prevSibling) == DTM.ELEMENT_NODE) {
					String nsUri2 = dtm.getNamespaceURI(prevSibling);
					String localName2 = dtm.getLocalName(prevSibling);
					boolean nsEqual = false;
					if ((nsUri == null) && (nsUri2 == null)) {
						nsEqual = true;  
					}
					else if ((nsUri != null) && nsUri.equals(nsUri2)) {
						nsEqual = true; 
					}

					if (localName2.equals(localName) && nsEqual) {
						prevSiblingCount++; 
					}
				}

				prevSibling = dtm.getPreviousSibling(prevSibling);
			}

			if ("#document".equals(name)) {
				result = "/" + result;  
			}
			else if ("".equals(result)) {
				String nsQualName = localName;
				if (nsUri != null) {
					nsQualName = "Q{" + nsUri + "}" + nsQualName;  
				}
				else {
					nsQualName = "Q{}" + nsQualName; 
				}

				if (prevSiblingCount > 0) {
					result = nsQualName + "[" + (prevSiblingCount + 1) + "]";
				}
				else {
					result = nsQualName + "[1]";
				}
			}
			else {
				String nsQualName = localName;
				if (nsUri != null) {
					nsQualName = "Q{" + nsUri + "}" + nsQualName;  
				}
				else {
					nsQualName = "Q{}" + nsQualName; 
				}

				if (prevSiblingCount > 0) {
					result = nsQualName + "[" + (prevSiblingCount + 1) + "]" + "/" + result;
				}
				else {
					result = nsQualName + "[1]/" + result;
				}
			}

			nodeHandle = dtm.getParent(nodeHandle);
		}

		return result;
	}
}
