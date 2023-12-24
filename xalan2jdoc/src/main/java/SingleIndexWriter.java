package xalanjdoc;

import com.sun.tools.doclets.DocletAbortException;
import com.sun.tools.doclets.IndexBuilder;
import java.io.IOException;

public class SingleIndexWriter extends AbstractIndexWriter {
   public SingleIndexWriter(String filename, IndexBuilder indexbuilder) throws IOException {
      super(filename, indexbuilder);
   }

   public static void generate(IndexBuilder indexbuilder) throws DocletAbortException {
      String filename = "index-all.html";

      try {
         SingleIndexWriter indexgen = new SingleIndexWriter(filename, indexbuilder);
         indexgen.generateIndexFile();
         indexgen.close();
      } catch (IOException var4) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var4.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateIndexFile() throws IOException {
      this.printHeader(this.getText("doclet.Window_Single_Index", Standard.configuration().windowtitle));
      this.navLinks(true);
      this.printLinksForIndexes();
      this.hr();

      for(int i = 0; i < super.indexbuilder.elements().length; ++i) {
         Character unicode = (Character)super.indexbuilder.elements()[i];
         this.generateContents(unicode, super.indexbuilder.getMemberList(unicode));
      }

      this.printLinksForIndexes();
      this.navLinks(false);
      this.printBottom();
      this.printBodyHtmlEnd();
   }

   protected void printLinksForIndexes() {
      for(int i = 0; i < super.indexbuilder.elements().length; ++i) {
         String unicode = super.indexbuilder.elements()[i].toString();
         this.printHyperLink("#_" + unicode + "_", unicode);
         this.print(' ');
      }
   }
}
