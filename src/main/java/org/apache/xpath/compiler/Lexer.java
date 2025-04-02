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
/*
 * $Id$
 */
package org.apache.xpath.compiler;

import java.util.Vector;

import org.apache.xml.utils.ObjectVector;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * This class is in charge of lexical processing of the XPath
 * expression into tokens.
 */
class Lexer
{

  /**
   * The target XPath.
   */
  private Compiler m_compiler;

  /**
   * The prefix resolver to map prefixes to namespaces in the XPath.
   */
  PrefixResolver m_namespaceContext;

  /**
   * The XPath processor object.
   */
  XPathParser m_processor;

  /**
   * This value is added to each element name in the TARGETEXTRA
   * that is a 'target' (right-most top-level element name).
   */
  static final int TARGETEXTRA = 10000;

  /**
   * Ignore this, it is going away.
   * This holds a map to the m_tokenQueue that tells where the top-level elements are.
   * It is used for pattern matching so the m_tokenQueue can be walked backwards.
   * Each element that is a 'target', (right-most top level element name) has
   * TARGETEXTRA added to it.
   *
   */
  private int m_patternMap[] = new int[100];

  /**
   * Ignore this, it is going away.
   * The number of elements that m_patternMap maps;
   */
  private int m_patternMapSize;
  
  private int startSubstring = -1;
  
  private int posOfNSSep = -1;
  
  private boolean isStartOfPat = true;
  
  private boolean isAttrName = false;
  
  private boolean isNum = false;

  /**
   * Create a Lexer object.
   *
   * @param compiler The owning compiler for this lexer.
   * @param resolver The prefix resolver for mapping qualified name prefixes 
   *                 to namespace URIs.
   * @param xpathProcessor The parser that is processing strings to opcodes.
   */
  Lexer(Compiler compiler, PrefixResolver resolver,
        XPathParser xpathProcessor)
  {

    m_compiler = compiler;
    m_namespaceContext = resolver;
    m_processor = xpathProcessor;
  }

  /**
   * Walk through the expression and build a token queue, and a map of the top-level
   * elements.
   * @param pat XSLT Expression.
   *
   * @throws javax.xml.transform.TransformerException
   */
  void tokenize(String pat) throws javax.xml.transform.TransformerException
  {
    tokenize(pat, null);
  }

