package xalanjdoc;

import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;

public class HelpWriter extends HtmlStandardWriter {
   public HelpWriter(String filename) throws IOException {
      super(filename);
   }

   public static void generate() throws DocletAbortException {
      String filename = "";

      try {
         if (Standard.configuration().helpfile.length() == 0 && !Standard.configuration().nohelp) {
            filename = "help-doc.html";
            HelpWriter helpgen = new HelpWriter(filename);
            helpgen.generateHelpFile();
            helpgen.close();
         }
      } catch (IOException var3) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var3.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateHelpFile() {
      this.printHeader(this.getText("doclet.Window_Help_title", Standard.configuration().windowtitle));
      this.navLinks(true);
      this.hr();
      this.printHelpFileContents();
      this.navLinks(false);
      this.printBottom();
      this.printBodyHtmlEnd();
   }

   protected void navLinkHelp() {
      this.navCellRevStart();
      this.fontStyle("NavBarFont1Rev");
      this.boldText("doclet.Help");
      this.fontEnd();
      this.navCellEnd();
   }

   protected void printHelpFileContents() {
      this.center();
      this.h1();
      this.printText("doclet.Help_line_1");
      this.h1End();
      this.centerEnd();
      this.printText("doclet.Help_line_2");
      if (Standard.configuration().createoverview) {
         this.h3();
         this.printText("doclet.Overview");
         this.h3End();
         this.blockquote();
         this.p();
         this.printText("doclet.Help_line_3", this.getHyperLink("overview-summary.html", this.getText("doclet.Overview")));
         this.blockquoteEnd();
      }

      this.h3();
      this.printText("doclet.Package");
      this.h3End();
      this.blockquote();
      this.p();
      this.printText("doclet.Help_line_4");
      this.ul();
      this.li();
      this.printText("doclet.Interfaces_Italic");
      this.li();
      this.printText("doclet.Classes");
      this.li();
      this.printText("doclet.Exceptions");
      this.li();
      this.printText("doclet.Errors");
      this.ulEnd();
      this.blockquoteEnd();
      this.h3();
      this.printText("doclet.Help_line_5");
      this.h3End();
      this.blockquote();
      this.p();
      this.printText("doclet.Help_line_6");
      this.ul();
      this.li();
      this.printText("doclet.Help_line_7");
      this.li();
      this.printText("doclet.Help_line_8");
      this.li();
      this.printText("doclet.Help_line_9");
      this.li();
      this.printText("doclet.Help_line_10");
      this.li();
      this.printText("doclet.Help_line_11");
      this.li();
      this.printText("doclet.Help_line_12");
      this.p();
      this.li();
      this.printText("doclet.Inner_Class_Summary");
      this.li();
      this.printText("doclet.Field_Summary");
      this.li();
      this.printText("doclet.Constructor_Summary");
      this.li();
      this.printText("doclet.Method_Summary");
      this.p();
      this.li();
      this.printText("doclet.Field_Detail");
      this.li();
      this.printText("doclet.Constructor_Detail");
      this.li();
      this.printText("doclet.Method_Detail");
      this.ulEnd();
      this.printText("doclet.Help_line_13");
      this.blockquoteEnd();
      if (Standard.configuration().classuse) {
         this.h3();
         this.printText("doclet.Help_line_14");
         this.h3End();
         this.blockquote();
         this.printText("doclet.Help_line_15");
         this.blockquoteEnd();
      }

      if (Standard.configuration().createtree) {
         this.h3();
         this.printText("doclet.Help_line_16");
         this.h3End();
         this.blockquote();
         this.printText("doclet.Help_line_17_with_tree_link", this.getHyperLink("overview-tree.html", this.getText("doclet.Class_Hierarchy")));
         this.ul();
         this.li();
         this.printText("doclet.Help_line_18");
         this.li();
         this.printText("doclet.Help_line_19");
         this.ulEnd();
         this.blockquoteEnd();
      }

      if (!Standard.configuration().nodeprecatedlist && !Standard.configuration().nodeprecated) {
         this.h3();
         this.printText("doclet.Deprecated_API");
         this.h3End();
         this.blockquote();
         this.printText("doclet.Help_line_20_with_deprecated_api_link", this.getHyperLink("deprecated-list.html", this.getText("doclet.Deprecated_API")));
         this.blockquoteEnd();
      }

      if (Standard.configuration().createindex) {
         String indexlink;
         if (Standard.configuration().splitindex) {
            indexlink = this.getHyperLink("index-files/index-1.html", this.getText("doclet.Index"));
         } else {
            indexlink = this.getHyperLink("index-all.html", this.getText("doclet.Index"));
         }

         this.h3();
         this.printText("doclet.Help_line_21");
         this.h3End();
         this.blockquote();
         this.printText("doclet.Help_line_22", indexlink);
         this.blockquoteEnd();
      }

      this.h3();
      this.printText("doclet.Help_line_23");
      this.h3End();
      this.printText("doclet.Help_line_24");
      this.h3();
      this.printText("doclet.Help_line_25");
      this.h3End();
      this.printText("doclet.Help_line_26");
      this.p();
      this.h3();
      this.printText("doclet.Serialized_Form");
      this.h3End();
      this.printText("doclet.Help_line_27");
      this.p();
      this.font("-1");
      this.em();
      this.printText("doclet.Help_line_28");
      this.emEnd();
      this.fontEnd();
      this.br();
      this.hr();
   }
}
