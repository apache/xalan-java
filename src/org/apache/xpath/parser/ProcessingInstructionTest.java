package org.apache.xpath.parser;

import org.apache.xml.dtm.DTMFilter;
import org.apache.xpath.objects.XString;
import org.apache.xpath.patterns.StepPattern;

/**
 * This is an expression node that only exists for construction 
 * purposes. 
 */
public class ProcessingInstructionTest extends KindTest
{
  public ProcessingInstructionTest(XPath parser)
  {
	  super(parser);
    m_name = StepPattern.WILD;
  }

  public int getWhatToShow()
  {
      return DTMFilter.SHOW_PROCESSING_INSTRUCTION;
  }	
   
   
  public String getLocalName()
  {
  	return m_name;
  }
  
  public void jjtAddChild(Node n, int i) 
  {
    if(n instanceof org.apache.xpath.objects.XString) // Includes KindTest
    {
    	m_name = ((XString)n).str();
    }
    else
    {
    	// Assertion, should never happen.
    	throw new RuntimeException("node can only be a QName or Wildcard!");
    }
  }


}

