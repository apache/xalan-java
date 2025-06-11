package org.apache.xpath.types;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.util.ByteListImpl;
import org.apache.xerces.impl.dv.util.HexBin;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyAtomicType;

/**
 * Implementation of XML Schema data type xs:hexBinary.
 * 
 * The core logic of this class has been reused from 
 * Apache Xerces-J.
 * 
 * @author Neeraj Bajaj, Sun Microsystems, inc.
 * @author Sandy Gao, IBM
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class XSHexBinary extends XSAnyAtomicType {

	private static final long serialVersionUID = -7240843767674081214L;

	private static final String XS_HEXBINARY = "xs:hexBinary";
	
	private Object m_hexBinaryValue;
	
	/**
	 * Default constructor.
	 */
	public XSHexBinary() {
	   // NO OP
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param strValue
	 */
	public XSHexBinary(String strValue) throws TransformerException {
		byte[] decoded = HexBin.decode(strValue);
		if (decoded == null) {
        	throw new TransformerException("FOCA0003 : An " + XS_HEXBINARY + " value cannot be "
                    										               + "constructed from string value " + strValue + ".");
        }

        m_hexBinaryValue = new XHex(decoded);
	}

	@Override
	public ResultSequence constructor(ResultSequence seq) throws TransformerException {
		ResultSequence result = new ResultSequence();
		
		XObject xObj = seq.item(0);
		String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
		
		XSHexBinary xsHex64Binary = new XSHexBinary(strVal);
		result.add(xsHex64Binary);
		
		return result;
	}

	@Override
	public String typeName() {
		return "hexBinary";
	}

	@Override
	public String stringType() {
		return XS_HEXBINARY;
	}
	
	public int getType() {
		return CLASS_HEXBINARY;
	}

	@Override	
	public String stringValue() {
		return ((XHex)m_hexBinaryValue).toString();
	}
	
	public Object value() {
		return m_hexBinaryValue;
	}
	
	public boolean eq(XSHexBinary obj2) {
		return this.equals(obj2);
	}
	
	public boolean equals(Object obj2) {
	   boolean result = false;
	   
	   if (obj2 instanceof XSHexBinary) {
		  XSHexBinary xsHexBinary = (XSHexBinary)obj2;
		  XHex xHex_2 = (XHex)(xsHexBinary.value());
		  result = ((XHex)m_hexBinaryValue).equals(xHex_2); 
	   }
	   
	   return result;
	}
	
	public int hashCode() {
	   int hashCode = 0;	   
	   XHex xHex = (XHex)m_hexBinaryValue;	   
	   hashCode = xHex.hashCode(); 
	   
	   return hashCode;
	}
	
    private static final class XHex extends ByteListImpl {

        public XHex(byte[] data) {
            super(data);
        }
        public synchronized String toString() {
            if (canonical == null) {
                canonical = HexBin.encode(data);
            }
            return canonical;
        }
        
        public boolean equals(Object obj) {
            if (!(obj instanceof XHex))
                return false;
            byte[] odata = ((XHex)obj).data;
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
