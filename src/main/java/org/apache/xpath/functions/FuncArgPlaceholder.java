package org.apache.xpath.functions;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/**
 * A class definition, to help implement XPath partial function 
 * application using placeholder function argument expression ?.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArgPlaceholder extends Expression {

	private static final long serialVersionUID = 684157463367484125L;
	
	/**
	 * Class constructor.
	 */
	public FuncArgPlaceholder() {
	   // NO OP	
	}

	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {
		// NO OP
		return null;
	}
	
	@Override
	public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
		// NO OP
	}

	@Override
	public void fixupVariables(Vector vars, int globalsSize) {
	   // NO OP
	}

	@Override
	public boolean deepEquals(Expression expr) {
		// NO OP
		return false;		
	}

}
