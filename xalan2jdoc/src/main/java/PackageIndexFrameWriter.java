package xalanjdoc;

import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;

public class PackageIndexFrameWriter extends AbstractPackageIndexWriter {
   public PackageIndexFrameWriter(String filename) throws IOException {
      super(filename);
   }

   public static void generate() throws DocletAbortException {
      String filename = "overview-frame.html";

      try {
         PackageIndexFrameWriter packgen = new PackageIndexFrameWriter(filename);
         packgen.generatePackageIndexFile();
         packgen.close();
      } catch (IOException var3) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var3.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void printAllClassesPackagesLink() {
      this.fontStyle("FrameItemFont");
      this.printTargetHyperLink("allclasses-frame.html", "packageFrame", this.getText("doclet.All_Classes"));
      this.fontEnd();
      this.p();
      this.fontSizeStyle("+1", "FrameHeadingFont");
      this.printText("doclet.Packages");
      this.fontEnd();
      this.br();
   }

   protected void printIndexFooter() {
      this.printTableFooter();
   }

   protected void printIndexHeader(String text) {
      this.printTableHeader();
   }

   protected void printIndexRow(PackageDoc pd) {
      this.fontStyle("FrameItemFont");
      this.printTargetHyperLink(this.pathString(pd, "package-frame.html"), "packageFrame", pd.name());
      this.fontEnd();
      this.br();
   }

   protected void printNavigationBarFooter() {
      this.p();
      this.space();
   }

   protected void printNavigationBarHeader() {
      if (Standard.configuration().header != null) {
         this.printTableHeader();
         this.fontSizeStyle("+1", "FrameTitleFont");
         this.bold(Standard.configuration().header);
         this.fontEnd();
         this.printTableFooter();
      }
   }

   protected void printOverviewHeader() {
   }

   protected void printTableFooter() {
      this.tdEnd();
      this.trEnd();
      this.tableEnd();
   }

   protected void printTableHeader() {
      this.table();
      this.tr();
      this.tdNowrap();
   }
}
