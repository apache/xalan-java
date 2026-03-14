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
	
	// unicode codepoint decimal values of certain characters mentioned below,
	// relevant to the implementation of this class.
	
	private static int UC_VAL_CR = 13;
	
	private static int UC_VAL_LINE_FEED = 10;
	
	private static int UC_VAL_TAB = 9;
	
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
		if (!isSatisfiesConstraints(srcString)) {
		   throw new javax.xml.transform.TransformerException("FORG0006 : The supplied string value cannot be used to construct "
		   		                                                          + "xs:normalizedString value, since the input string contains prohibited "
		   		                                                          + "characters for a xs:normalizedString value.");	
		}
		else {
		   resultSeq.add(new XSNormalizedString(srcString));	
		}

		return resultSeq;
	}
	
	/*
	 * Determine whether, the supplied string value satisfies constraints for 
	 * the datatype, xs:normalizedString. 
	 */
	protected boolean isSatisfiesConstraints(String srcString) {
	   
		boolean isXsNormalizedStr = true;
		
		// The xs:normalizedString value cannot contain, 'carriage return', 
		// 'line feed' and 'tab' characters.
		if ((srcString.indexOf(UC_VAL_CR) != -1) || (srcString.indexOf(UC_VAL_LINE_FEED) != -1) || 
				                                    (srcString.indexOf(UC_VAL_TAB) != -1)) {
		   // invalid input string
		   isXsNormalizedStr = false;
		}
		
		return isXsNormalizedStr;
		  
	} // isSatisfiesConstraints
	
    public int getType() {
       return CLASS_XS_NORMALIZED_STRING;
    }

}
