/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;

/**
 * A representation of the XSCtrType datatype.
 *
 * <p>All the XML Schema built-in types that have constructor functions
 * as defined by XPath 3.1 F&amp;O spec, XalanJ data type classes for those
 * XML Schema built-in types have this class as a parent or an ancestor 
 * class.</p>
 */
public abstract class XSCtrType extends XSAnyAtomicType {

    private static final long serialVersionUID = -1177633885817069140L;

    /**
	 * This function is used for, XML Schema built-in types constructor functions.
	 *
	 * <p>For e.g, xs:string($arg as xs:anyAtomicType?) as xs:string? ,
	 *          xs:boolean($arg as xs:anyAtomicType?) as xs:boolean? etc</p>
	 *
	 * @param arg    either an empty sequence, or an XML Schema atomic type
	 * 
	 * @return       the resulting ResultSequence
	 * 
     * @throws TransformerException 
	 */
	public abstract ResultSequence constructor(ResultSequence arg) throws TransformerException;

	/**
	 * Get the datatype's name.
	 * 
	 * @return String representation of the datatype's name
	 */
	public abstract String typeName();
	
}
