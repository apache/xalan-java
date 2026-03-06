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
package org.apache.xalan.transformer;

import java.util.Vector;

import org.apache.xalan.templates.ElemForEachGroup;
import org.apache.xalan.templates.GroupingKeyAndGroupPair;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * This class is used for xsl:for-each-group instruction's
 * xsl:sort implementation.
 * 
 * The code for this class, follows design of xsl:for-each instruction's
 * xsl:sort implementation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage internal
 */
public class XslForEachGroupNodeCompareElem {

	/** This maxkey value was chosen arbitrarily. We're assuming that the    
        maxkey + 1 keys will only occur fairly rarely and therefore, we will 
        get the node values for those keys dynamically.
	 */
	private int maxkey = 2;

	/** Value from first sort key */
	Object m_key1Value;

	/** Value from second sort key */
	Object m_key2Value;

	private GroupingKeyAndGroupPair m_groupingKeyAndGroupPair;

	/**
	 * Class constructor.
	 *
	 * @param elemForEachGrp                       Reference to xsl:for-each-group instruction 
	 *                                             object.
	 * @param groupingKeyAndGroupPair              An object instance, representing mapping between
	 *                                             a group and its grouping key.
	 * @param sortKeys                             Vector of sort key objects
	 * @param xctxt                                An XPath context object
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XslForEachGroupNodeCompareElem(ElemForEachGroup elemForEachGrp, GroupingKeyAndGroupPair 
			                                                                                      groupingKeyAndGroupPair, 
			                                                                                      Vector sortKeys, 
			                                                                                      XPathContext xctxt) throws javax.xml.transform.TransformerException {

		this.m_groupingKeyAndGroupPair = groupingKeyAndGroupPair; 

		int currentNode = ((m_groupingKeyAndGroupPair.getGroupNodeDtmHandles()).get(0)).intValue();

		TransformerImpl transformer = (TransformerImpl)(xctxt.getOwnerObject());
		elemForEachGrp.setGroupingKey(groupingKeyAndGroupPair.getGroupingKey());
		elemForEachGrp.setGroupNodesDtmHandles(groupingKeyAndGroupPair.getGroupNodeDtmHandles());
		transformer.setCurrentElement(elemForEachGrp);
		
		/**
		 * For xsl:for-each-group's xsl:sort implementation, an XPath context 
		 * item for xsl:sort's XPath expression is initial xdm item of the 
		 * group.
		 */
		
		try {
			xctxt.pushCurrentNode(currentNode);

			if (!sortKeys.isEmpty()) {
				NodeSortKey k1 = (NodeSortKey) sortKeys.elementAt(0);
				XObject r = (k1.m_selectPat).execute(xctxt, currentNode, k1.m_namespaceContext);

				double d;

				if (k1.m_treatAsNumbers) {
					d = r.num();
					m_key1Value = Double.valueOf(d);
				}
				else {
					String str1 = XslTransformEvaluationHelper.getStrVal(r);
					m_key1Value = k1.m_col.getCollationKey(str1);
				}

				if (r.getType() == XObject.CLASS_NODESET) {
					DTMCursorIterator ni = ((XMLNodeCursorImpl)r).iterRaw();
					int current = ni.getCurrentNode();
					if (DTM.NULL == current) {
						current = ni.nextNode();
					}
				}

				if (sortKeys.size() > 1) {
					NodeSortKey k2 = (NodeSortKey) sortKeys.elementAt(1);

					XObject r2 = (k2.m_selectPat).execute(xctxt, currentNode, k2.m_namespaceContext);

					if (k2.m_treatAsNumbers) {
						d = r2.num();
						m_key2Value = Double.valueOf(d);
					} else {
						String str1 = XslTransformEvaluationHelper.getStrVal(r2);
						m_key2Value = k2.m_col.getCollationKey(str1);
					}
				}
			}
		}
        finally {
		   xctxt.popCurrentNode();
        }
	}

	public GroupingKeyAndGroupPair getGroupingKeyAndGroupPair() {
		return m_groupingKeyAndGroupPair;
	}

	public void setGroupingKeyAndGroupPair(GroupingKeyAndGroupPair groupingKeyAndGroupPair) {
		this.m_groupingKeyAndGroupPair = groupingKeyAndGroupPair;
	}

}
