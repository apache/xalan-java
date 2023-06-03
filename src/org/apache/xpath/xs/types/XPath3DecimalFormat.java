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
package org.apache.xpath.xs.types;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.util.Locale;

/**
 * This class provides an XPath 3.0 specific implementation of decimal 
 * number string format, to handle few of the XPath specific decimal number 
 * string formatting requirements.
 * 
 * This class extends the Java class java.text.DecimalFormat and inherits 
 * most of its significant implementation behavior. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced 
 */
public class XPath3DecimalFormat extends DecimalFormat {

    private static final long serialVersionUID = 5055460273589749811L;

    private static final String NEG_INFINITY = "-INF";
	
	private static final String POS_INFINITY = "INF";

	/*
	 * Class constructor.
	 */
	public XPath3DecimalFormat(String patternStr) {
		super(patternStr, new DecimalFormatSymbols(Locale.getDefault()));
	}

	/**
	 * Format an object representing a number, to a string value.
	 * 
	 * @param   numberVal    a number object, that needs to be 
	 *                       formatted to string 
	 */
	public String performStrFormatting(Object numberVal) {
	    String curPattern = toPattern();
        String newPattern = curPattern.replaceAll("E0", "");
        
        if (numberVal instanceof Float) {
            return formatFloatValue(numberVal, curPattern, newPattern);
        }
        
        if (numberVal instanceof Double) {
            return formatDoubleValue(numberVal, curPattern, newPattern);
        }
        
        return super.format(numberVal, new StringBuffer(), new FieldPosition(0)).
                                                                    toString();
	}
	
	/*
     * Format a float numeric value, to a string.
     */
	private String formatFloatValue(Object floatVal, String curPattern, 
	                                                          String newPattern) {
        Float floatValue = (Float) floatVal;
        
        if (floatValue.floatValue() == Float.NEGATIVE_INFINITY) {
            return NEG_INFINITY;
        }
        
        if (floatValue.floatValue() == Float.POSITIVE_INFINITY) {
            return POS_INFINITY;
        }        
        
        if (floatValue.floatValue() > -1E6f && floatValue.floatValue() < 1E6f) {            
            applyPattern(newPattern);
        } 
        else if (floatValue.floatValue() <= -1E6f) {
            applyPattern(curPattern.replaceAll("0\\.#", "0.0" ));
        }
        
        return format(floatVal, new StringBuffer(), new FieldPosition(0)).toString();
    }

	/*
	 * Format a double numeric value, to a string.
	 */
	private String formatDoubleValue(Object doubleVal, String curPattern, 
	                                                              String newPattern) {
		Double doubleValue = (Double) doubleVal;
		
		if (doubleValue.doubleValue() == Double.NEGATIVE_INFINITY) {
			return NEG_INFINITY;
		}
		
		if (doubleValue.doubleValue() == Double.POSITIVE_INFINITY) {
			return POS_INFINITY;
		}
		
		BigDecimal doubValue = new BigDecimal((((Double) doubleVal)).
		                                                      doubleValue());
        BigDecimal minValue = new BigDecimal("-1E6");
        BigDecimal maxValue = new BigDecimal("1E6");
        
        if (doubValue.compareTo(minValue) > 0 && doubValue.compareTo(maxValue) < 0) {
            applyPattern(newPattern);
        } 
        else {
            applyPattern(curPattern.replaceAll("0\\.#", "0.0"));
        }
		
		return format(doubleVal, new StringBuffer(), new FieldPosition(0)).
		                                                            toString();
	}
	
}
