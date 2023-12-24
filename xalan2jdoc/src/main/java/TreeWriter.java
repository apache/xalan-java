package xalanjdoc;

import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.ClassTree;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;

public class TreeWriter extends AbstractTreeWriter {
   private PackageDoc[] packages;
   private boolean classesonly;

   public TreeWriter(String filename, ClassTree classtree) throws IOException, DocletAbortException {
      super(filename, classtree);
      this.packages = Standard.configuration().packages;
      this.classesonly = this.packages.length == 0;
   }

   public static void generate(ClassTree classtree) throws DocletAbortException {
      String filename = "overview-tree.html";

      try {
         TreeWriter treegen = new TreeWriter(filename, classtree);
         treegen.generateTreeFile();
         treegen.close();
      } catch (IOException var4) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var4.toString(), filename);
         throw new DocletAbortException();
      }
   }

   public void generateTreeFile() throws IOException {
      this.printHeader(this.getText("doclet.Window_Class_Hierarchy", Standard.configuration().windowtitle));
      this.printTreeHeader();
      this.printPageHeading();
      this.printPackageTreeLinks();
      this.generateTree(super.classtree.baseclasses(), "doclet.Class_Hierarchy");
      this.generateTree(super.classtree.baseinterfaces(), "doclet.Interface_Hierarchy");
      this.printTreeFooter();
   }

   protected void printPackageTreeLinks() {
      if (!this.classesonly) {
         this.dl();
         this.dt();
         this.boldText("doclet.Package_Hierarchies");
         this.dd();

         for(int i = 0; i < this.packages.length; ++i) {
            String filename = this.pathString(this.packages[i], "package-tree.html");
            this.printHyperLink(filename, "", this.packages[i].name());
            if (i < this.packages.length - 1) {
               this.print(", ");
            }
         }

         this.dlEnd();
         this.hr();
      }
   }

   protected void printPageHeading() {
      this.center();
      this.h2();
      this.printText("doclet.Hierarchy_For_All_Packages");
      this.h2End();
      this.centerEnd();
   }

   protected void printTreeFooter() {
      this.hr();
      this.navLinks(false);
      this.printBottom();
      this.printBodyHtmlEnd();
   }

   protected void printTreeHeader() {
      this.navLinks(true);
      this.hr();
   }
}
