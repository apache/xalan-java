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
package org.apache.xalan.xslt.util;

/**
 * This class definition, specifies few utility methods for 
 * number information handling.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage general
 */
public class NumberUtil {
	
	
	/**
	 * Method definition, to transform a positive integer
	 * value to its string ordinal representation.
	 * 
	 * @param num                 The supplied integer value
	 * @return                    The result string ordinal value
	 */
	public static String getOrdinalNumber(int num) {

		String result = null;

		String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
		switch (num % 100) {
		case 11:
		case 12:
		case 13:
			result = (num + "th");
		default:
			result = (num + suffixes[num % 10]);
		}

		return result;
	}
    
    /**
     * Method definition, to convert a decimal integer
     * to a roman numeral, within the decimal integer range
     * 1 upto 3999.
     * 
     * @param num					  The supplied decimal integer value
     * @param smallcase               Boolean value, indicating whether resulting
     *                                roman numeral should be with small or capital
     *                                case.
     * @return                        The computed roman numeral
     */
    public static String getRomanNumeral(int num, boolean smallcase) {

    	String result = null;

    	if ((num < 1) || (num > 3999)) {
    		result = "invalid_number_range";
    	}

    	int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

    	String[] romanChars = null;
    	if (smallcase) {
    		romanChars = new String[] {"m", "cm", "d", "cd", "c", "xc", "l", "xl", "x", "ix", "v", "iv", "i"}; 
    	}
    	else {
    		romanChars = new String[] {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
    	}

    	StringBuilder strBuff = new StringBuilder();
    	for (int idx = 0; idx < values.length; idx++) {
    		while (num >= values[idx]) {
    			num -= values[idx];
    			strBuff.append(romanChars[idx]);
    		}
    	}

    	result = strBuff.toString(); 

    	return result;
    }

}
