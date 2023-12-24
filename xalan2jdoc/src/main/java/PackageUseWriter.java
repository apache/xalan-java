package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.DirectoryManager;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class PackageUseWriter extends SubWriterHolderWriter {
   final PackageDoc pkgdoc;
   final SortedMap usingPackageToUsedClasses = new TreeMap();

   public PackageUseWriter(ClassUseMapper mapper, String filename, PackageDoc pkgdoc) throws IOException, DocletAbortException {
      super(DirectoryManager.getDirectoryPath(pkgdoc), filename, DirectoryManager.getRelativePath(pkgdoc.name()));
      this.pkgdoc = pkgdoc;
      ClassDoc[] content = pkgdoc.allClasses();

      for(int i = 0; i < content.length; ++i) {
         ClassDoc usedClass = content[i];
         Set usingClasses = (Set)mapper.classToClass.get(usedClass);
         if (usingClasses != null) {
            for(ClassDoc usingClass : usingClasses) {
               PackageDoc usingPackage = usingClass.containingPackage();
               Set usedClasses = (Set)this.usingPackageToUsedClasses.get(usingPackage);
               if (usedClasses == null) {
                  usedClasses = new TreeSet();
                  this.usingPackageToUsedClasses.put(usingPackage, usedClasses);
               }

               usedClasses.add(usedClass);
            }
         }
      }
   }

   public static void generate(ClassUseMapper mapper, PackageDoc pkgdoc) throws DocletAbortException {
      String filename = "package-use.html";

      try {
         PackageUseWriter pkgusegen = new PackageUseWriter(mapper, filename, pkgdoc);
         pkgusegen.generatePackageUseFile();
         pkgusegen.close();
      } catch (IOException var5) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var5.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateClassList() throws IOException {
      for(PackageDoc usingPackage : this.usingPackageToUsedClasses.keySet()) {
         this.anchor(usingPackage.name());
         this.tableIndexSummary();
         this.tableHeaderStart("#CCCCFF");
         this.printText("doclet.ClassUse_Classes.in.0.used.by.1", this.getPackageLink(this.pkgdoc), this.getPackageLink(usingPackage));
         Iterator itc = ((Collection)this.usingPackageToUsedClasses.get(usingPackage)).iterator();

         while(itc.hasNext()) {
            this.printClassRow((ClassDoc)itc.next(), usingPackage);
         }

         this.tableHeaderEnd();
         this.tableEnd();
         this.space();
         this.p();
      }
   }

   protected void generatePackageList() throws IOException {
      this.tableIndexSummary();
      this.tableHeaderStart("#CCCCFF");
      this.printText("doclet.ClassUse_Packages.that.use.0", this.getPackageLink(this.pkgdoc));
      this.tableHeaderEnd();

      for(PackageDoc pkg : this.usingPackageToUsedClasses.keySet()) {
         this.generatePackageUse(pkg);
      }

      this.tableEnd();
      this.space();
      this.p();
   }

   protected void generatePackageUse() throws IOException {
      if (Standard.configuration().packages.length > 1) {
         this.generatePackageList();
      }

      this.generateClassList();
   }

   protected void generatePackageUse(PackageDoc pkg) throws IOException {
      this.trBgcolorStyle("white", "TableRowColor");
      this.summaryRow(0);
      this.printHyperLink("", pkg.name(), pkg.name(), true);
      this.summaryRowEnd();
      this.summaryRow(0);
      this.printSummaryComment(pkg);
      this.space();
      this.summaryRowEnd();
      this.trEnd();
   }

   protected void generatePackageUseFile() throws IOException {
      this.printPackageUseHeader();
      if (this.usingPackageToUsedClasses.isEmpty()) {
         this.printText("doclet.ClassUse_No.usage.of.0", this.pkgdoc.name());
         this.p();
      } else {
         this.generatePackageUse();
      }

      this.printPackageUseFooter();
   }

   protected void navLinkClassUse() {
      this.navCellRevStart();
      this.fontStyle("NavBarFont1Rev");
      this.boldText("doclet.navClassUse");
      this.fontEnd();
      this.navCellEnd();
   }

   protected void navLinkPackage() {
      this.navCellStart();
      this.printHyperLink("package-summary.html", "", this.getText("doclet.Package"), true, "NavBarFont1");
      this.navCellEnd();
   }

   protected void navLinkTree() {
      this.navCellStart();
      this.printHyperLink("package-tree.html", "", this.getText("doclet.Tree"), true, "NavBarFont1");
      this.navCellEnd();
   }

   protected void printClassRow(ClassDoc usedClass, PackageDoc usingPackage) {
      String path = this.pathString(usedClass, "class-use/" + usedClass.name() + ".html");
      this.trBgcolorStyle("white", "TableRowColor");
      this.summaryRow(0);
      this.bold();
      this.printHyperLink(path, usingPackage.name(), usedClass.name(), true);
      this.boldEnd();
      this.println();
      this.br();
      this.printNbsps();
      this.printIndexComment(usedClass);
      this.summaryRowEnd();
      this.trEnd();
   }

   protected void printPackageUseFooter() {
      this.hr();
      this.navLinks(false);
      this.printBottom();
      this.printBodyHtmlEnd();
   }

   protected void printPackageUseHeader() {
      String packageLabel = this.getText("doclet.Package");
      String name = this.pkgdoc.name();
      this.printHeader(this.getText("doclet.Window_ClassUse_Header", Standard.configuration().windowtitle, packageLabel, name));
      this.navLinks(true);
      this.hr();
      this.center();
      this.h2();
      this.boldText("doclet.ClassUse_Title", packageLabel, name);
      this.h2End();
      this.centerEnd();
   }
}
