package xalanjdoc;

import com.sun.tools.doclets.DirectoryManager;
import com.sun.tools.doclets.DocletAbortException;
import com.sun.tools.doclets.IndexBuilder;
import java.io.IOException;

public class SplitIndexWriter extends AbstractIndexWriter {
   protected int prev;
   protected int next;

   public SplitIndexWriter(String path, String filename, String relpath, IndexBuilder indexbuilder, int prev, int next) throws IOException {
      super(path, filename, relpath, indexbuilder);
      this.prev = prev;
      this.next = next;
   }

   public static void generate(IndexBuilder indexbuilder) throws DocletAbortException {
      String filename = "";
      String path = DirectoryManager.getPath("index-files");
      String relpath = DirectoryManager.getRelativePath("index-files");

      try {
         for(int i = 0; i < indexbuilder.elements().length; ++i) {
            int j = i + 1;
            int prev = j == 1 ? -1 : i;
            int next = j == indexbuilder.elements().length ? -1 : j + 1;
            filename = "index-" + j + ".html";
            SplitIndexWriter indexgen = new SplitIndexWriter(path, filename, relpath, indexbuilder, prev, next);
            indexgen.generateIndexFile((Character)indexbuilder.elements()[i]);
            indexgen.close();
         }
      } catch (IOException var9) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var9.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateIndexFile(Character unicode) throws IOException {
      this.printHeader(this.getText("doclet.Window_Split_Index", Standard.configuration().windowtitle, unicode.toString()));
      this.navLinks(true);
      this.printLinksForIndexes();
      this.hr();
      this.generateContents(unicode, super.indexbuilder.getMemberList(unicode));
      this.navLinks(false);
      this.printLinksForIndexes();
      this.printBottom();
      this.printBodyHtmlEnd();
   }

   protected void navLinkNext() {
      if (this.next == -1) {
         this.printText("doclet.Next_Letter");
      } else {
         this.printHyperLink("index-" + this.next + ".html", "", this.getText("doclet.Next_Letter"), true);
      }
   }

   protected void navLinkPrevious() {
      if (this.prev == -1) {
         this.printText("doclet.Prev_Letter");
      } else {
         this.printHyperLink("index-" + this.prev + ".html", "", this.getText("doclet.Prev_Letter"), true);
      }
   }

   protected void printLinksForIndexes() {
      for(int i = 0; i < super.indexbuilder.elements().length; ++i) {
         int j = i + 1;
         this.printHyperLink("index-" + j + ".html", super.indexbuilder.elements()[i].toString());
         this.print(' ');
      }
   }
}
