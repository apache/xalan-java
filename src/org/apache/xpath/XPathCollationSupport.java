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
package org.apache.xpath;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.transform.TransformerException;

/**
 * This class provides, collation support for XalanJ's XPath 3.1 
 * implementation.
 * 
 * Ref : https://www.w3.org/TR/xpath-functions-31/#collations
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *
 * @xsl.usage advanced
 */
public class XPathCollationSupport {
    
    public static final String UNICODE_CODEPOINT_COLLATION_URI = "http://www.w3.org/2005/xpath-functions/collation/codepoint";
    
    public static final String UNICODE_COLLATION_ALGORITHM_URI = "http://www.w3.org/2013/collation/UCA";
    
    public static final String HTML_ASCII_CASE_INSENSITIVE_COLLATION_URI = "http://www.w3.org/2005/xpath-functions/collation/html-ascii-case-insensitive";
    
    private final String UCA_KEYWORD_FALLBACK = "fallback";
    
    private final String UCA_FALLBACK_YES = "yes";    
    private final String UCA_FALLBACK_NO = "no";
    
    private final String UCA_KEYWORD_LANG = "lang";
    
    private final String UCA_KEYWORD_STRENGTH = "strength";
    
    private final String UCA_STRENGTH_PRIMARY = "primary";
    private final String UCA_STRENGTH_SECONDARY = "secondary";
    private final String UCA_STRENGTH_TERTIARY = "tertiary";
    private final String UCA_STRENGTH_IDENTICAL = "identical";
    
    private final String DEFAULT_UCA_FALLBACK_VALUE = UCA_FALLBACK_YES;
    
    private final Locale DEFAULT_UCA_LOCALE = Locale.getDefault();     
    
    private final String DEFAULT_UCA_STRENGTH_VALUE = UCA_STRENGTH_TERTIARY;
    
    private final String UCA_QUERY_STRING_PREFIX = "?";
    
    private final String UCA_QUERY_STRING_PARTS_DELIM = ";";
    
    private final String UCA_QUERY_STRING_PART_SUB_DELIM = "=";
    
    private List<UCAParameter> fUcaSupportedParameters = new ArrayList<UCAParameter>();
    
    private String fQueryStrFallbackValue = null;
    
    private String fDefaultCollationUri = null;
    
    /**
     * Class constructor.
     */
    public XPathCollationSupport(String defaultCollationUri) {
       fDefaultCollationUri = defaultCollationUri; 
       buildSupportedUCAParamList();  
    }
    
    /**
     * This method, compares two string values, using a specified collation.
     * 
     * @param str1               the first string
     * @param str2               the second string
     * @param collationUri       collation uri
     * 
     * @return                   the string comparison result represented as an integer value. The value -1
     *                           indicates that string 'str1' collates before string 'str2', the value 1
     *                           indicates that string 'str1' collates after string 'str2', the value 0
     *                           indicates that string 'str1' is equal to string 'str2'. 
     *                             
     * @throws javax.xml.transform.TransformerException
     */
    public int compareStringsUsingCollation(String str1, String str2, String collationUri) 
                                                                                  throws javax.xml.transform.TransformerException {
       int comparisonResult = 0;
       
       if (UNICODE_CODEPOINT_COLLATION_URI.equals(collationUri)) {
          comparisonResult = compareStringsUsingUnicodeCodepointCollation(str1, str2);
       }
       else if (collationUri.startsWith(UNICODE_COLLATION_ALGORITHM_URI)) {
          try {
             Collator strComparisonCollator = getUCACollatorFromCollationUri(collationUri);
             
             if (strComparisonCollator != null) {
                comparisonResult = strComparisonCollator.compare(str1, str2);                
             }
             else if (UCA_FALLBACK_YES.equals(fQueryStrFallbackValue)) {                    
                comparisonResult = compareStringsUsingCollation(str1, str2, fDefaultCollationUri);
             }
             else {
                throw new javax.xml.transform.TransformerException("FOCH0002 : The requested collation '" + collationUri + 
                                                                                                                   "' is not supported.");  
             }
          }
          catch (javax.xml.transform.TransformerException ex) {
             throw new javax.xml.transform.TransformerException(ex.getMessage());    
          }
            
          if (comparisonResult < 0) {
             comparisonResult = -1;  
          }
          else if (comparisonResult > 0) {
             comparisonResult = 1; 
          }    
       }
       else if (HTML_ASCII_CASE_INSENSITIVE_COLLATION_URI.equals(collationUri)) {
          int str1Len = str1.length();
          int str2Len = str2.length();
           
          int idx1 = 0;
          int idx2 = 0;
           
          while (true) {
             if (idx1 == str1Len) {
                if (idx2 == str2Len) {
                   comparisonResult = 0;
                   break;
                } else {
                   comparisonResult = -1;
                   break;
                }
             }
             
             if (idx2 == str2Len) {
                comparisonResult = 1;
                break;
             }
             
             int codepoint1 = str1.codePointAt(idx1);
             idx1 += 1;
             
             int codepoint2 = str2.codePointAt(idx2);
             idx2 += 1;
             
             if ((codepoint1 >= 'a') && (codepoint1 <= 'z')) {
                codepoint1 += 'A' - 'a';
             }
             
             if ((codepoint2 >= 'a') && (codepoint2 <= 'z')) {
                codepoint2 += 'A' - 'a';
             }
             
             int codepointDiff = codepoint1 - codepoint2;             
             if (codepointDiff != 0) {
                if (codepointDiff < 0) {
                   comparisonResult = -1;
                }
                else {
                   comparisonResult = 1; 
                }                
                break;
             }
          }          
       }
       else {
          throw new javax.xml.transform.TransformerException("FOCH0002 : The requested collation '" + collationUri + "' "
                                                                                                           + "is not supported."); 
       }
       
       return comparisonResult;
    }
    
