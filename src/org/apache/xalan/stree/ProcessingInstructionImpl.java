package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public class ProcessingInstructionImpl extends Child 
  implements ProcessingInstruction
{
  private String m_name;
  private String m_data;
  
  /**
   * Implement the processingInstruction event.
   */
  ProcessingInstructionImpl(String target, String data)
  {
    m_name = target;
    m_data = data;
  }
  
  /** Get the PI name. */
  public String getNodeName()
  {
    return m_name;
  }
  
  /**
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   */
  public short getNodeType() 
  {
    return Node.PROCESSING_INSTRUCTION_NODE;
  }
  
  /**
   * A PI's "target" states what processor channel the PI's data
   * should be directed to. It is defined differently in HTML and XML.
   * <p>
   * In XML, a PI's "target" is the first (whitespace-delimited) token
   * following the "<?" token that begins the PI.
   * <p>
   * In HTML, target is always null.
   * <p>
   * Note that getNodeName is aliased to getTarget.
   */
  public String getTarget() 
  {
    return m_name;
  } // getTarget():String

  /**
   * The content of this processing instruction. This is from the first non 
   * white space character after the target to the character immediately 
   * preceding the <code>?&gt;</code>.
   * @exception DOMException
   *   NO_MODIFICATION_ALLOWED_ERR: Raised when the node is readonly.
   */
  public String       getData()
  {
    return m_data;
  }
  
  public String getNodeValue() 
  {
    return m_data;
  }

}
