/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
/*
 * $Id$
 */
package org.apache.xpath.objects;

import java.util.Iterator;
import java.util.List;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.NodeCursor;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import xml.xpath31.processor.types.XSUntyped;

/**
 * This class represents an XPath 3.1 data model (XDM) ordered list of 
 * nodes, that can be iterated similar to a programming language 
 * cursor. This class, implements an XDM data model's node() abstraction. 
 * An object instance of this class, is capable of being converted to 
 * other data types such as string.

 * In addition to having this class, Xalan-J's XSL 3 implementation 
 * also has a class named ResultSequence that is capable of having
 * an ordered list of heterogeneous XDM items (which can be of XDM 
 * kinds 'node', 'atomic value', 'function item', 'map' or an 'array').
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Joseph Kesselman <jkesselm@apache.org>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3.1 specific changes, to this class)
 * 
 * @xsl.usage general
 */
public class XMLNodeCursorImpl extends NodeCursor
{
	
  static final long serialVersionUID = 1916026368035639667L;
  
  /**
   * Default constructor for derived objects.
   */
  protected XMLNodeCursorImpl() { }
  
  /**
   * Class field to store value of an attribute named 'type', for 
   * validating this xdm node object.
   */
  private QName m_type = null;
  
  /**
   * Class field to store value of an attribute named 'validation', for 
   * validating this xdm node object.
   */
  private String m_validation = null;

