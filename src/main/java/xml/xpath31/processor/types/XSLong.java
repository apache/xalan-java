/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import java.math.BigInteger;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:long datatype.
 */
public class XSLong extends XSInteger {

    private static final long serialVersionUID = -1030394161532436404L;
    
    private static final String XS_LONG = "xs:long";
    
    private static BigInteger MIN_INCLUSIVE = BigInteger.valueOf(-9223372036854775808L);
    
    private static BigInteger MAX_INCLUSIVE = BigInteger.valueOf(9223372036854775807L);

	/*
	 * Class constructor.
	 */
	public XSLong() {
	   this(BigInteger.valueOf(0));
	}
	
	/*
     * Class constructor.
     */
	public XSLong(BigInteger val) {
		super(val);
	}
	
	/*
     * Class constructor.
     */
    public XSLong(String val) {
        super(val);
    }
	
	@Override
    public ResultSequence constructor(ResultSequence arg) throws RuntimeException {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);

        try {
            BigInteger bigInt = new BigInteger(xsAnyType.stringValue());     

            if (bigInt.compareTo(MIN_INCLUSIVE) == -1 || 
                                         bigInt.compareTo(MAX_INCLUSIVE) == 1) {
                throw new RuntimeException("An instance of type xs:long cannot be created. The numeric argument "
                                                                     + "'" + xsAnyType.stringValue() + "' provided is out of range for type xs:long.");  
            }
            
            resultSeq.add(new XSLong(bigInt));
        } catch (NumberFormatException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        
        return resultSeq;
    }
	
	public String stringType() {
		return XS_LONG;
	}
	
	public String typeName() {
		return "long";
	}
	
	public boolean equals(XSLong xsLong) {
        return _value.equals(xsLong.intValue()); 
    }
	
	public boolean lt(XSLong xsLong) {
	    return _value.compareTo(xsLong.intValue()) < 0; 
    }
	
	public boolean gt(XSLong xsLong) {
	    return _value.compareTo(xsLong.intValue()) > 0; 
    }
	
    public int getType() {
        return CLASS_XS_LONG;
    }

}
