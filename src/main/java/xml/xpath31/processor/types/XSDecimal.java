/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import java.math.BigDecimal;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:decimal datatype.
 */
public class XSDecimal extends XSNumericType {

    private static final long serialVersionUID = 3338738472846690263L;
    
    private static final String XS_DECIMAL = "xs:decimal";
    
    private BigDecimal _value;
    
    private XPath3DecimalFormat xpath3DecimalFormat = new XPath3DecimalFormat(
                                                                 "0.####################");
    
    /**
     * Class constructor.
     */
    public XSDecimal() {
       this(BigDecimal.valueOf(0));
    }
    
    /**
     * Class constructor.
     */
    public XSDecimal(BigDecimal bigDecimal) {
       _value = bigDecimal; 
    }
    
    /**
     * Class constructor.
     */
    public XSDecimal(String str) {
        _value = new BigDecimal(str);
    }

    @Override
    public String stringType() {
        return XS_DECIMAL;
    }
    
    public String typeName() {
        return "decimal";
    }

    @Override
    public String stringValue() {
        if (zero()) {
           return "0";
        }

        _value = new BigDecimal((_value.toString()).replaceFirst("0*", ""));
        
        return xpath3DecimalFormat.performStrFormatting(_value);
    }
    
    @Override
    public ResultSequence constructor(ResultSequence arg) {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);
        
        try {            
            XSDecimal xsDecimal = castToDecimal(xsAnyType);            
            resultSeq.add(xsDecimal);
        } catch (NumberFormatException ex) {
            // to do
            return null;
        }        
        
        return resultSeq;
    }
    
    /**
     * Check if this XSDecimal object represents the value 0.
     * 
     * @return    true if this XSDecimal object represents the value 0. 
     *            false otherwise.
     */
    public boolean zero() {
        return (_value.compareTo(new BigDecimal(0.0)) == 0);
    }
    
    /**
     * Get the actual value of the number stored within 
     * this XSDecimal object.
     * 
     * @return   the actual value of the number stored
     */
    public double doubleValue() {
        return _value.doubleValue();
    }
    
    public BigDecimal getValue() {
        return _value;
    }
    
    /**
     * Set the numeric double value, within this XSDecimal object.
     * 
     * @param val    number to be stored
     */
    public void setDouble(double val) {
        _value = new BigDecimal(val);
    }
    
    public boolean equals(XSDecimal xsDecimal) {
        return _value.equals(xsDecimal.getValue()); 
    }
    
    public boolean lt(XSDecimal xsDecimal) {
        return (_value.compareTo(xsDecimal.getValue()) == -1);
    }
    
    public boolean gt(XSDecimal xsDecimal) {
        return (_value.compareTo(xsDecimal.getValue()) == 1);
    }
    
    public int getType() {
        return CLASS_XS_DECIMAL;
    }
    
    /*
     * Cast an object of type XSAnyType, to an object of type 
     * XSDecimal.  
     */
    private XSDecimal castToDecimal(XSAnyType xsAnyType) {        
       if (xsAnyType instanceof XSBoolean) {            
          if ((xsAnyType.stringValue()).equals("true")) {
             return new XSDecimal(new BigDecimal("1"));
          } 
          else {
             return new XSDecimal(new BigDecimal("0"));
          }
       }
        
       return new XSDecimal(xsAnyType.stringValue());
    }

}
