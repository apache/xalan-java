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
package org.apache.xpath;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDuration;

/**
 * Class definition, to represent XPath 3.1 expression evaluation's 
 * dynamic context.
 * 
 * Please refer to, XPath 3.1 recommendation section, '2.1.2 Dynamic Context'.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Gary L Peskin <garyp@apache.org>
 * @author Myriam Midy <mmidy@apache.org>
 * 
 * @author Joseph Kesselman <keshlam@alum.mit.edu>
 * @author Morris Kwan <mkwan@apache.org>, Ilene Seelemann <ilene@apache.org>, Henry Zongaro <zongaro@ca.ibm.com>,
 *         Brian Minchau <minchau@apache.org>, Sarah McNamara <mcnamara@apache.org>
 *         
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3.1 specific changes, to this class)
 * 
 * @xsl.usage general
 */
public class XPathDynamicContext extends DTMManager {
	
	/** 
	 * The value of this class field, represents the value returned by fn:position() 
	 * function when called within XSL instructions 'xsl:for-each-group' or 'xsl:analyze-string'.
	 */
	private int m_pos;

	/** 
	 * The value of this class field, represents the value returned by fn:last() 
	 * function when called within XSL instructions 'xsl:for-each-group' or 'xsl:analyze-string'.
	 */
	private int m_last;
	
	/**
	 *  This data value, represents the XPath 3.1's current evaluation 
	 *  context item. This is in addition to, XPath context item
	 *  retrieved in other ways, by Xalan-J implementation.
	 */
	private XObject m_xpath3ContextItem = null;

	/**
	 *  This data value, represents the XPath 3.1's current evaluation 
	 *  context position.
	 */
	private int m_xpath3ContextPosition = -1;

	/**
	 *  This data value, represents the XPath 3.1's current evaluation 
	 *  context size.
	 */
	private int m_xpath3ContextSize = -1;
	
	/**
	 * Class field, that represents XPath dynamic context's 
	 * implicit timezone. This value is of XML Schema type xs:dayTimeDuration.
	 */
	private XSDuration m_timezone;
	
	/**
	 * This represents the value returned by XPath 3.1 function 
	 * fn:current-dateTime.
	 */
	private GregorianCalendar m_currentDateTime;
	
	/**
	 * We use this java.util.Map object, to store variable binding information 
	 * (i.e, mapping from variable name to its value) for implementations of 
	 * XPath 3.1 'inline function', 'for', 'let' and 'quantified' expressions.
	 * 
	 * We don't use, Xalan-J xpath variable stack for this purpose.
	 */
	private Map<QName, XObject> xpathVarMap = new HashMap<QName, XObject>();
	
	/**
	 * This data value, represents certain custom data (represented 
	 * as a java.util.Map object) within the current XPath 3.1 evaluation 
	 * context.
	 */
	private Map<String, String> m_customDataMap = new HashMap<String, String>();
	
	/**
	 * This data value, represents XPath context's default collection.
	 * 
	 * The value of XPath default collection is not defined, for Xalan
	 * XPath 3.1 implementation, which is implied by this value being
	 * equal to an xdm empty sequence. 
	 */
	private ResultSequence m_default_collection = new ResultSequence();
	  
	/**
	 * Stack of cached "reusable" DTMs for Result Tree Fragments.
	 * This is a kluge to handle the problem of starting an RTF before
	 * the old one is complete.
	 * 
	 * %REVIEW% I'm using a Vector rather than Stack so we can reuse
	 * the DTMs if the problem occurs multiple times. I'm not sure that's
	 * really a net win versus discarding the DTM and starting a new one...
	 * but the retained RTF DTM will have been tail-pruned so should be small.
	 */
	protected Vector m_rtfdtm_stack = null;

	/**
	 * Though XPathContext context extends 
	 * the DTMManager, it really is a proxy for this object, which 
	 * is the real DTMManager.
	 */
	protected DTMManager m_dtmManager = DTMManager.newInstance(
			                                                org.apache.xpath.objects.XMLStringFactoryImpl.getFactory());

