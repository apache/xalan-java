/** Abstract superclass for generated template classes produced
 * by the CompilingStylesheetProcessor/CompilingStylesheetHandler.
 * (This had been an interface, but I want to factor out some 
 * "boilerplate" code rather than regenerating it each time.)
 */
package org.apache.xalan.processor;
import org.apache.xalan.templates.ElemTemplate;

public abstract class CompiledTemplate
extends ElemTemplate 
implements java.io.Serializable
{
  // Object[] m_interpretArray is used to bind to nodes we don't yet
  // know how to compile. Set at construction.
  // This array resembles the DOM's getChildren().item(), but includes
  // some things that aren't children, and is our primary access
  // point for its contents even when they are kids.
  protected java.lang.Object[] m_interpretArray;

  // Namespace context tracking. Note that this is dynamic state
  // during execution, _NOT_ the static state tied to a single 
  // ElemTemplateElement during parsing. Also note that it needs to
  // be set in execute() but testable in getNamespaceForPrefix --
  // and the latter, most unfortunately, is not passed the xctxt so
  // making that threadsafe is a bit ugly. 
  transient protected java.util.Hashtable m_nsThreadContexts=new java.util.Hashtable();

  /** Accessor to let interpretive children query current namespace state.
   * Note that unlike the interpreted version, the namespace state of this
   * code changes over time... and that we need to maintain a unique state
   * for each thread if we're to support multitasking.
   */
  public String getNamespaceForPrefix(String nsPrefix)
  {
    String nsuri="";
    org.xml.sax.helpers.NamespaceSupport nsSupport=
      (org.xml.sax.helpers.NamespaceSupport)
      m_nsThreadContexts.get(Thread.currentThread());
    if(null!=nsSupport)
      nsuri=nsSupport.getURI(nsPrefix);
    if(null==nsuri || nsuri.length()==0)
      nsuri=m_parentNode.getNamespaceForPrefix(nsPrefix);
    return nsuri;
  }


  /** public constructor: Copy values from original
   * template object, pick up "uncompiled children"
   * array from compilation/instantiation process,
   * and set the Locator information explicitly.
   * (I want the locator to be visible as literals
   * in the generated code, for debugging purposes.)
   */
  public CompiledTemplate(ElemTemplate original,
			  int lineNumber, int columnNumber,
			  String publicId,String systemId,
			  java.lang.Object[] interpretArray)
  {
    org.xml.sax.helpers.LocatorImpl locator=
      new org.xml.sax.helpers.LocatorImpl();
    locator.setLineNumber(lineNumber);
    locator.setColumnNumber(columnNumber);
    locator.setPublicId(publicId);
    locator.setSystemId(systemId);
    setLocaterInfo(locator); // yes, there's an or/er clash
    
    // uncompiled descendents/children
    this.m_interpretArray=interpretArray;

    // other context values which seem to be needed
    setMatch(original.getMatch());
    setMode(original.getMode());
    setName(original.getName());
    setPriority(original.getPriority());
    setStylesheet(original.getStylesheet());

    // Reparent the interpreted ElemTemplateElements,
    // to break their dependency on interpretive code.
    // AVTs in this array don't need proceessing, as they are
    // apparently not directly aware of their Elements.

    for(int i=0;i<m_interpretArray.length;++i)
      {
	if(m_interpretArray[i] instanceof org.apache.xalan.templates.ElemTemplateElement)
	  {
            org.apache.xalan.templates.ElemTemplateElement ete=
	      (org.apache.xalan.templates.ElemTemplateElement)
	      m_interpretArray[i];
	    
            // Append alone is not enough; it's lightweight, and assumes
            // the child had no previous parent. Need to remove first.
            // (We know that there _was_ a previous parent, of course!)
            appendChild(ete.getParentElem().removeChild(ete));
	  }
      }
  } // Constructor initialization ends
  
  
  /** Main entry point for the Template transformation.
   * It's abstract in CompiledTemplate, but is filled in
   * when the actual template code is synthesized.
   */
  public abstract void execute(
			  org.apache.xalan.transformer.TransformerImpl transformer,
		      org.w3c.dom.Node sourceNode,
		      org.apache.xalan.utils.QName mode)
       throws org.xml.sax.SAXException;
  
  /** During deserialization, reinstantiate the transient thread-table
   */
  private void readObject(java.io.ObjectInputStream in)
     throws java.io.IOException, ClassNotFoundException
  {
	in.defaultReadObject();   

	m_nsThreadContexts=new java.util.Hashtable();
  }
  
}