    /**
     * Given a string, get a corresponding primitive integer array of
     * the codepoints of all the characters of the string in order.
     */
    public int[] getCodepointsFromString(String str) {
        int[] codePointsArr = null;
        
        codePointsArr = (str.codePoints()).toArray();
        
        return codePointsArr;
    }
    
    /**
     * This method compares, two string values using 'Unicode Codepoint Collation'
     * as specified by XPath 3.1 F&O spec.
     *
     * @param str1    the first string
     * @param str2    the second string
     * 
     * @return        an integer value denoting, the result of comparison
     */
    private int compareStringsUsingUnicodeCodepointCollation(String str1, String str2) {
       int comparisonResult = 0;
       
       int[] codePointsArr1 = getCodepointsFromString(str1);       
       int[] codePointsArr2 = getCodepointsFromString(str2);
       
       comparisonResult = compareCodepointArrays(codePointsArr1, codePointsArr2); 
       
       return comparisonResult; 
    }
    
    /**
     * This method compares two int[] arrays comprising unicode codepoints 
     * (corresponding to the strings to be compared), according to 'Unicode 
     * Codepoint Collation' as defined by XPath 3.1 F&O spec.
     */
    private int compareCodepointArrays(int[] codePointsArr1, int[] codePointsArr2) {
       
       int comparisonResult = 0;
       
       if (((codePointsArr1 == null) || (codePointsArr1.length == 0)) && 
           ((codePointsArr2 == null) || (codePointsArr2.length == 0))) {
          // both strings are equal
          comparisonResult = 0; 
       }
       else if (((codePointsArr1 == null) || (codePointsArr1.length == 0)) &&
                ((codePointsArr2 != null) && (codePointsArr2.length > 0))) {
          // the first string collates before the second one
          comparisonResult = -1; 
       }
       else if (((codePointsArr1 != null) && (codePointsArr1.length > 0)) &&
                ((codePointsArr2 == null) || (codePointsArr2.length == 0))) {
          // the first string collates after the second one
          comparisonResult = 1; 
       }
       else {
          // both the strings to be compared, have non empty code point 
          // arrays.
          int arr1FirstCodepoint = codePointsArr1[0];
          int arr2FirstCodepoint = codePointsArr2[0];
          if (arr1FirstCodepoint < arr2FirstCodepoint) {
             comparisonResult = -1;  
          }
          else if (arr1FirstCodepoint > arr2FirstCodepoint) {
             comparisonResult = 1; 
          }
          else {             
             List<Integer> list1 = getIntegerListFromIntArray(codePointsArr1);
             List<Integer> list2 = getIntegerListFromIntArray(codePointsArr2);
                 
             // get all, except the first item in the list 'list1'
             list1 = list1.subList(1, list1.size());
             
             // get all, except the first item in the list 'list2'
             list2 = list2.subList(1, list2.size());
             
             // recursive call to this function
             comparisonResult = compareCodepointArrays(getIntArrayFromIntegerList(list1), 
                                                                       getIntArrayFromIntegerList(list2));     
          }
       }
       
       return comparisonResult;
    }
    
    /**
     * Given an array of primitive integers, get the corresponding
     * list of type List<Integer>.
     */
    private List<Integer> getIntegerListFromIntArray(int[] intArr) {
       List<Integer> integerList = new ArrayList<Integer>();
       
       for (int idx = 0; idx < intArr.length; idx++) {
          integerList.add(Integer.valueOf(intArr[idx])); 
       }
       
       return integerList;
    }
    
