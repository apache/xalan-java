package org.apache.xml.dtm.ref;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.TestDTMNodes;


/**
 * Unit test for DTMManager/DTM
 *
 * Loads an XML document from a file (or, if no filename is supplied,
 * an internal string), then dumps its contents. Replaces the old
 * version, which was specific to the ultra-compressed implementation.
 * (Which, by the way, we probably ought to revisit as part of our ongoing
 * speed/size performance evaluation.)
 *
 * %REVIEW% Extend to test DOM2DTM, incremental, DOM view of the DTM, 
 * whitespace-filtered, indexed/nonindexed, ...
 * */
public class TestDTM {

  public static void main(String argv[])
  {
    try
    {
      // Pick our input source
      Source source;
      if(argv.length<1)
      {
	String defaultSource=
	  "<?xml version=\"1.0\"?>\n"+
	  "  <dummyDocument>\n"+
	  "   <A>\n"+
	  "    <B hat=\"new\" car=\"Honda\" dog=\"Boxer\">Life is good</B>\n"+
	  "   </A>\n"+
	  "   <C>My Anaconda<D/>Words</C>\n"+
	  "  To test with a more interesting docuent, provide the URI on the command line!\n"+
	  "  </dummyDocument>\n";
	source=new StreamSource(new java.io.StringReader(defaultSource));
      }
      else
      {
	// Read from a URI
	source=new StreamSource(argv[0]);
      }

      // Get a DTM manager, and ask it to load the DTM "uniquely",
      // with no whitespace filtering, nonincremental, but _with_
      // indexing (a fairly common case, and avoids the special
      // mode used for RTF DTMs).
      DTMManager manager=
	new org.apache.xml.dtm.ref.DTMManagerDefault().newInstance
	  (new org.apache.xpath.objects.XMLStringFactoryImpl());
      DTM dtm=manager.getDTM(source, true, null, false, true);
      
      // Get the root node. NOTE THE ASSUMPTION that this is a single-document
      // DTM -- which will always be true for a node obtained this way, but
      // won't be true for "shared" DTMs used to hold XSLT variables
      int rootNode=dtm.getDocument();
      
      // Simple test: Recursively dump the DTM's content.
      // We'll want to replace this with more serious examples
      recursiveDumpNode(dtm,rootNode);
    }
    catch(Exception e)
      {
        e.printStackTrace();
      }
  }

  static final String[] TYPENAME={
    "NULL",
    "ELEMENT",
    "ATTRIBUTE",
    "TEXT",
    "CDATA_SECTION",
    "ENTITY_REFERENCE",
    "ENTITY",
    "PROCESSING_INSTRUCTION",
    "COMMENT",
    "DOCUMENT",
    "DOCUMENT_TYPE",
    "DOCUMENT_FRAGMENT",
    "NOTATION",
    "NAMESPACE"
  };
  
  static void recursiveDumpNode(DTM dtm,int nodeHandle)
  {
    // ITERATE over siblings
    for(;
	nodeHandle!=DTM.NULL;
	nodeHandle=dtm.getNextSibling(nodeHandle))
    {
      printNode(dtm,nodeHandle,"");
      
      // List the namespaces, if any.
      // Include only node's local namespaces, not inherited
      // %ISSUE% Consider inherited?
      int kid=dtm.getFirstNamespaceNode(nodeHandle,false);
      if(kid!=DTM.NULL)
      {
	System.out.println("\tNAMESPACES:");
	for(;
	    kid!=DTM.NULL;
	    kid=dtm.getNextNamespaceNode(nodeHandle,kid,false))
	{
	  printNode(dtm,kid,"\t");
	}
      }
      
      // List the attributes, if any
      kid=dtm.getFirstAttribute(nodeHandle);
      if(kid!=DTM.NULL)
      {
	System.out.println("\tATTRIBUTES:");
	for(;
	    kid!=DTM.NULL;
	    kid=dtm.getNextSibling(kid))
	{
	  printNode(dtm,kid,"\t");
	}
      }
      
      // Recurse into the children, if any
      recursiveDumpNode(dtm,dtm.getFirstChild(nodeHandle));
      }
    }

  static void printNode(DTM dtm,int nodeHandle,String indent)
  {
    // Briefly display this node
    // Don't bother displaying namespaces or attrs; we do that at the
    // next level up.
    System.out.println(indent+
		       "Node "+nodeHandle+": \""+dtm.getNodeName(nodeHandle)+
		       "\" expandedType="+dtm.getExpandedTypeID(nodeHandle)+
		       " ("+TYPENAME[dtm.getNodeType(nodeHandle)]+")\n"+

		       indent+
		       "\tParent=" + dtm.getParent(nodeHandle) +
		       " FirstChild=" + dtm.getFirstChild(nodeHandle) +
		       " NextSib=" + dtm.getNextSibling(nodeHandle)+"\n"+
		       
		       indent+
		       "\tValue=\"" + dtm.getNodeValue(nodeHandle)+"\""
		       ); 

  }
  
}
