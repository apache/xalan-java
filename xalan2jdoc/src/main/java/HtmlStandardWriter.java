package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.DirectoryManager;
import com.sun.tools.doclets.HtmlDocWriter;
import java.io.File;
import java.io.IOException;

public class HtmlStandardWriter extends HtmlDocWriter {
   public static final String destdir = Standard.configuration().destdirname;
   public String relativepath = "";
   public String path = "";
   public String filename = "";
   public String backpath = DirectoryManager.getBackPath(destdir);
   public int displayLength = 0;
   public static ClassDoc currentcd = null;

   public HtmlStandardWriter(String filename) throws IOException {
      super(filename);
      this.filename = filename;
   }

   public HtmlStandardWriter(String path, String filename, String relativepath) throws IOException {
      super(path, filename);
      this.path = path;
      this.relativepath = relativepath;
      this.filename = filename;
   }

   public void boldText(String key) {
      this.bold(this.getText(key));
   }

   public void boldText(String key, String a1) {
      this.bold(this.getText(key, a1));
   }

   public void boldText(String key, String a1, String a2) {
      this.bold(this.getText(key, a1, a2));
   }

   public void error(String key, String a1) {
      Standard.configuration();
      ConfigurationStandard.standardmessage.notice(key, a1);
   }

   public void error(String key, String a1, String a2) {
      Standard.configuration();
      ConfigurationStandard.standardmessage.notice(key, a1, a2);
   }

   public void frame(String arg) {
      this.println("<FRAME " + arg + ">");
   }

   public void frameEnd() {
      this.println("</FRAME>");
   }

   public void frameSet(String arg) {
      this.println("<FRAMESET " + arg + ">");
   }

   public void frameSetEnd() {
      this.println("</FRAMESET>");
   }

   public void generateTagInfo(Doc doc) {
      Tag[] sinces = doc.tags("since");
      Tag[] sees = doc.seeTags();
      Tag[] usages = doc.tags("xsl.usage");
      Tag[] authors;
      if (HtmlDocWriter.configuration.showauthor) {
         authors = doc.tags("author");
      } else {
         authors = new Tag[0];
      }

      Tag[] versions;
      if (HtmlDocWriter.configuration.showversion) {
         versions = doc.tags("version");
      } else {
         versions = new Tag[0];
      }

      if (sinces.length > 0
         || sees.length > 0
         || authors.length > 0
         || versions.length > 0
         || usages.length > 0
         || doc.isClass() && ((ClassDoc)doc).isSerializable()) {
         this.dl();
         this.printSinceTag(doc);
         if (versions.length > 0) {
            this.dt();
            this.boldText("doclet.Version");
            this.dd();
            this.printInlineComment(versions[0]);
            this.ddEnd();
         }

         if (authors.length > 0) {
            this.dt();
            this.boldText("doclet.Author");
            this.dd();

            for(int i = 0; i < authors.length; ++i) {
               if (i > 0) {
                  this.print(", ");
               }

               this.printInlineComment(authors[i]);
            }

            this.ddEnd();
         }

         this.printSeeTags(doc);
         this.printUsageTags(doc);
         this.dlEnd();
      }
   }

   public String getClassLink(ClassDoc cd) {
      return this.getClassLink(cd, false);
   }

   public String getClassLink(ClassDoc cd, String label) {
      return this.getClassLink(cd, "", label, false);
   }

   public String getClassLink(ClassDoc cd, String where, String label) {
      return this.getClassLink(cd, where, label, false);
   }

   public String getClassLink(ClassDoc cd, String where, String label, boolean bold) {
      return this.getClassLink(cd, where, label, bold, "");
   }

