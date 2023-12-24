package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractSubWriter {
   protected final SubWriterHolderWriter writer;

   AbstractSubWriter(SubWriterHolderWriter writer) {
      this.writer = writer;
   }

   protected void bold(String str) {
      this.writer.bold(str);
      this.writer.displayLength += str.length();
   }

   protected ProgramElementDoc[] eligibleMembers(ProgramElementDoc[] members) {
      if (!Standard.configuration().nodeprecated) {
         return members;
      } else {
         List list = new ArrayList();

         for(int i = 0; i < members.length; ++i) {
            if (members[i].tags("deprecated").length == 0) {
               list.add(members[i]);
            }
         }

         return list.toArray(new ProgramElementDoc[list.size()]);
      }
   }

   protected String makeSpace(int len) {
      if (len <= 0) {
         return "";
      } else {
         StringBuffer sb = new StringBuffer(len);

         for(int i = 0; i < len; ++i) {
            sb.append(' ');
         }

         return sb.toString();
      }
   }

   public abstract ProgramElementDoc[] members(ClassDoc var1);

   protected String modifierString(MemberDoc member) {
      int ms = member.modifierSpecifier();
      int no = 288;
      return Modifier.toString(ms & ~no);
   }

   protected String name(ProgramElementDoc member) {
      return member.name();
   }

   protected void navDetailLink(ClassDoc cd) {
      ProgramElementDoc[] members = this.members(cd);
      this.printNavDetailLink(members.length > 0);
   }

   protected void navSummaryLink(ClassDoc cd) {
      ProgramElementDoc[] members = this.members(cd);
      if (members.length > 0) {
         this.printNavSummaryLink(null, true);
      } else {
         for(ClassDoc icd = cd.superclass(); icd != null; icd = icd.superclass()) {
            ProgramElementDoc[] inhmembers = this.members(icd);
            if (inhmembers.length > 0) {
               this.printNavSummaryLink(icd, true);
               return;
            }
         }

         this.printNavSummaryLink(null, false);
      }
   }

   protected void print(char ch) {
      this.writer.print(ch);
      ++this.writer.displayLength;
   }

   protected void print(String str) {
      this.writer.print(str);
      this.writer.displayLength += str.length();
   }

   protected abstract void printBodyHtmlEnd(ClassDoc var1);

   protected void printComment(ProgramElementDoc member) {
      if (member.inlineTags().length > 0) {
         this.writer.dd();
         this.writer.printInlineComment(member);
      }
   }

   protected void printCommentAndTags(ProgramElementDoc member) {
      this.printComment(member);
      this.printTags(member);
   }

   protected void printDeprecated(ProgramElementDoc member) {
      Tag[] deprs = member.tags("deprecated");
      if (deprs.length > 0) {
         this.writer.dd();
         this.writer.boldText("doclet.Deprecated");
         this.writer.space();
         this.writer.printInlineDeprecatedComment(deprs[0]);
      } else {
         this.printDeprecatedClassComment(member);
      }
   }

   protected void printDeprecatedAPI(List deprmembers, String headingKey) {
      if (deprmembers.size() > 0) {
         this.writer.tableIndexSummary();
         this.writer.tableHeaderStart("#CCCCFF");
         this.writer.boldText(headingKey);
         this.writer.tableHeaderEnd();

         for(int i = 0; i < deprmembers.size(); ++i) {
            ProgramElementDoc member = (ProgramElementDoc)deprmembers.get(i);
            ClassDoc cd = member.containingClass();
            this.writer.trBgcolorStyle("white", "TableRowColor");
            this.writer.summaryRow(0);
            this.printDeprecatedLink(member);
            this.writer.br();
            this.writer.printNbsps();
            this.writer.printInlineDeprecatedComment(member.tags("deprecated")[0]);
            this.writer.space();
            this.writer.summaryRowEnd();
            this.writer.trEnd();
         }

         this.writer.tableEnd();
         this.writer.space();
         this.writer.p();
      }
   }

   protected void printDeprecatedClassComment(ProgramElementDoc member) {
      Tag[] deprs = member.containingClass().tags("deprecated");
      if (deprs.length > 0) {
         this.writer.dd();
         this.writer.boldText("doclet.Deprecated");
         this.writer.space();
      }
   }

   protected abstract void printDeprecatedLink(ProgramElementDoc var1);

   protected void printFullComment(ProgramElementDoc member) {
      this.writer.dl();
      this.printDeprecated(member);
      this.printCommentAndTags(member);
      this.writer.dlEnd();
   }

   protected void printHead(MemberDoc member) {
      this.writer.h3();
      this.writer.print(member.name());
      this.writer.h3End();
   }

   protected abstract void printHeader(ClassDoc var1);

   protected void printInheritedMembersInfo(ClassDoc icd) {
      ProgramElementDoc[] members = this.members(icd);
      if (members.length > 0) {
         Arrays.sort((Object[])members);
         this.printInheritedSummaryHeader(icd);
         this.printInheritedSummaryMember(icd, members[0]);

         for(int i = 1; i < members.length; ++i) {
            this.print(", ");
            this.writer.println(' ');
            this.printInheritedSummaryMember(icd, members[i]);
         }

         this.printInheritedSummaryFooter(icd);
      }
   }

   public void printInheritedMembersSummary(ClassDoc cd) {
      if (cd.isClass()) {
         for(ClassDoc icd = cd.superclass(); icd != null; icd = icd.superclass()) {
            this.printInheritedMembersInfo(icd);
         }
      } else {
         ClassDoc[] iin = cd.interfaces();

         for(int i = 0; i < iin.length; ++i) {
            this.printInheritedMembersInfo(iin[i]);
         }

         for(int i = 0; i < iin.length; ++i) {
            this.printInheritedMembersSummary(iin[i]);
         }
      }
   }

   public abstract void printInheritedSummaryAnchor(ClassDoc var1);

   public void printInheritedSummaryFooter(ClassDoc cd) {
      this.writer.printInheritedSummaryFooter(this, cd);
   }

   public void printInheritedSummaryHeader(ClassDoc cd) {
      this.writer.printInheritedSummaryHeader(this, cd);
   }

   public abstract void printInheritedSummaryLabel(ClassDoc var1);

   protected abstract void printInheritedSummaryLink(ClassDoc var1, ProgramElementDoc var2);

   public void printInheritedSummaryMember(ClassDoc cd, ProgramElementDoc member) {
      this.writer.printInheritedSummaryMember(this, cd, member);
   }

   protected abstract void printMember(ProgramElementDoc var1);

   protected void printMemberCommentsFromInterfaces(ProgramElementDoc member) {
   }

   public void printMembers(ClassDoc cd) {
      ProgramElementDoc[] members = this.members(cd);
      if (members.length > 0) {
         this.printHeader(cd);

         for(int i = 0; i < members.length; ++i) {
            if (i > 0) {
               this.writer.printMemberHeader();
            }

            this.writer.println("");
            this.printMember(members[i]);
            this.writer.printMemberFooter();
         }

         this.printBodyHtmlEnd(cd);
      }
   }

   public void printMembersSummary(ClassDoc cd) {
      ProgramElementDoc[] members = this.members(cd);
      if (members.length > 0) {
         Arrays.sort((Object[])members);
         this.printSummaryHeader(cd);

         for(int i = 0; i < members.length; ++i) {
            this.printSummaryMember(cd, members[i]);
         }

         this.printSummaryFooter(cd);
      }
   }

   protected void printModifier(ProgramElementDoc member) {
      if (member.isProtected()) {
         this.print("protected ");
      } else if (member.isPrivate()) {
         this.print("private ");
      } else if (!member.isPublic()) {
         this.writer.printText("doclet.Package_private");
         this.print(" ");
      }

      if (member.isMethod() && ((MethodDoc)member).isAbstract()) {
         this.print("abstract ");
      }

      if (member.isStatic()) {
         this.print("static");
      }

      this.writer.space();
   }

   protected void printModifierAndType(ProgramElementDoc member, Type type) {
      this.writer.printTypeSummaryHeader();
      this.printModifier(member);
      if (type == null) {
         if (member.isOrdinaryClass()) {
            this.print("class");
         } else {
            this.print("interface");
         }
      } else {
         this.printTypeLink(type);
      }

      this.writer.printTypeSummaryFooter();
   }

   protected void printModifiers(MemberDoc member) {
      String mod = this.modifierString(member);
      if (mod.length() > 0) {
         this.print(mod);
         this.print(' ');
      }
   }

   protected abstract void printNavDetailLink(boolean var1);

   protected abstract void printNavSummaryLink(ClassDoc var1, boolean var2);

   protected void printStaticAndType(boolean isStatic, Type type) {
      this.writer.printTypeSummaryHeader();
      if (isStatic) {
         this.print("static");
      }

      this.writer.space();
      if (type != null) {
         this.printTypeLink(type);
      }

      this.writer.printTypeSummaryFooter();
   }

   public abstract void printSummaryAnchor(ClassDoc var1);

   public void printSummaryFooter(ClassDoc cd) {
      this.writer.printSummaryFooter(this, cd);
   }

   public void printSummaryHeader(ClassDoc cd) {
      this.writer.printSummaryHeader(this, cd);
   }

   public abstract void printSummaryLabel(ClassDoc var1);

   protected abstract void printSummaryLink(ClassDoc var1, ProgramElementDoc var2);

   public void printSummaryMember(ClassDoc cd, ProgramElementDoc member) {
      this.writer.printSummaryMember(this, cd, member);
   }

   protected abstract void printSummaryType(ProgramElementDoc var1);

   protected void printTags(ProgramElementDoc member) {
      Tag[] since = member.tags("since");
      Tag[] usages = member.tags("xsl.usage");
      if (usages.length + member.seeTags().length + since.length > 0) {
         this.writer.dd();
         this.writer.dl();
         this.writer.printSeeTags(member);
         this.writer.printSinceTag(member);
         this.writer.printUsageTags(member);
         this.writer.dlEnd();
         this.writer.ddEnd();
      }
   }

   protected void printTypeLink(Type type) {
      this.printTypeLinkNoDimension(type);
      this.print(type.dimension());
   }

   protected void printTypeLinkNoDimension(Type type) {
      ClassDoc cd = type.asClassDoc();
      if (cd == null) {
         this.print(type.typeName());
      } else {
         this.writer.printClassLink(cd);
      }
   }

   protected void printTypedName(Type type, String name) {
      if (type != null) {
         this.printTypeLink(type);
      }

      if (name.length() > 0) {
         this.writer.space();
         this.writer.print(name);
      }
   }

   protected void printUseInfo(Object mems, String heading) {
      if (mems != null) {
         List members = (List)mems;
         if (members.size() > 0) {
            this.writer.tableIndexSummary();
            this.writer.tableUseInfoHeaderStart("#CCCCFF");
            this.writer.print(heading);
            this.writer.tableHeaderEnd();
            Iterator it = members.iterator();

            while(it.hasNext()) {
               this.printSummaryMember(null, (ProgramElementDoc)it.next());
            }

            this.writer.tableEnd();
            this.writer.space();
            this.writer.p();
         }
      }
   }

   protected String typeString(MemberDoc member) {
      String type = "";
      if (member instanceof MethodDoc) {
         type = ((MethodDoc)member).returnType().toString();
      } else if (member instanceof FieldDoc) {
         type = ((FieldDoc)member).type().toString();
      }

      return type;
   }
}
