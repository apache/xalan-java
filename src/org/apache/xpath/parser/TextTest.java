package org.apache.xpath.parser;

import org.apache.xml.dtm.DTMFilter;

/**
 * This is an expression node that only exists for construction 
 * purposes. 
 */
public class TextTest extends KindTest
{
   public TextTest(XPath parser)
   {
	  super(parser);
   }

   public int getWhatToShow()
   {
      return DTMFilter.SHOW_TEXT;
   }	
}

