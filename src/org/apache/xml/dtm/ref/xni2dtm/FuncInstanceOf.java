package org.apache.xml.dtm.ref.xni2dtm;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.w3c.dom.Node;

/** Temporary extension function, prototyping XSLT 2.0 type matching.
 * */
public class FuncInstanceOf {
	private static final boolean JJK_DISABLE_VALIDATOR=false; // debugging hook
	private static final boolean JJK_DUMMY_CODE=true; // debugging hook
	
	
	public static boolean eval(org.apache.xalan.extensions.ExpressionContext expressionContext,
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
		
		return sourceDTM.isNodeSchemaType(
			sourceHandle,qn.getNamespaceURI(),qn.getLocalName())
			;
	}
	
	public static boolean eval(org.apache.xalan.extensions.ExpressionContext expressionContext,
		Node root, String typeNamespace, String typeName) 
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

		return sourceDTM.isNodeSchemaType(
			sourceHandle,typeNamespace,typeName)
			;
	}
}

