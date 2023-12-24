package xalanjdoc;

import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;

public class StylesheetWriter extends HtmlStandardWriter {
   public StylesheetWriter(String filename) throws IOException {
      super(filename);
   }

   public static void generate() throws DocletAbortException {
      String filename = "";

      try {
         if (Standard.configuration().stylesheetfile.length() == 0) {
            filename = "stylesheet.css";
            StylesheetWriter stylegen = new StylesheetWriter(filename);
            stylegen.generateStyleFile();
            stylegen.close();
         }
      } catch (IOException var3) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var3.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateStyleFile() {
      this.print("/* ");
      this.printText("doclet.Style_line_1");
      this.println(" */");
      this.println("");
      this.print("/* ");
      this.printText("doclet.Style_line_2");
      this.println(" */");
      this.println("");
      this.print("/* ");
      this.printText("doclet.Style_line_3");
      this.println(" */");
      this.println("body { background-color: #FFFFFF }");
      this.println("");
      this.print("/* ");
      this.printText("doclet.Style_line_4");
      this.println(" */");
      this.print("#TableHeadingColor     { background: #CCCCFF }");
      this.print(" /* ");
      this.printText("doclet.Style_line_5");
      this.println(" */");
      this.print("#TableSubHeadingColor  { background: #EEEEFF }");
      this.print(" /* ");
      this.printText("doclet.Style_line_6");
      this.println(" */");
      this.print("#TableRowColor         { background: #FFFFFF }");
      this.print(" /* ");
      this.printText("doclet.Style_line_7");
      this.println(" */");
      this.println("");
      this.print("/* ");
      this.printText("doclet.Style_line_8");
      this.println(" */");
      this.println("#FrameTitleFont   { font-size: normal; font-family: normal }");
      this.println("#FrameHeadingFont { font-size: normal; font-family: normal }");
      this.println("#FrameItemFont    { font-size: normal; font-family: normal }");
      this.println("");
      this.print("/* ");
      this.printText("doclet.Style_line_9");
      this.println(" */");
      this.print("/* ");
      this.print("#FrameItemFont  { font-size: 10pt; font-family: ");
      this.print("Helvetica, Arial, sans-serif }");
      this.println(" */");
      this.println("");
      this.print("/* ");
      this.printText("doclet.Style_line_10");
      this.println(" */");
      this.print("#NavBarCell1    { background-color:#EEEEFF;}");
      this.print("/* ");
      this.printText("doclet.Style_line_6");
      this.println(" */");
      this.print("#NavBarCell1Rev { background-color:#00008B;}");
      this.print("/* ");
      this.printText("doclet.Style_line_11");
      this.println(" */");
      this.print("#NavBarFont1    { font-family: Arial, Helvetica, sans-serif; ");
      this.println("color:#000000;}");
      this.print("#NavBarFont1Rev { font-family: Arial, Helvetica, sans-serif; ");
      this.println("color:#FFFFFF;}");
      this.println("");
      this.print("#NavBarCell2    { font-family: Arial, Helvetica, sans-serif; ");
      this.println("background-color:#FFFFFF;}");
      this.print("#NavBarCell3    { font-family: Arial, Helvetica, sans-serif; ");
      this.println("background-color:#FFFFFF;}");
      this.println("");
   }
}