   public String getClassLink(ClassDoc cd, String where, String label, boolean bold, String color) {
      boolean nameUnspecified = label.length() == 0;
      if (nameUnspecified) {
         label = cd.name();
      }

      this.displayLength += label.length();
      if (cd.isIncluded()) {
         String filename = this.pathToClass(cd);
         return this.getHyperLink(filename, where, label, bold, color);
      } else {
         String crosslink = this.getCrossClassLink(cd);
         if (crosslink != null) {
            return this.getHyperLink(crosslink, where, label, bold, color);
         } else {
            if (nameUnspecified) {
               this.displayLength -= label.length();
               label = cd.qualifiedName();
               this.displayLength += label.length();
            }

            return label;
         }
      }
   }

   public String getClassLink(ClassDoc cd, boolean bold) {
      return this.getClassLink(cd, "", "", bold);
   }

   public String getCrossClassLink(ClassDoc cd) {
      return this.getCrossLink(cd.containingPackage().name(), cd.name() + ".html");
   }

   public String getCrossLink(String packagename, String link) {
      Extern fnd = Extern.findPackage(packagename);
      if (fnd != null) {
         String externlink = fnd.path + link;
         return fnd.relative ? this.relativepath + externlink : externlink;
      } else {
         return null;
      }
   }

   public String getCrossPackageLink(String packagename) {
      return this.getCrossLink(packagename, "package-summary.html");
   }

   public String getDocLink(Doc doc, String label) {
      return this.getDocLink(doc, label, false);
   }

   public String getDocLink(Doc doc, String label, boolean bold) {
      if (doc instanceof PackageDoc) {
         return this.getPackageLink((PackageDoc)doc, label);
      } else if (doc instanceof ClassDoc) {
         return this.getClassLink((ClassDoc)doc, "", label, bold);
      } else if (doc instanceof ExecutableMemberDoc) {
         ExecutableMemberDoc emd = (ExecutableMemberDoc)doc;
         return this.getClassLink(emd.containingClass(), emd.name() + emd.signature(), label, bold);
      } else if (doc instanceof MemberDoc) {
         MemberDoc md = (MemberDoc)doc;
         return this.getClassLink(md.containingClass(), md.name(), label, bold);
      } else {
         return doc instanceof RootDoc ? this.getHyperLink("overview-summary.html", label) : label;
      }
   }

   public String getPackageLink(PackageDoc pkg) {
      return this.getPackageLink(pkg, pkg.name());
   }

   public String getPackageLink(PackageDoc pkg, String linklabel) {
      if (pkg.isIncluded()) {
         return this.getHyperLink(this.pathString(pkg, "package-summary.html"), linklabel);
      } else {
         String crossPkgLink = this.getCrossPackageLink(pkg.name());
         return crossPkgLink != null ? this.getHyperLink(crossPkgLink, linklabel) : linklabel;
      }
   }

   public String getPreQualifiedClassLink(ClassDoc cd) {
      return this.getPreQualifiedClassLink(cd, false);
   }

   public String getPreQualifiedClassLink(ClassDoc cd, boolean bold) {
      String classlink = this.getPkgName(cd);
      return classlink + this.getClassLink(cd, "", cd.name(), bold);
   }

   public String getQualifiedClassLink(ClassDoc cd) {
      return this.getClassLink(cd, "", cd.qualifiedName());
   }

   public String getTargetHyperLink(String link, String where, String target, String label, boolean bold) {
      StringBuffer str = new StringBuffer();
      str.append("<A HREF=\"");
      str.append(link);
      if (where.length() > 0) {
         str.append("#" + where);
      }

      str.append("\"");
      str.append(" TARGET=\"");
      str.append(target);
      str.append("\">");
      if (bold) {
         str.append("<B>");
      }

      str.append(label);
      if (bold) {
         str.append("</B>");
      }

      str.append("</A>");
      return str.toString();
   }

   public String getText(String key) {
      Standard.configuration();
      return ConfigurationStandard.standardmessage.getText(key);
   }

   public String getText(String key, String a1) {
      Standard.configuration();
      return ConfigurationStandard.standardmessage.getText(key, a1);
   }

   public String getText(String key, String a1, String a2) {
      Standard.configuration();
      return ConfigurationStandard.standardmessage.getText(key, a1, a2);
   }

