package xalanjdoc;

import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;

public class PackagesFileWriter extends HtmlStandardWriter {
   public PackagesFileWriter(String filename) throws IOException {
      super(filename);
   }

   public static void generate() throws DocletAbortException {
      String filename = "";

      try {
         filename = "packages.html";
         PackagesFileWriter packgen = new PackagesFileWriter(filename);
         packgen.generatePackagesFile();
         packgen.close();
      } catch (IOException var3) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var3.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generatePackagesFile() {
      this.printHeader(this.getText("doclet.Window_Packages_title", Standard.configuration().windowtitle));
      this.printPackagesFileContents();
      this.printBodyHtmlEnd();
   }

   protected void printPackagesFileContents() {
      this.br();
      this.br();
      this.br();
      this.center();
      this.printText("doclet.Packages_File_line_1");
      this.printText("doclet.Packages_File_line_2");
      this.br();
      this.printNbsps();
      this.printHyperLink("index.html", this.getText("doclet.Frame_Version"));
      this.br();
      this.printNbsps();
      this.printHyperLink(Standard.configuration().topFile, this.getText("doclet.Non_Frame_Version"));
      this.centerEnd();
   }
}
