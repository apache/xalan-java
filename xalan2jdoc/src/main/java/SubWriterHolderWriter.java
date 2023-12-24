package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.DocletAbortException;
import java.io.IOException;

public abstract class SubWriterHolderWriter extends HtmlStandardWriter {
   public SubWriterHolderWriter(String filename) throws IOException {
      super(filename);
   }

   public SubWriterHolderWriter(String path, String filename, String relpath) throws IOException, DocletAbortException {
      super(path, filename, relpath);
   }

   protected void printCommentDef(Doc member) {
      this.printNbsps();
      this.printIndexComment(member);
   }

   protected void printIndexComment(Doc member) {
      Tag[] deprs = member.tags("deprecated");
      boolean deprecated = false;
      if (deprs.length > 0) {
         this.boldText("doclet.Deprecated");
         this.space();
         this.printInlineDeprecatedComment(deprs[0]);
      } else {
         ClassDoc cd = ((ProgramElementDoc)member).containingClass();
         if (cd != null && cd.tags("deprecated").length > 0) {
            this.boldText("doclet.Deprecated");
            this.space();
         }

         this.printSummaryComment(member);
      }
   }

   public void printInheritedSummaryFooter(AbstractSubWriter mw, ClassDoc cd) {
      this.codeEnd();
      this.summaryRowEnd();
      this.trEnd();
      this.tableEnd();
      this.space();
   }

   public void printInheritedSummaryHeader(AbstractSubWriter mw, ClassDoc cd) {
      mw.printInheritedSummaryAnchor(cd);
      this.tableIndexSummary();
      this.tableInheritedHeaderStart("#EEEEFF");
      mw.printInheritedSummaryLabel(cd);
      this.tableInheritedHeaderEnd();
      this.trBgcolorStyle("white", "TableRowColor");
      this.summaryRow(0);
      this.code();
   }

   public void printInheritedSummaryMember(AbstractSubWriter mw, ClassDoc cd, ProgramElementDoc member) {
      mw.printInheritedSummaryLink(cd, member);
   }

   public void printMemberFooter() {
   }

   public void printMemberHeader() {
      this.hr();
   }

   public void printSummaryFooter(AbstractSubWriter mw, ClassDoc cd) {
      this.tableEnd();
      this.space();
   }

   public void printSummaryHeader(AbstractSubWriter mw, ClassDoc cd) {
      mw.printSummaryAnchor(cd);
      this.tableIndexSummary();
      this.tableHeaderStart("#CCCCFF");
      mw.printSummaryLabel(cd);
      this.tableHeaderEnd();
   }

   public void printSummaryMember(AbstractSubWriter mw, ClassDoc cd, ProgramElementDoc member) {
      this.trBgcolorStyle("white", "TableRowColor");
      mw.printSummaryType(member);
      this.summaryRow(0);
      this.code();
      mw.printSummaryLink(cd, member);
      this.codeEnd();
      this.println();
      this.br();
      this.printCommentDef(member);
      this.summaryRowEnd();
      this.trEnd();
   }

   public void printTableHeadingBackground(String str) {
      this.tableIndexDetail();
      this.tableHeaderStart("#CCCCFF", 1);
      this.bold(str);
      this.tableHeaderEnd();
      this.tableEnd();
   }

   public void printTypeSummaryFooter() {
      this.codeEnd();
      this.fontEnd();
      this.tdEnd();
   }

   public void printTypeSummaryHeader() {
      this.tdIndex();
      this.font("-1");
      this.code();
   }
}