   public String getText(String key, String a1, String a2, String a3) {
      Standard.configuration();
      return ConfigurationStandard.standardmessage.getText(key, a1, a2, a3);
   }

   public boolean isCoreClass(ClassDoc cd) {
      return cd.containingClass() == null || cd.isStatic();
   }

   public boolean isCrossClassIncluded(ClassDoc cd) {
      if (cd.isIncluded()) {
         return true;
      } else {
         return Extern.findPackage(cd.containingPackage().name()) != null;
      }
   }

   public String italicsClassName(ClassDoc cd, boolean qual) {
      String name = qual ? cd.qualifiedName() : cd.name();
      return cd.isInterface() ? this.italicsText(name) : name;
   }

   protected void navCellEnd() {
      this.space();
      this.tdEnd();
   }

   protected void navCellRevStart() {
      this.print("  ");
      this.tdBgcolorStyle("#FFFFFF", "NavBarCell1Rev");
      this.print(" ");
      this.space();
   }

   protected void navCellStart() {
      this.print("  ");
      this.tdBgcolorStyle("#EEEEFF", "NavBarCell1");
      this.print("    ");
   }

   protected void navDetail() {
      this.printText("doclet.Detail");
   }

   protected void navHideLists() {
      this.navHideLists(this.filename);
   }

   protected void navHideLists(String link) {
      this.printBoldTargetHyperLink(link, "_top", this.getText("doclet.NO_FRAMES"));
   }

   protected void navLinkClass() {
      this.navCellStart();
      this.fontStyle("NavBarFont1");
      this.printText("doclet.Class");
      this.fontEnd();
      this.navCellEnd();
   }

   protected void navLinkClassUse() {
      this.navCellStart();
      this.fontStyle("NavBarFont1");
      this.printText("doclet.navClassUse");
      this.fontEnd();
      this.navCellEnd();
   }

   protected void navLinkContents() {
      this.navCellStart();
      this.printHyperLink(this.relativepath + "overview-summary.html", "", this.getText("doclet.Overview"), true, "NavBarFont1");
      this.navCellEnd();
   }

   protected void navLinkDeprecated() {
      this.navCellStart();
      this.printHyperLink(this.relativepath + "deprecated-list.html", "", this.getText("doclet.navDeprecated"), true, "NavBarFont1");
      this.navCellEnd();
   }

   protected void navLinkHelp() {
      String helpfilenm = Standard.configuration().helpfile;
      if (helpfilenm.equals("")) {
         helpfilenm = "help-doc.html";
      } else {
         int lastsep;
         if ((lastsep = helpfilenm.lastIndexOf(File.separatorChar)) != -1) {
            helpfilenm = helpfilenm.substring(lastsep + 1);
         }
      }

      this.navCellStart();
      this.printHyperLink(this.relativepath + helpfilenm, "", this.getText("doclet.Help"), true, "NavBarFont1");
      this.navCellEnd();
   }

   protected void navLinkIndex() {
      this.navCellStart();
      this.printHyperLink(
         this.relativepath
            + (Standard.configuration().splitindex ? DirectoryManager.getPath("index-files") + "/" : "")
            + (Standard.configuration().splitindex ? "index-1.html" : "index-all.html"),
         "",
         this.getText("doclet.Index"),
         true,
         "NavBarFont1"
      );
      this.navCellEnd();
   }

   protected void navLinkMainTree(String label) {
      this.printHyperLink(this.relativepath + "overview-tree.html", label);
   }

   protected void navLinkNext() {
      this.navLinkNext(null);
   }

   public void navLinkNext(String next) {
      String tag = this.getText("doclet.Next");
      if (next != null) {
         this.printHyperLink(next, "", tag, true);
      } else {
         this.print(tag);
      }
   }

   protected void navLinkPackage() {
      this.navCellStart();
      this.fontStyle("NavBarFont1");
      this.printText("doclet.Package");
      this.fontEnd();
      this.navCellEnd();
   }

