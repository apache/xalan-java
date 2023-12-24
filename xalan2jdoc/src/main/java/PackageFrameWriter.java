package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.DirectoryManager;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;
import java.util.Arrays;

public class PackageFrameWriter extends AbstractPackageWriter {
   public PackageFrameWriter(String path, String filename, PackageDoc packagedoc) throws IOException, DocletAbortException {
      super(path, filename, packagedoc);
   }

   public static void generate(PackageDoc pkg) throws DocletAbortException {
      String path = DirectoryManager.getDirectoryPath(pkg);
      String filename = "package-frame.html";

      try {
         PackageFrameWriter packgen = new PackageFrameWriter(path, filename, pkg);
         packgen.generatePackageFile();
         packgen.close();
      } catch (IOException var5) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var5.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateClassKindListing(ClassDoc[] arr, String label) {
      if (arr.length > 0) {
         Arrays.sort((Object[])arr);
         this.printPackageTableHeader();
         this.fontSizeStyle("+1", "FrameHeadingFont");
         this.print(label);
         this.fontEnd();
         this.println("&nbsp;");
         this.fontStyle("FrameItemFont");

         for(int i = 0; i < arr.length; ++i) {
            if (this.isCoreClass(arr[i])) {
               this.br();
               this.printTargetClassLink(arr[i], "classFrame");
            }
         }

         this.fontEnd();
         this.printPackageTableFooter();
         this.println();
      }
   }

   protected void generateClassListing() {
      this.generateClassKindListing(super.packagedoc.interfaces(), this.getText("doclet.Interfaces"));
      this.generateClassKindListing(super.packagedoc.ordinaryClasses(), this.getText("doclet.Classes"));
      this.generateClassKindListing(super.packagedoc.exceptions(), this.getText("doclet.Exceptions"));
      this.generateClassKindListing(super.packagedoc.errors(), this.getText("doclet.Errors"));
   }

   protected void printPackageDescription() throws IOException {
   }

   protected void printPackageFooter() {
   }

   protected void printPackageHeader(String heading) {
      this.fontSizeStyle("+1", "FrameTitleFont");
      this.printTargetPackageLink(super.packagedoc, "classFrame", heading);
      this.fontEnd();
   }

   protected void printPackageTableFooter() {
      this.tdEnd();
      this.trEnd();
      this.tableEnd();
   }

   protected void printPackageTableHeader() {
      this.table();
      this.tr();
      this.tdNowrap();
   }
}