	/**
	 * Get an instance of a DTM, loaded with the content from the
	 * specified source. If the unique flag is true, a new instance will
	 * always be returned. Otherwise it is up to the DTMManager to return a
	 * new instance or an instance that it already created and may be being used
	 * by someone else.
	 * (I think more parameters will need to be added for error handling, and entity
	 * resolution).
	 *
	 * @param source the specification of the source object, which may be null, 
	 *               in which case it is assumed that node construction will take 
	 *               by some other means.
	 * @param unique true if the returned DTM must be unique, probably because it
	 * is going to be mutated.
	 * @param wsfilter Enables filtering of whitespace nodes, and may be null.
	 * @param incremental true if the construction should try and be incremental.
	 * @param doIndexing true if the caller considers it worth it to use 
	 *                   indexing schemes.
	 *
	 * @return a non-null DTM reference.
	 */
	public DTM getDTM(javax.xml.transform.Source source, boolean unique, 
																		DTMWSFilter wsfilter,
																		boolean incremental,
																		boolean doIndexing) {
		return m_dtmManager.getDTM(source, unique, wsfilter, incremental, doIndexing);
	}

	/**
	 * Get an instance of a DTM that "owns" a node handle. 
	 *
	 * @param nodeHandle the nodeHandle.
	 *
	 * @return a non-null DTM reference.
	 */
	public DTM getDTM(int nodeHandle) {
		return m_dtmManager.getDTM(nodeHandle);
	}

	/**
	 * Given a W3C DOM node, try and return a DTM handle.
	 * Note: calling this may be non-optimal.
	 * 
	 * @param node Non-null reference to a DOM node.
	 * 
	 * @return a valid DTM handle.
	 */
	public int getDTMHandleFromNode(org.w3c.dom.Node node) {
		return m_dtmManager.getDTMHandleFromNode(node);
	}

	/**
	 * Creates an empty <code>DocumentFragment</code> object. 
	 * @return A new <code>DocumentFragment handle</code>.
	 */
	public DTM createDocumentFragment() {
		return m_dtmManager.createDocumentFragment();
	}

	/**
	 * Release a DTM either to a lru pool, or completely remove reference.
	 * DTMs without system IDs are always hard deleted.
	 * State: experimental.
	 * 
	 * @param dtm The DTM to be released.
	 * @param shouldHardDelete True if the DTM should be removed no matter what.
	 * @return true if the DTM was removed, false if it was put back in a lru pool.
	 */
	public boolean release(DTM dtm, boolean shouldHardDelete) {		
		if (m_rtfdtm_stack!=null && m_rtfdtm_stack.contains(dtm))
		{
			return false;
		}

		return m_dtmManager.release(dtm, shouldHardDelete);
	}

	/**
	 * Create a new <code>DTMIterator</code> based on an XPath
	 * <a href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a> or
	 * a <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
	 *
	 * @param xpathCompiler
	 *
	 * @param pos The position in the expression
	 * 
	 * @return The newly created <code>DTMIterator</code>.
	 */
	public DTMCursorIterator createDTMIterator(Object xpathCompiler, int pos) {
		return m_dtmManager.createDTMIterator(xpathCompiler, pos);
	}

	/**
	 * Create a new <code>DTMIterator</code> based on an XPath
	 * <a href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a> or
	 * a <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
	 *
	 * @param xpathString Must be a valid string expressing a
	 * <a href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a> or
	 * a <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
	 *
	 * @param presolver An object that can resolve prefixes to namespace URLs.
	 *
	 * @return The newly created <code>DTMIterator</code>.
	 */
	public DTMCursorIterator createDTMIterator(String xpathString, PrefixResolver presolver) {
		return m_dtmManager.createDTMIterator(xpathString, presolver);
	}

	//
	/**
	 * Create a new <code>DTMIterator</code> based only on a whatToShow and
	 * a DTMFilter. The traversal semantics are defined as the descendant
	 * access.
	 *
	 * @param whatToShow This flag specifies which node types may appear in
	 *   the logical view of the tree presented by the iterator. See the
	 *   description of <code>NodeFilter</code> for the set of possible
	 *   <code>SHOW_</code> values.These flags can be combined using
	 *   <code>OR</code>.
	 * @param filter The <code>NodeFilter</code> to be used with this
	 *   <code>TreeWalker</code>, or <code>null</code> to indicate no filter.
	 * @param entityReferenceExpansion The value of this flag determines
	 *   whether entity reference nodes are expanded.
	 *
	 * @return The newly created <code>NodeIterator</code>.
	 */
	public DTMCursorIterator createDTMIterator(int whatToShow, DTMFilter filter, 
			                                                                   boolean entityReferenceExpansion) {
		return m_dtmManager.createDTMIterator(whatToShow, filter, entityReferenceExpansion);
	}

