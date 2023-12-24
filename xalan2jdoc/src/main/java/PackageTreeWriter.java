package xalanjdoc;

import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.ClassTree;
import com.sun.tools.doclets.DirectoryManager;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;

public class PackageTreeWriter extends AbstractTreeWriter {
   protected PackageDoc packagedoc;
   protected PackageDoc prev;
   protected PackageDoc next;

   public PackageTreeWriter(String path, String filename, PackageDoc packagedoc, PackageDoc prev, PackageDoc next, boolean noDeprecated) throws IOException, DocletAbortException {
      super(path, filename, new ClassTree(packagedoc.allClasses(), noDeprecated), packagedoc);
      this.packagedoc = packagedoc;
      this.prev = prev;
      this.next = next;
   }

   public static void generate(PackageDoc pkg, PackageDoc prev, PackageDoc next, boolean noDeprecated) throws DocletAbortException {
      String path = DirectoryManager.getDirectoryPath(pkg);
      String filename = "package-tree.html";

      try {
         PackageTreeWriter packgen = new PackageTreeWriter(path, filename, pkg, prev, next, noDeprecated);
         packgen.generatePackageTreeFile();
         packgen.close();
      } catch (IOException var8) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var8.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generatePackageTreeFile() throws IOException {
      this.printHeader(this.getText("doclet.Window_Package_Class_Hierarchy", Standard.configuration().windowtitle, this.packagedoc.name()));
      this.printPackageTreeHeader();
      if (Standard.configuration().packages.length > 1) {
         this.printLinkToMainTree();
      }

      this.generateTree(super.classtree.baseclasses(), "doclet.Class_Hierarchy");
      this.generateTree(super.classtree.baseinterfaces(), "doclet.Interface_Hierarchy");
      this.printPackageTreeFooter();
      this.printBottom();
      this.printBodyHtmlEnd();
   }

   protected void navLinkNext() {
      if (this.next == null) {
         this.navLinkNext(null);
      } else {
         String path = DirectoryManager.getRelativePath(this.packagedoc.name(), this.next.name());
         this.navLinkNext(path + "package-tree.html");
      }
   }

   protected void navLinkPackage() {
      this.navCellStart();
      this.printHyperLink("package-summary.html", "", this.getText("doclet.Package"), true, "NavBarFont1");
      this.navCellEnd();
   }

   protected void navLinkPrevious() {
      if (this.prev == null) {
         this.navLinkPrevious(null);
      } else {
         String path = DirectoryManager.getRelativePath(this.packagedoc.name(), this.prev.name());
         this.navLinkPrevious(path + "package-tree.html");
      }
   }

   protected void printLinkToMainTree() {
      this.dl();
      this.dt();
      this.boldText("doclet.Package_Hierarchies");
      this.dd();
      this.navLinkMainTree(this.getText("doclet.All_Packages"));
      this.dlEnd();
      this.hr();
   }

   protected void printPackageTreeFooter() {
      this.hr();
      this.navLinks(false);
   }

   protected void printPackageTreeHeader() {
      this.navLinks(true);
      this.hr();
      this.center();
      this.h2(this.getText("doclet.Hierarchy_For_Package", this.packagedoc.name()));
      this.centerEnd();
   }
}