   protected void navLinkPackage(PackageDoc pkg) {
      this.printPackageLink(pkg, this.getFontColor("NavBarFont1") + this.getBold() + this.getText("doclet.Package") + this.getBoldEnd() + this.getFontEnd());
   }

   protected void navLinkPrevious() {
      this.navLinkPrevious(null);
   }

   public void navLinkPrevious(String prev) {
      String tag = this.getText("doclet.Prev");
      if (prev != null) {
         this.printHyperLink(prev, "", tag, true);
      } else {
         this.print(tag);
      }
   }

   protected void navLinkTree() {
      this.navCellStart();
      PackageDoc[] packages = Standard.configuration().packages;
      if (packages.length == 1) {
         this.printHyperLink(this.pathString(packages[0], "package-tree.html"), "", this.getText("doclet.Tree"), true, "NavBarFont1");
      } else {
         this.printHyperLink(this.relativepath + "overview-tree.html", "", this.getText("doclet.Tree"), true, "NavBarFont1");
      }

      this.navCellEnd();
   }

   protected void navLinks(boolean header) {
      this.println("");
      this.println("<!-- ========== START OF NAVBAR ========== -->");
      if (!Standard.configuration().nonavbar) {
         if (header) {
            this.anchor("navbar_top");
         } else {
            this.anchor("navbar_bottom");
         }

         this.table(0, "100%", 1, 0);
         this.tr();
         this.tdColspanBgcolorStyle(2, "#EEEEFF", "NavBarCell1");
         this.println("");
         if (header) {
            this.anchor("navbar_top_firstrow");
         } else {
            this.anchor("navbar_bottom_firstrow");
         }

         this.table(0, 0, 3);
         this.print("  ");
         this.trAlignVAlign("center", "top");
         if (Standard.configuration().createoverview) {
            this.navLinkContents();
         }

         if (Standard.configuration().packages.length > 0) {
            this.navLinkPackage();
         }

         this.navLinkClass();
         if (Standard.configuration().classuse) {
            this.navLinkClassUse();
         }

         if (Standard.configuration().createtree) {
            this.navLinkTree();
         }

         if (!Standard.configuration().nodeprecated && !Standard.configuration().nodeprecatedlist) {
            this.navLinkDeprecated();
         }

         if (Standard.configuration().createindex) {
            this.navLinkIndex();
         }

         if (!Standard.configuration().nohelp) {
            this.navLinkHelp();
         }

         this.print("  ");
         this.trEnd();
         this.tableEnd();
         this.tdEnd();
         this.tdAlignVAlignRowspan("right", "top", 3);
         this.printUserHeaderFooter(header);
         this.tdEnd();
         this.trEnd();
         this.println("");
         this.tr();
         this.tdBgcolorStyle("white", "NavBarCell2");
         this.font("-2");
         this.space();
         this.navLinkPrevious();
         this.space();
         this.println("");
         this.space();
         this.navLinkNext();
         this.fontEnd();
         this.tdEnd();
         this.tdBgcolorStyle("white", "NavBarCell2");
         this.font("-2");
         this.print("  ");
         this.navShowLists();
         this.print("  ");
         this.space();
         this.println("");
         this.space();
         this.navHideLists();
         this.fontEnd();
         this.tdEnd();
         this.trEnd();
         this.printSummaryDetailLinks();
         this.tableEnd();
         this.println("<!-- =========== END OF NAVBAR =========== -->");
         this.println("");
      }
   }

   protected void navShowLists() {
      this.navShowLists(this.relativepath + "index.html");
   }

   protected void navShowLists(String link) {
      this.printBoldTargetHyperLink(link, "_top", this.getText("doclet.FRAMES"));
   }

   protected void navSummary() {
      this.printText("doclet.Summary");
   }

   public void notice(String key, String a1) {
      Standard.configuration();
      ConfigurationStandard.standardmessage.notice(key, a1);
   }

   public void notice(String key, String a1, String a2) {
      Standard.configuration();
      ConfigurationStandard.standardmessage.notice(key, a1, a2);
   }