  /**
   * Construct a XMLNodeCursorImpl object.
   *
   * @param val Value of the XMLNodeCursorImpl object
   */
  public XMLNodeCursorImpl(DTMCursorIterator val)
  {
  	super();
  	if(val instanceof XMLNodeCursorImpl)
  	{
        final XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl) val;
	    setIter(nodeSet.m_iter);
	    m_dtmMgr = nodeSet.m_dtmMgr;
	    m_last = nodeSet.m_last;
        // First make sure the DTMIterator val has a cache,
        // so if it doesn't have one, make one.
	    if(!nodeSet.hasCache())
	        nodeSet.setShouldCacheNodes(true);
        
        // Get the cache from val and use it ourselves (we share it).
	    setObject(nodeSet.getIteratorCache());
  	}
  	else
    	setIter(val);
  }
  
  /**
   * Construct a XMLNodeCursorImpl object.
   *
   * @param val Value of the XMLNodeCursorImpl object
   */
  public XMLNodeCursorImpl(XMLNodeCursorImpl val)
  {
  	super();
    setIter(val.m_iter);
    m_dtmMgr = val.m_dtmMgr;
    m_last = val.m_last;
    if(!val.hasCache())
    	val.setShouldCacheNodes(true);
    setObject(val.m_obj);
  }


  /**
   * Construct an empty XMLNodeCursorImpl object.  This is used to create a mutable 
   * nodeset to which random nodes may be added.
   */
  public XMLNodeCursorImpl(DTMManager dtmMgr) 
  {
     this(DTM.NULL,dtmMgr);
  }

  /**
   * Construct a XMLNodeCursorImpl object for one node.
   *
   * @param n Node to add to the new XMLNodeCursorImpl object
   */
  public XMLNodeCursorImpl(int n, DTMManager dtmMgr)
  {

    super(new NodeSetDTM(dtmMgr));
    m_dtmMgr = dtmMgr;

    if (DTM.NULL != n)
    {
      ((NodeSetDTM) m_obj).addNode(n);
      m_last = 1;
    }
    else
    	m_last = 0;
  }
  
  /**
   * Construct a XMLNodeCursorImpl object, for multiple nodes.
   *
   * @param nodesDtmList Nodes to add to the new XMLNodeCursorImpl object
   */
  public XMLNodeCursorImpl(List<Integer> nodesDtmList, DTMManager dtmMgr)
  {
      super(new NodeSetDTM(dtmMgr));
      m_dtmMgr = dtmMgr;

      for (Iterator<Integer> iter = nodesDtmList.iterator(); iter.hasNext(); ) {
         int nodeDtmHandle = (iter.next()).intValue();
         ((NodeSetDTM) m_obj).addNode(nodeDtmHandle);
      }
    
      m_last = nodesDtmList.size();    
  }

  /**
   * Tell that this is a CLASS_NODESET.
   *
   * @return type CLASS_NODESET
   */
  public int getType()
  {
    return CLASS_NODESET;
  }

  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return type string "#NODESET"
   */
  public String getTypeString()
  {
    return "#NODESET";
  }

  /**
   * Get numeric value of the string conversion from a single node.
   *
   * @param n Node to convert
   *
   * @return numeric value of the string conversion from a single node.
   */
  public double getNumberFromNode(int n)
  {
    XMLString xstr = m_dtmMgr.getDTM(n).getStringValue(n);
    return xstr.toDouble();
  }

  /**
   * Cast result object to a number.
   *
   * @return numeric value of the string conversion from the 
   * next node in the NodeSetDTM, or NAN if no node was found
   */
  public double num()
  {

    int node = item(0);
    return (node != DTM.NULL) ? getNumberFromNode(node) : Double.NaN;
  }
  
  /**
   * Cast result object to a number, but allow side effects, such as the 
   * incrementing of an iterator.
   *
   * @return numeric value of the string conversion from the 
   * next node in the NodeSetDTM, or NAN if no node was found
   */
  public double numWithSideEffects()
  {
    int node = nextNode();

    return (node != DTM.NULL) ? getNumberFromNode(node) : Double.NaN;
  }


  /**
   * Cast result object to a boolean.
   *
   * @return True if there is a next node in the nodeset
   */
  public boolean bool()
  {
    return (item(0) != DTM.NULL);
  }
  
  /**
   * Cast result object to a boolean, but allow side effects, such as the 
   * incrementing of an iterator.
   *
   * @return True if there is a next node in the nodeset
   */
  public boolean boolWithSideEffects()
  {
    return (nextNode() != DTM.NULL);
  }

  
  /**
   * Get the string conversion from a single node.
   *
   * @param n Node to convert
   *
   * @return the string conversion from a single node.
   */
  public XMLString getStringFromNode(int n)
  {
    // %OPT%
    // I guess we'll have to get a static instance of the DTM manager...
    if(DTM.NULL != n)
    {
      return m_dtmMgr.getDTM(n).getStringValue(n);
    }
    else
    {
      return org.apache.xpath.objects.XString.EMPTYSTRING;
    }
  }
  
  /**
   * Directly call the
   * characters method on the passed ContentHandler for the
   * string-value. Multiple calls to the
   * ContentHandler's characters methods may well occur for a single call to
   * this method.
   *
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchCharactersEvents(org.xml.sax.ContentHandler ch)
          throws org.xml.sax.SAXException
  {
    int node = item(0);
	
    if(node != DTM.NULL)
    {
      m_dtmMgr.getDTM(node).dispatchCharactersEvents(node, ch, false);
    }
    
  }
  
  /**
   * Cast result object to an XMLString.
   *
   * @return The document fragment node data or the empty string. 
   */
  public XMLString xstr()
  {
    int node = item(0);
    return (node != DTM.NULL) ? getStringFromNode(node) : XString.EMPTYSTRING;
  }
  
  /**
   * Cast result object to a string.
   *
   * @return The string this wraps or the empty string if null
   */
  public void appendToFsb(org.apache.xml.utils.FastStringBuffer fsb)
  {
    XString xstring = (XString)xstr();
    xstring.appendToFsb(fsb);
  }
  

  /**
   * Cast result object to a string.
   *
   * @return the string conversion from the next node in the nodeset
   * or "" if there is no next node
   */
  public String str()
  {
    int node = item(0);
    return (node != DTM.NULL) ? getStringFromNode(node).toString() : "";   
  }
  
  /**
   * Return a java object that's closest to the representation
   * that should be handed to an extension.
   *
   * @return The object that this class wraps
   */
  public Object object()
  {
    if(null == m_obj)
    	return this;
    else
    	return m_obj;
  }

  // %REVIEW%
  // hmmm...
