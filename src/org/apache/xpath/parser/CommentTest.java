package org.apache.xpath.parser;

import org.apache.xml.dtm.DTMFilter;

/**
 * This is an expression node that only exists for construction 
 * purposes. 
 */
public class CommentTest extends KindTest
{
   public CommentTest(XPath parser)
   {
  	 super(parser);
   }

   public int getWhatToShow()
   {
      return DTMFilter.SHOW_COMMENT;
   }	
}

