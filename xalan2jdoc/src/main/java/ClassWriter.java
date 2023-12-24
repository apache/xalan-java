package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.ClassTree;
import com.sun.tools.doclets.DirectoryManager;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;
import java.util.List;

public class ClassWriter extends SubWriterHolderWriter {
   protected ClassDoc classdoc;
   protected ClassTree classtree;
   protected ClassDoc prev;
   protected ClassDoc next;
   protected boolean nopackage;
   protected MethodSubWriter methodSubWriter;
   protected ConstructorSubWriter constrSubWriter;
   protected FieldSubWriter fieldSubWriter;
   protected ClassSubWriter innerSubWriter;

   public ClassWriter(String path, String filename, ClassDoc classdoc, ClassDoc prev, ClassDoc next, ClassTree classtree, boolean nopackage) throws IOException, DocletAbortException {
      super(path, filename, DirectoryManager.getRelativePath(classdoc.containingPackage().name()));
      this.classdoc = classdoc;
      HtmlStandardWriter.currentcd = classdoc;
      this.classtree = classtree;
      this.prev = prev;
      this.next = next;
      this.nopackage = nopackage;
      this.methodSubWriter = new MethodSubWriter(this);
      this.constrSubWriter = new ConstructorSubWriter(this);
      this.fieldSubWriter = new FieldSubWriter(this);
      this.innerSubWriter = new ClassSubWriter(this);
   }

