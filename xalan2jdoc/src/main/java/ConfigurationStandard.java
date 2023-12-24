package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.Configuration;
import com.sun.tools.doclets.DirectoryManager;
import com.sun.tools.doclets.MessageRetriever;
import java.io.File;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ConfigurationStandard extends Configuration {
   public String header = "";
   public String footer = "";
   public String doctitle = "";
   public String windowtitle = "";
   public String bottom = "";
   public String helpfile = "";
   public String stylesheetfile = "";
   public boolean nohelp = false;
   public boolean splitindex = false;
   public boolean createindex = true;
   public boolean classuse = false;
   public boolean createtree = true;
   public boolean nodeprecatedlist = false;
   public boolean nonavbar = false;
   private boolean nooverview = false;
   public boolean overview = false;
   public boolean createoverview = false;
   public static MessageRetriever standardmessage = null;
   public String topFile = "";

   public ConfigurationStandard() {
      if (standardmessage == null) {
         ResourceBundle rb = ResourceBundle.getBundle("xalanjdoc.resources.standard");
         standardmessage = new MessageRetriever(rb);
      }
   }

   protected void setCreateOverview() {
      if ((this.overview || super.packages.length > 1) && !this.nooverview) {
         this.createoverview = true;
      }
   }

   public void setSpecificDocletOptions(RootDoc root) {
      String[][] options = root.options();

      for(int oi = 0; oi < options.length; ++oi) {
         String[] os = options[oi];
         String opt = os[0].toLowerCase();
         if (opt.equals("-footer")) {
            this.footer = os[1];
         } else if (opt.equals("-header")) {
            this.header = os[1];
         } else if (opt.equals("-doctitle")) {
            this.doctitle = os[1];
         } else if (opt.equals("-windowtitle")) {
            this.windowtitle = os[1];
         } else if (opt.equals("-bottom")) {
            this.bottom = os[1];
         } else if (opt.equals("-helpfile")) {
            this.helpfile = os[1];
         } else if (opt.equals("-stylesheetfile")) {
            this.stylesheetfile = os[1];
         } else if (opt.equals("-nohelp")) {
            this.nohelp = true;
         } else if (opt.equals("-splitindex")) {
            this.splitindex = true;
         } else if (opt.equals("-noindex")) {
            this.createindex = false;
         } else if (opt.equals("-use")) {
            this.classuse = true;
         } else if (opt.equals("-notree")) {
            this.createtree = false;
         } else if (opt.equals("-nodeprecatedlist")) {
            this.nodeprecatedlist = true;
         } else if (opt.equals("-nonavbar")) {
            this.nonavbar = true;
         } else if (opt.equals("-nooverview")) {
            this.nooverview = true;
         } else if (opt.equals("-overview")) {
            this.overview = true;
         }
      }

      this.setCreateOverview();
      this.setTopFile(root);
   }

   protected void setTopFile(RootDoc root) {
      if (this.createoverview) {
         this.topFile = "overview-summary.html";
      } else if (super.packages.length == 0) {
         if (root.classes().length > 0) {
            ClassDoc[] classarr = root.classes();
            Arrays.sort((Object[])classarr);
            this.topFile = DirectoryManager.getPathToClass(classarr[0]);
         }
      } else {
         this.topFile = DirectoryManager.getPathToPackage(super.packages[0], "package-summary.html");
      }
   }

   public int specificDocletOptionLength(String option) {
      if (option.equals("-nodeprecatedlist")
         || option.equals("-noindex")
         || option.equals("-notree")
         || option.equals("-nohelp")
         || option.equals("-splitindex")
         || option.equals("-use")
         || option.equals("-nonavbar")
         || option.equals("-nooverview")) {
         return 1;
      } else if (option.equals("-help")) {
         standardmessage.notice("doclet.usage");
         return 1;
      } else if (option.equals("-x")) {
         standardmessage.notice("doclet.xusage");
         return -1;
      } else if (option.equals("-footer")
         || option.equals("-header")
         || option.equals("-doctitle")
         || option.equals("-windowtitle")
         || option.equals("-bottom")
         || option.equals("-helpfile")
         || option.equals("-stylesheetfile")
         || option.equals("-link")
         || option.equals("-overview")) {
         return 2;
      } else {
         return !option.equals("-group") && !option.equals("-linkoffline") ? 0 : 3;
      }
   }

   public boolean specificDocletValidOptions(String[][] options, DocErrorReporter reporter) {
      boolean helpfile = false;
      boolean nohelp = false;
      boolean overview = false;
      boolean nooverview = false;
      boolean splitindex = false;
      boolean noindex = false;

      for(int oi = 0; oi < options.length; ++oi) {
         String[] os = options[oi];
         String opt = os[0].toLowerCase();
         if (opt.equals("-helpfile")) {
            if (nohelp) {
               reporter.printError(standardmessage.getText("doclet.Option_conflict", "-helpfile", "-nohelp"));
               return false;
            }

            if (helpfile) {
               reporter.printError(standardmessage.getText("doclet.Option_reuse", "-helpfile"));
               return false;
            }

            File help = new File(os[1]);
            if (!help.exists()) {
               reporter.printError(standardmessage.getText("doclet.File_not_found", os[1]));
               return false;
            }

            helpfile = true;
         } else if (opt.equals("-nohelp")) {
            if (helpfile) {
               reporter.printError(standardmessage.getText("doclet.Option_conflict", "-nohelp", "-helpfile"));
               return false;
            }

            nohelp = true;
         } else if (opt.equals("-overview")) {
            if (nooverview) {
               reporter.printError(standardmessage.getText("doclet.Option_conflict", "-overview", "-nooverview"));
               return false;
            }

            if (overview) {
               reporter.printError(standardmessage.getText("doclet.Option_reuse", "-overview"));
               return false;
            }

            overview = true;
         } else if (opt.equals("-nooverview")) {
            if (overview) {
               reporter.printError(standardmessage.getText("doclet.Option_conflict", "-nooverview", "-overview"));
               return false;
            }

            nooverview = true;
         } else if (opt.equals("-splitindex")) {
            if (noindex) {
               reporter.printError(standardmessage.getText("doclet.Option_conflict", "-splitindex", "-noindex"));
               return false;
            }

            splitindex = true;
         } else if (opt.equals("-noindex")) {
            if (splitindex) {
               reporter.printError(standardmessage.getText("doclet.Option_conflict", "-noindex", "-splitindex"));
               return false;
            }

            noindex = true;
         } else if (opt.equals("-group")) {
            if (!Group.checkPackageGroups(os[1], os[2], reporter)) {
               return false;
            }
         } else if (opt.equals("-link")) {
            String url = os[1];
            if (!Extern.url(url, url, reporter)) {
               return false;
            }
         } else if (opt.equals("-linkoffline")) {
            String url = os[1];
            String pkglisturl = os[2];
            if (!Extern.url(url, pkglisturl, reporter)) {
               return false;
            }
         }
      }

      return true;
   }
}
