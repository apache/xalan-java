package org.apache.xml.dtm.ref.xni2dtm;

import org.w3c.dom.Node;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMDefaultBase;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.dtm.ref.xni2dtm.XNI2DTM;
import org.apache.xml.dtm.ref.xni2dtm.DTM2XNI;


/** Basic test driver: Run a DTM through DTM2XNI and back into an instance
 * of XNI2DTM.
 * */
public class FuncValidate {
	public static Node eval(org.apache.xalan.extensions.ExpressionContext context,
		Node root) 
	{
		return eval(context,root,null);
	}
	public static Node eval(org.apache.xalan.extensions.ExpressionContext context,
		Node root, String contextPath) 
	{
		// TEMPORARY KLUGE:
		// The casts are highly dependent on exact usage. We
		// really want to get the xctxt, get DTMManager from that,
		// and ask it to map the node to a DTM/handle. Extensions
		// apparently can't do that; fix when we move this internal.
		DTMNodeProxy proxy=(DTMNodeProxy) root;
		DTM sourceDTM=proxy.getDTM();
		int sourceHandle=proxy.getDTMNodeNumber();
		DTMManager dtmmgr=((DTMDefaultBase)sourceDTM).getManager();

		// Set up an XNI scanner to read from the source DTM,
		// run that through validation (operates as a filter?),
		// and pass that into Xalan to produce a new document
		// %REVIEW% system ID???
		DTM2XNI d2x=new DTM2XNI(sourceDTM,sourceHandle);
		
		// VALIDATION GOES HERE!
		// Sandy Gao recommends "create a customized parser configuration,
		// which contains an XNI event source, a validator, and (optionally)
		// a document handler." See "pipelines" in the XNI docs.
		
		XNISource xsrc=new XNISource(d2x,null);
		DTM newDTM=dtmmgr.getDTM(xsrc,
			true, // unique
			null, // whitespace filter
			true, // incremental -- not supported at this writing
			false // doIndexing -- open to debate
			);
		
		return newDTM.getNode(newDTM.getDocument());
	}
}