	/**
	 * Create a new <code>DTMIterator</code> that holds exactly one node.
	 *
	 * @param node The node handle that the DTMIterator will iterate to.
	 *
	 * @return The newly created <code>DTMIterator</code>.
	 */
	public DTMCursorIterator createDTMIterator(int node) {
		DTMCursorIterator iter = new org.apache.xpath.axes.OneStepIteratorForward(Axis.SELF);
		iter.setRoot(node, this);
		
		return iter;
	}

	/**
	 * Given a DTM, find the id number in the DTM tables which addresses
	 * the start of the document.
	 */
	public int getDTMIdentity(DTM dtm) {
		return m_dtmManager.getDTMIdentity(dtm);
	}
	
	/**
	 * Get the value of implicit timezone.
	 * 
	 * @return                             Implicit timezone as xs:dayTimeDuration value. 
	 */
	public XSDuration getTimezone() {
		if (m_timezone == null) {
			ZonedDateTime zonedDateTime = ZonedDateTime.now();
			ZoneOffset zoneOffset = zonedDateTime.getOffset();
			String zoneOffsetStr = zoneOffset.toString();
			String[] zoneOffsetStrParts = zoneOffsetStr.split("\\+|\\-|:");
			int zoneHrs = 0;
			int zoneMinutes = 0;
			if (zoneOffsetStrParts.length > 1) {
				zoneHrs = Integer.valueOf(zoneOffsetStrParts[1]).intValue();
				zoneMinutes = Integer.valueOf(zoneOffsetStrParts[2]).intValue();
			}
			boolean isNegativeTimezone = !zoneOffsetStr.startsWith("+");
			
			m_timezone = new XSDayTimeDuration(0, zoneHrs, zoneMinutes, 0, isNegativeTimezone);
		}

		return m_timezone;
	}

	public void setTimezone(XSDuration timezone) {
		this.m_timezone = timezone;
	}
	
	public void setPos(int pos) {
		this.m_pos = pos;
	}

	public int getPos() {
		return this.m_pos;
	}

	public void setLast(int last) {
		this.m_last = last;	
	}

	public int getLast() {
		return this.m_last;
	}
	
	public XObject getXPath3ContextItem() {
		return m_xpath3ContextItem;
	}

	public void setXPath3ContextItem(XObject xpath3ContextItem) {
		this.m_xpath3ContextItem = xpath3ContextItem;
	}

	public int getXPath3ContextPosition() {
		return m_xpath3ContextPosition;
	}

	public void setXPath3ContextPosition(int xpath3ContextPosition) {
		this.m_xpath3ContextPosition = xpath3ContextPosition;
	}

	public int getXPath3ContextSize() {
		return m_xpath3ContextSize;
	}

	public void setXPath3ContextSize(int xpath3ContextSize) {
		this.m_xpath3ContextSize = xpath3ContextSize;
	}
	
	public GregorianCalendar getCurrentDateTime() {
		if (m_currentDateTime == null) {
			m_currentDateTime = new GregorianCalendar(TimeZone.getDefault());
		}

		return m_currentDateTime;
	}

	public void setCurrentDateTime(GregorianCalendar currentDateTime) {
		this.m_currentDateTime = currentDateTime;
	}

	public Map<QName, XObject> getXPathVarMap() {
		return xpathVarMap;
	}

	public void setXPathVarMap(Map<QName, XObject> xpathVarMap) {
		this.xpathVarMap = xpathVarMap;
	}
	
	public Map<String, String> getCustomDataMap() {
		return m_customDataMap;
	}

	public void setCustomDataMap(Map<String, String> customDataMap) {
		this.m_customDataMap = customDataMap;
	}
	
	public ResultSequence getDefaultCollection() {
		return m_default_collection;
	}

}
