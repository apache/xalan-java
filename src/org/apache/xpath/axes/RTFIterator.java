/**
 * <meta name="usage" content="advanced"/>
 * This class implements an RTF Iterator. Currently exists for sole
 * purpose of enabling EXSLT object-type function to return "RTF".
 * 
  */
package org.apache.xpath.axes;

import javax.xml.transform.TransformerException;
import org.apache.xpath.compiler.Compiler;

public class RTFIterator extends OneStepIteratorForward {

	/**
	 * Constructor for RTFIterator
	 */
	RTFIterator(Compiler compiler, int opPos, int analysis)
		throws TransformerException {
		super(compiler, opPos, analysis);
	}

	/**
	 * Constructor for RTFIterator
	 */
	public RTFIterator(int axis) {
		super(axis);
	}

}

