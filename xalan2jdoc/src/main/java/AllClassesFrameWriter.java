package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.tools.doclets.DocletAbortException;
import com.sun.tools.doclets.IndexBuilder;
import java.io.IOException;
import java.util.List;

public class AllClassesFrameWriter extends HtmlStandardWriter {
   protected IndexBuilder indexbuilder;

   public AllClassesFrameWriter(String filename, IndexBuilder indexbuilder) throws IOException, DocletAbortException {
      super(filename);
      this.indexbuilder = indexbuilder;
   }

   public static void generate(IndexBuilder indexbuilder) throws DocletAbortException {
      String filename = "allclasses-frame.html";

      try {
         AllClassesFrameWriter allclassgen = new AllClassesFrameWriter(filename, indexbuilder);
         allclassgen.generateAllClassesFile();
         allclassgen.close();
      } catch (IOException var4) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var4.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateAllClassesFile() throws IOException {
      String label = this.getText("doclet.All_Classes");
      this.printHeader(label);
      this.printAllClassesTableHeader();
      this.printAllClasses();
      this.printAllClassesTableFooter();
      this.printBodyHtmlEnd();
   }

   protected void generateContents(List classlist) {
      for(int i = 0; i < classlist.size(); ++i) {
         ClassDoc cd = (ClassDoc)classlist.get(i);
         if (this.isCoreClass(cd)) {
            String label = this.italicsClassName(cd, false);
            this.printTargetHyperLink(this.pathToClass(cd), "classFrame", label);
            this.br();
         }
      }
   }

   protected void printAllClasses() {
      for(int i = 0; i < this.indexbuilder.elements().length; ++i) {
         Character unicode = (Character)this.indexbuilder.elements()[i];
         this.generateContents(this.indexbuilder.getMemberList(unicode));
      }
   }

   protected void printAllClassesTableFooter() {
      this.fontEnd();
      this.tdEnd();
      this.trEnd();
      this.tableEnd();
   }

   protected void printAllClassesTableHeader() {
      this.fontSizeStyle("+1", "FrameHeadingFont");
      this.boldText("doclet.All_Classes");
      this.fontEnd();
      this.br();
      this.table();
      this.tr();
      this.tdNowrap();
      this.fontStyle("FrameItemFont");
   }
}
