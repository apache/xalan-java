package xalanjdoc;

import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;

public class PackageListWriter extends HtmlStandardWriter {
   public PackageListWriter(String filename) throws IOException {
      super(filename);
   }

   public static void generate(RootDoc root) throws DocletAbortException {
      String filename = "package-list";

      try {
         PackageListWriter packgen = new PackageListWriter(filename);
         packgen.generatePackageListFile(root);
         packgen.close();
      } catch (IOException var4) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var4.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generatePackageListFile(RootDoc root) {
      PackageDoc[] packages = Standard.configuration().packages;

      for(int i = 0; i < packages.length; ++i) {
         this.println(packages[i].name());
      }
   }
}
