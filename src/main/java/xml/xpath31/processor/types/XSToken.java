/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;

/**
 * A representation of the xs:token datatype.
 */
public class XSToken extends XSNormalizedString {
	
	private static final long serialVersionUID = -8504348395152396275L;
	
	private static final String XS_TOKEN = "xs:token";

	/**
	 * Initialises using the supplied string value.
	 * 
	 * @param strVal    the string to initialise to
	 */
	public XSToken(String strVal) {
		super(strVal);
	}

	/**
	 * Initialises to null.
	 */
	public XSToken() {
		this(null);
	}

	/**
	 * Retrieves the datatype's full pathname.
	 * 
	 * @return "xs:token" which is the datatype's full pathname
	 */
	public String stringType() {
		return XS_TOKEN;
	}

	/**
	 * Retrieves the datatype's name.
	 * 
	 * @return "token" which is the datatype's name
	 */
	public String typeName() {
		return "token";
	}

	/**
	 * Creates a new ResultSequence consisting of the extractable String in the
	 * supplied ResultSequence.
	 *
	 * @param arg the ResultSequence from which to extract the String
	 * @return a new ResultSequence consisting of the supplied String
	 * @throws TransformerException if the supplied string value does not
     *                              satisfy constraints for the datatype
	 */
	public ResultSequence constructor(ResultSequence arg) throws TransformerException {
		ResultSequence resultSeq = new ResultSequence();

		if (arg.size() == 0) {
		   return resultSeq;
		}

		XSAnyType xsAnyType = (XSAnyType)arg.item(0);

		String srcString = xsAnyType.stringValue();
		
		srcString = srcString.replace("\r", "");
		srcString = srcString.replace("\n", "");
		srcString = srcString.replace("\t", "");
		srcString = srcString.trim();
		srcString = srcString.replaceAll("[ ]{2,}", " ");
		
		resultSeq.add(new XSToken(srcString));

		return resultSeq;
	}
	
    public int getType() {
        return CLASS_XS_TOKEN;
    }

}
