package org.apache.xpath.parser;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

// This is a dummy class, created because I can't seem to get the XPath2 
// node off the top of the tree by using #void.
public class RootOfRoot extends NonExecutableExpression
{
  public RootOfRoot(XPath parser)
  {
	  super(parser);
  }
  
  public void jjtClose() 
  {
  	// We don't want this node to any longer be the child's parent!
  	Node n = jjtGetChild(0);
   	n.jjtSetParent(null);
  	super.jjtClose();
  }

}