    /**
     * Given a list of type List<Integer>, get the corresponding array
     * of primitive integers.  
     */
    private int[] getIntArrayFromIntegerList(List<Integer> integerList) {
       int[] intArray = new int[integerList.size()];
       
       for (int idx = 0; idx < integerList.size(); idx++) {
          intArray[idx] = (integerList.get(idx)).intValue();  
       }
       
       return intArray;
    }
    
    /**
     * This method implements, 'Unicode Collation Algorithm' as specified by XPath 3.1 F&O spec
     * (which in turn is based on UTS #10 [Unicode Technical Standard #10 : Unicode Collation
     * Algorithm]).
     * 
     * @param collationUri     the requested collation uri, during XPath 3.1 string comparisons,
     *                         and sorting of strings.
     *                         
     * @return                 a configured Java object of type java.text.Collator, that callers of
     *                         this method can use to do locale specific string comparisons.
     * 
     * @throws TransformerException
     */
    private Collator getUCACollatorFromCollationUri(String collationUri) throws TransformerException {
       
       Collator strComparisonCollator = null;
       
       try {
           if (collationUri.equals(UNICODE_COLLATION_ALGORITHM_URI)) {
              strComparisonCollator = getDefaultUCACollator();
           }
           else {
              int ucaUriPrefixLength = UNICODE_COLLATION_ALGORITHM_URI.length();              
              String uriAndQueryStrDelim = collationUri.substring(ucaUriPrefixLength, ucaUriPrefixLength + 1);
              
              if (UCA_QUERY_STRING_PREFIX.equals(uriAndQueryStrDelim)) {
                 String uriQueryStr = collationUri.substring(collationUri.indexOf(UCA_QUERY_STRING_PREFIX) + 1);
                 Map<String, String> queryStrMap = getUCAQueryStrComponents(uriQueryStr);
                 
                 String queryStrFallbackValue = queryStrMap.get(UCA_KEYWORD_FALLBACK);
                 String queryStrLangCode = queryStrMap.get(UCA_KEYWORD_LANG);
                 String queryStrStrengthValue = queryStrMap.get(UCA_KEYWORD_STRENGTH);
                    
                 if (queryStrFallbackValue == null) {
                    fQueryStrFallbackValue = DEFAULT_UCA_FALLBACK_VALUE;  
                 }
                 else {
                    fQueryStrFallbackValue = queryStrFallbackValue;  
                 }
                    
                 if (queryStrLangCode == null) {
                    queryStrLangCode = DEFAULT_UCA_LOCALE.getCountry(); 
                 }
                    
                 if (queryStrStrengthValue == null) {
                    queryStrStrengthValue = DEFAULT_UCA_STRENGTH_VALUE;  
                 }
                    
                 strComparisonCollator = Collator.getInstance(new Locale(queryStrLangCode));
                    
                 switch (queryStrStrengthValue) {
                    case UCA_STRENGTH_PRIMARY :
                       strComparisonCollator.setStrength(Collator.PRIMARY);
                       break;
                    case UCA_STRENGTH_SECONDARY :
                       strComparisonCollator.setStrength(Collator.SECONDARY);
                       break;
                    case UCA_STRENGTH_TERTIARY :
                       strComparisonCollator.setStrength(Collator.TERTIARY);
                       break;
                    case UCA_STRENGTH_IDENTICAL :
                       strComparisonCollator.setStrength(Collator.IDENTICAL);
                       break;
                    default:
                       // no op    
                 }
              }
              else {
                 throw new TransformerException("FOCH0002 : The first character if present after collation uri '" + 
                                                                        UNICODE_COLLATION_ALGORITHM_URI + "' must be "
                                                                        + "'" + UCA_QUERY_STRING_PREFIX + "', to denote the "
                                                                        + "start of query string within the collation uri.");   
              }
           }
       }
       catch (Exception ex) {
           throw new TransformerException(ex.getMessage());  
       }
       
       return strComparisonCollator;
    }
    
    /**
     * Get the java.text.Collator object, corresponding to XalanJ's 
     * default collation when using 'Unicode Collation Algorithm' (UCA).
     */
    private Collator getDefaultUCACollator() {
        
        Collator strComparisonCollator = Collator.getInstance(DEFAULT_UCA_LOCALE);
        
        switch (DEFAULT_UCA_STRENGTH_VALUE) {
            case UCA_STRENGTH_PRIMARY :
               strComparisonCollator.setStrength(Collator.PRIMARY);
               break;
            case UCA_STRENGTH_SECONDARY :
               strComparisonCollator.setStrength(Collator.SECONDARY); 
               break;
            case UCA_STRENGTH_TERTIARY :   
               strComparisonCollator.setStrength(Collator.TERTIARY);
               break;
            case UCA_STRENGTH_IDENTICAL :
               strComparisonCollator.setStrength(Collator.IDENTICAL);
               break;
            default :
               // no op
        }
        
        return strComparisonCollator;
    }
    
