package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.ClassTree;
import com.sun.tools.doclets.DirectoryManager;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractTreeWriter extends HtmlStandardWriter {
   protected final ClassTree classtree;

   protected AbstractTreeWriter(String filename, ClassTree classtree) throws IOException, DocletAbortException {
      super(filename);
      this.classtree = classtree;
   }

   protected AbstractTreeWriter(String path, String filename, ClassTree classtree, PackageDoc pkg) throws IOException, DocletAbortException {
      super(path, filename, DirectoryManager.getRelativePath(pkg.name()));
      this.classtree = classtree;
   }

   protected void generateLevelInfo(ClassDoc parent, List list) {
      if (list.size() > 0) {
         this.ul();

         for(int i = 0; i < list.size(); ++i) {
            ClassDoc local = (ClassDoc)list.get(i);
            this.printPartialInfo(local);
            this.printExtendsImplements(parent, local);
            this.generateLevelInfo(local, this.classtree.subs(local));
         }

         this.ulEnd();
      }
   }

   protected void generateTree(List list, String heading) {
      if (list.size() > 0) {
         ClassDoc cd = (ClassDoc)list.get(0);
         this.printTreeHeading(heading);
         this.generateLevelInfo(cd.isClass() ? (ClassDoc)list.get(0) : null, list);
      }
   }

   protected void navLinkTree() {
      this.navCellRevStart();
      this.fontStyle("NavBarFont1Rev");
      this.boldText("doclet.Tree");
      this.fontEnd();
      this.navCellEnd();
   }

   protected void printExtendsImplements(ClassDoc parent, ClassDoc cd) {
      ClassDoc[] interfaces = cd.interfaces();
      if (interfaces.length > (cd.isInterface() ? 1 : 0)) {
         Arrays.sort((Object[])interfaces);
         if (cd.isInterface()) {
            this.print("(" + this.getText("doclet.also") + " extends ");
         } else {
            this.print(" (implements ");
         }

         boolean printcomma = false;

         for(int i = 0; i < interfaces.length; ++i) {
            if (parent != interfaces[i]) {
               if (printcomma) {
                  this.print(", ");
               }

               this.printPreQualifiedClassLink(interfaces[i]);
               printcomma = true;
            }
         }

         this.println(")");
      }
   }

   protected void printPartialInfo(ClassDoc cd) {
      boolean isInterface = cd.isInterface();
      this.li("circle");
      this.print(isInterface ? "interface " : "class ");
      this.printPreQualifiedBoldClassLink(cd);
   }

   protected void printTreeHeading(String heading) {
      this.h2();
      this.println(this.getText(heading));
      this.h2End();
   }
}
