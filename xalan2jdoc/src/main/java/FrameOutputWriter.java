package xalanjdoc;

import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;

public class FrameOutputWriter extends HtmlStandardWriter {
   int noOfPackages = Standard.configuration().packages.length;

   public FrameOutputWriter(String filename) throws IOException {
      super(filename);
   }

   public static void generate() throws DocletAbortException {
      String filename = "";

      try {
         filename = "index.html";
         FrameOutputWriter framegen = new FrameOutputWriter(filename);
         framegen.generateFrameFile();
         framegen.close();
      } catch (IOException var3) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var3.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateFrameFile() {
      if (Standard.configuration().windowtitle.length() > 0) {
         this.printPartialHeader(Standard.configuration().windowtitle);
      } else {
         this.printPartialHeader(this.getText("doclet.Generated_Docs_Untitled"));
      }

      this.printFrameDetails();
      this.printFrameWarning();
      this.printFrameFooter();
   }

   protected void printFrameDetails() {
      this.frameSet("cols=\"20%,80%\"");
      if (this.noOfPackages <= 1) {
         this.frame("src=\"allclasses-frame.html\" name=\"packageFrame\"");
         this.frame("src=\"" + Standard.configuration().topFile + "\" name=\"classFrame\"");
      } else if (this.noOfPackages > 1) {
         this.frameSet("rows=\"30%,70%\"");
         this.frame("src=\"overview-frame.html\" name=\"packageListFrame\"");
         this.frame("src=\"allclasses-frame.html\" name=\"packageFrame\"");
         this.frameSetEnd();
         this.frame("src=\"" + Standard.configuration().topFile + "\" name=\"classFrame\"");
      }

      this.frameSetEnd();
   }

   protected void printFrameWarning() {
      this.noFrames();
      this.h2();
      this.printText("doclet.Frame_Alert");
      this.h2End();
      this.p();
      this.printText("doclet.Frame_Warning_Message");
      this.br();
      this.printText("doclet.Link_To");
      this.printHyperLink(Standard.configuration().topFile, this.getText("doclet.Non_Frame_Version"));
      this.noFramesEnd();
   }
}
