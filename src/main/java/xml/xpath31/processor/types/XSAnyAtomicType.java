/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;

/**
 * Base class for all the XML Schema atomic data types.
 */
public abstract class XSAnyAtomicType extends XSAnySimpleType {
    
    private static final long serialVersionUID = 4800376096762047151L;

    /*
     * This function supports, creating XML Schema built-in types, XPath 3.1
     * XDM objects with data types xs:boolean, xs:decimal etc.
     * 
     */
    public abstract ResultSequence constructor(ResultSequence arg) throws TransformerException;
    
    /**
     * Get the datatype's name
     * 
     * @return  String representation of the datatype's name
     */
    public abstract String typeName();
    
}
