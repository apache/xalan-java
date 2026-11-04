/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:NOTATION datatype.
 */
public class XSNotation extends XSCtrType {

	private static final long serialVersionUID = 4863038034316076532L;
	
	private static final String XS_NOTATION = "xs:NOTATION";

	public ResultSequence constructor(ResultSequence arg) throws TransformerException {				
	   throw new TransformerException("XPST0080 : An xdm value couldn't be cast to an XML Schema "
				                                                                                 + "type 'NOTATION'.");
	}

	@Override
	public String typeName() {
	   return "NOTATION";
	}

	@Override
	public String stringType() {
		return XS_NOTATION;
	}

	@Override
	public String stringValue() {
		return null;
	}

}
