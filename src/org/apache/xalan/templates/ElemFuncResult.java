package org.apache.xalan.templates;

//import org.w3c.dom.*;
import org.apache.xml.dtm.DTM;

import org.xml.sax.*;

import org.apache.xpath.*;
import org.apache.xpath.Expression;
import org.apache.xpath.objects.XObjectFactory;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XRTreeFragSelectWrapper;
import org.apache.xml.utils.QName;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;

import javax.xml.transform.TransformerException;

/**
 * Handles the xsl:result element within an xsl:function element.
 */
public class ElemFuncResult extends ElemVariable
{
 
  /**
   * Generate the xsl:function return value, and assign it to the variable
   * index slot assigned for it in ElemFunction compose().
   * 
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {    
    XPathContext context = transformer.getXPathContext();
    VariableStack varStack = context.getVarStack();
    // ElemFunc result should always be the last child of an ElemFunction.
    ElemFunction owner = this.getParentElem() instanceof ElemFunction ? 
                         (ElemFunction)this.getParentElem() : null;
    if (owner != null)
    {
      int resultIndex = owner.getResultIndex();
      int sourceNode = context.getCurrentNode();
      // Set the return value;
      XObject var = getValue(transformer, sourceNode);   
      varStack.setLocalVariable(resultIndex, var);
    }    
  }

  /**
   * Get an integer representation of the element type.
   *
   * @return An integer representation of the element, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_FUNCRESULT;
  }
  
  /**
   * Return the node name, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants.
   * @return The node name
   * 
   */
   public String getNodeName()
  {
    return Constants.ELEMNAME_FUNCRESULT_STRING;
  }

  /**
   * @see org.apache.xalan.templates.ElemVariable#addVariableName(ComposeState)
   */
  protected void addVariableName(StylesheetRoot.ComposeState cstate)
  {
    // We don't want to add this to the list.
    // super.addVariableName(cstate);
  }

}
