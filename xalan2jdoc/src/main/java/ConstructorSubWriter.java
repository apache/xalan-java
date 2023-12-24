package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;

public class ConstructorSubWriter extends ExecutableMemberSubWriter {
   protected boolean foundNonPublicMember = false;

   ConstructorSubWriter(SubWriterHolderWriter writer) {
      super(writer);
   }

   protected void checkForNonPublicMembers(ProgramElementDoc[] members) {
      for(int i = 0; i < members.length; ++i) {
         if (!this.foundNonPublicMember && !members[i].isPublic()) {
            this.foundNonPublicMember = true;
            break;
         }
      }
   }

   public ProgramElementDoc[] members(ClassDoc cd) {
      ProgramElementDoc[] members = this.eligibleMembers(cd.constructors());
      this.checkForNonPublicMembers(members);
      return members;
   }

   protected void navSummaryLink(ClassDoc cd) {
      this.printNavSummaryLink(cd, this.members(cd).length > 0);
   }

   protected void printHeader(ClassDoc cd) {
      super.writer.anchor("constructor_detail");
      super.writer.printTableHeadingBackground(super.writer.getText("doclet.Constructor_Detail"));
   }

   public void printInheritedSummaryAnchor(ClassDoc cd) {
   }

   public void printInheritedSummaryLabel(ClassDoc cd) {
   }

   protected void printNavDetailLink(boolean link) {
      if (link) {
         super.writer.printHyperLink("", "constructor_detail", super.writer.getText("doclet.navConstructor"));
      } else {
         super.writer.printText("doclet.navConstructor");
      }
   }

   protected void printNavSummaryLink(ClassDoc cd, boolean link) {
      if (link) {
         super.writer.printHyperLink("", "constructor_summary", super.writer.getText("doclet.navConstructor"));
      } else {
         super.writer.printText("doclet.navConstructor");
      }
   }

   public void printSummaryAnchor(ClassDoc cd) {
      super.writer.anchor("constructor_summary");
   }

   public void printSummaryLabel(ClassDoc cd) {
      super.writer.boldText("doclet.Constructor_Summary");
   }

   protected void printSummaryType(ProgramElementDoc member) {
      if (this.foundNonPublicMember) {
         super.writer.printTypeSummaryHeader();
         if (member.isProtected()) {
            this.print("protected ");
         } else if (member.isPrivate()) {
            this.print("private ");
         } else if (member.isPublic()) {
            super.writer.space();
         } else {
            super.writer.printText("doclet.Package_private");
         }

         super.writer.printTypeSummaryFooter();
      }
   }

   protected void printTags(ProgramElementDoc member) {
      ParamTag[] params = ((ConstructorDoc)member).paramTags();
      ThrowsTag[] thrown = ((ConstructorDoc)member).throwsTags();
      Tag[] sinces = member.tags("since");
      SeeTag[] sees = member.seeTags();
      Tag[] usages = member.tags("xsl.usage");
      if (usages.length + params.length + thrown.length + sees.length + sinces.length > 0) {
         super.writer.dd();
         super.writer.dl();
         this.printParamTags(params);
         this.printThrowsTags(thrown);
         super.writer.printSinceTag(member);
         super.writer.printSeeTags(member);
         super.writer.printUsageTags(member);
         super.writer.dlEnd();
         super.writer.ddEnd();
      }
   }
}
