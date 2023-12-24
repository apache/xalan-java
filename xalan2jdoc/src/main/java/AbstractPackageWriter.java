package xalanjdoc;

import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.DirectoryManager;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;

public abstract class AbstractPackageWriter extends HtmlStandardWriter {
   PackageDoc packagedoc;

   public AbstractPackageWriter(String path, String filename, PackageDoc packagedoc) throws IOException, DocletAbortException {
      super(path, filename, DirectoryManager.getRelativePath(packagedoc.name()));
      this.packagedoc = packagedoc;
   }

   protected abstract void generateClassListing();

   protected void generatePackageFile() throws IOException {
      String pkgName = this.packagedoc.toString();
      String heading = this.getText("doclet.Window_Package", Standard.configuration().windowtitle, pkgName);
      this.printHeader(heading);
      this.printPackageHeader(pkgName);
      this.generateClassListing();
      this.printPackageDescription();
      this.printPackageFooter();
      this.printBodyHtmlEnd();
   }

   protected void navLinkPackage() {
      this.navCellRevStart();
      this.fontStyle("NavBarFont1Rev");
      this.boldText("doclet.Package");
      this.fontEnd();
      this.navCellEnd();
   }

   protected abstract void printPackageDescription() throws IOException;

   protected abstract void printPackageFooter();

   protected abstract void printPackageHeader(String var1);
}
