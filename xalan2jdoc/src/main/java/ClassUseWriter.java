package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.tools.doclets.DirectoryManager;
import com.sun.tools.doclets.DocletAbortException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class ClassUseWriter extends SubWriterHolderWriter {
   final ClassDoc classdoc;
   final Map pkgToSubclass;
   final Map pkgToSubinterface;
   final Map pkgToImplementingClass;
   final Map pkgToField;
   final Map pkgToMethodReturn;
   final Map pkgToMethodArgs;
   final Map pkgToMethodThrows;
   final Map pkgToConstructorArgs;
   final Map pkgToConstructorThrows;
   final SortedSet pkgSet;
   final MethodSubWriter methodSubWriter;
   final ConstructorSubWriter constrSubWriter;
   final FieldSubWriter fieldSubWriter;
   final ClassSubWriter classSubWriter;

   public ClassUseWriter(ClassUseMapper mapper, String path, String filename, String relpath, ClassDoc classdoc) throws IOException, DocletAbortException {
      super(path, filename, relpath);
      this.classdoc = classdoc;
      HtmlStandardWriter.currentcd = classdoc;
      this.pkgSet = new TreeSet();
      this.pkgToSubclass = this.pkgDivide(mapper.classToSubclass);
      this.pkgToSubinterface = this.pkgDivide(mapper.classToSubinterface);
      this.pkgToImplementingClass = this.pkgDivide(mapper.classToImplementingClass);
      this.pkgToField = this.pkgDivide(mapper.classToField);
      this.pkgToMethodReturn = this.pkgDivide(mapper.classToMethodReturn);
      this.pkgToMethodArgs = this.pkgDivide(mapper.classToMethodArgs);
      this.pkgToMethodThrows = this.pkgDivide(mapper.classToMethodThrows);
      this.pkgToConstructorArgs = this.pkgDivide(mapper.classToConstructorArgs);
      this.pkgToConstructorThrows = this.pkgDivide(mapper.classToConstructorThrows);
      if (!this.pkgSet.equals(mapper.classToPackage.get(classdoc))) {
         System.err.println("Internal error: package sets don't match: " + this.pkgSet + " with: " + mapper.classToPackage.get(classdoc));
      }

      this.methodSubWriter = new MethodSubWriter(this);
      this.constrSubWriter = new ConstructorSubWriter(this);
      this.fieldSubWriter = new FieldSubWriter(this);
      this.classSubWriter = new ClassSubWriter(this);
   }

   public static void generate(ClassUseMapper mapper, ClassDoc classdoc) throws DocletAbortException {
      String path = DirectoryManager.getDirectoryPath(classdoc.containingPackage());
      if (path.length() > 0) {
         path = path + File.separator;
      }

      path = path + "class-use";
      String filename = classdoc.name() + ".html";
      String pkgname = classdoc.containingPackage().name();
      pkgname = pkgname + (pkgname.length() > 0 ? ".class-use" : "class-use");
      String relpath = DirectoryManager.getRelativePath(pkgname);

      try {
         ClassUseWriter clsgen = new ClassUseWriter(mapper, path, filename, relpath, classdoc);
         clsgen.generateClassUseFile();
         clsgen.close();
      } catch (IOException var8) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var8.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateClassList() throws IOException {
      for(PackageDoc pkg : this.pkgSet) {
         this.anchor(pkg.name());
         this.tableIndexSummary();
         this.tableHeaderStart("#CCCCFF");
         this.printText("doclet.ClassUse_Uses.of.0.in.1", this.getClassLink(this.classdoc), this.getPackageLink(pkg));
         this.tableHeaderEnd();
         this.tableEnd();
         this.space();
         this.p();
         this.generateClassUse(pkg);
      }
   }

   protected void generateClassUse() throws IOException {
      if (Standard.configuration().packages.length > 1) {
         this.generatePackageList();
      }

      this.generateClassList();
   }

   protected void generateClassUse(PackageDoc pkg) throws IOException {
      String classLink = this.getClassLink(this.classdoc);
      String pkgLink = this.getPackageLink(pkg);
      this.classSubWriter.printUseInfo(this.pkgToSubclass.get(pkg), this.getText("doclet.ClassUse_Subclass", classLink, pkgLink));
      this.classSubWriter.printUseInfo(this.pkgToSubinterface.get(pkg), this.getText("doclet.ClassUse_Subinterface", classLink, pkgLink));
      this.classSubWriter.printUseInfo(this.pkgToImplementingClass.get(pkg), this.getText("doclet.ClassUse_ImplementingClass", classLink, pkgLink));
      this.fieldSubWriter.printUseInfo(this.pkgToField.get(pkg), this.getText("doclet.ClassUse_Field", classLink, pkgLink));
      this.methodSubWriter.printUseInfo(this.pkgToMethodReturn.get(pkg), this.getText("doclet.ClassUse_MethodReturn", classLink, pkgLink));
      this.methodSubWriter.printUseInfo(this.pkgToMethodArgs.get(pkg), this.getText("doclet.ClassUse_MethodArgs", classLink, pkgLink));
      this.methodSubWriter.printUseInfo(this.pkgToMethodThrows.get(pkg), this.getText("doclet.ClassUse_MethodThrows", classLink, pkgLink));
      this.constrSubWriter.printUseInfo(this.pkgToConstructorArgs.get(pkg), this.getText("doclet.ClassUse_ConstructorArgs", classLink, pkgLink));
      this.constrSubWriter.printUseInfo(this.pkgToConstructorThrows.get(pkg), this.getText("doclet.ClassUse_ConstructorThrows", classLink, pkgLink));
   }

   protected void generateClassUseFile() throws IOException {
      this.printClassUseHeader();
      if (this.pkgSet.size() > 0) {
         this.generateClassUse();
      } else {
         this.printText("doclet.ClassUse_No.usage.of.0", this.classdoc.qualifiedName());
         this.p();
      }

      this.printClassUseFooter();
   }

   protected void generatePackageList() throws IOException {
      this.tableIndexSummary();
      this.tableHeaderStart("#CCCCFF");
      this.printText("doclet.ClassUse_Packages.that.use.0", this.getClassLink(this.classdoc));
      this.tableHeaderEnd();

      for(PackageDoc pkg : this.pkgSet) {
         this.generatePackageUse(pkg);
      }

      this.tableEnd();
      this.space();
      this.p();
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

   protected void navLinkClass() {
      this.navCellStart();
      this.printClassLink(this.classdoc, "", this.getText("doclet.Class"), true, "NavBarFont1");
      this.navCellEnd();
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
      this.printHyperLink("../package-summary.html", "", this.getText("doclet.Package"), true, "NavBarFont1");
      this.navCellEnd();
   }

   protected void navLinkTree() {
      this.navCellStart();
      if (this.classdoc.containingPackage().isIncluded()) {
         this.printHyperLink("../package-tree.html", "", this.getText("doclet.Tree"), true, "NavBarFont1");
      } else {
         this.printHyperLink(super.relativepath + "overview-tree.html", "", this.getText("doclet.Tree"), true, "NavBarFont1");
      }

      this.navCellEnd();
   }

   private Map pkgDivide(Map classMap) {
      Map map = new HashMap();
      List list = (List)classMap.get(this.classdoc);
      if (list != null) {
         for(ProgramElementDoc doc : list) {
            PackageDoc pkg = doc.containingPackage();
            this.pkgSet.add(pkg);
            List inPkg = (List)map.get(pkg);
            if (inPkg == null) {
               inPkg = new ArrayList();
               map.put(pkg, inPkg);
            }

            inPkg.add(doc);
         }
      }

      return map;
   }

   protected void printClassUseFooter() {
      this.hr();
      this.navLinks(false);
      this.printBottom();
      this.printBodyHtmlEnd();
   }

   protected void printClassUseHeader() {
      String cltype = this.getText(this.classdoc.isInterface() ? "doclet.Interface" : "doclet.Class");
      String clname = this.classdoc.qualifiedName();
      this.printHeader(this.getText("doclet.Window_ClassUse_Header", Standard.configuration().windowtitle, cltype, clname));
      this.navLinks(true);
      this.hr();
      this.center();
      this.h2();
      this.boldText("doclet.ClassUse_Title", cltype, clname);
      this.h2End();
      this.centerEnd();
   }
}
