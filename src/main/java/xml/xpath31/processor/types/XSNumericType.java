/*
 * This src code, is property of Apache Xerces-J project, and is made
 * available here for code development purposes.
 */
package xml.xpath31.processor.types;

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;

/**
 * This class serves as base type, of all the XML Schema built-in 
 * numeric types.
 */
public class XSNumericType extends XSCtrType {

    private static final long serialVersionUID = 6842313858622701811L;

    @Override
    public ResultSequence constructor(ResultSequence arg) throws TransformerException {
        return null;
    }

    @Override
    public String typeName() {
        return null;
    }

    @Override
    public String stringType() {
        return null;
    }

    @Override
    public String stringValue() {
        return null;
    }

}
