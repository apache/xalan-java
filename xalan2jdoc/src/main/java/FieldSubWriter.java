package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.ProgramElementDoc;

public class FieldSubWriter extends AbstractSubWriter {
   FieldSubWriter(SubWriterHolderWriter writer) {
      super(writer);
   }

   public ProgramElementDoc[] members(ClassDoc cd) {
      return this.eligibleMembers(cd.fields());
   }

   protected void printBodyHtmlEnd(ClassDoc cd) {
   }

   protected void printDeprecatedLink(ProgramElementDoc member) {
      super.writer.printClassLink(member.containingClass(), member.name(), ((FieldDoc)member).qualifiedName());
   }

   protected void printHeader(ClassDoc cd) {
      super.writer.anchor("field_detail");
      super.writer.printTableHeadingBackground(super.writer.getText("doclet.Field_Detail"));
   }

   public void printInheritedSummaryAnchor(ClassDoc cd) {
      super.writer.anchor("fields_inherited_from_class_" + cd.qualifiedName());
   }

   public void printInheritedSummaryLabel(ClassDoc cd) {
      String classlink = super.writer.getPreQualifiedClassLink(cd);
      super.writer.bold();
      super.writer.printText("doclet.Fields_Inherited_From_Class", classlink);
      super.writer.boldEnd();
   }

   protected void printInheritedSummaryLink(ClassDoc cd, ProgramElementDoc member) {
      String name = member.name();
      super.writer.printClassLink(cd, name, name, false);
   }

   protected void printMember(ProgramElementDoc member) {
      FieldDoc field = (FieldDoc)member;
      super.writer.anchor(field.name());
      this.printHead(field);
      this.printSignature(field);
      this.printFullComment(field);
   }

   protected void printNavDetailLink(boolean link) {
      if (link) {
         super.writer.printHyperLink("", "field_detail", super.writer.getText("doclet.navField"));
      } else {
         super.writer.printText("doclet.navField");
      }
   }

   protected void printNavSummaryLink(ClassDoc cd, boolean link) {
      if (link) {
         super.writer
            .printHyperLink("", cd == null ? "field_summary" : "fields_inherited_from_class_" + cd.qualifiedName(), super.writer.getText("doclet.navField"));
      } else {
         super.writer.printText("doclet.navField");
      }
   }

   void printSignature(MemberDoc member) {
      FieldDoc field = (FieldDoc)member;
      super.writer.pre();
      this.printModifiers(field);
      this.printTypeLink(field.type());
      this.print(' ');
      this.bold(field.name());
      super.writer.preEnd();
   }

   public void printSummaryAnchor(ClassDoc cd) {
      super.writer.anchor("field_summary");
   }

   public void printSummaryLabel(ClassDoc cd) {
      super.writer.boldText("doclet.Field_Summary");
   }

   protected void printSummaryLink(ClassDoc cd, ProgramElementDoc member) {
      String name = member.name();
      ClassDoc mcd = member.containingClass();
      super.writer.bold();
      if (mcd != cd) {
         super.writer.print(mcd.name() + ".");
      }

      super.writer.printClassLink(mcd, name, name, false);
      super.writer.boldEnd();
   }

   protected void printSummaryType(ProgramElementDoc member) {
      FieldDoc field = (FieldDoc)member;
      this.printModifierAndType(field, field.type());
   }
}
