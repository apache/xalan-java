package xalanjdoc;

import com.sun.javadoc.Doc;
import java.util.StringTokenizer;

public class Usage {
   public static final int UNSPECIFIED = 0;
   public static final int GENERAL = 1;
   public static final int ADVANCED = 2;
   public static final int INTERNAL = 3;
   public static final int EXPERIMENTAL = 4;

   static int findUsage(Doc doc) {
      String token0 = "meta";
      String token1 = "name";
      String token2 = "\"usage\"";
      String token3 = "content";
      String adv = "\"advanced\"";
      String intern = "\"internal\"";
      String experiment = "\"experimental\"";
      String commentAll = doc.commentText().toLowerCase();
      String comment;
      if (commentAll.length() > 50) {
         comment = commentAll.substring(0, 50);
      } else {
         comment = commentAll;
      }

      StringTokenizer tokens = new StringTokenizer(comment, " \n\t\r<>=/");
      if (tokens.countTokens() < 5) {
         return 0;
      } else if (token0.equals(tokens.nextToken())
         && token1.equals(tokens.nextToken())
         && token2.equals(tokens.nextToken())
         && token3.equals(tokens.nextToken())) {
         String token4 = tokens.nextToken();
         if (token4.equals(adv)) {
            return 2;
         } else if (token4.equals(intern)) {
            return 3;
         } else {
            return token4.equals(experiment) ? 4 : 1;
         }
      } else {
         return 0;
      }
   }

   public static int usagePattern(Doc doc) {
      return findUsage(doc);
   }
}