//  /**
//   * Cast result object to a result tree fragment.
//   *
//   * @param support The XPath context to use for the conversion 
//   *
//   * @return the nodeset as a result tree fragment.
//   */
//  public DocumentFragment rtree(XPathContext support)
//  {
//    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//    DocumentBuilder db = dbf.newDocumentBuilder();
//    Document myDoc = db.newDocument();
//    
//    DocumentFragment docFrag = myDoc.createDocumentFragment();
//
//    DTMIterator nl = iter();
//    int node;
//
//    while (DTM.NULL != (node = nl.nextNode()))
//    {
//      frag.appendChild(node, true, true);
//    }
//
//    return frag.getDocument();
//  }

  /**
   * Cast result object to a nodelist.
   *
   * @return a NodeIterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public NodeIterator nodeset() throws javax.xml.transform.TransformerException
  {
    return new org.apache.xml.dtm.ref.DTMNodeIterator(iter());
  }
  
  /**
   * Cast result object to a nodelist.
   *
   * @return a NodeList.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public NodeList nodelist() throws javax.xml.transform.TransformerException
  {
    org.apache.xml.dtm.ref.DTMNodeList nodelist = new org.apache.xml.dtm.ref.DTMNodeList(this);
    // Creating a DTMNodeList has the side-effect that it will create a clone
    // XMLNodeCursorImpl with cache and run m_iter to the end. You cannot get any node
    // from m_iter after this call. As a fix, we call SetVector() on the clone's 
    // cache. See Bugzilla 14406.
    XMLNodeCursorImpl clone = (XMLNodeCursorImpl)nodelist.getDTMIterator();
    SetVector(clone.getVector());
    return nodelist;
  }

  
//  /**
//   * Return a java object that's closest to the representation
//   * that should be handed to an extension.
//   *
//   * @return The object that this class wraps
//   */
//  public Object object()
//  {
//    return new org.apache.xml.dtm.ref.DTMNodeList(iter());
//  }

  /**
   * Return the iterator without cloning, etc.
   */
  public DTMCursorIterator iterRaw()
  {
    return this;
  }
  
  public void release(DTMCursorIterator iter)
  {
  }
  
  /**
   * Cast result object to a nodelist.
   *
   * @return The nodeset as a nodelist
   */
  public DTMCursorIterator iter()
  {
    try
    {
    	if(hasCache())
      		return cloneWithReset();
      	else
      		return this; // don't bother to clone... won't do any good!
    }
    catch (CloneNotSupportedException cnse)
    {
      throw new RuntimeException(cnse.getMessage());
    }
  }
  
  /**
   * Get a fresh copy of the object.  For use with variables.
   *
   * @return A fresh nodelist.
   */
  public XObject getFresh()
  {
    try
    {
    	if(hasCache())
      		return (XObject)cloneWithReset();
      	else
      		return this; // don't bother to clone... won't do any good!
    }
    catch (CloneNotSupportedException cnse)
    {
      throw new RuntimeException(cnse.getMessage());
    }
  }

  /**
   * Cast result object to a mutableNodeset.
   *
   * @return The nodeset as a mutableNodeset
   */
  public NodeSetDTM mutableNodeset()
  {
    NodeSetDTM mnl;

    if(m_obj instanceof NodeSetDTM)
    {
      mnl = (NodeSetDTM) m_obj;
    }
    else
    {
      mnl = new NodeSetDTM(iter());
      setObject(mnl);
      setCurrentPos(0);
    }

    return mnl;
  }

  /** Less than comparator         */
  static final LessThanComparator S_LT = new LessThanComparator();

  /** Less than or equal comparator          */
  static final LessThanOrEqualComparator S_LTE = new LessThanOrEqualComparator();

  /** Greater than comparator         */
  static final GreaterThanComparator S_GT = new GreaterThanComparator();

  /** Greater than or equal comparator          */
  static final GreaterThanOrEqualComparator S_GTE =
    new GreaterThanOrEqualComparator();

  /** Equal comparator         */
  static final EqualComparator S_EQ = new EqualComparator();

  /** Not equal comparator         */
  static final NotEqualComparator S_NEQ = new NotEqualComparator();

  /**
   * Tell if one object is less than the other.
   *
   * @param obj2 Object to compare this nodeset to
   * @param comparator Comparator to use
   *
   * @return See the comments below for each object type comparison 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean compare(XObject obj2, Comparator comparator)
          throws javax.xml.transform.TransformerException
  {

    boolean result = false;
    int type = obj2.getType();

    if (XObject.CLASS_NODESET == type)
    {
      // %OPT% This should be XMLString based instead of string based...

      // From http://www.w3.org/TR/xpath: 
      // If both objects to be compared are node-sets, then the comparison 
      // will be true if and only if there is a node in the first node-set 
      // and a node in the second node-set such that the result of performing 
      // the comparison on the string-values of the two nodes is true.
      // Note this little gem from the draft:
      // NOTE: If $x is bound to a node-set, then $x="foo" 
      // does not mean the same as not($x!="foo"): the former 
      // is true if and only if some node in $x has the string-value 
      // foo; the latter is true if and only if all nodes in $x have 
      // the string-value foo.
      DTMCursorIterator list1 = iterRaw();
      DTMCursorIterator list2 = ((XMLNodeCursorImpl) obj2).iterRaw();
      int node1;
      java.util.Vector node2Strings = null;

      while (DTM.NULL != (node1 = list1.nextNode()))
      {
        XMLString s1 = getStringFromNode(node1);

        if (null == node2Strings)
        {
          int node2;

          while (DTM.NULL != (node2 = list2.nextNode()))
          {
            XMLString s2 = getStringFromNode(node2);

            if (comparator.compareStrings(s1, s2))
            {
              result = true;

              break;
            }

            if (null == node2Strings)
              node2Strings = new java.util.Vector();

            node2Strings.addElement(s2);
          }
        }
        else
        {
          int n = node2Strings.size();

          for (int i = 0; i < n; i++)
          {
            if (comparator.compareStrings(s1, (XMLString)node2Strings.elementAt(i)))
            {
              result = true;

              break;
            }
          }
        }
      }
      list1.reset();
      list2.reset();
    }
    else if (XObject.CLASS_RESULT_SEQUENCE == type) {        
        ResultSequence rSeq = (ResultSequence)obj2;
        
        DTMCursorIterator list1 = iterRaw();        
        int node1;
        while (DTM.NULL != (node1 = list1.nextNode()))
        {
            XMLString s1 = getStringFromNode(node1);
            
            for (int idx = 0; idx < rSeq.size(); idx++) {
               XObject xObj = rSeq.item(idx);               
               XMLString s2 = new XString(xObj.str());
               if (comparator.compareStrings(s1, s2))
               {
                   result = true;
                   break;
               }
            }
            
            if (result) {
               break;  
            }
        }        
    }
    else if (XObject.CLASS_BOOLEAN == type)
    {

      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a boolean, 
      // then the comparison will be true if and only if the result of 
      // performing the comparison on the boolean and on the result of 
      // converting the node-set to a boolean using the boolean function 
      // is true.
      double num1 = bool() ? 1.0 : 0.0;
      double num2 = obj2.num();

      result = comparator.compareNumbers(num1, num2);
    }
    else if (XObject.CLASS_NUMBER == type)
    {

      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a number, 
      // then the comparison will be true if and only if there is a 
      // node in the node-set such that the result of performing the 
      // comparison on the number to be compared and on the result of 
      // converting the string-value of that node to a number using 
      // the number function is true.      
      DTMCursorIterator list1 = iterRaw();
      double num2 = obj2.num();
      int node;

      while (DTM.NULL != (node = list1.nextNode()))
      {
          double num1 = getNumberFromNode(node);

          if (comparator.compareNumbers(num1, num2))
          {
              result = true;

              break;
          }
      }
      list1.reset();
    }
    else if (XObject.CLASS_RTREEFRAG == type)
    {
      XMLString s2 = obj2.xstr();
      DTMCursorIterator list1 = iterRaw();
      int node;

      while (DTM.NULL != (node = list1.nextNode()))
      {
        XMLString s1 = getStringFromNode(node);

        if (comparator.compareStrings(s1, s2))
        {
          result = true;

          break;
        }
      }
      list1.reset();
    }
    else if (XObject.CLASS_STRING == type)
    {

      // From http://www.w3.org/TR/xpath: 
      // If one object to be compared is a node-set and the other is a 
      // string, then the comparison will be true if and only if there 
      // is a node in the node-set such that the result of performing 
      // the comparison on the string-value of the node and the other 
      // string is true. 
      XMLString s2 = obj2.xstr();
      DTMCursorIterator list1 = iterRaw();
      int node;

      while (DTM.NULL != (node = list1.nextNode()))
      {
        XMLString s1 = getStringFromNode(node);
        if (comparator.compareStrings(s1, s2))
        {
          result = true;

          break;
        }
      }
      list1.reset();
    }
    else if (obj2 instanceof XSUntyped) {
       XSUntyped obj2Val = (XSUntyped)obj2;
       
       XMLString s2 = new XString(obj2Val.stringValue());
       DTMCursorIterator list1 = iterRaw();
       int node;

       while (DTM.NULL != (node = list1.nextNode()))
       {
         XMLString s1 = getStringFromNode(node);
         if (comparator.compareStrings(s1, s2))
         {
           result = true;

           break;
         }
       }
       list1.reset();       
    }
    else
    {
      result = comparator.compareNumbers(this.num(), obj2.num());
    }

    return result;
  }
  
  /*
   * Check whether two xdm nodes are functionally equal, on the basis
   * of string values of their names, and string values of nodes 
   * themselves.
   */
  public boolean compareWithNodeNames(XObject obj2, XPathContext xctxt, 
		                             Comparator comparator) throws javax.xml.transform.TransformerException {

	    boolean result = false;
	    
	    int obj2Type = obj2.getType();
	
	    if (obj2Type == XObject.CLASS_NODESET) {
		   DTMCursorIterator dtmIter1 = iterRaw();
		   DTMCursorIterator dtmIter2 = ((XMLNodeCursorImpl) obj2).iterRaw();
		   int nodeHandle1;
		
		   while ((nodeHandle1 = dtmIter1.nextNode()) != DTM.NULL) {
		        XMLString s1 = getStringFromNode(nodeHandle1);
		          
		        int nodeHandle2;
		
		        while ((nodeHandle2 = dtmIter2.nextNode()) != DTM.NULL) {
		            XMLString s2 = getStringFromNode(nodeHandle2);
		
		            if (comparator.compareStrings(s1, s2)) {
		               DTM dtm1 = xctxt.getDTM(nodeHandle1);
		     	       Node node1 = dtm1.getNode(nodeHandle1);
		     	       DTM dtm2 = xctxt.getDTM(nodeHandle2);
		     	       Node node2 = dtm2.getNode(nodeHandle2);
		     	       if (isNodeNameEqual(node1, node2)) {
		     	    	  result = true;  
		     	       }		              
		
		               break;
		            }
		        }
		  }
		      
		  dtmIter1.reset();
		  dtmIter2.reset();
	   }
	   else if (obj2Type == XObject.CLASS_RESULT_SEQUENCE) {        
	      ResultSequence rSeq = (ResultSequence)obj2;
	        
	      DTMCursorIterator list1 = iterRaw();        
	      int nodeHandle1;
	      while ((nodeHandle1 = list1.nextNode()) != DTM.NULL) {
	         XMLString s1 = getStringFromNode(nodeHandle1);
	            
	         for (int idx = 0; idx < rSeq.size(); idx++) {
	            XObject xObj = rSeq.item(idx);               
	            XMLString s2 = new XString(xObj.str());
	            if (comparator.compareStrings(s1, s2))
	            {
	                DTM dtm = xctxt.getDTM(nodeHandle1);
		     	    Node node1 = dtm.getNode(nodeHandle1);
		     	    if (xObj instanceof XMLNodeCursorImpl) {
		     	       XMLNodeCursorImpl xObjNodeSet = (XMLNodeCursorImpl)xObj;
		     	       int nodeHandle2 = (xObjNodeSet.iter()).nextNode();
		     	       DTM dtm2 = xctxt.getDTM(nodeHandle2);
		     	       Node node2 = dtm.getNode(nodeHandle2);
		     	       if (isNodeNameEqual(node1, node2)) {
		     	    	  result = true;  
		     	       }
		     	    }
		     	    else {
		     	       result = true;   
		     	    }
		
		            break;
	            }
	         }
	            
	         if (result) {
	            break;  
	         }
	      }        
	   }
	   else if (obj2Type == XObject.CLASS_BOOLEAN) {
	      double num1 = bool() ? 1.0 : 0.0;
	      double num2 = obj2.num();
	
	      result = comparator.compareNumbers(num1, num2);
	   }
	   else if (obj2Type == XObject.CLASS_NUMBER) {     
	      DTMCursorIterator list1 = iterRaw();
	      double num2 = obj2.num();
	      int node;
	
	      while ((node = list1.nextNode()) != DTM.NULL) {
	        double num1 = getNumberFromNode(node);
	
	        if (comparator.compareNumbers(num1, num2)) {
	          result = true;	
	          break;
	        }
	      }
	      
	      list1.reset();
	   }
	   else if (obj2Type == XObject.CLASS_STRING) {
	      XMLString s2 = obj2.xstr();
	      DTMCursorIterator list1 = iterRaw();
	      int node;
	
	      while ((node = list1.nextNode()) != DTM.NULL) {
	        XMLString s1 = getStringFromNode(node);
	        if (comparator.compareStrings(s1, s2)) {
	          result = true;
	
	          break;
	        }
	      }
	      
	      list1.reset();
	    }
	    else if (obj2 instanceof XSUntyped) {
	       XSUntyped obj2Val = (XSUntyped)obj2;
	       
	       XMLString s2 = new XString(obj2Val.stringValue());
	       DTMCursorIterator list1 = iterRaw();
	       int node;
	
	       while ((node = list1.nextNode()) != DTM.NULL) {
	         XMLString s1 = getStringFromNode(node);
	         if (comparator.compareStrings(s1, s2)) {
	           result = true;
	
	           break;
	         }
	       }
	       
	       list1.reset();       
	    }
	    else {
	       result = comparator.compareNumbers(this.num(), obj2.num());
	    }
	
	    return result;
  }

  /**
   * Tell if one object is less than the other.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean lessThan(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, S_LT);
  }

  /**
   * Tell if one object is less than or equal to the other.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean lessThanOrEqual(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, S_LTE);
  }

  /**
   * Tell if one object is less than the other.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean greaterThan(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, S_GT);
  }

  /**
   * Tell if one object is less than the other.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean greaterThanOrEqual(XObject obj2)
          throws javax.xml.transform.TransformerException
  {
    return compare(obj2, S_GTE);
  }

  /**
   * Tell if two objects are functionally equal.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean equals(XObject obj2)
  {
    try
    {
      return compare(obj2, S_EQ);
    }
    catch(javax.xml.transform.TransformerException te)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(te);
    }
  }
  
  /*
   * Check whether two xdm nodes are equal, considering string
   * values of their names as well.
   */
  public boolean equalsWithNodeName(XObject obj2, XPathContext xctxt)
  {
    try
    {
       return compareWithNodeNames(obj2, xctxt, S_EQ);
    }
    catch(javax.xml.transform.TransformerException te)
    {
       throw new org.apache.xml.utils.WrappedRuntimeException(te);
    }
  }

  /**
   * Tell if two objects are functionally not equal.
   *
   * @param obj2 object to compare this nodeset to
   *
   * @return see this.compare(...) 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean notEquals(XObject obj2) throws javax.xml.transform.TransformerException
  {
    return compare(obj2, S_NEQ);
  }
  
  /**
   * Check whether fully qualified names of two XML nodes are equal.
   */
  private boolean isNodeNameEqual(Node node1, Node node2) {	 
	 boolean isNodeNameEqual = false;
	 
	 if ((node1.getNodeType() == Node.ELEMENT_NODE) && (node2.getNodeType() == Node.ELEMENT_NODE)) {
		String localName1 = node1.getLocalName();
		String localName2 = node2.getLocalName();
		if (localName1.equals(localName2) && isNsNameEqual(node1.getNamespaceURI(), node2.getNamespaceURI())) {
		   isNodeNameEqual = true;
		}
	 }
	 else if ((node1.getNodeType() == Node.ATTRIBUTE_NODE) && (node2.getNodeType() == Node.ATTRIBUTE_NODE)) {
		String localName1 = node1.getLocalName();
		String localName2 = node2.getLocalName();
		if (localName1.equals(localName2) && isNsNameEqual(node1.getNamespaceURI(), node2.getNamespaceURI())) {
		   isNodeNameEqual = true;
		}
	 }
	 else if ((node1.getNodeType() == Node.TEXT_NODE) && (node2.getNodeType() == Node.TEXT_NODE)) {
		isNodeNameEqual = true;	 
	 }
	 else if ((node1.getNodeType() == Node.COMMENT_NODE) && (node2.getNodeType() == Node.COMMENT_NODE)) {
	    isNodeNameEqual = true;	 
     }
	 
	 return isNodeNameEqual;
  }
  
  /**
   * Check whether two XML namespace names are equal.
   */
  private boolean isNsNameEqual(String nsName1, String nsName2) {	 
	 boolean isNsNameEqual = true;
	 
	 if (((nsName1 != null) && (nsName2 == null)) || 
		  ((nsName1 == null) && (nsName2 != null))) {
		isNsNameEqual = false; 
	 }
	 else if ((nsName1 != null) && (nsName2 != null) && nsName1.equals(nsName2)) {
		isNsNameEqual = true; 
	 }
	 
	 return isNsNameEqual;
  }

  public void setTypeAttrForValidation(QName type) {
	 m_type = type;	
  }
  
  public QName getTypeAttrForValidation() {
	 return m_type;  
  }

  public void setValidationAttrForValidation(String validation) {
	 m_validation = validation;	
  }
  
  public String getValidationAttrForValidation() {
     return m_validation;  
  }
  
}

