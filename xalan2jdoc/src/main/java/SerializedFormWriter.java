package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;
import java.util.Arrays;

public class SerializedFormWriter extends SubWriterHolderWriter {
   public SerializedFormWriter(String filename) throws IOException, DocletAbortException {
      super(filename);
   }

   public static void generate(RootDoc root) throws DocletAbortException {
      String filename = "serialized-form.html";

      try {
         SerializedFormWriter serialgen = new SerializedFormWriter(filename);
         serialgen.generateSerializedFormFile(root);
         serialgen.close();
      } catch (IOException var4) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var4.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateContents(RootDoc root) {
      PackageDoc[] packages = Standard.configuration().packages;
      ClassDoc[] cmdlineClasses = root.specifiedClasses();
      boolean first = true;

      for(int i = 0; i < packages.length; ++i) {
         ClassDoc[] classes = packages[i].allClasses();
         boolean printPackageName = true;
         Arrays.sort((Object[])classes);

         for(int j = 0; j < classes.length; ++j) {
            ClassDoc classdoc = classes[j];
            if (classdoc.isClass() && classdoc.isSerializable()) {
               if (printPackageName) {
                  this.hr(4, "noshade");
                  this.printPackageName(packages[i].name());
                  printPackageName = false;
               }

               first = false;
               this.printSerialMemberInfo(classdoc);
            }
         }
      }

      if (cmdlineClasses.length > 0) {
         Arrays.sort((Object[])cmdlineClasses);

         for(int i = 0; i < cmdlineClasses.length; ++i) {
            ClassDoc classdoc = cmdlineClasses[i];
            if (classdoc.isClass() && classdoc.isSerializable()) {
               if (!first) {
                  this.hr(4, "noshade");
               }

               first = false;
               this.printSerialMemberInfo(classdoc);
            }
         }
      }
   }

   public void generateSerializedFormFile(RootDoc root) {
      this.printHeader(this.getText("doclet.Serialized_Form"));
      this.navLinks(true);
      this.hr();
      this.center();
      this.h1();
      this.printText("doclet.Serialized_Form");
      this.h1End();
      this.centerEnd();
      this.generateContents(root);
      this.hr();
      this.navLinks(false);
      this.printBottom();
      this.printBodyHtmlEnd();
   }

   protected void printClassName(String classstr) {
      this.tableHeader();
      this.tdColspan(2);
      this.font("+2");
      this.bold(classstr);
      this.tableFooter();
   }

   protected void printMembers(ClassDoc cd) {
      new SerialMethodSubWriter(this).printMembers(cd);
      new SerialFieldSubWriter(this).printMembers(cd);
   }

   protected void printPackageName(String pkgname) {
      this.tableHeader();
      this.tdAlign("center");
      this.font("+2");
      this.boldText("doclet.Package");
      this.print(' ');
      this.bold(pkgname);
      this.tableFooter();
   }

   protected void printSerialMemberInfo(ClassDoc cd) {
      String classlink = this.getQualifiedClassLink(cd);
      this.anchor(cd.qualifiedName());
      this.printClassName(this.getText("doclet.Class_0_implements_serializable", classlink));
      this.printMembers(cd);
      this.p();
   }

   protected void tableFooter() {
      this.fontEnd();
      this.tdEnd();
      this.trEnd();
      this.tableEnd();
      this.p();
   }

   protected void tableHeader() {
      this.tableIndexSummary();
      this.trBgcolorStyle("#CCCCFF", "TableSubHeadingColor");
   }
}
