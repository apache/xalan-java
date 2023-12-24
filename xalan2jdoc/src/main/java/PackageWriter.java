package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.DirectoryManager;
import com.sun.tools.doclets.DocletAbortException;
import com.sun.tools.doclets.SourcePath;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PackageWriter extends AbstractPackageWriter {
   protected PackageDoc prev;
   protected PackageDoc next;

   public PackageWriter(String path, String filename, PackageDoc packagedoc, PackageDoc prev, PackageDoc next) throws IOException, DocletAbortException {
      super(path, filename, packagedoc);
      this.prev = prev;
      this.next = next;
   }

   protected void copyDocFiles(String path) throws DocletAbortException {
      SourcePath sourcePath = new SourcePath(Standard.configuration().sourcepath);
      String docfilesdir = path + "/" + "doc-files";
      File sourcedir = sourcePath.getDirectory(docfilesdir);
      if (sourcedir != null) {
         String destname = HtmlStandardWriter.destdir;
         if (destname.length() > 0 && !destname.endsWith("/")) {
            destname = destname + "/";
         }

         String src = sourcedir.toString();
         String dest = destname + path + "/" + "doc-files";

         try {
            File srcdir = new File(src);
            File destdir = new File(dest);
            DirectoryManager.createDirectory(dest);
            String[] files = srcdir.list();

            for(int i = 0; i < files.length; ++i) {
               File srcfile = new File(srcdir, files[i]);
               File destfile = new File(destdir, files[i]);
               if (srcfile.isFile()) {
                  this.notice("doclet.Copying_File_0_To_Dir_1", srcfile.toString(), destdir.toString());
                  Standard.copyFile(destfile, srcfile);
               }
            }
         } catch (SecurityException var14) {
            throw new DocletAbortException();
         } catch (IOException var15) {
            throw new DocletAbortException();
         }
      }
   }

   public static void generate(PackageDoc pkg, PackageDoc prev, PackageDoc next) throws DocletAbortException {
      String path = DirectoryManager.getDirectoryPath(pkg);
      String filename = "package-summary.html";

      try {
         PackageWriter packgen = new PackageWriter(path, filename, pkg, prev, next);
         packgen.generatePackageFile();
         packgen.close();
         packgen.copyDocFiles(path);
      } catch (IOException var7) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var7.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateClassKindListing(ClassDoc[] arr, String label) {
      if (arr.length > 0) {
         Arrays.sort((Object[])arr);
         this.tableIndexSummary();
         this.printFirstRow(label);

         for(int i = 0; i < arr.length; ++i) {
            boolean deprecated = arr[i].tags("deprecated").length > 0;
            if ((!Standard.configuration().nodeprecated || !deprecated) && this.isCoreClass(arr[i])) {
               this.trBgcolorStyle("white", "TableRowColor");
               this.summaryRow(15);
               this.bold();
               this.printClassLinkForSameDir(arr[i]);
               this.boldEnd();
               this.summaryRowEnd();
               this.summaryRow(0);
               if (deprecated) {
                  this.boldText("doclet.Deprecated");
                  this.space();
                  this.printSummaryDeprecatedComment(arr[i].tags("deprecated")[0]);
               } else {
                  this.printSummaryComment(arr[i]);
               }

               this.summaryRowEnd();
               this.trEnd();
            }
         }

         this.tableEnd();
         this.println("&nbsp;");
         this.p();
      }
   }

   protected void generateClassListing() {
      this.generateClassKindListing(super.packagedoc.interfaces(), this.getText("doclet.Interface_Summary"));
      this.generateClassKindListing(super.packagedoc.ordinaryClasses(), this.getText("doclet.Class_Summary"));
      this.generateClassKindListing(super.packagedoc.exceptions(), this.getText("doclet.Exception_Summary"));
      this.generateClassKindListing(super.packagedoc.errors(), this.getText("doclet.Error_Summary"));
   }

   protected void navLinkClassUse() {
      this.navCellStart();
      this.printHyperLink("package-use.html", "", this.getText("doclet.navClassUse"), true, "NavBarFont1");
      this.navCellEnd();
   }

   protected void navLinkNext() {
      if (this.next == null) {
         this.printText("doclet.Next_Package");
      } else {
         String path = DirectoryManager.getRelativePath(super.packagedoc.name(), this.next.name());
         this.printHyperLink(path + "package-summary.html", "", this.getText("doclet.Next_Package"), true);
      }
   }

   protected void navLinkPrevious() {
      if (this.prev == null) {
         this.printText("doclet.Prev_Package");
      } else {
         String path = DirectoryManager.getRelativePath(super.packagedoc.name(), this.prev.name());
         this.printHyperLink(path + "package-summary.html", "", this.getText("doclet.Prev_Package"), true);
      }
   }

   protected void navLinkTree() {
      this.navCellStart();
      this.printHyperLink("package-tree.html", "", this.getText("doclet.Tree"), true, "NavBarFont1");
      this.navCellEnd();
   }

   protected void printFirstRow(String label) {
      this.tableHeaderStart("#CCCCFF");
      this.bold(label);
      this.tableHeaderEnd();
   }

   protected void printPackageComment() {
      if (super.packagedoc.inlineTags().length > 0) {
         this.anchor("package_description");
         this.h2(this.getText("doclet.Package_Description", super.packagedoc.name()));
         this.p();
         this.printInlineComment(super.packagedoc);
         this.p();
      }
   }

   protected void printPackageDescription() throws IOException {
      this.printPackageComment();
      this.generateTagInfo(super.packagedoc);
   }

   protected void printPackageFooter() {
      this.hr();
      this.navLinks(false);
      this.printBottom();
   }

   protected void printPackageHeader(String heading) {
      this.navLinks(true);
      this.hr();
      this.h2(this.getText("doclet.Package") + " " + heading);
      if (super.packagedoc.inlineTags().length > 0) {
         this.printSummaryComment(super.packagedoc);
         this.p();
         this.bold(this.getText("doclet.See"));
         this.br();
         this.printNbsps();
         this.printHyperLink("", "package_description", this.getText("doclet.Description"), true);
         this.p();
      }
   }
}
