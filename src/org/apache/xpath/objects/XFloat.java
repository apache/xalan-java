package org.apache.xpath.objects;

import org.apache.xml.dtm.XType;
import org.apache.xpath.XPathContext;
import org.apache.xpath.parser.Token;

public class XFloat extends XNumber
{
  float m_val;

  /**
   * Constructor for XDouble
   */
  public XFloat(float num)
  {
    m_val = num;
  }
  
  /**
   * Constructor for XDouble
   */
  public XFloat(Float num)
  {
    m_val = num.floatValue();
  }
  
  /**
   * Constructor for XDouble
   */
  public XFloat()
  {
  }
  
  public Object object()
  {
  	return new Float(m_val);
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
   * Get result object as a integer.
   *
   * @return At this level, the num() value cast to an integer.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public float floatVal() throws javax.xml.transform.TransformerException
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
  	m_val = Float.parseFloat(strNum);
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