   protected String pathString(ClassDoc cd, String name) {
      return this.pathString(cd.containingPackage(), name);
   }

   protected String pathString(PackageDoc pd, String name) {
      StringBuffer buf = new StringBuffer(this.relativepath);
      buf.append(DirectoryManager.getPathToPackage(pd, name));
      return buf.toString();
   }

   protected String pathToClass(ClassDoc cd) {
      return this.pathString(cd.containingPackage(), cd.name() + ".html");
   }

   public void printBoldTargetHyperLink(String link, String target, String label) {
      this.printTargetHyperLink(link, target, label, true);
   }

   public void printBottom() {
      this.hr();
      this.print(Standard.configuration().bottom);
   }

   public void printClassLink(ClassDoc cd) {
      this.print(this.getClassLink(cd, false));
   }

   public void printClassLink(ClassDoc cd, String label) {
      this.print(this.getClassLink(cd, "", label, false));
   }

   public void printClassLink(ClassDoc cd, String where, String label) {
      this.print(this.getClassLink(cd, where, label, false));
   }

   public void printClassLink(ClassDoc cd, String where, String label, boolean bold) {
      this.print(this.getClassLink(cd, where, label, bold));
   }

   public void printClassLink(ClassDoc cd, String where, String label, boolean bold, String color) {
      this.print(this.getClassLink(cd, where, label, bold, color));
   }

   public void printClassLink(ClassDoc cd, String label, boolean bold) {
      this.print(this.getClassLink(cd, "", label, bold));
   }

   public void printClassLink(ClassDoc cd, boolean bold) {
      this.print(this.getClassLink(cd, bold));
   }

   public void printClassLinkForSameDir(ClassDoc cd) {
      if (cd.isIncluded()) {
         this.printHyperLink(cd.name() + ".html", "", this.italicsClassName(cd, false));
      } else {
         this.print(this.italicsClassName(cd, true));
      }
   }

   private void printCommentTags(Tag[] tags, boolean depr, boolean first) {
      if (depr) {
         this.italic();
      }

      for(int i = 0; i < tags.length; ++i) {
         Tag tagelem = tags[i];
         if (tagelem instanceof SeeTag) {
            this.printSeeTag((SeeTag)tagelem);
         } else {
            String text = tagelem.text();
            if (first) {
               text = this.removeNonInlineTags(text);
            }

            this.print(text);
         }
      }

      if (depr) {
         this.italicEnd();
      }

      if (tags.length == 0) {
         this.space();
      }
   }

   private void printCommentTags(Tag[] tags, boolean depr, boolean first, int usagePattern) {
      switch(usagePattern) {
         case 2:
            this.print("<i><font size=\"-1\" color=\"#00FF00\">**For advanced use only** ");
            this.print("</font></i>");
            this.printCommentTags(tags, depr, first);
            break;
         case 3:
            this.print("<i><font size=\"-1\" color=\"#FF0000\">**For internal use only** ");
            this.print("</font></i>");
            this.printCommentTags(tags, depr, first);
            break;
         case 4:
            this.print("<i><font size=\"-1\" color=\"#0000FF\">**Experimental** ");
            this.print("</font></i>");
            this.printCommentTags(tags, depr, first);
            break;
         default:
            this.printCommentTags(tags, depr, first);
      }
   }

   public void printDocLink(Doc doc, String label) {
      this.printDocLink(doc, label, false);
   }

   public void printDocLink(Doc doc, String label, boolean bold) {
      this.print(this.getDocLink(doc, label, bold));
   }

   public void printHeader(String title) {
      this.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0//EN\">");
      this.println("<!--NewPage-->");
      this.html();
      this.head();
      this.print("<!-- Generated by javadoc on ");
      this.print(this.today());
      this.println(" -->");
      this.title();
      this.println(title);
      this.titleEnd();
      this.printStyleSheetProperties();
      this.headEnd();
      this.body("white");
   }

