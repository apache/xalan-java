package javax.xml.trax.dom;

import javax.xml.trax.*;

import java.lang.String;

import java.io.OutputStream;
import java.io.Writer;

import org.w3c.dom.Node;

/**
 * Acts as an holder for a transformation Source tree.
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class DOMSource implements Source
{

  /**
   * Zero-argument default constructor.
   */
  public DOMSource() {}

  /**
   * Create a new output target with a DOM node.
   *
   * @param n The DOM node that will contain the Source tree.
   */
  public DOMSource(Node n)
  {
    setNode(n);
  }

  /**
   * Set the node that will contain the Source DOM tree.
   *
   * @param node
   */
  public void setNode(Node node)
  {
    this.node = node;
  }

  /**
   * Get the node that will contain the Source tree.
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
