package org.apache.xml.dtm.dom2dtm2;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/** Returns an object which can map a Node in a specific DOM into a 
 * unique-within-document identifying integer. Different
 * DOMs may require different solutions, so most of the
 * actual behavior will occur in subclasses; the factory
 * method <code>createResolverForDOM</code> can be used to
 * (attempt to?) automatically select an appropriate
 * implementation.
 * 
 * @author keshlam
 * @since Sep 6, 2002
 */
public abstract class NodeDTMIDResolverFactory
{
	/** FACTORY METHOD: Obtain a resolver appropriate for a
	 * specific DOM implementation. This may look at anything
	 * from class/interface ancestry to isSupported() calls to
	 * behavioral probes.
	 * 
	 * @param domimpl The DOMImplementation object for this DOM.
	 * If null, we will attempt to retrieve it from the document.
	 * 
	 * @param doc The root node for a specific DOM in this
	 * implementation. If null, we may have to create (and then
	 * discard) a temporary document as part of our analysis...
	 * but this is problematic, since an implementation may deliver
	 * different classes, with different behaviors, depending on the
	 * doctype and root element instantiated.
	 * 
	 * @return An instance of NodeDTMIDResolverFactory which we hope
	 * will be suitable for use with this DOM, or <code>null</code>
	 * if we haven't a clue.
	 * */
	public static NodeDTMIDResolver createResolverForDOM
		(DOMImplementation domimpl, Node node)
	{
		if (domimpl==null && node==null)
			return null; // Can't do anything useful!
			
		// This is problematic, as discussed above
		Document doc;
		if(node==null)
			doc=domimpl.createDocument(null,"dummy",null);
		else
		{
			// Go from general node to its Document node.
			// Unfortunately Document.getOwnerDocument() is null...
			doc=node.getOwnerDocument();
			if(doc==null) doc=(Document)node;
		}
	
		if (domimpl==null)
			domimpl=doc.getImplementation();
			
		// Start seeking mapper, in order of preference
		NodeDTMIDResolver resolver=null;
	
		// Try Xerces-specific (preview DOM3)
		resolver=NodeDTMIDResolver_xerces.getInstance(domimpl,doc);
		
		if(resolver==null)
		{	
			// DOM3-specific? Requires DOM3 in classpath, or some
			// reflection magic.
			
			// Consider a last-ditch isSameNode search.
			
			// Consider a last-last-ditch "In this DOM, we happen to
			// know that object identity equals node identity" search.
		
			// We've run out of good ideas
		}
		return resolver;
	}
}

