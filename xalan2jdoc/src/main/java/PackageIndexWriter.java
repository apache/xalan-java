package xalanjdoc;

import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PackageIndexWriter extends AbstractPackageIndexWriter {
   private RootDoc root;
   private Map groupPackageMap;
   private List groupList;

   public PackageIndexWriter(String filename, RootDoc root) throws IOException {
      super(filename);
      this.root = root;
      this.groupPackageMap = Group.groupPackages(super.packages);
      this.groupList = Group.getGroupList();
   }

   public static void generate(RootDoc root) throws DocletAbortException {
      String filename = "overview-summary.html";

      try {
         PackageIndexWriter packgen = new PackageIndexWriter(filename, root);
         packgen.generatePackageIndexFile();
         packgen.close();
      } catch (IOException var4) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.error("doclet.exception_encountered", var4.toString(), filename);
         throw new DocletAbortException();
      }
   }

   protected void generateIndex() {
      for(int i = 0; i < this.groupList.size(); ++i) {
         String groupname = (String)this.groupList.get(i);
         List list = (List)this.groupPackageMap.get(groupname);
         if (list != null && list.size() > 0) {
            this.printIndexContents(list.toArray(new PackageDoc[list.size()]), groupname);
         }
      }
   }

   protected void printIndexFooter() {
      this.tableEnd();
      this.p();
      this.space();
   }

   protected void printIndexHeader(String text) {
      this.tableIndexSummary();
      this.tableHeaderStart("#CCCCFF");
      this.bold(text);
      this.tableHeaderEnd();
   }

   protected void printIndexRow(PackageDoc packagedoc) {
      this.trBgcolorStyle("white", "TableRowColor");
      this.summaryRow(20);
      this.bold();
      this.printPackageLink(packagedoc);
      this.boldEnd();
      this.summaryRowEnd();
      this.summaryRow(0);
      this.printSummaryComment(packagedoc);
      this.summaryRowEnd();
      this.trEnd();
   }

   protected void printNavigationBarFooter() {
      this.hr();
      this.navLinks(false);
      this.printBottom();
   }

   protected void printNavigationBarHeader() {
      this.navLinks(true);
      this.hr();
      this.printConfigurationTitle();
   }

   protected void printOverview() throws IOException {
      this.printOverviewComment();
      this.generateTagInfo(this.root);
   }

   protected void printOverviewComment() {
      if (this.root.inlineTags().length > 0) {
         this.anchor("overview_description");
         this.p();
         this.printInlineComment(this.root);
         this.p();
      }
   }

   protected void printOverviewHeader() {
      if (this.root.inlineTags().length > 0) {
         this.printSummaryComment(this.root);
         this.p();
         this.bold(this.getText("doclet.See"));
         this.br();
         this.printNbsps();
         this.printHyperLink("", "overview_description", this.getText("doclet.Description"), true);
         this.p();
      }
   }
}
