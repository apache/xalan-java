/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.transform.TransformerException;

/**
 * A class definition, that provides support for XPath 3.1 
 * collation implementation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *
 * @xsl.usage advanced
 */
public class XPathCollationSupport {
    
    public static final String UNICODE_CODEPOINT_COLLATION_URI = "http://www.w3.org/2005/xpath-functions/collation/codepoint";
    
    public static final String UNICODE_COLLATION_ALGORITHM_URI = "http://www.w3.org/2013/collation/UCA";
    
    public static final String HTML_ASCII_CASE_INSENSITIVE_COLLATION_URI = "http://www.w3.org/2005/xpath-functions/collation/html-ascii-case-insensitive";
    
    // Case insensitive collation, uri
    public static final String CASE_BLIND_COLLATION_URI1 = "http://www.w3.org/xslts/collation/caseblind";
    
    // Case insensitive collation, uri. This is synonym for the uri http://www.w3.org/xslts/collation/caseblind.  
    public static final String CASE_BLIND_COLLATION_URI2 = "http://www.w3.org/2010/09/qt-fots-catalog/collation/caseblind";
    
    private static final String UCA_KEYWORD_FALLBACK = "fallback";
    
    private static final String UCA_FALLBACK_YES = "yes";    
    private static final String UCA_FALLBACK_NO = "no";
    
    private static final String UCA_KEYWORD_LANG = "lang";
    
    private static final String UCA_KEYWORD_STRENGTH = "strength";
    
    private static final String UCA_STRENGTH_PRIMARY = "primary";
    private static final String UCA_STRENGTH_SECONDARY = "secondary";
    private static final String UCA_STRENGTH_TERTIARY = "tertiary";
    private static final String UCA_STRENGTH_IDENTICAL = "identical";
    
    private static final String UCA_KEYWORD_MAX_VARIABLE = "maxVariable";
    
    private static final String UCA_MAX_VARIABLE_SPACE = "space";
    private static final String UCA_MAX_VARIABLE_PUNCT = "punct";
    
    private static final String PUNC_MARKS_EN_LANG = ".?!,;:-()[]{}'\"'";
    
    private static final String UCA_KEYWORD_ALTERNATE = "alternate";
    
    private static final String UCA_ALTERNATE_NON_IGNORABLE = "non-ignorable";
    private static final String UCA_ALTERNATE_SHIFTED = "shifted";
    private static final String UCA_ALTERNATE_BLANKED = "blanked";
    
    private static final String UCA_KEYWORD_NUMERIC = "numeric";
    
    private static final String UCA_NUMERIC_YES = "yes";
    private static final String UCA_NUMERIC_NO = "no";
    
    private static final String UCA_FALLBACK_DEFAULT_VALUE = UCA_FALLBACK_YES;
    
    private static final Locale UCA_LOCALE_DEFAULT_VALUE = Locale.getDefault();     
    
    private static final String UCA_STRENGTH_DEFAULT_VALUE = UCA_STRENGTH_TERTIARY;
    
    private static final String UCA_MAX_VARIABLE_DEFAULT_VALUE = UCA_MAX_VARIABLE_PUNCT;
    
    private static final String UCA_ALTERNATE_DEFAULT_VALUE = UCA_ALTERNATE_NON_IGNORABLE;
    
    private static final String UCA_NUMERIC_DEFAULT_VALUE = UCA_NUMERIC_NO;
    
    private static final String UCA_QUERY_STRING_PREFIX = "?";
    
    private static final String UCA_QUERY_STRING_PARTS_DELIM = ";";
    
    private static final String UCA_QUERY_STRING_PART_SUB_DELIM = "="; 
    
    private static final String EN_LANG = "en";
    
    private List<UCAParameter> m_ucaParamSupportedList = new ArrayList<UCAParameter>();
    
    private String m_queryFallbackStr = null;
    
    private String m_defaultCollation = null;
    
    /**
     * Default constructor.
     */
    public XPathCollationSupport() {
    	// No op
    }
    
    /**
     * Class constructor.
     */
    public XPathCollationSupport(String defaultCollation) {
       m_defaultCollation = defaultCollation;
       
       buildSupportedUCAParamList();  
    }
    
