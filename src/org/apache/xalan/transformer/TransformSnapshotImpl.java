package org.apache.xalan.transformer;

import org.apache.xpath.XPathContext;
import org.apache.xpath.VariableStack;
import org.apache.xpath.axes.ContextNodeList;
import org.apache.xml.utils.NodeVector;
import org.apache.xml.utils.BoolStack;
import java.util.Stack;
import org.xml.sax.helpers.NamespaceSupport;
import java.util.Enumeration;

/**
 * This class holds a "snapshot" of it's current transformer state, 
 * which can later be restored.
 * 
 * This only saves state which can change over the course of the side-effect-free 
 * (i.e. no extensions that call setURIResolver, etc.).
 */
class TransformSnapshotImpl implements TransformSnapshot
{
  /**
   * The stack of Variable stack frames.
   */
  private VariableStack m_variableStacks;
  
  /** The stack of <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a> objects.
   *  Not to be confused with the current node list.  */
  private NodeVector m_currentNodes;
  
  /** A stack of the current sub-expression nodes.  */
  private NodeVector m_currentExpressionNodes;

  /**
   * The current context node lists stack.
   */
  private Stack m_contextNodeLists;
	
	/**
   * The current context node list.
   */
  private ContextNodeList m_contextNodeList;

  /**
   * Stack of AxesIterators.
   */
  private Stack m_axesIteratorStack;

  /**
   * Is > 0 when we're processing a for-each.
   */
  private BoolStack m_currentTemplateRuleIsNull = new BoolStack();

  /** A node vector used as a stack to track the current 
   * ElemTemplateElement.  Needed for the 
   * org.apache.xalan.transformer.TransformState interface,  
   * so a tool can discover the calling template. */
  private NodeVector m_currentTemplateElements = new NodeVector(64);

  /** A node vector used as a stack to track the current 
   * ElemTemplate that was matched, as well as the node that 
   * was matched.  Needed for the 
   * org.apache.xalan.transformer.TransformState interface,  
   * so a tool can discover the matched template, and matched 
   * node. */
  private NodeVector m_currentMatchTemplates = new NodeVector();

  /**
   * The table of counters for xsl:number support.
   * @see ElemNumber
   */
  private CountersTable m_countersTable = null;

  /**
   * Stack for the purposes of flagging infinite recursion with
   * attribute sets.
   */
  private Stack m_attrSetStack = null;
  
  /** Indicate whether a namespace context was pushed          */
  boolean m_nsContextPushed;

  /**
   * Use the SAX2 helper class to track result namespaces.
   */
  private NamespaceSupport m_nsSupport;

  /** The number of events queued          */
  int m_eventCount;

  /** Queued start document          */
  QueuedStartDocument m_startDoc;

  /** Queued start element          */
  QueuedStartElement m_startElement;
	
	TransformSnapshotImpl(TransformerImpl transformer)
  {
    try
    {
      // Are all these clones deep enough?
      
      ResultTreeHandler rtf = transformer.getResultTreeHandler();
      
      m_startElement = (QueuedStartElement)rtf.m_startElement.clone();
      m_startDoc = (QueuedStartDocument)rtf.m_startDoc.clone();
			m_eventCount = rtf.m_eventCount;
            
      // yuck.  No clone. Hope this is good enough.
      m_nsSupport = new NamespaceSupport();
      Enumeration prefixes = rtf.m_nsSupport.getPrefixes();
      while(prefixes.hasMoreElements())
      {
        String prefix = (String)prefixes.nextElement();
        String uri = rtf.m_nsSupport.getURI(prefix);
        m_nsSupport.declarePrefix(prefix, uri);
      }
      
      m_nsContextPushed = rtf.m_nsContextPushed;
      
      XPathContext xpc = transformer.getXPathContext();
      
      m_variableStacks = (VariableStack)xpc.getVarStack().clone();
      m_currentNodes = (NodeVector)xpc.getCurrentNodeStack().clone();
      m_currentExpressionNodes = (NodeVector)xpc.getCurrentExpressionNodeStack().clone();
      m_contextNodeLists = (Stack)xpc.getContextNodeListsStack().clone();
			if (!m_contextNodeLists.empty())
				m_contextNodeList = (ContextNodeList)xpc.getContextNodeList().clone();
      m_axesIteratorStack = (Stack)xpc.getAxesIteratorStackStacks().clone();
  
      m_currentTemplateRuleIsNull = (BoolStack)transformer.m_currentTemplateRuleIsNull.clone();
      m_currentTemplateElements = (NodeVector)transformer.m_currentTemplateElements.clone();
      m_currentMatchTemplates = (NodeVector)transformer.m_currentMatchTemplates.clone();
      m_countersTable = (CountersTable)transformer.getCountersTable().clone();
			if (transformer.m_attrSetStack  != null)
				m_attrSetStack = (Stack)transformer.m_attrSetStack.clone();
    }
    catch(CloneNotSupportedException cnse)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(cnse);
    }
  }
  
  void apply(TransformerImpl transformer)
  {
    try
    {
      // Are all these clones deep enough?

      ResultTreeHandler rtf = transformer.getResultTreeHandler();
			if (rtf != null)
			{
				rtf.m_startElement = (QueuedStartElement)m_startElement.clone();
				rtf.m_startDoc = (QueuedStartDocument)m_startDoc.clone();
				rtf.m_eventCount = 1; //1 for start document event! m_eventCount;
				
				// yuck.  No clone. Hope this is good enough.
				rtf.m_nsSupport = new NamespaceSupport();
				Enumeration prefixes = m_nsSupport.getPrefixes();
				while(prefixes.hasMoreElements())
				{
					String prefix = (String)prefixes.nextElement();
					String uri = m_nsSupport.getURI(prefix);
					rtf.m_nsSupport.declarePrefix(prefix, uri);
				}
				
				rtf.m_nsContextPushed = m_nsContextPushed;
			}
      XPathContext xpc = transformer.getXPathContext();
      
      xpc.setVarStack((VariableStack)m_variableStacks.clone());
      xpc.setCurrentNodeStack((NodeVector)m_currentNodes.clone());
      xpc.setCurrentExpressionNodeStack((NodeVector)m_currentExpressionNodes.clone());
      xpc.setContextNodeListsStack((Stack)m_contextNodeLists.clone());
			if (m_contextNodeList != null)
				xpc.pushContextNodeList((ContextNodeList)m_contextNodeList.clone());
      xpc.setAxesIteratorStackStacks((Stack)m_axesIteratorStack.clone());
  
      transformer.m_currentTemplateRuleIsNull = (BoolStack)m_currentTemplateRuleIsNull.clone();
      transformer.m_currentTemplateElements = (NodeVector)m_currentTemplateElements.clone();
      transformer.m_currentMatchTemplates = (NodeVector)m_currentMatchTemplates.clone();
      transformer.m_countersTable = (CountersTable)m_countersTable.clone();
			if (m_attrSetStack  != null)
				transformer.m_attrSetStack = (Stack)m_attrSetStack.clone();
    }
    catch(CloneNotSupportedException cnse)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(cnse);
    }
  }
}

