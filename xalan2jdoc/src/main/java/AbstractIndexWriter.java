package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.IndexBuilder;
import java.io.IOException;
import java.util.List;

public class AbstractIndexWriter extends HtmlStandardWriter {
   protected IndexBuilder indexbuilder;

   protected AbstractIndexWriter(String filename, IndexBuilder indexbuilder) throws IOException {
      super(filename);
      this.indexbuilder = indexbuilder;
   }

   protected AbstractIndexWriter(String path, String filename, String relpath, IndexBuilder indexbuilder) throws IOException {
      super(path, filename, relpath);
      this.indexbuilder = indexbuilder;
   }

   protected void generateContents(Character unicode, List memberlist) {
      this.anchor("_" + unicode + "_");
      this.h2();
      this.bold(unicode.toString());
      this.h2End();
      this.dl();

      for(int i = 0; i < memberlist.size(); ++i) {
         Doc element = (Doc)memberlist.get(i);
         if (element instanceof MemberDoc) {
            this.printDescription((MemberDoc)element);
         } else if (element instanceof ClassDoc) {
            this.printDescription((ClassDoc)element);
         } else if (element instanceof PackageDoc) {
            this.printDescription((PackageDoc)element);
         }
      }

      this.dlEnd();
      this.hr();
   }

   protected void navLinkIndex() {
      this.navCellRevStart();
      this.fontStyle("NavBarFont1Rev");
      this.boldText("doclet.Index");
      this.fontEnd();
      this.navCellEnd();
   }

   protected void printClassInfo(ClassDoc cd) {
      if (cd.isOrdinaryClass()) {
         this.print("class ");
      } else if (cd.isInterface()) {
         this.print("interface ");
      } else if (cd.isException()) {
         this.print("exception ");
      } else {
         this.print("error ");
      }

      this.printPreQualifiedClassLink(cd);
      this.print('.');
   }

   protected void printComment(ProgramElementDoc element) {
      Tag[] tags;
      if ((tags = element.tags("deprecated")).length > 0) {
         this.boldText("doclet.Deprecated");
         this.space();
         this.printInlineDeprecatedComment(tags[0]);
      } else {
         for(ClassDoc cont = element.containingClass(); cont != null; cont = cont.containingClass()) {
            if (cont.tags("deprecated").length > 0) {
               this.boldText("doclet.Deprecated");
               this.space();
               break;
            }
         }

         this.printSummaryComment(element);
      }
   }

   protected void printDescription(ClassDoc cd) {
      this.dt();
      this.printClassLink(cd, true);
      this.print(" - ");
      this.printClassInfo(cd);
      this.dd();
      this.printComment(cd);
   }

   protected void printDescription(MemberDoc element) {
      String name = element instanceof ExecutableMemberDoc ? element.name() + ((ExecutableMemberDoc)element).flatSignature() : element.name();
      ClassDoc containing = element.containingClass();
      String qualname = containing.qualifiedName();
      String baseClassName = containing.name();
      this.dt();
      this.printDocLink(element, name, true);
      this.println(" - ");
      this.printMemberDesc(element);
      this.println();
      this.dd();
      this.printComment(element);
      this.println();
   }

   protected void printDescription(PackageDoc pd) {
      this.dt();
      this.printPackageLink(pd);
      this.print(" - ");
      this.print("package " + pd.name());
      this.dd();
      this.printSummaryComment(pd);
   }

   protected void printMemberDesc(MemberDoc member) {
      ClassDoc containing = member.containingClass();
      String classdesc = (containing.isInterface() ? "interface " : "class ") + this.getPreQualifiedClassLink(containing);
      if (member.isField()) {
         if (member.isStatic()) {
            this.printText("doclet.Static_variable_in", classdesc);
         } else {
            this.printText("doclet.Variable_in", classdesc);
         }
      } else if (member.isConstructor()) {
         this.printText("doclet.Constructor_for", classdesc);
      } else if (member.isMethod()) {
         if (member.isStatic()) {
            this.printText("doclet.Static_method_in", classdesc);
         } else {
            this.printText("doclet.Method_in", classdesc);
         }
      }
   }
}