/**
 * compares nodes for various boolean operations.
 */
abstract class Comparator
{

  /**
   * Compare two strings
   *
   *
   * @param s1 First string to compare
   * @param s2 Second String to compare 
   *
   * @return Whether the strings are equal or not
   */
  abstract boolean compareStrings(XMLString s1, XMLString s2);

  /**
   * Compare two numbers
   *
   *
   * @param n1 First number to compare
   * @param n2 Second number to compare
   *
   * @return Whether the numbers are equal or not
   */
  abstract boolean compareNumbers(double n1, double n2);
}

/**
 * Compare strings or numbers for less than.
 */
class LessThanComparator extends Comparator
{

  /**
   * Compare two strings for less than.
   *
   *
   * @param s1 First string to compare
   * @param s2 Second String to compare 
   *
   * @return True if s1 is less than s2
   */
  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return (s1.toDouble() < s2.toDouble());
    // return s1.compareTo(s2) < 0;
  }

  /**
   * Compare two numbers for less than.
   *
   *
   * @param n1 First number to compare
   * @param n2 Second number to compare
   *
   * @return true if n1 is less than n2
   */
  boolean compareNumbers(double n1, double n2)
  {
    return n1 < n2;
  }
}

/**
 * Compare strings or numbers for less than or equal.
 */