    /**
     * Method definition, to compare two string values, 
     * using a specified collation.
     * 
     * @param str1               The supplied, first string
     * @param str2               The supplied, second string
     * @param collationUri       The supplied, collation uri
     * 
     * @return                   The string comparison result represented as an integer value. The value -1
     *                           indicates that string 'str1' collates before string 'str2', the value 1
     *                           indicates that string 'str1' collates after string 'str2', the value 0
     *                           indicates that string 'str1' is equal to string 'str2'. 
     *                             
     * @throws javax.xml.transform.TransformerException
     */
    public int compareStringsUsingCollation(String str1, String str2, String collationUri) 
                                                                                  throws javax.xml.transform.TransformerException {
       int result = 0;
       
       if (UNICODE_CODEPOINT_COLLATION_URI.equals(collationUri)) {
          result = compareStringsUsingUnicodeCodepointCollation(str1, str2);
       }
       else if (collationUri.startsWith(UNICODE_COLLATION_ALGORITHM_URI)) {
          try {
             Collator strCmpCollator = getUCACollatorFromCollationUri(collationUri);
             
             int idx = UNICODE_COLLATION_ALGORITHM_URI.length();              
             String uriAndQueryStrDelim = collationUri.substring(idx, idx + 1);

             Map<String, String> queryStrMap = null;

             if (UCA_QUERY_STRING_PREFIX.equals(uriAndQueryStrDelim)) {
            	 String uriQueryStr = collationUri.substring(collationUri.indexOf(UCA_QUERY_STRING_PREFIX) + 1);
            	 queryStrMap = getUCAQueryStrComponents(uriQueryStr);
             }
             
             if (strCmpCollator != null) {
                if (queryStrMap != null) {
                   String queryStrLangCode = queryStrMap.get(UCA_KEYWORD_LANG);
            	   String queryStrMaxVariable = queryStrMap.get(UCA_KEYWORD_MAX_VARIABLE);
                   String queryStrAlternate = queryStrMap.get(UCA_KEYWORD_ALTERNATE);
                   
                   String queryStrNumeric = queryStrMap.get(UCA_KEYWORD_NUMERIC);
                   
                   if (queryStrMaxVariable == null) {
                	  queryStrMaxVariable = UCA_MAX_VARIABLE_DEFAULT_VALUE;   
                   }
                   
                   if (queryStrAlternate == null) {
                	  queryStrAlternate = UCA_ALTERNATE_DEFAULT_VALUE;   
                   }
                   
                   if (queryStrNumeric == null) {
                	  queryStrNumeric = UCA_NUMERIC_DEFAULT_VALUE;
                   }
                   
                   int mapSize1 = queryStrMap.size();
                   
                   if (mapSize1 == 2) {
                	   if (EN_LANG.equals(queryStrLangCode) && UCA_ALTERNATE_BLANKED.equals(queryStrAlternate)) {
                		   /**
                		    * The collation uri's query string is like, ?lang=en;alternate=blanked
                		    * Therefore, whitespace characters and punctuation marks are 
                		    * considered insignificant, for comparison.
                		    */

                		   int size1 = PUNC_MARKS_EN_LANG.length();

                		   for (int idx2 = 0; idx2 < size1; idx2++) {
                			   char c1 = PUNC_MARKS_EN_LANG.charAt(idx2);

                			   str1 = str1.replaceAll("\\s", "");
                			   str2 = str2.replaceAll("\\s", "");

                			   str1 = str1.replace(c1 + "", "");
                			   str2 = str2.replace(c1 + "", "");
                		   }                	  
                	   }
                	   else if (EN_LANG.equals(queryStrLangCode) && (queryStrNumeric != null)) {
                		   if (UCA_NUMERIC_YES.equals(queryStrNumeric)) {
                			  result = compareStringNumeric(str1, str2, strCmpCollator);
                			  
                			  return result;
                		   }
                		   else if (UCA_NUMERIC_NO.equals(queryStrNumeric)) {
                			  result = strCmpCollator.compare(str1, str2);
           					
           					  return result;
                		   }
                	   }
                   }
                   else if (mapSize1 == 1) {
                	   if (EN_LANG.equals(queryStrLangCode)) {
                		   result = strCmpCollator.compare(str1, str2);

                		   return result; 
                	   }
                   }
                   else if (mapSize1 == 1) {
                	   if (UCA_NUMERIC_YES.equals(queryStrNumeric)) {
                		   result = compareStringNumeric(str1, str2, strCmpCollator);

                		   return result; 
                	   }
                   }
                }
            	
            	result = strCmpCollator.compare(str1, str2);                
             }
             else if (UCA_FALLBACK_YES.equals(m_queryFallbackStr)) {                    
                result = compareStringsUsingCollation(str1, str2, m_defaultCollation);
             }
             else {
                throw new javax.xml.transform.TransformerException("FOCH0002 : The requested collation '" + collationUri + "' is not supported.");  
             }
          }
          catch (javax.xml.transform.TransformerException ex) {
             throw new javax.xml.transform.TransformerException(ex.getMessage());    
          }
            
          if (result < 0) {
             result = -1;  
          }
          else if (result > 0) {
             result = 1; 
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
                   result = 0;
                   
                   break;
                } 
                else {
                   result = -1;
                   
                   break;
                }
             }
             
             if (idx2 == str2Len) {
                result = 1;
                
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
                   result = -1;
                }
                else {
                   result = 1; 
                }  
                
                break;
             }
          }          
       }
       else if (CASE_BLIND_COLLATION_URI1.equals(collationUri) || CASE_BLIND_COLLATION_URI2.equals(collationUri)) {
    	  result = str1.compareToIgnoreCase(str2);
       }
       else {
          throw new javax.xml.transform.TransformerException("FOCH0002 : The requested collation '" + collationUri + "' "
                                                                                                           + "is not supported."); 
       }
       
       return result;
    }

	/**
     * Method definition, to get an integer array having codepoints of
     * all the characters for the supplied string.
     * 
     * @param str						The supplied string value
     * @return                          The codepoint array
     */
    public int[] getCodepointsFromString(String str) {
        
    	int[] resultArr = null;
        
        resultArr = (str.codePoints()).toArray();
        
        return resultArr;
    }
    
    /**
     * Method definition, implementing 'Unicode Collation Algorithm' as specified by XPath 3.1 F&O spec
     * (which in turn is based upn UTS #10 [Unicode Technical Standard #10 : Unicode Collation
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
    public Collator getUCACollatorFromCollationUri(String collationUri) throws TransformerException {
       
       Collator result = null;
       
       try {
           if (collationUri.equals(UNICODE_COLLATION_ALGORITHM_URI)) {
              result = getDefaultUCACollator();
           }
           else {
              int idx = UNICODE_COLLATION_ALGORITHM_URI.length();              
              String uriAndQueryStrDelim = collationUri.substring(idx, idx + 1);
              
              if (UCA_QUERY_STRING_PREFIX.equals(uriAndQueryStrDelim)) {
                 String uriQueryStr = collationUri.substring(collationUri.indexOf(UCA_QUERY_STRING_PREFIX) + 1);
                 Map<String, String> queryStrMap = getUCAQueryStrComponents(uriQueryStr);
                 
                 String queryStrFallbackValue = queryStrMap.get(UCA_KEYWORD_FALLBACK);
                 String queryStrLangCode = queryStrMap.get(UCA_KEYWORD_LANG);
                 String queryStrStrengthValue = queryStrMap.get(UCA_KEYWORD_STRENGTH);
                    
                 if (queryStrFallbackValue == null) {
                    m_queryFallbackStr = UCA_FALLBACK_DEFAULT_VALUE;  
                 }
                 else {
                    m_queryFallbackStr = queryStrFallbackValue;  
                 }
                    
                 if (queryStrLangCode == null) {
                    queryStrLangCode = UCA_LOCALE_DEFAULT_VALUE.getCountry(); 
                 }
                    
                 if (queryStrStrengthValue == null) {
                    queryStrStrengthValue = UCA_STRENGTH_DEFAULT_VALUE;  
                 }
                    
                 result = Collator.getInstance(new Locale(queryStrLangCode));
                    
                 switch (queryStrStrengthValue) {
                    case UCA_STRENGTH_PRIMARY :
                       result.setStrength(Collator.PRIMARY);
                       
                       break;
                    case UCA_STRENGTH_SECONDARY :
                       result.setStrength(Collator.SECONDARY);
                       
                       break;
                    case UCA_STRENGTH_TERTIARY :
                       result.setStrength(Collator.TERTIARY);
                       
                       break;
                    case UCA_STRENGTH_IDENTICAL :
                       result.setStrength(Collator.IDENTICAL);
                       
                       break;
                    default:
                       // No op    
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
       
       return result;
    }
    
    /**
     * Method definition, to check whether the supplied collation
     * uri is supported by Xalan-J XSL 3 implementation.
     * 
     * @param collationUri                    The supplied collation uri
     * @return                                Boolean value true or false
     */
    public static boolean isCollationSupported(String collationUri) {
    	
    	boolean result = false;

    	if (UNICODE_CODEPOINT_COLLATION_URI.equals(collationUri) || collationUri.startsWith(UNICODE_COLLATION_ALGORITHM_URI)
    															 || HTML_ASCII_CASE_INSENSITIVE_COLLATION_URI.equals(collationUri)
    			                                                 || CASE_BLIND_COLLATION_URI1.equals(collationUri)
    			                                                 || CASE_BLIND_COLLATION_URI2.equals(collationUri)) {
    		result = true;
    	}

    	return result;
    }
    
    /**
     * Method definition, to compare the supplied string values 
     * using 'Unicode Codepoint Collation', as specified by 
     * XPath 3.1 F&O spec.
     *
     * @param str1    					The supplied, first string value
     * @param str2    					The supplied, second string value
     * 
     * @return        					An integer value comparison result
     */
    private int compareStringsUsingUnicodeCodepointCollation(String str1, String str2) {
       
       int result = 0;
       
       int[] codePointArr1 = getCodepointsFromString(str1);       
       int[] codePointArr2 = getCodepointsFromString(str2);
       
       result = compareCodepointArrays(codePointArr1, codePointArr2); 
       
       return result; 
    }
    
    /**
     * Method definition, to compare the supplied integer arrays, containing 
     * unicode codepoint values for the strings to be compared, as specified 
     * by 'Unicode Codepoint Collation' algorithm within XPath 3.1 F&O spec. 
     * 
     * @param codePointArr1                 The supplied, first codepoint array
     * @param codePointArr2                 The supplied, second codepoint array
     * @return                              Integer value, corresponding to the
     *                                      comparison result.
     */
    private int compareCodepointArrays(int[] codePointArr1, int[] codePointArr2) {
       
       int result = 0;
       
       if (((codePointArr1 == null) || (codePointArr1.length == 0)) && 
           ((codePointArr2 == null) || (codePointArr2.length == 0))) {
          // The string values are equal
          result = 0; 
       }
       else if (((codePointArr1 == null) || (codePointArr1.length == 0)) &&
                ((codePointArr2 != null) && (codePointArr2.length > 0))) {
          // The first string collates before, the second string
          result = -1; 
       }
       else if (((codePointArr1 != null) && (codePointArr1.length > 0)) &&
                ((codePointArr2 == null) || (codePointArr2.length == 0))) {
    	  // The first string collates after, the second string
          result = 1; 
       }
       else {
          // The supplied string, codepoint arrays are non-empty
    	   
          int arr1FirstCodepoint = codePointArr1[0];
          int arr2FirstCodepoint = codePointArr2[0];
          
          if (arr1FirstCodepoint < arr2FirstCodepoint) {
             result = -1;  
          }
          else if (arr1FirstCodepoint > arr2FirstCodepoint) {
             result = 1; 
          }
          else {             
             List<Integer> list1 = getIntegerListFromIntArray(codePointArr1);
             List<Integer> list2 = getIntegerListFromIntArray(codePointArr2);
                 
             // Get all, except the first item wthin the list 'list1'
             list1 = list1.subList(1, list1.size());
             
             // Get all, except the first item within the list 'list2'
             list2 = list2.subList(1, list2.size());
             
             result = compareCodepointArrays(getIntArrayFromIntegerList(list1), 
                                                                       getIntArrayFromIntegerList(list2));     
          }
       }
       
       return result;
    }
    
    /**
     * Method definition, to get list of java.lang.Integer objects
     * from the supplied primitive integer array. 
     * 
     * @param intArr                   The supplied primitive integer
     *                                 array
     * @return                         List of java.lang.Integer objects 
     */
    private List<Integer> getIntegerListFromIntArray(int[] intArr) {
       
       List<Integer> resultList = new ArrayList<Integer>();
       
       for (int idx = 0; idx < intArr.length; idx++) {
          resultList.add(Integer.valueOf(intArr[idx])); 
       }
       
       return resultList;
    }
    
    /**
     * Method definition, to get primitive integer array
     * from the supplied list of java.lang.Integer objects.  
     * 
     * @param intList                  List of java.lang.Integer objects
     * @return                         Primitive integer array 
     */
    private int[] getIntArrayFromIntegerList(List<Integer> intList) {
       
       int[] resultArr = new int[intList.size()];
       
       for (int idx = 0; idx < intList.size(); idx++) {
          resultArr[idx] = (intList.get(idx)).intValue();  
       }
       
       return resultArr;
    }
    
    /**
     * Method definition, to get a populated java.text.Collator 
     * object, corresponding to Xalan-J default collation.
     * 
     * This is used, by Xalan-J XSL 3 implementation, when using
     * 'Unicode Collation Algorithm' (UCA).
     */
    private Collator getDefaultUCACollator() {
        
        Collator collatorResult = Collator.getInstance(UCA_LOCALE_DEFAULT_VALUE);
        
        switch (UCA_STRENGTH_DEFAULT_VALUE) {
            case UCA_STRENGTH_PRIMARY :
               collatorResult.setStrength(Collator.PRIMARY);
               
               break;
            case UCA_STRENGTH_SECONDARY :
               collatorResult.setStrength(Collator.SECONDARY); 
               
               break;
            case UCA_STRENGTH_TERTIARY :   
               collatorResult.setStrength(Collator.TERTIARY);
               
               break;
            case UCA_STRENGTH_IDENTICAL :
               collatorResult.setStrength(Collator.IDENTICAL);
               
               break;
            default :
               // No op
        }
        
        return collatorResult;
    }
    
    /**
     * Method definition, to get java.util.Map object instance, for the
     * supplied uri query string. 
     * 
     * @param uriQueryStr                      The supplied uri query string
     * @return                                 The java.util.Map object instance 
     * @throws TransformerException
     */
    private Map<String, String> getUCAQueryStrComponents(String uriQueryStr) throws TransformerException {
       
       Map<String, String> mapResult1 = new HashMap<String, String>();
       
       String[] queryStrParts = uriQueryStr.split(UCA_QUERY_STRING_PARTS_DELIM);
       
       for (int idx = 0; idx < queryStrParts.length; idx++) {
          String queryStrPart = queryStrParts[idx];
          int delimIdx = queryStrPart.indexOf(UCA_QUERY_STRING_PART_SUB_DELIM);
          String keyword = queryStrPart.substring(0, delimIdx);
          String value = queryStrPart.substring(delimIdx + 1);
          
          if (!mapResult1.containsKey(keyword)) {
             if (isUCAKeywordAndValueOk(keyword, value)) {
                mapResult1.put(keyword, value);
             }
             else {
                throw new TransformerException("FOCH0004 : The keyword '" + keyword + "' and corresponding value '" + 
                                                                                                          value + "', provided within the "
                                                                                                          + "requested collation uri is not supported.");  
             }
          }
          else {
             throw new TransformerException("FOCH0002 : The keyword '" + keyword + "' occurs more than once, within "
                                                                                                            + "the specified collation uri."); 
          }
       }
       
       return mapResult1;
    }
    
    /**
     * Method definition, to check whether, within requested collation uri
     * query string, the given keyword and value is supported by Xalan-J 
     * XSL 3 processor. 
     * 
     * @param keyword                     The supplied keyword string value
     * @param value                       The supplied, value string
     * @return                            Boolean value true or false
     */
    private boolean isUCAKeywordAndValueOk(String keyword, String value) {
       
       boolean result = false;
       
       int size1 = m_ucaParamSupportedList.size();
       
       for (int idx = 0; idx < size1; idx++) {
          UCAParameter ucaParameter = m_ucaParamSupportedList.get(idx);
          
          if ((ucaParameter.getKeywordName()).equals(keyword)) {
             List<String> paramValues = ucaParameter.getParamValues();
             
             if (paramValues.contains(value)) {
                result = true;
                
                break;
             }
          }
       }
        
       return result; 
    }
    
    /**
     * Method definition, to configure the collation support provided by
     * Xalan-J XSL 3 implementation.  
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
        
        List<String> maxVariableList = new ArrayList<String>();
        
        maxVariableList.add(UCA_MAX_VARIABLE_SPACE);
        maxVariableList.add(UCA_MAX_VARIABLE_PUNCT);
        
        UCAParameter ucaMaxVariableParam = new UCAParameter(UCA_KEYWORD_MAX_VARIABLE, maxVariableList);
        
        List<String> alternateList = new ArrayList<String>();
        
        alternateList.add(UCA_ALTERNATE_NON_IGNORABLE);
        alternateList.add(UCA_ALTERNATE_SHIFTED);
        alternateList.add(UCA_ALTERNATE_BLANKED);
        
        UCAParameter ucaAlternateParam = new UCAParameter(UCA_KEYWORD_ALTERNATE, alternateList);
        
        List<String> numericList = new ArrayList<String>();
        
        numericList.add(UCA_NUMERIC_YES);
        numericList.add(UCA_NUMERIC_NO);
        
        UCAParameter ucaNumericParam = new UCAParameter(UCA_KEYWORD_NUMERIC, numericList);
        
        m_ucaParamSupportedList.add(ucaFallbackParam);
        m_ucaParamSupportedList.add(ucaLanguageParam);
        m_ucaParamSupportedList.add(ucaCollationStrengthParam);
        m_ucaParamSupportedList.add(ucaMaxVariableParam);
        m_ucaParamSupportedList.add(ucaAlternateParam);
        m_ucaParamSupportedList.add(ucaNumericParam);
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
        
        // Variable representing, UCA keyword name (for e.g, 
        // 'fallback', 'lang', 'strength').
        private String m_keyword;
        
        /**
         * Variable representing, permitted values for an UCA keyword (for e.g,
         * the 'fallback' parameter has possible values 'yes', 'no'. The
         * 'strength' parameter has possible values 'primary', 'secondary',
         * 'tertiary', 'identical').
         */
        private List<String> m_paramList;
        
        public UCAParameter(String keyword, List<String> paramList) {
           this.m_keyword = keyword;
           this.m_paramList = paramList;
        }

        public String getKeywordName() {
            return m_keyword;
        }

        public List<String> getParamValues() {
            return m_paramList;
        }
        
    }
    
    /**
     * Method definition, to get first index value for
     * numeric character within the supplied string value.
     * 
     * @param str1                       The supplied string value
     * @return                           An integer value, greater than
     *                                   -1 if a numeric character is found
     *                                   within the supplied string value,
     *                                   otherwise an integer value -1.
     */
    private int indexOfNumericCharacter(String str1) {
		
    	int result = -1;
    	
    	int size1 = str1.length();
    	
    	if (size1 > 0) {
    	   for (int idx = 0; idx < size1; idx++) {
    		 char chr1 = str1.charAt(idx);
    		 
    		 try {
    			new Double(chr1 + "");    			
    			result = idx;
    			
    			break;
    		 }
    		 catch (NumberFormatException ex) {
    			// No op
    		 }
    	   }
    	}
		
		return result;
	}
    
    /**
     * Method definition, to compare two string values, as if
     * the supplied string value's all the characters are numeric.
     * 
     * @param str1                        The first supplied string value
     * @param str2                        The second supplied string value
     * @param strCmpCollator              A non-null java.text.Collator object
     *                                    instance.     
     * @return                            Integer value -1, 0, 1, for string comparison
     *                                    results as less than, equal or greater than.
     */
	private int compareStringNumeric(String str1, String str2, Collator strCmpCollator) {
		
		int result = 0;

		boolean isStrValueNumeric = true;

		try {
			Double dbl1 = Double.valueOf(str1);
			Double dbl2 = Double.valueOf(str2);
			
			// Both the supplied string values are
			// numeric.

			if (dbl1 < dbl2) {
				result = -1; 
			}
			else if (dbl1 > dbl2) {
				result = 1;
			}

			return result;
		}
		catch (NumberFormatException ex) {
			isStrValueNumeric = false; 
		}

		if (!isStrValueNumeric) {
			int idx1 = indexOfNumericCharacter(str1); 
			int idx2 = indexOfNumericCharacter(str2);

			if ((idx1 != -1) && (idx1 == idx2)) {
				/**
				 * Both string values have non-null equal length non-numeric 
				 * prefix, and numeric suffix that've to be compared 
				 * numerically. The result of numeric suffix comparison 
				 * is the result of string comparison by this method. For e.g, 
				 * the supplied string values are of type abc12, abc123. 
				 */
				
				String str1_p = str1.substring(idx1);  
				String str1_q = str2.substring(idx2);

				try {
					Double dbl1 = Double.valueOf(str1_p);
					Double dbl2 = Double.valueOf(str1_q);

					if (dbl1 < dbl2) {
						result = -1; 
					}
					else if (dbl1 > dbl2) {
						result = 1;
					}

					return result;
				}
				catch (NumberFormatException ex) {
					// No op
				}

				result = strCmpCollator.compare(str1, str2);

				return result;
			}
		}

		return result;
	}

}
