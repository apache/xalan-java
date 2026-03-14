/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:integer datatype.
 */
public class XSInteger extends XSDecimal {

    private static final long serialVersionUID = -4634168510820898744L;

    private static final String XS_INTEGER = "xs:integer";
	
    // the underlying java.math.BigInteger value, representing 
    // this XML Schema datatype value. 
	protected BigInteger _value;

	/*
	 * Class constructor.
	 */
	public XSInteger() {
		this(BigInteger.valueOf(0));
	}

	/*
     * Class constructor.
     */
	public XSInteger(BigInteger val) {
		super(new BigDecimal(val));
		_value = val;
	}

	/*
     * Class constructor.
     */
	public XSInteger(String val) {
		super(new BigDecimal(val));
		_value = new BigInteger(val);
	}

	public String stringType() {
		return XS_INTEGER;
	}

	public String typeName() {
		return "integer";
	}

	/**
	 * Get a string representation of an integer value stored, 
	 * within this object.
	 * 
	 * @return   string representation of the integer value stored
	 */
	public String stringValue() {
		return _value.toString();
	}

	/**
     * Check if this XSInteger object represents the value 0.
     * 
     * @return    true if this XSInteger object represents the value 0. 
     *            false otherwise.
     */
	public boolean zero() {
		return (_value.compareTo(BigInteger.ZERO) == 0);
	}

	public ResultSequence constructor(ResultSequence arg) {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);
        
        try {
            BigInteger bigInteger = castToInteger(xsAnyType);            
            resultSeq.add(new XSInteger(bigInteger));            
        } catch (NumberFormatException e) {
            // to do
            return null;
        }
        
        return resultSeq;
	}

	/**
     * Get the actual value of an integer number stored within 
     * this object.
     * 
     * @return   the actual value of the number stored
     */
	public BigInteger intValue() {
		return _value;
	}

	/**
     * Set the numeric integer value, within this object.
     * 
     * @param val    number to be stored
     */
	public void setInt(BigInteger val) {
		_value = val;
	}
	
	public boolean equals(XSInteger xsInteger) {
        return _value.equals(xsInteger.intValue()); 
    }
	
	public boolean lt(XSInteger xsInteger) {
	    return (intValue()).compareTo(xsInteger.intValue()) < 0; 
    }
	
	public boolean gt(XSInteger xsInteger) {
	    return (intValue()).compareTo(xsInteger.intValue()) > 0; 
    }
	
	public XSInteger multiply(XSInteger xsInteger) {
	    return new XSInteger((intValue()).multiply(xsInteger.intValue()));   
	}
	
    public int getType() {
        return CLASS_XS_INTEGER;
    }
	
	/*
     * Cast an object of type XSAnyType, to an object of type 
     * java.math.BigInteger.  
     */
    private BigInteger castToInteger(XSAnyType xsAnyType) {
        
        if (xsAnyType instanceof XSBoolean) {
            if ((xsAnyType.stringValue()).equals("true")) {
                return BigInteger.ONE;
            } 
            else {
                return BigInteger.ZERO;
            }
        }
        
        if ((xsAnyType instanceof XSDecimal) || (xsAnyType instanceof XSFloat) ||
                                                (xsAnyType instanceof XSDouble)) {
           BigDecimal bigDecimal =  new BigDecimal(xsAnyType.stringValue());
           
           return bigDecimal.toBigInteger();
        }
        
        return new BigInteger(xsAnyType.stringValue());
    }

}
