/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.templates;

import java.util.List;

/**
 * This class is used for an implementation, when xsl:for-each-group 
 * instruction has one or more xsl:sort child elements i.e, an 
 * xsl:for-each-group instruction like following,
 * 
 * <xsl:for-each-group ...>
 *    <xsl:sort ...
 * </xsl:for-each-group>
 * 
 * An object of this class, has complete information about one group 
 * (which is grouping key of the group, and all members of the group) 
 * among all the possible groups.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class GroupingKeyAndGroupPairForXslSort {
    
	/**
	 * Grouping key value of a group.
	 */
    public Object groupingKey;
    
    /**
     * XML node handles for all the members of a group.
     */
    public List<Integer> groupNodesDtmHandles;

    /**
     * Class constructor.
     * 
     * @param groupingKey				Grouping key value for a group. All members of a
     *                                  group have same value for grouping key.
     * @param groupNodesDtmHandles      XML node handles for members of a group
     */
    public GroupingKeyAndGroupPairForXslSort(Object groupingKey, List<Integer> groupNodesDtmHandles) {
        this.groupingKey = groupingKey;
        this.groupNodesDtmHandles = groupNodesDtmHandles;
    }

    public Object getGroupingKey() {
        return groupingKey;
    }

    public void setGroupingKey(Object groupingKey) {
        this.groupingKey = groupingKey;
    }

    public List<Integer> getGroupNodesDtmHandles() {
        return groupNodesDtmHandles;
    }

    public void setGroupNodesDtmHandles(List<Integer> groupNodesDtmHandles) {
        this.groupNodesDtmHandles = groupNodesDtmHandles;
    }

}
