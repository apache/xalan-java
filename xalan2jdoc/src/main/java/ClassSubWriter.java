package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ProgramElementDoc;

public class ClassSubWriter extends AbstractSubWriter {
   ClassSubWriter(SubWriterHolderWriter writer) {
      super(writer);
   }

   public ProgramElementDoc[] members(ClassDoc cd) {
      return this.eligibleMembers(cd.innerClasses());
   }

   protected void printBodyHtmlEnd(ClassDoc cd) {
   }

   protected void printDeprecatedLink(ProgramElementDoc member) {
      super.writer.printQualifiedClassLink((ClassDoc)member);
   }

   protected void printHeader(ClassDoc cd) {
   }

   public void printInheritedSummaryAnchor(ClassDoc cd) {
      super.writer.anchor("inner_classes_inherited_from_class_" + cd.qualifiedName());
   }

   public void printInheritedSummaryLabel(ClassDoc cd) {
      String clslink = super.writer.getPreQualifiedClassLink(cd);
      super.writer.bold();
      super.writer.printText("doclet.Inner_Classes_Inherited_From_Class", clslink);
      super.writer.boldEnd();
   }

   protected void printInheritedSummaryLink(ClassDoc cd, ProgramElementDoc member) {
      this.printSummaryLink(cd, member);
   }

   protected void printMember(ProgramElementDoc member) {
   }

   protected void printMemberLink(ProgramElementDoc member) {
   }

   protected void printMembersSummaryLink(ClassDoc cd, ClassDoc icd, boolean link) {
      if (link) {
         super.writer
            .printHyperLink(
               cd.name() + ".html",
               cd == icd ? "inner_class_summary" : "inner_classes_inherited_from_class_" + icd.qualifiedName(),
               super.writer.getText("doclet.Inner_Class_Summary")
            );
      } else {
         super.writer.printText("doclet.Inner_Class_Summary");
      }
   }

   protected void printNavDetailLink(boolean link) {
   }

   protected void printNavSummaryLink(ClassDoc cd, boolean link) {
      if (link) {
         super.writer
            .printHyperLink(
               "", cd == null ? "inner_class_summary" : "inner_classes_inherited_from_class_" + cd.qualifiedName(), super.writer.getText("doclet.navInner")
            );
      } else {
         super.writer.printText("doclet.navInner");
      }
   }

   public void printSummaryAnchor(ClassDoc cd) {
      super.writer.anchor("inner_class_summary");
   }

   public void printSummaryLabel(ClassDoc cd) {
      super.writer.boldText("doclet.Inner_Class_Summary");
   }

   protected void printSummaryLink(ClassDoc cd, ProgramElementDoc member) {
      super.writer.bold();
      super.writer.printClassLink((ClassDoc)member);
      super.writer.boldEnd();
   }

   protected void printSummaryType(ProgramElementDoc member) {
      ClassDoc cd = (ClassDoc)member;
      this.printModifierAndType(cd, null);
   }
}
