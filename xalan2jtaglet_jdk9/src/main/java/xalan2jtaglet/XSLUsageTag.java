package xalan2jtaglet;

import com.sun.source.doctree.DocTree;
import jdk.javadoc.doclet.Taglet;

import javax.lang.model.element.Element;
import java.util.*;

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
public class XSLUsageTag implements Taglet {
  private static final String HEADER = "Usage:";

  @Override
  public Set<Location> getAllowedLocations() {
    return new HashSet<>(Arrays.asList(Location.values()));
  }

  @Override
  public boolean isInlineTag() {
    return false;
  }

  @Override
  public String getName() {
    return "xsl.usage";
  }

  @Override
  public String toString(List<? extends DocTree> tags, Element element) {
    if (tags == null || tags.isEmpty())
      return "";

    String string = "\n<DT><b>Usage:</b><DD>";
    for (DocTree tag : tags)
      string = string + XSLUsage.getHTML(tag) + ", ";

    // Remove trailing ", ", add end tag
    return string.substring(0, string.length() - 2) + "</DD>\n";
  }

  public static void register(Map tagletMap) {
    XSLUsageTag tag = new XSLUsageTag();
    Taglet t = (Taglet) tagletMap.get(tag.getName());
    if (t != null)
      tagletMap.remove(tag.getName());
    tagletMap.put(tag.getName(), tag);
  }
}
