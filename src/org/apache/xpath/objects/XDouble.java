package org.apache.xpath.objects;

import org.apache.xml.dtm.XType;
import org.apache.xpath.XPathContext;
import org.apache.xpath.parser.Token;

public class XDouble extends XNumber
{
  double m_val;
  //Double m_doubleobj;		// Cache; best guess is we don't need it

  /**
   * Constructor for XDouble
   */
  public XDouble(double num)
  {
    m_val = num;
  }
  
  /**
   * Constructor for XDouble
   */
  public XDouble(Double num)
  {
    m_val = num.doubleValue();
    //m_doubleobj = num;
  }
  
  /**
   * Constructor for XDouble
   */
  public XDouble()
  {
  }
  
  public Object object()
  {
  	//if(m_doubleobj==null)
  	//	m_doubleobj=new Double(m_val);
  	//return m_doubleobj;
  	return new Double(m_val);
  }

  /**
   * Cast result object to a number.
   *
   * @return the value of the XNumber object
   */
  public double num()
  {
    return m_val;
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
    return m_val;
  }

  /**
   * Cast result object to a boolean.
   *
   * @return false if the value is NaN or exactly equal to 0.0
   */
  public boolean bool()
  {
    return (Double.isNaN(m_val) || (m_val == 0.0)) ? false : true;
  }
  
  public void processToken(Token t) 
  { 
  	String strNum = t.image;
  	m_val = Double.parseDouble(strNum);
  	//m_doubleobj=null; // Reset/defer
  }

  /**
   * Return the sequence representing this object.
   * @return XSequence
   */
  public XSequence xseq()
  {
    return new XSequenceSingleton(this);
  }

}

