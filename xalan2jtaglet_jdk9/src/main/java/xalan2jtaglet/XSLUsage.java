package xalan2jtaglet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownBlockTagTree;

/**
 * Taglet for Xalan-Java documentation, giving us a standard way to
 * indicate when classes are public only because they are shared
 * across packages within Xalan code, not because they are intended for use
 * by others. Typical: "@xsl.usage internal"
 * <p>
 * Technically it might be better to OSGIfy the Xalan code, which
 * would also permit demand-loading of only the classes actually being
 * used by this execution... but that's an idea for the future.
 * <p>
 * This code renders the tag keywords (internal, advanced, experimental)
 * into their expanded renderings in the Javadoc.
 * <p>
 * Source code recreated from xalan2jtaglet.jar by IntelliJ IDEA (powered by
 * FernFlower decompiler), then adjusted to JDK 9+ taglet API.
 */
public class XSLUsage {
  public static final String TAG = "xsl.usage";
  private static final int INTERNAL = 0;
  private static final int ADVANCED = 1;
  private static final int EXPERIMENTAL = 2;
  private static final int UNSPECIFIED = -1;
  private static final String[] names = new String[]{
    "internal", "advanced", "experimental"
  };
  private static final String[] colours = new String[]{
    "FF0000", "00FF00", "0000FF"
  };
  private static final String[] messages = new String[]{
    "**For internal use only**", "**For advanced use only**", "**Experimental**"
  };

  public static String getHTML(DocTree usageTag) {
    int key = getKey(usageTag);
    return key == -1
      ? ""
      : "<i><font size=\"-1\" color=\"#" + colours[key] + "\"> " + messages[key] + "</font></i></DD>\n";
  }

  private static int getKey(DocTree usageTag) {
    if (usageTag instanceof UnknownBlockTagTree) {
      UnknownBlockTagTree tag = (UnknownBlockTagTree) usageTag;
      for (int i = 0; i < names.length; ++i) {
        if (names[i].equals(tag.getContent().toString()))
          return i;
      }
    }
    return -1;
  }
}
