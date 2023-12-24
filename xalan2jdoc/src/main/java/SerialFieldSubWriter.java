package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SerialFieldTag;
import com.sun.javadoc.Tag;
import java.util.Arrays;

public class SerialFieldSubWriter extends FieldSubWriter {
   ProgramElementDoc[] members = null;

   SerialFieldSubWriter(SubWriterHolderWriter writer) {
      super(writer);
   }

   public ProgramElementDoc[] members(ClassDoc cd) {
      if (this.members == null) {
         FieldDoc[] array = cd.serializableFields();
         Arrays.sort((Object[])array);
         this.members = this.eligibleMembers(array);
      }

      return this.members;
   }

   protected void printBodyHtmlEnd(ClassDoc cd) {
   }

   protected void printComment(ProgramElementDoc member) {
      String fieldComment = member.commentText();
      if (fieldComment.length() > 0) {
         super.writer.dd();
         this.print(fieldComment);
      }

      Tag[] tags = member.tags("serial");
      if (tags.length > 0 && tags[0].text().length() > 0) {
         String serialComment = tags[0].text();
         super.writer.dd();
         this.print(serialComment);
      }
   }

   private void printComment(SerialFieldTag sftag) {
      super.writer.dl();
      super.writer.dd();
      super.writer.print(sftag.description());
      super.writer.dlEnd();
   }

   protected void printDeprecatedLink(ProgramElementDoc member) {
   }

   protected void printHeader(ClassDoc cd) {
      super.writer.anchor("serializedForm");
      this.printSerializableClassComment(cd);
      super.writer.printTableHeadingBackground(super.writer.getText("doclet.Serialized_Form_fields"));
   }

   public void printInheritedSummaryLabel(ClassDoc cd) {
   }

   protected void printInheritedSummaryLink(ClassDoc cd, ProgramElementDoc member) {
   }

   protected void printMember(ProgramElementDoc member) {
      FieldDoc field = (FieldDoc)member;
      ClassDoc cd = field.containingClass();
      if (cd.definesSerializableFields()) {
         SerialFieldTag[] tags = field.serialFieldTags();
         Arrays.sort((Object[])tags);

         for(int i = 0; i < tags.length; ++i) {
            if (i > 0) {
               super.writer.printMemberHeader();
            }

            this.printSignature(tags[i]);
            this.printComment(tags[i]);
            super.writer.printMemberFooter();
         }
      } else {
         if (field.tags("serial").length == 0 && !field.isSynthetic()) {
            Standard.configuration();
            ConfigurationStandard.standardmessage.warning("doclet.MissingSerialTag", cd.qualifiedName(), field.name());
         }

         this.printSignature(field);
         this.printFullComment(field);
      }
   }

   private void printSerializableClassComment(ClassDoc cd) {
      if (cd.definesSerializableFields()) {
         FieldDoc serialPersistentFields = (FieldDoc)this.members(cd)[0];
         String comment = serialPersistentFields.commentText();
         if (comment.length() > 0) {
            super.writer.printTableHeadingBackground(super.writer.getText("doclet.Serialized_Form_class"));
            this.printFullComment(serialPersistentFields);
         }
      }
   }

   void printSignature(MemberDoc member) {
      FieldDoc field = (FieldDoc)member;
      this.printHead(member);
      super.writer.pre();
      this.printTypeLink(field.type());
      this.print(' ');
      this.bold(field.name());
      super.writer.preEnd();
   }

   void printSignature(SerialFieldTag sftag) {
      super.writer.pre();
      ClassDoc fieldTypeDoc = sftag.fieldTypeDoc();
      if (fieldTypeDoc != null) {
         super.writer.printClassLink(fieldTypeDoc);
      } else {
         super.writer.print(sftag.fieldType());
      }

      this.print(' ');
      this.bold(sftag.fieldName());
      super.writer.preEnd();
   }

   public void printSummaryLabel(ClassDoc cd) {
   }

   protected void printSummaryLink(ClassDoc cd, ProgramElementDoc member) {
   }

   protected void printSummaryType(ProgramElementDoc member) {
   }
}