class LessThanOrEqualComparator extends Comparator
{

  /**
   * Compare two strings for less than or equal.
   *
   *
   * @param s1 First string to compare
   * @param s2 Second String to compare
   *
   * @return true if s1 is less than or equal to s2
   */
  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return (s1.toDouble() <= s2.toDouble());
    // return s1.compareTo(s2) <= 0;
  }

  /**
   * Compare two numbers for less than or equal.
   *
   *
   * @param n1 First number to compare
   * @param n2 Second number to compare
   *
   * @return true if n1 is less than or equal to n2
   */
  boolean compareNumbers(double n1, double n2)
  {
    return n1 <= n2;
  }
}

/**
 * Compare strings or numbers for greater than.
 */
class GreaterThanComparator extends Comparator
{

  /**
   * Compare two strings for greater than.
   *
   *
   * @param s1 First string to compare
   * @param s2 Second String to compare
   *
   * @return true if s1 is greater than s2
   */
  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return (s1.toDouble() > s2.toDouble());
    // return s1.compareTo(s2) > 0;
  }

  /**
   * Compare two numbers for greater than.
   *
   *
   * @param n1 First number to compare
   * @param n2 Second number to compare
   *
   * @return true if n1 is greater than n2
   */
  boolean compareNumbers(double n1, double n2)
  {
    return n1 > n2;
  }
}

