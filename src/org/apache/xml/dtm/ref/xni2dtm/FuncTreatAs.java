package org.apache.xml.dtm.ref.xni2dtm;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.w3c.dom.Node;

/** Temporary extension function, prototyping proposed XPath2 "treat as"
 * and "assert as" operators. Tests whether an expression is of the
 * specified type, returning the expression's value if so and the 
 * architected Error value otherwise. 
 * 
 * There's a bunch of stuff left open in this prototype, since to do it
 * right we need to tie in to static typing of expressions (not yet
 * supported, and the only place where treat/assert differ), and we need
 * typed general expressions (also not yet supported; all we have is
 * typed nodes in the source documents or validated RTFs).
 * 
 * I'm also not sure what the "error value" will be. For now I'm settling
 * for announcing an error and returning null.
 * */
public class FuncTreatAs {
	private static final boolean JJK_DISABLE_VALIDATOR=false; // debugging hook
	private static final boolean JJK_DUMMY_CODE=true; // debugging hook
	
	
	public static Node eval(org.apache.xalan.extensions.ExpressionContext expressionContext,
		Node root, String typeQName) 
		throws javax.xml.transform.TransformerException
	{
		// This happens to work in current code. It isn't really
		// documented. Future versions expect to expose it more elegantly,
		// according to Don Leslie. But since this extension is just
		// temporary, let's use the cheat... We know it's going to be a
		// particular inner class, which has an accessor to retrieve its
		// associated XPathContext, so we reach in and ask it to reach back.
		XPathContext xctxt = ((XPathContext.XPathExpressionContext)expressionContext).getXPathContext();
		
    	int sourceHandle=xctxt.getDTMHandleFromNode(root);
    	DTM sourceDTM=xctxt.getDTM(sourceHandle);

		DTM2XNI d2x=new DTM2XNI(sourceDTM,sourceHandle);


		// Need to resolve typeQName
		org.apache.xml.utils.PrefixResolver pfxresolver=xctxt.getNamespaceContext();
		org.apache.xml.utils.QName qn=new org.apache.xml.utils.QName(typeQName,pfxresolver);
		
		if(sourceDTM.isNodeSchemaType(
			sourceHandle,qn.getNamespaceURI(),qn.getLocalName())
			)
		{
			return root;
		}
		else
		{
		    // %REVIEW% Should be routed to the JAXP error listener, presumably.
		    // What's the easiest way to get that from here?
		    javax.xml.transform.ErrorListener handler=xctxt.getErrorListener();
		    javax.xml.transform.TransformerException err=
				new javax.xml.transform.TransformerException(
				"Type assertion failed; expected "+
				qn.getNamespaceURI()+":"+qn.getLocalName()+" but found "+
				sourceDTM.getSchemaTypeNamespace(sourceHandle)+":"+
				sourceDTM.getSchemaTypeName(sourceHandle) );
			if(handler==null)
				throw err;
			else
				handler.error(err);
				
			return null; // in case handler said "proceed normally".
		}
	}
	
}

