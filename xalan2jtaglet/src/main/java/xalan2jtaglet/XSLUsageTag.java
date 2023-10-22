/** Taglet for Xalan-Java documentation, giving us a standard way to
    indicate when classes are public only because they are shared
    across packages within Xalan code, not because they are intended for use
    by others. Typical: "@xsl.usage internal"

    Technically it might be better to OSGIfy the Xalan code, which
    would also permit demand-loading of only the classes actually being
    used by this execution... but that's an idea for the future.
 */
//
// Source code recreated from xalan2jtaglet.jar by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package xalan2jtaglet;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

import java.util.Map;

public class XSLUsageTag implements Taglet {
  private static final String HEADER = "Usage:";

  public XSLUsageTag() {}

  public boolean inConstructor() {
    return true;
  }

  public boolean inField() {
    return true;
  }

  public boolean inMethod() {
    return true;
  }

  public boolean inOverview() {
    return true;
  }

  public boolean inPackage() {
    return true;
  }

  public boolean inType() {
    return true;
  }

  public boolean isInlineTag() {
    return false;
  }

  public String getName() {
    return "xsl.usage";
  }

  public String toString(Tag arg0) {
    return "\n<DT><b>Usage:</b><DD>" + XSLUsage.getHTML(arg0) + "</DD>\n";
  }

  public String toString(Tag[] arg0) {
    String string = "";
    if (arg0 != null && arg0.length != 0) {
      string = "\n<DT><b>Usage:</b><DD>";

      for (int i = 0; i < arg0.length - 1; ++i) {
        string = string + XSLUsage.getHTML(arg0[i]) + ", ";
      }

      string = string + XSLUsage.getHTML(arg0[arg0.length - 1]);
      string = string + "</DD>\n";
      return string;
    } else {
      return string;
    }
  }

  public static void register(Map tagletMap) {
    XSLUsageTag tag = new XSLUsageTag();
    Taglet t = (Taglet) tagletMap.get(tag.getName());
    if (t != null) {
      tagletMap.remove(tag.getName());
    }

    tagletMap.put(tag.getName(), tag);
  }
}
