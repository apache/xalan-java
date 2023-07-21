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
package org.apache.xalan.xslt.util;

import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * This class, has few utility methods, to help process the XPath 
 * data model sequences.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathSequenceHelper {
    
    /**
     * Given an XDM input sequence, expand that sequence to produce a new sequence
     * none of whose items are sequence with cardinality greater than one.
     * 
     * The caller of this method, needs to pass an XDM sequence to be expanded
     * as an argument, and another argument reference to get the result from this 
     * method.
     */
    public static void expandResultSequence(ResultSequence seqToBeExpanded, 
                                                                  ResultSequence finalResultSeq) {               
        for (int idx = 0; idx < seqToBeExpanded.size(); idx++) {
          XObject xObject = seqToBeExpanded.item(idx);
          if (xObject instanceof ResultSequence) {
             expandResultSequence((ResultSequence)xObject, finalResultSeq); 
          }
          else {
             finalResultSeq.add(xObject);
          }
       }
    }

}