   public void printIndexHeading(String str) {
      this.h2();
      this.print(str);
      this.h2End();
   }

   public void printInlineComment(Doc doc) {
      this.printCommentTags(doc.inlineTags(), false, false, Usage.usagePattern(doc));
   }

   public void printInlineComment(Tag tag) {
      this.printCommentTags(tag.inlineTags(), false, false);
   }

   public void printInlineDeprecatedComment(Doc doc) {
      this.printCommentTags(doc.inlineTags(), true, false);
   }

   public void printInlineDeprecatedComment(Tag tag) {
      this.printCommentTags(tag.inlineTags(), true, false);
   }

   public void printPackageLink(PackageDoc pkg) {
      this.print(this.getPackageLink(pkg));
   }

   public void printPackageLink(PackageDoc pkg, String linklabel) {
      this.print(this.getPackageLink(pkg, linklabel));
   }

   public void printPreQualifiedBoldClassLink(ClassDoc cd) {
      this.print(this.getPreQualifiedClassLink(cd, true));
   }

   public void printPreQualifiedClassLink(ClassDoc cd) {
      this.print(this.getPreQualifiedClassLink(cd, false));
   }

   public void printQualifiedClassLink(ClassDoc cd) {
      this.printClassLink(cd, "", cd.qualifiedName());
   }

   public void printSeeTag(SeeTag see) {
      PackageDoc refPackage = see.referencedPackage();
      ClassDoc refClass = see.referencedClass();
      String refClassName = see.referencedClassName();
      MemberDoc refMem = see.referencedMember();
      String refMemName = see.referencedMemberName();
      String label = see.label();
      String seetext = see.text();
      String text = this.getCode() + seetext + this.getCodeEnd();
      if (seetext.startsWith("<")) {
         this.print(seetext);
      } else {
         if (refClass == null) {
            if (refPackage != null && refPackage.isIncluded()) {
               this.printPackageLink(refPackage);
            } else if (refClassName != null && refClassName.length() > 0) {
               String crosslink = this.getCrossPackageLink(refClassName);
               if (crosslink != null) {
                  this.printHyperLink(crosslink, "", refClassName, false);
               } else {
                  this.warning("doclet.see.class_or_package_not_found", seetext);
                  this.print(label.length() == 0 ? text : label);
               }
            } else {
               this.error("doclet.see.malformed_tag", seetext);
            }
         } else if (refMemName == null) {
            if (label.length() == 0) {
               label = this.getCode() + refClass.name() + this.getCodeEnd();
               this.printClassLink(refClass, label);
            } else {
               this.printClassLink(refClass, label.length() == 0 ? text : label);
            }
         } else if (refMem == null) {
            this.print(label.length() == 0 ? text : label);
         } else {
            ClassDoc containing = refMem.containingClass();
            if (currentcd != containing) {
               refMemName = containing.name() + "." + refMemName;
            }

            if (refMem instanceof ExecutableMemberDoc && refMemName.indexOf(40) < 0) {
               refMemName = refMemName + ((ExecutableMemberDoc)refMem).signature();
            }

            text = this.getCode() + refMemName + this.getCodeEnd();
            this.printDocLink(refMem, label.length() == 0 ? text : label);
         }
      }
   }

   public void printSeeTags(Doc doc) {
      SeeTag[] sees = doc.seeTags();
      if (sees.length > 0) {
         this.dt();
         this.boldText("doclet.See_Also");
         this.dd();

         for(int i = 0; i < sees.length; ++i) {
            if (i > 0) {
               this.println(", ");
            }

            this.printSeeTag(sees[i]);
         }
      }

      if (doc.isClass() && ((ClassDoc)doc).isSerializable()) {
         if (sees.length > 0) {
            this.print(", ");
         } else {
            this.dt();
            this.boldText("doclet.See_Also");
            this.dd();
         }

         this.printHyperLink(this.relativepath + "serialized-form.html", ((ClassDoc)doc).qualifiedName(), this.getText("doclet.Serialized_Form"));
      }
   }

