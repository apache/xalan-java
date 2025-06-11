package org.apache.xpath.types;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.util.Base64;
import org.apache.xerces.impl.dv.util.ByteListImpl;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyAtomicType;

/**
 * Implementation of XML Schema data type xs:base64Binary.
 * 
 * The core logic of this class has been reused from 
 * Apache Xerces-J.
 * 
 * @author Neeraj Bajaj, Sun Microsystems, inc.
 * @author Sandy Gao, IBM
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class XSBase64Binary extends XSAnyAtomicType {

	private static final long serialVersionUID = 4921509527694977643L;
	
	private static final String XS_BASE64BINARY = "xs:base64Binary";
	
	private Object m_base64BinaryValue;
	
	/**
	 * Default constructor.
	 */
	public XSBase64Binary() {
	   // NO OP
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param strValue
	 */
	public XSBase64Binary(String strValue) throws TransformerException {		
		byte[] decoded = Base64.decode(strValue);
        if (decoded == null) {
        	throw new TransformerException("FOCA0003 : An " + XS_BASE64BINARY + " value cannot be "
                    										                  + "constructed from string value " + strValue + ".");
        }

        m_base64BinaryValue = new XBase64(decoded);
	}

	@Override
	public ResultSequence constructor(ResultSequence seq) throws TransformerException {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strValue = XslTransformEvaluationHelper.getStrVal(xObj);
		
		XSBase64Binary xsBase64Binary = new XSBase64Binary(strValue);
		result.add(xsBase64Binary);
		
		return result;
	}

	@Override
	public String typeName() {
		return "base64Binary";
	}

	@Override
	public String stringType() {
		return XS_BASE64BINARY;
	}
	
	public int getType() {
		return CLASS_BASE64BINARY;
	}

	@Override	
	public String stringValue() {
		return ((XBase64)m_base64BinaryValue).toString();
	}
	
	public Object value() {
		return m_base64BinaryValue;
	}
	
	public boolean eq(XSBase64Binary obj2) {
		return this.equals(obj2);
	}
	
	public boolean equals(Object obj2) {
	   boolean result = false;
	   
	   if (obj2 instanceof XSBase64Binary) {
		  XSBase64Binary xsBase64Binary2 = (XSBase64Binary)obj2;
		  XBase64 xBase64_2 = (XBase64)(xsBase64Binary2.value());
		  result = ((XBase64)m_base64BinaryValue).equals(xBase64_2); 
	   }
	   
	   return result;
	}
	
	public int hashCode() {
	   int hashCode = 0;	   
	   XBase64 xBase64 = (XBase64)m_base64BinaryValue;	   
	   hashCode = xBase64.hashCode(); 
	   
	   return hashCode;
	}
	
    private static final class XBase64 extends ByteListImpl {

        public XBase64(byte[] data) {
            super(data);
        }
        public synchronized String toString() {
            if (canonical == null) {
                canonical = Base64.encode(data);
            }
            return canonical;
        }
        
        public boolean equals(Object obj) {
            if (!(obj instanceof XBase64))
                return false;
            byte[] odata = ((XBase64)obj).data;
            int len = data.length;
            if (len != odata.length)
                return false;
            for (int i = 0; i < len; i++) {
                if (data[i] != odata[i])
                    return false;
            }
            return true;
        }
        
        public int hashCode() {
            int hash = 0;
            for (int i = 0; i < data.length; ++i) {
                hash = hash * 37 + (((int) data[i]) & 0xff);
            }
            return hash;
        }
    }

}
