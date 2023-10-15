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
package org.apache.xpath.xs.types;

/**
 * Base class for all calendar based classes.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public abstract class XSCalendarType extends XSCtrType {

    private static final long serialVersionUID = -6546129697566314664L;
    
    /**
     * Determine whether, two timezone values (represented as XSDuration objects) 
     * are equal. 
     */
    protected boolean isTimezoneEqual(XSDuration tz1, XSDuration tz2, 
                                               boolean isPopulatedFromFnDateFunc1, 
                                               boolean isPopulatedFromFnDateFunc2) {
         
        boolean isTimezoneEqual = false;         
        
        if (tz1 == null && tz2 == null) {
           isTimezoneEqual = true;
        }
        else if (tz1 != null && tz2 != null) {
           isTimezoneEqual = ((tz1.hours() == tz2.hours()) && 
                                                   (tz1.minutes() == tz2.minutes()) && 
                                                           (tz1.negative() == tz2.negative()));
        }
        else if (isPopulatedFromFnDateFunc1 || isPopulatedFromFnDateFunc2) {
            isTimezoneEqual = true; 
        }
        
        return isTimezoneEqual;
    }
	
}
