/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import java.math.BigInteger;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:int datatype.
 */
public class XSInt extends XSLong {

    private static final long serialVersionUID = -6853519104620633955L;
    
    private static final String XS_INT = "xs:int";
    
    private static BigInteger MIN_INCLUSIVE = BigInteger.valueOf(-2147483648L);
    
    private static BigInteger MAX_INCLUSIVE = BigInteger.valueOf(2147483647L);
    
	/*
	 * Class constructor.
	 */
	public XSInt() {
	  this(BigInteger.valueOf(0));
	}
	
	/*
     * Class constructor.
     */
	public XSInt(BigInteger val) {
		super(val);
	}
	
	/*
     * Class constructor.
     */
    public XSInt(String val) {
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
                throw new RuntimeException("An instance of type xs:int cannot be created. The numeric argument "
                                                                           + "'" + xsAnyType.stringValue() + "' provided is out of range for type xs:int.");  
            }
            
            resultSeq.add(new XSInt(bigInt));
        } catch (NumberFormatException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        
        return resultSeq;
    }
	
	public String stringType() {
		return XS_INT;
	}
	
	public String typeName() {
		return "int";
	}
	
	public boolean equals(XSInt xsInt) {
        return _value.equals(xsInt.intValue()); 
    }
	
	public boolean lt(XSInt xsInt) {
	    return _value.compareTo(xsInt.intValue()) < 0; 
    }
	
	public boolean gt(XSInt xsInt) {
	    return _value.compareTo(xsInt.intValue()) > 0; 
    }
	
    public int getType() {
        return CLASS_XS_INT;
    }
	
}
