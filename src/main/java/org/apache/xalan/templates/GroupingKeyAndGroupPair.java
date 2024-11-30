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

/*
 * A list of objects of this class, shall be sorted as per xsl:sort 
 * element(s) within xsl:for-each-group element.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class GroupingKeyAndGroupPair {
    
    public Object groupingKey;
    
    public List<Integer> groupNodesDtmHandles;

    public GroupingKeyAndGroupPair(Object groupingKey, 
                                   List<Integer> groupNodesDtmHandles) {
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
