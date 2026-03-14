/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import org.apache.xml.utils.FastStringBuffer;
import org.apache.xpath.objects.XObject;

/**
 * Base class for all the XML Schema types.
 *
 * <p>(please refer,
 * <a href="https://www.w3.org/TR/xmlschema11-2/#built-in-datatypes">https://www.w3.org/TR/xmlschema11-2/#built-in-datatypes</a>
 * that illustrates the XML Schema 1.1 built-in datatypes hierarchy)</p>
 */
public abstract class XSAnyType extends XObject {
	
    private static final long serialVersionUID = -3385975335330221518L;

    /**
	 * Get the datatype's name. For e.g "xs:boolean", "xs:decimal".
	 * 
	 * @return datatype's name
	 */
	public abstract String stringType();

	/**
	 * Get the string representation of the value stored.
	 * 
	 * @return get the string representation of the, value 
	 *         stored adhering to this type.
	 */
	public abstract String stringValue();
	
	public void appendToFsb(FastStringBuffer fsb) {
	   fsb.append(stringValue());
	} 
	
}
