package org.apache.xpath.objects;

import org.apache.xml.dtm.XType;
import org.apache.xpath.XPathContext;
import org.apache.xpath.parser.Token;

public class XInteger extends XNumber
{
  int m_val;
  //Integer m_intobj; // Cache; current guess is we don't need it

  /**
   * Constructor for XInteger
   * 
   * This one's actually being used, unlike most XObject empty ctors,
   * because it may be created and _then_ set (by processToken)
   * during stylesheet parsing.
   */
  public XInteger()
  {
  }

  /**
   * Constructor for XInteger
   */
  public XInteger(int num)
  {
    m_val = num;
  }
  
  /**
   * Constructor for XInteger
   */
  public XInteger(Integer num)
  {
  	//m_intobj=num;
    m_val = num.intValue();
  }
  
  /**
   * Cast result object to a number.
   *
   * @return the value of the XNumber object
   */
  public double num()
  {
    return (double)m_val;
  }
  
  /**
   * Evaluate expression to a number.
   *
   * @return 0.0
   *
   * @throws javax.xml.transform.TransformerException
   */
  public double num(XPathContext xctxt) 
    throws javax.xml.transform.TransformerException
  {
    return (double)m_val;
  }
  
  /**
   * Get result object as a integer.
   *
   * @return At this level, the num() value cast to an integer.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public int integer() throws javax.xml.transform.TransformerException
  {

    return m_val;
  }


  /**
   * Cast result object to a boolean.
   *
   * @return false if the value is NaN or equal to 0.0
   */
  public boolean bool()
  {
    return (m_val == 0) ? false : true;
  }


  public void processToken(Token t) 
  { 
  	String strNum = t.image;
  	try
    {
      m_val = Integer.parseInt(strNum);
      //m_intobj=null; // Reset/defer
    }
    catch (java.lang.NumberFormatException e)
    {
      // TBD: Use error listener. -sb
      throw e;
    }
  }
  
  /**
   * Return the sequence representing this object.
   * @return XSequence
   */
  public XSequence xseq()
  {
    return new XSequenceSingleton(this);
  }


  public Object object()
  {
  	//if(m_intobj==null)
  	//	m_intobj=new Integer(m_val);
  	//return m_intobj;
  	return new Integer(m_val);
  }
}