   public void printSinceTag(Doc doc) {
      Tag[] sinces = doc.tags("since");
      if (sinces.length > 0) {
         this.dt();
         this.boldText("doclet.Since");
         this.dd();
         this.printInlineComment(sinces[0]);
         this.ddEnd();
      }
   }

   public void printStyleSheetProperties() {
      String filename = Standard.configuration().stylesheetfile;
      if (filename.length() > 0) {
         File stylefile = new File(filename);
         String parent = stylefile.getParent();
         filename = parent == null ? filename : filename.substring(parent.length() + 1);
      } else {
         filename = "stylesheet.css";
      }

      filename = this.relativepath + filename;
      this.link("REL =\"stylesheet\" TYPE=\"text/css\" HREF=\"" + filename + "\" " + "TITLE=\"Style\"");
   }

   public void printSummaryComment(Doc doc) {
      this.printCommentTags(doc.firstSentenceTags(), false, true, Usage.usagePattern(doc));
   }

   public void printSummaryDeprecatedComment(Doc doc) {
      this.printCommentTags(doc.firstSentenceTags(), true, true);
   }

   public void printSummaryDeprecatedComment(Tag tag) {
      this.printCommentTags(tag.firstSentenceTags(), true, true);
   }

   protected void printSummaryDetailLinks() {
   }

   public void printTargetClassLink(ClassDoc cd, String target) {
      String filename = cd.name() + ".html";
      this.printTargetHyperLink(filename, target, cd.isInterface() ? this.italicsText(cd.name()) : cd.name());
   }

   public void printTargetHyperLink(String link, String target, String label) {
      this.printTargetHyperLink(link, "", target, label, false);
   }

   public void printTargetHyperLink(String link, String where, String target, String label, boolean bold) {
      this.print(this.getTargetHyperLink(link, where, target, label, bold));
   }

   public void printTargetHyperLink(String link, String target, String label, boolean bold) {
      this.printTargetHyperLink(link, "", target, label, bold);
   }

   public void printTargetPackageLink(PackageDoc pd, String target, String label) {
      this.printTargetHyperLink(this.pathString(pd, "package-summary.html"), target, label);
   }

   public void printText(String key) {
      this.print(this.getText(key));
   }

   public void printText(String key, String a1) {
      this.print(this.getText(key, a1));
   }

   public void printText(String key, String a1, String a2) {
      this.print(this.getText(key, a1, a2));
   }

   public void printUsageTags(Doc doc) {
      Tag[] usages = doc.tags("xsl.usage");
      if (usages.length > 0) {
         this.dt();
         this.boldText("doclet.xsl.usage.header");
         this.dd();

         for(int i = 0; i < usages.length - 1; ++i) {
            this.print(XSLUsage.getHTML(usages[i]) + ", ");
         }

         this.print(XSLUsage.getHTML(usages[usages.length - 1]));
         this.ddEnd();
      }
   }

   public void printUserHeaderFooter(boolean header) {
      this.em();
      if (header) {
         this.print(Standard.configuration().header);
      } else if (Standard.configuration().footer.length() != 0) {
         this.print(Standard.configuration().footer);
      } else {
         this.print(Standard.configuration().header);
      }

      this.emEnd();
   }