  /**
   * Walk through the expression and build a token queue, and a map of the top-level
   * elements.
   * @param pat XSLT Expression.
   * @param targetStrings Vector to hold Strings, may be null.
   *
   * @throws javax.xml.transform.TransformerException
   */
  void tokenize(String pat, Vector targetStrings)
          throws javax.xml.transform.TransformerException
  {

    m_compiler.m_currentPattern = pat;
    m_patternMapSize = 0; 

    // Use a conservative estimate that the OpMapVector needs about 
    // five time the length of the input path expression, to a maximum 
    // of MAXTOKENQUEUESIZE*5. If the OpMapVector needs to grow, grow
    // it freely (second argument to constructor).
    int initTokQueueSize = ((pat.length() < OpMap.MAXTOKENQUEUESIZE)
                                 ? pat.length() :  OpMap.MAXTOKENQUEUESIZE) * 5;
    m_compiler.m_opMap = new OpMapVector(initTokQueueSize,
                                         OpMap.BLOCKTOKENQUEUESIZE * 5,
                                         OpMap.MAPINDEX_LENGTH);

    int nChars = pat.length();    

    // Nesting of '[' so we can know if the given element should be
    // counted inside the m_patternMap.
    int nesting = 0;            

    // char[] chars = pat.toCharArray();
    for (int i = 0; i < nChars; i++)
    {
      char c = pat.charAt(i);

      switch (c)
      {
      case '\"' :
      {
        if (startSubstring != -1)
        {
          isNum = false;
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;

          if (-1 != posOfNSSep)
          {
            posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
          }
          else
          {
            addToTokenQueue(pat.substring(startSubstring, i));
          }
        }

        startSubstring = i;

        for (i++; (i < nChars) && ((c = pat.charAt(i)) != '\"'); i++);

        if (c == '\"' && i < nChars)
        {
          addToTokenQueue(pat.substring(startSubstring, i + 1));

          startSubstring = -1;
        }
        else
        {
          m_processor.error(XPATHErrorResources.ER_EXPECTED_DOUBLE_QUOTE,
                            null);  //"misquoted literal... expected double quote!");
        }
      }
      break;
      case '\'' :
        if (startSubstring != -1)
        {
          isNum = false;
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;

          if (-1 != posOfNSSep)
          {
            posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
          }
          else
          {
            addToTokenQueue(pat.substring(startSubstring, i));
          }
        }

        startSubstring = i;

        for (i++; (i < nChars) && ((c = pat.charAt(i)) != '\''); i++);

        if (c == '\'' && i < nChars)
        {
          addToTokenQueue(pat.substring(startSubstring, i + 1));

          startSubstring = -1;
        }
        else
        {
          m_processor.error(XPATHErrorResources.ER_EXPECTED_SINGLE_QUOTE,
                            null);  //"misquoted literal... expected single quote!");
        }
        break;
      case 0x0A :
      case 0x0D :
      case ' ' :
      case '\t' :
        if (startSubstring != -1)
        {
          isNum = false;
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;

          if (-1 != posOfNSSep)
          {
            posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
          }
          else
          {
            addToTokenQueue(pat.substring(startSubstring, i));
          }

          startSubstring = -1;
        }
        break;
      case '@' :
        isAttrName = true;

        // fall-through on purpose
      case '-' :
        if ('-' == c)
        {
          if (!(isNum || (startSubstring == -1)))
          {
            break;
          }

          isNum = false;
        }
        
        // fall-through on purpose
      case '(' :
      case '[' :
      case ')' :
      case ']' :
      case '{' :
      case '}' :
      case '?' :    
      case '|' :
        if ((pat.length() > (i + 1)) && (pat.charAt(i + 1) == '|')) {
          // To recognize the character sequence "||", as an XPath 
          // token.
          addToTokenQueue(pat.substring(i, i + 2));
          i += 1;
          break;
        }        
      case '/' :
      case '*' :
      case '+' :
      case '=' :
    	if ((pat.length() > (i + 1)) && (pat.charAt(i + 1) == '>')) {
           // To recognize the character sequence "=>", as an XPath 
           // token.
           addToTokenQueue(pat.substring(i, i + 2));
           i += 1;
           break;
        }
      case ',' :      
      case '\\' :  // Unused at the moment
      case '^' :   // Unused at the moment
      case '!' :
      case '$' :    	  
      case '<' :
      case '>' :
        if (startSubstring != -1)
        {
          isNum = false;
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;

          if (-1 != posOfNSSep)
          {
            posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
          }
          else
          {
        	 String str = pat.substring(startSubstring, i);
        	 ObjectVector tokenQueue = m_compiler.getTokenQueue();
        	 int tokenQueueSize = tokenQueue.size();
        	 boolean isAddToTokenQueue = true;
        	 if ((tokenQueueSize - 2) > 0) {
        		 String s1 = (tokenQueue.elementAt(tokenQueueSize - 2)).toString(); 
        		 String s2 = (tokenQueue.elementAt(tokenQueueSize - 1)).toString();
        		 if (str.equals(s1 + s2)) {
        			isAddToTokenQueue = false; 
        		 }
        	 }
        	 if (isAddToTokenQueue) {
                addToTokenQueue(pat.substring(startSubstring, i));
        	 }
          }

          startSubstring = -1;
        }
        else if (('/' == c) && isStartOfPat)
        {
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
        }
        else if ('*' == c)
        {
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;
        }

        if (0 == nesting)
        {
          if ('|' == c)
          {
            if (null != targetStrings)
            {
              recordTokenString(targetStrings);
            }

            isStartOfPat = true;
          }
        }

        if ((')' == c) || (']' == c))
        {
          nesting--;
        }
        else if (('(' == c) || ('[' == c))
        {
          nesting++;
        }

        addToTokenQueue(pat.substring(i, i + 1));
        break;
      case ':' :
        if (i > 0)
        {
          boolean isXPathMapExpr = isXPathMapExpr(pat);
          if (isXPathMapExpr) {
        	 // Handle ':' character as XPath map entry's key, value separator
        	 boolean isBreakFromSwitch = handleColonWithXdmMap(pat, i);
        	 if (isBreakFromSwitch) {
        	    break; 
        	 }
          }
          else if (posOfNSSep == (i - 1))
          {
            if (startSubstring != -1)
            {
              if (startSubstring < (i - 1))
                addToTokenQueue(pat.substring(startSubstring, i - 1));
            }

            isNum = false;
            isAttrName = false;
            startSubstring = -1;
            posOfNSSep = -1;

            addToTokenQueue(pat.substring(i - 1, i + 1));

            break;
          }
          else
          {
            posOfNSSep = i;
          }
        }

      // fall through on purpose
      default :
        if (-1 == startSubstring)
        {
          startSubstring = i;
          isNum = Character.isDigit(c);
        }
        else if (isNum)
        {
          isNum = Character.isDigit(c);
        }
      }
    }
    
    if (startSubstring != -1)
    {
      isNum = false;
      isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);

      if ((-1 != posOfNSSep) || 
         ((m_namespaceContext != null) && (m_namespaceContext.handlesNullPrefixes())))
      {
        posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, nChars);
      }
      else
      {
        addToTokenQueue(pat.substring(startSubstring, nChars));
      }
    }

    if (0 == m_compiler.getTokenQueueSize())
    {
      m_processor.error(XPATHErrorResources.ER_EMPTY_EXPRESSION, null);  //"Empty expression!");
    }
    else if (null != targetStrings)
    {
      recordTokenString(targetStrings);
    }

    m_processor.m_queueMark = 0;
  }

  /**
   * Record the current position on the token queue as long as
   * this is a top-level element.  Must be called before the
   * next token is added to the m_tokenQueue.
   *
   * @param nesting The nesting count for the pattern element.
   * @param isStart true if this is the start of a pattern.
   * @param isAttrName true if we have determined that this is an attribute name.
   *
   * @return true if this is the start of a pattern.
   */
  private boolean mapPatternElemPos(int nesting, boolean isStart,
                                    boolean isAttrName)
  {

    if (0 == nesting)
    {
      if(m_patternMapSize >= m_patternMap.length)
      {
        int patternMap[] = m_patternMap;
        int len = m_patternMap.length;
        m_patternMap = new int[m_patternMapSize + 100];
        System.arraycopy(patternMap, 0, m_patternMap, 0, len);
      } 
      if (!isStart)
      {
        m_patternMap[m_patternMapSize - 1] -= TARGETEXTRA;
      }
      m_patternMap[m_patternMapSize] =
        (m_compiler.getTokenQueueSize() - (isAttrName ? 1 : 0)) + TARGETEXTRA;

      m_patternMapSize++;

      isStart = false;
    }

    return isStart;
  }

  /**
   * Given a map pos, return the corresponding token queue pos.
   *
   * @param i The index in the m_patternMap.
   *
   * @return the token queue position.
   */
  private int getTokenQueuePosFromMap(int i)
  {

    int pos = m_patternMap[i];

    return (pos >= TARGETEXTRA) ? (pos - TARGETEXTRA) : pos;
  }

  /**
   * Reset token queue mark and m_token to a
   * given position.
   * @param mark The new position.
   */
  private final void resetTokenMark(int mark)
  {

    int qsz = m_compiler.getTokenQueueSize();

    m_processor.m_queueMark = (mark > 0)
                              ? ((mark <= qsz) ? mark - 1 : mark) : 0;

    if (m_processor.m_queueMark < qsz)
    {
      m_processor.m_token =
        (String) m_compiler.getTokenQueue().elementAt(m_processor.m_queueMark++);
      m_processor.m_tokenChar = m_processor.m_token.charAt(0);
    }
    else
    {
      m_processor.m_token = null;
      m_processor.m_tokenChar = 0;
    }
  }

  /**
   * Given a string, return the corresponding keyword token.
   *
   * @param key The keyword.
   *
   * @return An opcode value.
   */
  final int getKeywordToken(String key)
  {

    int tok;

    try
    {
      Integer itok = (Integer) Keywords.getKeyWord(key);

      tok = (null != itok) ? itok.intValue() : 0;
    }
    catch (NullPointerException npe)
    {
      tok = 0;
    }
    catch (ClassCastException cce)
    {
      tok = 0;
    }

    return tok;
  }

  /**
   * Record the current token in the passed vector.
   *
   * @param targetStrings Vector of string.
   */
  private void recordTokenString(Vector targetStrings)
  {

    int tokPos = getTokenQueuePosFromMap(m_patternMapSize - 1);

    resetTokenMark(tokPos + 1);

    if (m_processor.lookahead('(', 1))
    {
      int tok = getKeywordToken(m_processor.m_token);

      switch (tok)
      {
      case OpCodes.NODETYPE_COMMENT :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_COMMENT);
        break;
      case OpCodes.NODETYPE_TEXT :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_TEXT);
        break;
      case OpCodes.NODETYPE_NODE :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
        break;
      case OpCodes.NODETYPE_ROOT :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_ROOT);
        break;
      case OpCodes.NODETYPE_ANYELEMENT :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
        break;
      case OpCodes.NODETYPE_PI :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
        break;
      default :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
      }
    }
    else
    {
      if (m_processor.tokenIs('@'))
      {
        tokPos++;

        resetTokenMark(tokPos + 1);
      }

      if (m_processor.lookahead(':', 1))
      {
        tokPos += 2;
      }

      targetStrings.addElement(m_compiler.getTokenQueue().elementAt(tokPos));
    }
  }

  /**
   * Add a token to the token queue.
   *
   * @param s The token.
   */
  private final void addToTokenQueue(String s)
  {
    m_compiler.getTokenQueue().addElement(s);
  }

  /**
   * When a seperator token is found, see if there's a element name or
   * the like to map.
   *
   * @param pat The XPath name string.
   * @param startSubstring The start of the name string.
   * @param posOfNSSep The position of the namespace seperator (':').
   * @param posOfScan The end of the name index.
   *
   * @throws javax.xml.transform.TransformerException
   *
   * @return -1 always.
   */
  private int mapNSTokens(String pat, int startSubstring, int posOfNSSep,
                          int posOfScan)
           throws javax.xml.transform.TransformerException
 {

    String prefix = "";
    
    if ((startSubstring >= 0) && (posOfNSSep >= 0))
    {
       prefix = pat.substring(startSubstring, posOfNSSep);
    }    
    String uName;

    if ((null != m_namespaceContext) &&!prefix.equals("*")
                                            &&!prefix.equals("xmlns"))
    {
      try
      {
        if (prefix.length() > 0) {
           uName = ((PrefixResolver) m_namespaceContext).getNamespaceForPrefix(prefix);
        }
        else
        {
           if (((m_compiler.getTokenQueue()).indexOf("map") != -1)) 
           {
        	   // Handle XPath "map" expression string 
        	   addToTokenQueue(":");
        	   return -1;
           }
           else {
              uName = ((PrefixResolver) m_namespaceContext).getNamespaceForPrefix(prefix);
           }
        }
      }
      catch (ClassCastException cce)
      {
        uName = m_namespaceContext.getNamespaceForPrefix(prefix);
      }
    }
    else
    {
      uName = prefix;
    }
    
    // Handle XPath "let" expression variable binding strings like $varName := val, 
    // otherwise the character ':' as part of symbol := used for "let" expression 
    // variable binding shall be treated for XML namespace processing.
    boolean isLetExprNsCheckOk = false;
    if (((m_compiler.getTokenQueue()).indexOf("let") != -1)) 
    {
       if (":=".equals(pat.substring(posOfNSSep, posOfNSSep + 2)))
       {
          isLetExprNsCheckOk = true;
       }
    }
    
    if ((null != uName) && (uName.length() > 0))
    {
      if (!isLetExprNsCheckOk) 
      {
          addToTokenQueue(uName);
          addToTokenQueue(":");
    
          String s = pat.substring(posOfNSSep + 1, posOfScan);
    
          if (s.length() > 0)
            addToTokenQueue(s);
      }
      else 
      {          
          String xpathLetExprBindingVarNameStr = prefix; 
          if ("".equals(xpathLetExprBindingVarNameStr)) 
          {
             // Handle XPath "let" expression variable binding strings 
        	 // like $varName := val.
             addToTokenQueue(":");
          }
          else 
          {
             // Handle XPath "let" expression variable binding strings 
        	 // like $varName:= val.
             addToTokenQueue(xpathLetExprBindingVarNameStr);
             addToTokenQueue(":");    
          }   
      }
    }
    else
    {
        if (isLetExprNsCheckOk) 
        {
            String xpathLetExprBindingVarNameStr = prefix; 
            if ("".equals(xpathLetExprBindingVarNameStr)) 
            {
               // Handle XPath "let" expression variable binding strings 
               // like $varName := val.
               addToTokenQueue(":");
            }
            else 
            {
               // Handle XPath "let" expression variable binding strings 
               // like $varName:= val.
               addToTokenQueue(xpathLetExprBindingVarNameStr);
               addToTokenQueue(":");    
            }    
        }
        else 
        {
		    m_processor.errorForDOM3(XPATHErrorResources.ER_PREFIX_MUST_RESOLVE,
						                 new String[] {prefix});  //"Prefix must resolve to a namespace: {0}";
        }		
    }

    return -1;
  }
  
  /**
   * Method definition to check whether, an XPath expression string
   * represents start of an XPath 'map' expression or is a fn:transform function 
   * call with a literal 'map { ...' argument, by doing a regex prefix check of 
   * an XPath expression pattern string. 
   * 
   * @param pat		An XPath expression pattern string that this Lexer 
   *                is processing.
   */
  private boolean isXPathMapExpr(String pat) {
	  boolean isXPathMapExpr = false;
	  
	  String trimmedPat = pat.trim();
	  
	  if ((pat.length() > 0) && java.util.regex.Pattern.matches("map[\\s]*[\\{].*", trimmedPat)) {
		  isXPathMapExpr = true;	
	  }
	  
	  // Check whether XPath map expression is an argument of 
	  // XPath fn:transform function call.
	  if (!isXPathMapExpr) {
		 String[] strParts = trimmedPat.split("\\(");
		 if (strParts.length >= 2) {
			String strPart1 = strParts[0];
			String strPart2 = strParts[1];
			if ((strPart1.equals(Keywords.FUNC_TRANSFORM) || strPart1.endsWith(":" + Keywords.FUNC_TRANSFORM)) && 
					                                          java.util.regex.Pattern.matches("map[\\s]*[\\{].*", strPart2)) {
			   isXPathMapExpr = true;
			}
		 }
	  }
	  
	  return isXPathMapExpr;
  }
  
  /**
   * Method definition to handle the occurrence of character ':', within 
   * XPath map expression string.
   * 
   * @param pat   An XPath expression pattern string that this Lexer is 
   *              processing. The string value of this variable never 
   *              changes during this Lexer's processing session. 
   * @param i     The current string index within XPath expression pattern 
   *              string, during this Lexer's processing session.
   * @return      true if the control needs to break from enclosing switch 
   *              statement, false otherwise.
   */
  private boolean handleColonWithXdmMap(String pat, int i) {
	  
	 ObjectVector tokenQueue = m_compiler.getTokenQueue();
 	 
	 int tokenQueueSize = tokenQueue.size();	 
 	 if (tokenQueueSize == 2) {
         StringBuffer strBuff = new StringBuffer();
         for (int idx = (i - 1); idx >= 0; idx--) {
            char c1 = pat.charAt(idx);
            if (c1 != '{') {
         	   strBuff.append(c1); 
            }
            else {
         	   break; 
            }
         }
         String tokenStr = strBuff.toString();
         addToTokenQueue(tokenStr);
         addToTokenQueue(pat.substring(i, i + 1));
         
         startSubstring = -1;
         
         return true;                
 	 }
 	 else {
 		 String str1 = (tokenQueue.elementAt(tokenQueueSize - 1)).toString();
     	 String str2 = (tokenQueue.elementAt(tokenQueueSize - 2)).toString();
     	 String str3 = (tokenQueue.elementAt(tokenQueueSize - 3)).toString();	        	 
     	 if (",".equals(str2) || "map".equals(str3)) {	        		
     		String str4 = (tokenQueue.elementAt(tokenQueueSize - 1)).toString();
     		if ("$".equals(str4)) {
     			StringBuffer strBuff = new StringBuffer();
     			for (int idx = (i - 1); idx >= 0; idx--) {
     			   char c1 = pat.charAt(idx);
     			   if (c1 != '$') {
     				  strBuff.append(c1);  
     			   }
     			   else {
     				  break; 
     			   }
     			}
     			String str = strBuff.toString();
     			strBuff = new StringBuffer();
     			// Reverse the string
     			for (int idx = (str.length() - 1); idx >= 0; idx--) {
     			   char c1 = str.charAt(idx);
     			   strBuff.append(c1);
     			}
     			str = strBuff.toString();
     			if (str.length() > 0) {
     			   addToTokenQueue(str);
     			}
     		}
     		
     		addToTokenQueue(pat.substring(i, i + 1));
     		
     		return true;
     	 }
     	 else if (",".equals(str1)) {
     		StringBuffer strBuff = new StringBuffer();
     		for (int idx = (i - 1); idx >= 0; idx--) {
     		   char c1 = pat.charAt(idx);
     		   if (c1 != ',') {
     			   strBuff.append(c1); 
     		   }
     		   else {
     			   break;
     		   }
     		}

     		String str = strBuff.toString();
     		strBuff = new StringBuffer();
     		// Reverse the string
     		for (int idx = (str.length() - 1); idx >= 0; idx--) {
     		   char c1 = str.charAt(idx);
     		   strBuff.append(c1);
     		}
     		
     		str = strBuff.toString();
     		addToTokenQueue(str);
     		addToTokenQueue(pat.substring(i, i + 1));
     		
     		startSubstring = -1;
     		
     		return true;
     	 }
     	 else if (posOfNSSep == (i - 1)) {
     		if (startSubstring != -1)
             {
                if (startSubstring < (i - 1))
                  addToTokenQueue(pat.substring(startSubstring, i - 1));
             }

             isNum = false;
             isAttrName = false;
             startSubstring = -1;
             posOfNSSep = -1;

             addToTokenQueue(pat.substring(i - 1, i + 1));
             
             return true;
     	 }
     	 else
         {
             posOfNSSep = i;
         }
      }
 	 
 	  return false;
  }
  
}
