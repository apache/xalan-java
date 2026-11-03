/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;

/**
 * This class serves as base type, of all the XML Schema built-in 
 * numeric types. XPath 3.1 also defines, an XML Schema type xs:numeric
 * as a union of XML Schema built-in types xs:integer, xs:decimal, 
 * xs:float, xs:double.
 */
public class XSNumericType extends XSCtrType {

    private static final long serialVersionUID = 6842313858622701811L;
    
    private static final String XS_NUMERIC = "xs:numeric";
    
    private XSInteger _xs_integer;
    
    private XSDecimal _xs_decimal;
    
    private XSFloat _xs_float;
    
    private XSDouble _xs_double;
    
    /**
     * Default constructor.
     */
    public XSNumericType() throws TransformerException {
       // No op
    }
    
    /**
     * Class constructor.
     * 
     * @param strVal
     * @throws javax.xml.transform.TransformerException
     */
    public XSNumericType(String strVal) throws javax.xml.transform.TransformerException {
    	try {    	   
    		_xs_double = new XSDouble(strVal);
    	}
    	catch (TransformerException ex) {
    	   throw new TransformerException("FORG0001 : An XML Schema type 'numeric' could not be instantiated, "
    	   		                                                                                              + "with string valued argument '" 
    			                                                                                              + strVal + "'."); 
    	}
    }

    @Override
    public ResultSequence constructor(ResultSequence arg) throws TransformerException {
        ResultSequence resultSeq = null;
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        resultSeq = new ResultSequence();
        
        try {
        	XSAnyType xsAnyType = (XSAnyType)arg.item(0);

        	if ((xsAnyType instanceof XSInteger) || (xsAnyType instanceof XSDecimal) 
        			                             || (xsAnyType instanceof XSFloat) 
        			                             || (xsAnyType instanceof XSDouble)
        			                             || (xsAnyType instanceof XSNumericType)) {
        		resultSeq.add(xsAnyType);
        	}
        	else if (xsAnyType instanceof XSString) {
        		String str1 = ((XSString)xsAnyType).stringValue();

        		resultSeq.add(new XSDouble(str1));
        	}
        	else {
        		throw new TransformerException("FORG0001 : An XML Schema type 'numeric' could'nt be "
        				                                                               + "instantiated with the supplied argument.");
        	}
        }
        catch (Exception ex) {
        	if (ex instanceof TransformerException) {
        	   throw ex;
        	}
        	else {
        	   throw new TransformerException("FORG0001 : An XML Schema type 'numeric' could'nt be "
        				                                                              + "instantiated with the supplied argument.");
        	}
        }
        
        return resultSeq;
    }

    @Override
    public String typeName() {
    	return "numeric";
    }

    @Override
    public String stringType() {
    	return XS_NUMERIC;
    }

    @Override
    public String stringValue() {        
    	
    	String result = null;

    	if (_xs_integer != null) {
    		result = _xs_integer.stringValue(); 
    	}
    	else if (_xs_decimal != null) {
    		result = _xs_decimal.stringValue();
    	}
    	else if (_xs_float != null) {
    		result = _xs_float.stringValue();
    	}
    	else if (_xs_double != null) {
    		result = _xs_double.stringValue();
    	}

    	return result;
    }
    
    public Object getValue() {
    	
    	Object result = null;
    	
    	if (_xs_integer != null) {
    		result = _xs_integer; 
    	}
    	else if (_xs_decimal != null) {
    		result = _xs_decimal;
    	}
    	else if (_xs_float != null) {
    		result = _xs_float;
    	}
    	else if (_xs_double != null) {
    		result = _xs_double;
    	}
    	
    	return result; 
    }

}