   public String removeNonInlineTags(String text) {
      if (text.indexOf(60) < 0) {
         return text;
      } else {
         String[] noninlinetags = new String[]{
            "<ul>",
            "</ul>",
            "<ol>",
            "</ol>",
            "<dl>",
            "</dl>",
            "<table>",
            "</table>",
            "<tr>",
            "</tr>",
            "<td>",
            "</td>",
            "<th>",
            "</th>",
            "<p>",
            "</p>",
            "<li>",
            "</li>",
            "<dd>",
            "</dd>",
            "<dir>",
            "</dir>",
            "<dt>",
            "</dt>",
            "<h1>",
            "</h1>",
            "<h2>",
            "</h2>",
            "<h3>",
            "</h3>",
            "<h4>",
            "</h4>",
            "<h5>",
            "</h5>",
            "<h6>",
            "</h6>",
            "<pre>",
            "</pre>",
            "<menu>",
            "</menu>",
            "<listing>",
            "</listing>",
            "<hr>",
            "<blockquote>",
            "</blockquote>",
            "<center>",
            "</center>",
            "<UL>",
            "</UL>",
            "<OL>",
            "</OL>",
            "<DL>",
            "</DL>",
            "<TABLE>",
            "</TABLE>",
            "<TR>",
            "</TR>",
            "<TD>",
            "</TD>",
            "<TH>",
            "</TH>",
            "<P>",
            "</P>",
            "<LI>",
            "</LI>",
            "<DD>",
            "</DD>",
            "<DIR>",
            "</DIR>",
            "<DT>",
            "</DT>",
            "<H1>",
            "</H1>",
            "<H2>",
            "</H2>",
            "<H3>",
            "</H3>",
            "<H4>",
            "</H4>",
            "<H5>",
            "</H5>",
            "<H6>",
            "</H6>",
            "<PRE>",
            "</PRE>",
            "<MENU>",
            "</MENU>",
            "<LISTING>",
            "</LISTING>",
            "<HR>",
            "<BLOCKQUOTE>",
            "</BLOCKQUOTE>",
            "<CENTER>",
            "</CENTER>"
         };

         for(int i = 0; i < noninlinetags.length; ++i) {
            text = this.replace(text, noninlinetags[i], "");
         }

         return text;
      }
   }

   public String replace(String text, String tobe, String by) {
      while(true) {
         int startindex = text.indexOf(tobe);
         if (startindex < 0) {
            return text;
         }

         int endindex = startindex + tobe.length();
         StringBuffer replaced = new StringBuffer();
         if (startindex > 0) {
            replaced.append(text.substring(0, startindex));
         }

         replaced.append(by);
         if (text.length() > endindex) {
            replaced.append(text.substring(endindex));
         }

         text = replaced.toString();
      }
   }

   public void summaryRow(int width) {
      if (width != 0) {
         this.tdWidth(width + "%");
      } else {
         this.td();
      }
   }

   public void summaryRowEnd() {
      this.tdEnd();
   }

   public void tableHeaderEnd() {
      this.fontEnd();
      this.tdEnd();
      this.trEnd();
   }

   public void tableHeaderStart() {
      this.tableHeaderStart(2);
   }

   public void tableHeaderStart(int span) {
      this.tableHeaderStart("#CCCCFF", span);
   }

   public void tableHeaderStart(String color) {
      this.tableHeaderStart(color, 2);
   }

   public void tableHeaderStart(String color, int span) {
      this.trBgcolorStyle(color, "TableHeadingColor");
      this.tdColspan(span);
      this.font("+2");
   }

   public void tableIndexDetail() {
      this.println("\n<TABLE BORDER=\"1\" CELLPADDING=\"3\" CELLSPACING=\"0\" WIDTH=\"100%\">");
   }

   public void tableIndexSummary() {
      this.println("\n<TABLE BORDER=\"1\" CELLPADDING=\"3\" CELLSPACING=\"0\" WIDTH=\"100%\">");
   }

   public void tableInheritedHeaderEnd() {
      this.tdEnd();
      this.trEnd();
   }

   public void tableInheritedHeaderStart(String color) {
      this.trBgcolorStyle(color, "TableSubHeadingColor");
      this.td();
   }

   public void tableUseInfoHeaderStart(String color) {
      this.trBgcolorStyle(color, "TableSubHeadingColor");
      this.tdColspan(2);
   }

   public void tdIndex() {
      this.print("<TD ALIGN=\"right\" VALIGN=\"top\" WIDTH=\"1%\">");
   }

   public void warning(String key, String a1) {
      Standard.configuration();
      ConfigurationStandard.standardmessage.warning(key, a1);
   }
}
