/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * @author Igor Hersht, igorh@ca.ibm.com
 */

package org.apache.xml.utils;

import java.util.Vector;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.text.CollationElementIterator;
import java.util.Locale;
import java.text.CollationKey;


/**
* International friendly string comparison with case-order
*/
public class StringComparable implements Comparable  {
    
     public final static int UNKNOWN_CASE = -1;
     public final static int UPPER_CASE = 1;
     public final static int LOWER_CASE = 2;
     
     private  String m_text;
     private  Locale m_locale;
     private RuleBasedCollator m_collator;
     private String m_caseOrder;
     private int m_mask = 0xFFFFFFFF; 
     
    public StringComparable(final String text, final Locale locale, final Collator collator, final String caseOrder){
         m_text =  text;
         m_locale = locale;
         m_collator = (RuleBasedCollator)collator;
         m_caseOrder = caseOrder;
         m_mask = getMask(m_collator.getStrength());
    }
  
   public final static Comparable getComparator( final String text, final Locale locale, final Collator collator, final String caseOrder){
       if((caseOrder == null) ||(caseOrder.length() == 0)){// no case-order specified
            return  ((RuleBasedCollator)collator).getCollationKey(text);
       }else{
            return new StringComparable(text, locale, collator, caseOrder);
       }       
   }
   
   public final String toString(){return m_text;}
   
   public int compareTo(Object o) {
   final String pattern = ((StringComparable)o).toString();
   if(m_text.equals(pattern)){//Code-point equals 
      return 0;
   }
   final int savedStrength = m_collator.getStrength(); 
   int comp = 0;
      // Is there difference more significant than case-order?     
     if(((savedStrength == Collator.PRIMARY) || (savedStrength == Collator.SECONDARY))){  
         comp = m_collator.compare(m_text, pattern );     
     }else{// more than SECONDARY
         m_collator.setStrength(Collator.SECONDARY);
         comp = m_collator.compare(m_text, pattern );
         m_collator.setStrength(savedStrength);
     }
     if(comp != 0){//Difference more significant than case-order 
        return comp ; 
     }      
        
      // No difference more significant than case-order.     
      // Find case difference
       comp = getCaseDiff(m_text, pattern);
       if(comp != 0){  
           return comp;
       }else{// No case differences. Less significant difference could exist 
            return m_collator.compare(m_text, pattern );
       }      
  }
  
 
  private final int getCaseDiff (final String text, final String pattern){
     final int savedStrength = m_collator.getStrength();
     final int savedDecomposition = m_collator.getDecomposition();
     m_collator.setStrength(Collator.TERTIARY);// not to ignore case  
     m_collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION );// corresponds NDF
     
    final int diff[] =getFirstCaseDiff (text, pattern, m_locale);
    m_collator.setStrength(savedStrength);// restore
    m_collator.setDecomposition(savedDecomposition); //restore
    if(diff != null){  
       if((m_caseOrder).equals("upper-first")){
            if(diff[0] == UPPER_CASE){
                return -1;
            }else{
                return 1;
            }
       }else{// lower-first
            if(diff[0] == LOWER_CASE){
                return -1;
            }else{
                return 1;
            }
       }
   }else{// No case differences
        return 0;
   }
    
  }
  
   
    
  private final int[] getFirstCaseDiff(final String text, final String pattern, final Locale locale){
        
        final CollationElementIterator targIter = m_collator.getCollationElementIterator(text);
        final CollationElementIterator patIter = m_collator.getCollationElementIterator(pattern);  
        int startTarg = -1;
        int endTarg = -1;
        int startPatt = -1;
        int endPatt = -1;
        final int done = getElement(CollationElementIterator.NULLORDER);
        int patternElement = 0, targetElement = 0;
        boolean getPattern = true, getTarget = true;
        
        while (true) { 
            if (getPattern){
                 startPatt = patIter.getOffset();
                 patternElement = getElement(patIter.next());
                 endPatt = patIter.getOffset();
            }
            if ((getTarget)){               
                 startTarg  = targIter.getOffset(); 
                 targetElement   = getElement(targIter.next()); 
                 endTarg  = targIter.getOffset();
            }
            getTarget = getPattern = true;                          
            if ((patternElement == done) ||( targetElement == done)) {
                return null;                      
            } else if (targetElement == 0) {
              getPattern = false;           
            } else if (patternElement == 0) {
              getTarget = false;           
            } else if (targetElement != patternElement) {// mismatch
                if((startPatt < endPatt) && (startTarg < endTarg)){
                    final String  subText = text.substring(startTarg, endTarg);
                    final String  subPatt = pattern.substring(startPatt, endPatt);
                    final String  subTextUp = subText.toUpperCase(locale);
                    final String  subPattUp = subPatt.toUpperCase(locale);
                    if(m_collator.compare(subTextUp, subPattUp) != 0){ // not case diffference
                        continue;
                    }
                    
                    int diff[] = {UNKNOWN_CASE, UNKNOWN_CASE};
                    if(m_collator.compare(subText, subTextUp) == 0){
                        diff[0] = UPPER_CASE;
                    }else if(m_collator.compare(subText, subText.toLowerCase(locale)) == 0){
                       diff[0] = LOWER_CASE; 
                    }
                    if(m_collator.compare(subPatt, subPattUp) == 0){
                        diff[1] = UPPER_CASE;
                    }else if(m_collator.compare(subPatt, subPatt.toLowerCase(locale)) == 0){
                       diff[1] = LOWER_CASE; 
                    }
                    
                    if(((diff[0] == UPPER_CASE) && ( diff[1] == LOWER_CASE)) ||
                       ((diff[0] == LOWER_CASE) && ( diff[1] == UPPER_CASE))){        
                        return diff;
                    }else{// not case diff
                      continue; 
                    }  
                }else{
                    continue; 
                }
                    
           }
        }
                           
  }
  
  
 // Return a mask for the part of the order we're interested in
    private static final int getMask(final int strength) {
        switch (strength) {
            case Collator.PRIMARY:
                return 0xFFFF0000;
            case Collator.SECONDARY:
                return 0xFFFFFF00;
            default: 
                return 0xFFFFFFFF;
        }
    }
    //get collation element with given strength
    // from the element with max strength
  private final int getElement(int maxStrengthElement){
    
    return (maxStrengthElement & m_mask);
  }  

}//StringComparable 


