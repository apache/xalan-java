package javax.xml.trax.dom;

import javax.xml.trax.*;

import java.lang.String;

import java.io.OutputStream;
import java.io.Writer;

import org.w3c.dom.Node;

/**
 * Acts as an holder for a transformation result tree.
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class DOMResult implements Result
{

  /**
   * Zero-argument default constructor.
   */
  public DOMResult() {}

  /**
   * Create a new output target with a DOM node.
   *
   * @param n The DOM node that will contain the result tree.
   */
  public DOMResult(Node n)
  {
    setNode(n);
  }

  /**
   * Set the node that will contain the result DOM tree.
   *
   * @param node
   */
  public void setNode(Node node)
  {
    this.node = node;
  }

  /**
   * Get the node that will contain the result tree.
   *
   * @return
   */
  public Node getNode()
  {
    return node;
  }

  /**
   * Field node
   */
  private Node node;
}
