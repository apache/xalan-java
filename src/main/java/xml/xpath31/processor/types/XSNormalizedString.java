/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import org.apache.xpath.objects.ResultSequence;

/**
 * A representation of the xs:normalizedString datatype.
 */
public class XSNormalizedString extends XSString {

	private static final long serialVersionUID = 1297073093964057453L;
	
	private static final String XS_NORMALIZEDSTRING = "xs:normalizedString";
	
	/**
	 * Initialises this xs:normalizedString object, using the 
	 * supplied string.
	 * 
	 * @param normStr   the string value to initialise to
	 */
	public XSNormalizedString(String normStr) {
		super(normStr);
	}

	/**
	 * Initialize this xs:normalizedString value to null.
	 */
	public XSNormalizedString() {
		this(null);
	}

	/**
	 * Retrieves this datatype's full pathname.
	 * 
	 * @return "xs:normalizedString" which is the datatype's full pathname
	 */
	public String stringType() {
		return XS_NORMALIZEDSTRING;
	}

	/**
	 * Retrieves the datatype's name.
	 * 
	 * @return "normalizedString" which is the datatype's name
	 */
	public String typeName() {
		return "normalizedString";
	}

	/**
	 * Creates a new ResultSequence object, consisting of the string value
	 * within the supplied ResultSequence.
	 * 
	 * @param arg    the ResultSequence from which to get the string
	 * 
	 * @return       new ResultSequence consisting of the supplied string
	 * 
	 * @throws       javax.xml.transform.TransformerException
	 */
	public ResultSequence constructor(ResultSequence arg) throws javax.xml.transform.TransformerException {
		ResultSequence resultSeq = new ResultSequence();

		if (arg.size() == 0) {
		   return resultSeq;
		}

		XSAnyType xsAnyType = (XSAnyType)arg.item(0);

		String srcString = xsAnyType.stringValue();		
								
		srcString = srcString.replace("\r", " ");
		srcString = srcString.replace("\n", " ");
		srcString = srcString.replace("\t", " ");
		
		resultSeq.add(new XSNormalizedString(srcString));

		return resultSeq;
	}
	
    public int getType() {
       return CLASS_XS_NORMALIZED_STRING;
    }

}