/**
 * Compare strings or numbers for greater than or equal.
 */
class GreaterThanOrEqualComparator extends Comparator
{

  /**
   * Compare two strings for greater than or equal.
   *
   *
   * @param s1 First string to compare
   * @param s2 Second String to compare
   *
   * @return true if s1 is greater than or equal to s2
   */
  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return (s1.toDouble() >= s2.toDouble());
    // return s1.compareTo(s2) >= 0;
  }

  /**
   * Compare two numbers for greater than or equal.
   *
   *
   * @param n1 First number to compare
   * @param n2 Second number to compare
   *
   * @return true if n1 is greater than or equal to n2
   */
  boolean compareNumbers(double n1, double n2)
  {
    return n1 >= n2;
  }
}

/**
 * Compare strings or numbers for equality.
 */
class EqualComparator extends Comparator
{

  /**
   * Compare two strings for equality.
   *
   *
   * @param s1 First string to compare
   * @param s2 Second String to compare
   *
   * @return true if s1 is equal to s2
   */
  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return s1.equals(s2);
  }

  /**
   * Compare two numbers for equality.
   *
   *
   * @param n1 First number to compare
   * @param n2 Second number to compare
   *
   * @return true if n1 is equal to n2
   */
  boolean compareNumbers(double n1, double n2)
  {
    return n1 == n2;
  }
}

/**
 * Compare strings or numbers for non-equality.
 */
class NotEqualComparator extends Comparator
{

  /**
   * Compare two strings for non-equality.
   *
   *
   * @param s1 First string to compare
   * @param s2 Second String to compare
   *
   * @return true if s1 is not equal to s2
   */
  boolean compareStrings(XMLString s1, XMLString s2)
  {
    return !s1.equals(s2);
  }

  /**
   * Compare two numbers for non-equality.
   *
   *
   * @param n1 First number to compare
   * @param n2 Second number to compare
   *
   * @return true if n1 is not equal to n2
   */
  boolean compareNumbers(double n1, double n2)
  {
    return n1 != n2;
  }
}
