package xalanjdoc;

import com.sun.javadoc.Tag;

public class XSLUsage {
   public static final String TAG = "xsl.usage";
   private static final int INTERNAL = 0;
   private static final int ADVANCED = 1;
   private static final int EXPERIMENTAL = 2;
   private static final int UNSPECIFIED = -1;
   private static final String[] names = new String[]{"internal", "advanced", "experimental"};
   private static final String[] colours = new String[]{"FF0000", "00FF00", "0000FF"};
   private static final String[] messages = new String[]{"**For internal use only**", "**For advanced use only**", "**Experimental**"};

   public static String getHTML(Tag usageTag) {
      int key = getKey(usageTag);
      return key == -1 ? "" : "<i><font size=\"-1\" color=\"#" + colours[key] + "\"> " + messages[key] + "</font></i></DD>\n";
   }

   private static int getKey(Tag usageTag) {
      for(int i = 0; i < names.length; ++i) {
         if (names[i].equals(usageTag.text())) {
            return i;
         }
      }

      return -1;
   }
}