   public static void generate(ClassDoc classdoc, ClassDoc prev, ClassDoc next, ClassTree classtree, boolean nopackage) throws DocletAbortException {
      String path = DirectoryManager.getDirectoryPath(classdoc.containingPackage());
      String filename = classdoc.name() + ".html";

      try {
         ClassWriter clsgen = new ClassWriter(path, filename, classdoc, prev, next, classtree, nopackage);
         clsgen.generateClassFile();
         clsgen.close();
      } catch (IOException var9) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var9.toString(), filename);
         throw new DocletAbortException();
      }
   }

   public void generateClassFile() {
      String cltype = this.getText(this.classdoc.isInterface() ? "doclet.Interface" : "doclet.Class") + " ";
      PackageDoc pkg = this.classdoc.containingPackage();
      String pkgname = pkg != null ? pkg.name() : "";
      String clname = this.classdoc.name();
      String label = cltype + ' ' + clname;
      this.printHeader(this.getText("doclet.Window_ClassFile_label", Standard.configuration().windowtitle, label));
      this.navLinks(true);
      this.hr();
      this.println("<!-- ======== START OF CLASS DATA ======== -->");
      this.h2();
      if (pkgname.length() > 0) {
         this.font("-1");
         this.print(pkgname);
         this.fontEnd();
         this.br();
      }

      this.print(label);
      this.h2End();
      if (!this.classdoc.isInterface()) {
         this.pre();
         this.printTreeForClass(this.classdoc);
         this.preEnd();
      }

      this.printSubClassInterfaceInfo();
      if (this.classdoc.isInterface()) {
         this.printImplementingClasses();
      }

      this.hr();
      this.printDeprecated();
      this.printClassDescription();
      this.p();
      if (this.classdoc.inlineTags().length > 0) {
         this.printInlineComment(this.classdoc);
         this.p();
      }

      this.generateTagInfo(this.classdoc);
      this.hr();
      this.p();
      this.printAllMembers();
      this.println("<!-- ========= END OF CLASS DATA ========= -->");
      this.hr();
      this.navLinks(false);
      this.printBottom();
      this.printBodyHtmlEnd();
   }

   protected void navDetailLinks() {
      this.printText("doclet.Detail");
      this.print("&nbsp;");
      this.fieldSubWriter.navDetailLink(this.classdoc);
      this.navGap();
      this.constrSubWriter.navDetailLink(this.classdoc);
      this.navGap();
      this.methodSubWriter.navDetailLink(this.classdoc);
   }

   protected void navGap() {
      this.space();
      this.print('|');
      this.space();
   }

   protected void navLinkClass() {
      this.navCellRevStart();
      this.fontStyle("NavBarFont1Rev");
      this.boldText("doclet.Class");
      this.fontEnd();
      this.navCellEnd();
   }

   protected void navLinkClassUse() {
      this.navCellStart();
      this.printHyperLink("class-use/" + super.filename, "", this.getText("doclet.navClassUse"), true, "NavBarFont1");
      this.navCellEnd();
   }

   protected void navLinkNext() {
      if (this.next == null) {
         this.printText("doclet.Next_Class");
      } else {
         this.printClassLink(this.next, this.getText("doclet.Next_Class"), true);
      }
   }

   protected void navLinkPackage() {
      this.navCellStart();
      this.printHyperLink("package-summary.html", "", this.getText("doclet.Package"), true, "NavBarFont1");
      this.navCellEnd();
   }

   protected void navLinkPrevious() {
      if (this.prev == null) {
         this.printText("doclet.Prev_Class");
      } else {
         this.printClassLink(this.prev, this.getText("doclet.Prev_Class"), true);
      }
   }

   protected void navLinkTree() {
      this.navCellStart();
      if (this.nopackage) {
         this.printHyperLink(super.relativepath + "overview-tree.html", "", this.getText("doclet.Tree"), true, "NavBarFont1");
      } else {
         this.printHyperLink("package-tree.html", "", this.getText("doclet.Tree"), true, "NavBarFont1");
      }

      this.navCellEnd();
   }

   protected void navSummaryLinks() {
      this.printText("doclet.Summary");
      this.print("&nbsp;");
      this.innerSubWriter.navSummaryLink(this.classdoc);
      this.navGap();
      this.fieldSubWriter.navSummaryLink(this.classdoc);
      this.navGap();
      this.constrSubWriter.navSummaryLink(this.classdoc);
      this.navGap();
      this.methodSubWriter.navSummaryLink(this.classdoc);
   }

   protected void printAllMembers() {
      this.println("<!-- ======== INNER CLASS SUMMARY ======== -->");
      this.println();
      this.innerSubWriter.printMembersSummary(this.classdoc);
      this.innerSubWriter.printInheritedMembersSummary(this.classdoc);
      this.println();
      this.println("<!-- =========== FIELD SUMMARY =========== -->");
      this.println();
      this.fieldSubWriter.printMembersSummary(this.classdoc);
      this.fieldSubWriter.printInheritedMembersSummary(this.classdoc);
      this.println();
      this.println("<!-- ======== CONSTRUCTOR SUMMARY ======== -->");
      this.println();
      this.constrSubWriter.printMembersSummary(this.classdoc);
      this.println();
      this.println("<!-- ========== METHOD SUMMARY =========== -->");
      this.println();
      this.methodSubWriter.printMembersSummary(this.classdoc);
      this.methodSubWriter.printInheritedMembersSummary(this.classdoc);
      this.p();
      this.println();
      this.println("<!-- ============ FIELD DETAIL =========== -->");
      this.println();
      this.fieldSubWriter.printMembers(this.classdoc);
      this.println();
      this.println("<!-- ========= CONSTRUCTOR DETAIL ======== -->");
      this.println();
      this.constrSubWriter.printMembers(this.classdoc);
      this.println();
      this.println("<!-- ============ METHOD DETAIL ========== -->");
      this.println();
      this.methodSubWriter.printMembers(this.classdoc);
   }

   protected void printClassDescription() {
      boolean isInterface = this.classdoc.isInterface();
      this.dl();
      this.dt();
      this.print(this.classdoc.modifiers() + " ");
      if (!isInterface) {
         this.print("class ");
      }

      this.bold(this.classdoc.name());
      if (!isInterface) {
         ClassDoc superclass = this.classdoc.superclass();
         if (superclass != null) {
            this.dt();
            this.print("extends ");
            this.printClassLink(superclass);
         }
      }

      ClassDoc[] implIntfacs = this.classdoc.interfaces();
      if (implIntfacs != null && implIntfacs.length > 0) {
         this.dt();
         this.print(isInterface ? "extends " : "implements ");
         this.printClassLink(implIntfacs[0]);

         for(int i = 1; i < implIntfacs.length; ++i) {
            this.print(", ");
            this.printClassLink(implIntfacs[i]);
         }
      }

      this.dlEnd();
   }

   protected void printDeprecated() {
      Tag[] deprs = this.classdoc.tags("deprecated");
      if (deprs.length > 0) {
         Tag[] commentTags = deprs[0].inlineTags();
         if (commentTags.length > 0) {
            this.boldText("doclet.Deprecated");
            this.space();
            this.printInlineDeprecatedComment(deprs[0]);
         }

         this.p();
      }
   }

   protected void printImplementingClasses() {
      if (!this.classdoc.qualifiedName().equals("java.lang.Cloneable") && !this.classdoc.qualifiedName().equals("java.io.Serializable")) {
         List implcl = this.classtree.implementingclasses(this.classdoc);
         if (implcl.size() > 0) {
            this.printSubClassInfoHeader(implcl);
            this.boldText("doclet.Implementing_Classes");
            this.printSubClassLinkInfo(implcl);
         }
      }
   }

   protected void printStep(int indent) {
      String spc = this.spaces(6 * indent - 4);
      this.print(spc);
      this.println("|");
      this.print(spc);
      this.print("+--");
   }

   protected void printSubClassInfoHeader(List list) {
      this.dl();
      this.dt();
   }

   protected void printSubClassInterfaceInfo() {
      if (!this.classdoc.qualifiedName().equals("java.lang.Object") && !this.classdoc.qualifiedName().equals("org.omg.CORBA.Object")) {
         List subclasses = this.classdoc.isClass() ? this.classtree.subs(this.classdoc) : this.classtree.allSubs(this.classdoc);
         if (subclasses.size() > 0) {
            this.printSubClassInfoHeader(subclasses);
            if (this.classdoc.isClass()) {
               this.boldText("doclet.Subclasses");
            } else {
               this.boldText("doclet.Subinterfaces");
            }

            this.printSubClassLinkInfo(subclasses);
         }
      }
   }

   protected void printSubClassLinkInfo(List list) {
      int i = 0;
      this.print(' ');
      this.dd();

      while(i < list.size() - 1) {
         this.printClassLink((ClassDoc)list.get(i));
         this.print(", ");
         ++i;
      }

      this.printClassLink((ClassDoc)list.get(i));
      this.ddEnd();
      this.dlEnd();
   }

   protected void printSummaryDetailLinks() {
      this.tr();
      this.tdVAlignClass("top", "NavBarCell3");
      this.font("-2");
      this.print("  ");
      this.navSummaryLinks();
      this.fontEnd();
      this.tdEnd();
      this.tdVAlignClass("top", "NavBarCell3");
      this.font("-2");
      this.navDetailLinks();
      this.fontEnd();
      this.tdEnd();
      this.trEnd();
   }

   protected int printTreeForClass(ClassDoc cd) {
      ClassDoc sup = cd.superclass();
      int indent = 0;
      if (sup != null) {
         indent = this.printTreeForClass(sup);
         this.printStep(indent);
      }

      if (cd.equals(this.classdoc)) {
         this.bold(cd.qualifiedName());
      } else {
         this.printQualifiedClassLink(cd);
      }

      this.println();
      return indent + 1;
   }
}