    /**
     * From the requested collation uri, build a corresponding java.util.Map
     * object representation.  
     */
    private Map<String, String> getUCAQueryStrComponents(String uriQueryStr) throws TransformerException {
       Map<String, String> queryStrMap = new HashMap<String, String>();
       
       String[] queryStrParts = uriQueryStr.split(UCA_QUERY_STRING_PARTS_DELIM);
       
       for (int idx = 0; idx < queryStrParts.length; idx++) {
          String queryStrPart = queryStrParts[idx];
          int delimIdx = queryStrPart.indexOf(UCA_QUERY_STRING_PART_SUB_DELIM);
          String keyword = queryStrPart.substring(0, delimIdx);
          String value = queryStrPart.substring(delimIdx + 1);
          if (!queryStrMap.containsKey(keyword)) {
             if (isUCAKeywordAndValueOk(keyword, value)) {
                queryStrMap.put(keyword, value);
             }
             else {
                throw new TransformerException("FOCH0002 : The keyword '"+keyword+"' and corresponding value '" + 
                                                                                      value + "', provided within the "
                                                                                      + "requested collation uri is not supported.");  
             }
          }
          else {
             throw new TransformerException("FOCH0002 : The keyword '" + keyword + "' occurs more than once, within "
                                                                                        + "the specified collation uri."); 
          }
       }
       
       return queryStrMap;
    }
    
    /**
     * Check whether, within requested collation uri's query string, the given
     * keyword and value is supported by XalanJ's XPath 3.1 processor.
     */
    private boolean isUCAKeywordAndValueOk(String keyword, String value) {
       boolean isUCAKeywordAndValueOk = false;
       
       for (int idx = 0; idx < fUcaSupportedParameters.size(); idx++) {
          UCAParameter ucaParameter = fUcaSupportedParameters.get(idx);
          if ((ucaParameter.getKeywordName()).equals(keyword)) {
             List<String> paramValues = ucaParameter.getParamValues();
             if (paramValues.contains(value)) {
                isUCAKeywordAndValueOk = true;
                break;
             }
          }
       }
        
       return isUCAKeywordAndValueOk; 
    }
    
    /**
     * This method configures, the collation support provided by
     * XalanJ XPath 3.1 implementation.
     */
    private void buildSupportedUCAParamList() {        
        List<String> fallbackList = new ArrayList<String>();
        fallbackList.add(UCA_FALLBACK_YES);
        fallbackList.add(UCA_FALLBACK_NO);
        UCAParameter ucaFallbackParam = new UCAParameter(UCA_KEYWORD_FALLBACK, fallbackList);
        
        String[] isoLanguageCodes = Locale.getISOLanguages();
        List<String> isoLanguageList = Arrays.asList(isoLanguageCodes);
        UCAParameter ucaLanguageParam = new UCAParameter(UCA_KEYWORD_LANG, isoLanguageList);
        
        List<String> collationStrengthList = new ArrayList<String>();
        collationStrengthList.add(UCA_STRENGTH_PRIMARY);
        collationStrengthList.add(UCA_STRENGTH_SECONDARY);
        collationStrengthList.add(UCA_STRENGTH_TERTIARY);
        collationStrengthList.add(UCA_STRENGTH_IDENTICAL);
        UCAParameter ucaCollationStrengthParam = new UCAParameter(UCA_KEYWORD_STRENGTH, collationStrengthList);
        
        fUcaSupportedParameters.add(ucaFallbackParam);
        fUcaSupportedParameters.add(ucaLanguageParam);
        fUcaSupportedParameters.add(ucaCollationStrengthParam);
    }
    
    /**
     * An object of this class, stores data for one 'Unicode 
     * Collation Algorithm' (UCA) collation uri query parameter/
     * keyword (the words 'parameter' and 'keyword' are synonym,
     * here).
     * 
     * XalanJ's XPath 3.1 implementation, currently supports only
     * following UCA parameters : 'fallback', 'lang', 'strength'. 
     */
    private class UCAParameter {        
        
        // Variable denoting, UCA keyword name (for e.g, 
        // 'fallback', 'lang', 'strength').
        private String keywordName;
        
        // Variable denoting, permitted values for an UCA keyword (for e.g, 
        // the 'fallback' parameter has possible values 'yes', 'no'. The 
        // 'strength' parameter has possible values 'primary', 'secondary',
        // 'tertiary', 'identical').
        private List<String> paramValues;
        
        public UCAParameter(String keywordName, List<String> paramValues) {
           this.keywordName = keywordName;
           this.paramValues = paramValues;
        }

        public String getKeywordName() {
            return keywordName;
        }

        public List<String> getParamValues() {
            return paramValues;
        }
        
    }

}
