package org.apache.xml.dtm.ref.xni2dtm;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMSequence;
import org.apache.xpath.XPathContext;
import org.w3c.dom.Node;

/** Temporary extension function, prototyping proposed XPath2 xs:data()
 * function. Returns a Java Object(which will probably become an XObject for
 * a "non-XSLT type".
 * 
 * This may be moved back to XSLT/XQuery, or may be folded all the way down
 * as an operation implied by low-level expression syntax, depending on what
 * happens in the Working Group.
 * 
 * If the value passed in is NOT a Node, we're supposed to return the
 * architected Error value. Currently, we assume it is a node.
 * */
public class FuncData {
	private static final boolean JJK_DISABLE_VALIDATOR=false; // debugging hook
	private static final boolean JJK_DUMMY_CODE=true; // debugging hook
	
	
	public static DTMSequence data(org.apache.xalan.extensions.ExpressionContext context,
		Node root) 
	{
		// This happens to work in current code. It isn't really
		// documented. Future versions expect to expose it more elegantly,
		// according to Don Leslie. But since this extension is just
		// temporary, let's use the cheat... We know it's going to be a
		// particular inner class, which has an accessor to retrieve its
		// associated XPathContext, so we reach in and ask it to reach back.
		XPathContext xctxt = ((XPathContext.XPathExpressionContext)context).getXPathContext();
		
    	int sourceHandle=xctxt.getDTMHandleFromNode(root);
    	DTM sourceDTM=xctxt.getDTM(sourceHandle);

		return sourceDTM.getTypedValue(sourceHandle);
	}	
}

