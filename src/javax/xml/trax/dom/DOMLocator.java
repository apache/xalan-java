package javax.xml.trax.dom;

import javax.xml.trax.SourceLocator;
import org.w3c.dom.Node;

/**
 * Interface DOMLocator
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public interface DOMLocator extends SourceLocator
{

  /**
   * Method getOriginatingNode
   *
   * @return
   */
  public Node getOriginatingNode();
}
