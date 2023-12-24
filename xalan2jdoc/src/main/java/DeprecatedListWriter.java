package xalanjdoc;

import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;

public class DeprecatedListWriter extends SubWriterHolderWriter {
   public DeprecatedListWriter(String filename) throws IOException {
      super(filename);
   }

   public static void generate(RootDoc root) throws DocletAbortException {
      String filename = "deprecated-list.html";

      try {
         DeprecatedListWriter depr = new DeprecatedListWriter(filename);
         depr.generateDeprecatedListFile(new DeprecatedAPIListBuilder(root));
         depr.close();
      } catch (IOException var3) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var3.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateDeprecatedListFile(DeprecatedAPIListBuilder deprapi) throws IOException {
      ClassSubWriter classW = new ClassSubWriter(this);
      FieldSubWriter fieldW = new FieldSubWriter(this);
      MethodSubWriter methodW = new MethodSubWriter(this);
      ConstructorSubWriter consW = new ConstructorSubWriter(this);
      this.printDeprecatedHeader();
      classW.printDeprecatedAPI(deprapi.getDeprecatedClasses(), "doclet.Deprecated_Classes");
      classW.printDeprecatedAPI(deprapi.getDeprecatedInterfaces(), "doclet.Deprecated_Interfaces");
      classW.printDeprecatedAPI(deprapi.getDeprecatedExceptions(), "doclet.Deprecated_Exceptions");
      classW.printDeprecatedAPI(deprapi.getDeprecatedErrors(), "doclet.Deprecated_Errors");
      fieldW.printDeprecatedAPI(deprapi.getDeprecatedFields(), "doclet.Deprecated_Fields");
      methodW.printDeprecatedAPI(deprapi.getDeprecatedMethods(), "doclet.Deprecated_Methods");
      consW.printDeprecatedAPI(deprapi.getDeprecatedConstructors(), "doclet.Deprecated_Constructors");
      this.printDeprecatedFooter();
   }

   protected void navLinkDeprecated() {
      this.navCellRevStart();
      this.fontStyle("NavBarFont1Rev");
      this.boldText("doclet.navDeprecated");
      this.fontEnd();
      this.navCellEnd();
   }

   protected void printDeprecatedFooter() {
      this.hr();
      this.navLinks(false);
      this.printBottom();
      this.printBodyHtmlEnd();
   }

   protected void printDeprecatedHeader() {
      this.printHeader(this.getText("doclet.Window_Deprecated_List", Standard.configuration().windowtitle));
      this.navLinks(true);
      this.hr();
      this.center();
      this.h2();
      this.boldText("doclet.Deprecated_API");
      this.h2End();
      this.centerEnd();
   }
}
