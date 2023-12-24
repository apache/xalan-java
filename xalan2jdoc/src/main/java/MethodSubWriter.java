package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodSubWriter extends ExecutableMemberSubWriter {
   MethodSubWriter(SubWriterHolderWriter writer) {
      super(writer);
   }

   protected void composeInheritedMethodMap(ClassDoc icd, List tcms, Map imsmap, List hierarchy) {
      MethodDoc[] methods = icd.methods();
      List methodlist = new ArrayList();

      for(int i = 0; i < methods.length; ++i) {
         if (!this.contains(tcms, methods[i])) {
            methodlist.add(methods[i]);
            tcms.add(methods[i]);
         }
      }

      imsmap.put(icd, methodlist);
      hierarchy.add(icd);
   }

   protected boolean contains(List tcmethods, MethodDoc method) {
      for(int i = 0; i < tcmethods.size(); ++i) {
         MethodDoc tcmethod = (MethodDoc)tcmethods.get(i);
         if (tcmethod.name().equals(method.name()) && tcmethod.signature().equals(method.signature())) {
            return true;
         }
      }

      return false;
   }

   protected Map getInheritedMethodMapForClass(ClassDoc cd, List tcms, Map imsmap, List hierarchy) {
      for(ClassDoc icd = cd.superclass(); icd != null; icd = icd.superclass()) {
         this.composeInheritedMethodMap(icd, tcms, imsmap, hierarchy);
      }

      return imsmap;
   }

   protected Map getInheritedMethodMapForInterface(ClassDoc cd, List tims, Map imsmap, List hierarchy) {
      ClassDoc[] iin = cd.interfaces();

      for(int i = 0; i < iin.length; ++i) {
         this.composeInheritedMethodMap(iin[i], tims, imsmap, hierarchy);
      }

      for(int i = 0; i < iin.length; ++i) {
         this.getInheritedMethodMapForInterface(iin[i], tims, imsmap, hierarchy);
      }

      return imsmap;
   }

   public ProgramElementDoc[] members(ClassDoc cd) {
      return this.eligibleMembers(cd.methods());
   }

   protected void printHeader(ClassDoc cd) {
      super.writer.anchor("method_detail");
      super.writer.printTableHeadingBackground(super.writer.getText("doclet.Method_Detail"));
   }

   protected void printInheritedMembersInfo(ClassDoc icd, List members) {
      if (members.size() > 0) {
         Collections.sort(members);
         this.printInheritedSummaryHeader(icd);
         this.printInheritedSummaryMember(icd, (ProgramElementDoc)members.get(0));

         for(int i = 1; i < members.size(); ++i) {
            super.writer.println(", ");
            this.printInheritedSummaryMember(icd, (ProgramElementDoc)members.get(i));
         }

         this.printInheritedSummaryFooter(icd);
      }
   }

   public void printInheritedMembersSummary(ClassDoc cd) {
      List tcms = Group.asList(cd.methods());
      Map imsmap = new HashMap();
      List hierarchy = new ArrayList();
      if (cd.isClass()) {
         imsmap = this.getInheritedMethodMapForClass(cd, tcms, imsmap, hierarchy);
      } else {
         imsmap = this.getInheritedMethodMapForInterface(cd, tcms, imsmap, hierarchy);
      }

      if (hierarchy.size() > 0) {
         for(int i = 0; i < hierarchy.size(); ++i) {
            ClassDoc classkey = (ClassDoc)hierarchy.get(i);
            List methodlist = (List)imsmap.get(classkey);
            this.printInheritedMembersInfo(classkey, methodlist);
         }
      }
   }

   public void printInheritedSummaryAnchor(ClassDoc cd) {
      super.writer.anchor("methods_inherited_from_class_" + cd.qualifiedName());
   }

   public void printInheritedSummaryLabel(ClassDoc cd) {
      String classlink = super.writer.getPreQualifiedClassLink(cd);
      super.writer.bold();
      if (cd.isClass()) {
         super.writer.printText("doclet.Methods_Inherited_From_Class", classlink);
      } else {
         super.writer.printText("doclet.Methods_Inherited_From_Interface", classlink);
      }

      super.writer.boldEnd();
   }

   protected void printNavDetailLink(boolean link) {
      if (link) {
         super.writer.printHyperLink("", "method_detail", super.writer.getText("doclet.navMethod"));
      } else {
         super.writer.printText("doclet.navMethod");
      }
   }

   protected void printNavSummaryLink(ClassDoc cd, boolean link) {
      if (link) {
         super.writer
            .printHyperLink("", cd == null ? "method_summary" : "methods_inherited_from_class_" + cd.qualifiedName(), super.writer.getText("doclet.navMethod"));
      } else {
         super.writer.printText("doclet.navMethod");
      }
   }

   protected void printOverridden(ClassDoc overridden, MethodDoc method) {
      if (overridden != null) {
         String overriddenclasslink = super.writer.getClassLink(overridden);
         String methlink = "";
         String name = method.name();
         super.writer.dt();
         super.writer.boldText("doclet.Overrides");
         super.writer.dd();
         methlink = super.writer.getClassLink(overridden, name + method.signature(), name, false);
         super.writer.printText("doclet.in_class", methlink, overriddenclasslink);
      }
   }

   protected void printReturnTag(Tag[] returns) {
      if (returns.length > 0) {
         super.writer.dt();
         super.writer.boldText("doclet.Returns");
         super.writer.dd();
         super.writer.printInlineComment(returns[0]);
      }
   }

   protected void printReturnType(MethodDoc method) {
      Type type = method.returnType();
      if (type != null) {
         this.printTypeLink(type);
         this.print(' ');
      }
   }

   protected void printSignature(ExecutableMemberDoc member) {
      super.writer.displayLength = 0;
      super.writer.pre();
      this.printModifiers(member);
      this.printReturnType((MethodDoc)member);
      this.bold(member.name());
      this.printParameters(member);
      this.printExceptions(member);
      super.writer.preEnd();
   }

   public void printSummaryAnchor(ClassDoc cd) {
      super.writer.anchor("method_summary");
   }

   public void printSummaryLabel(ClassDoc cd) {
      super.writer.boldText("doclet.Method_Summary");
   }

   protected void printSummaryType(ProgramElementDoc member) {
      MethodDoc meth = (MethodDoc)member;
      this.printModifierAndType(meth, meth.returnType());
   }

   protected void printTags(ProgramElementDoc member) {
      MethodDoc method = (MethodDoc)member;
      ParamTag[] params = method.paramTags();
      Tag[] returns = method.tags("return");
      Tag[] sinces = method.tags("since");
      ThrowsTag[] thrown = method.throwsTags();
      SeeTag[] sees = method.seeTags();
      ClassDoc[] intfacs = member.containingClass().interfaces();
      ClassDoc overridden = method.overriddenClass();
      Tag[] usages = member.tags("xsl.usage");
      if (usages.length + params.length + returns.length + thrown.length + sinces.length + intfacs.length + sees.length > 0 || overridden != null) {
         super.writer.dd();
         super.writer.dl();
         this.printImplementsInfo(method);
         this.printParamTags(params);
         this.printReturnTag(returns);
         this.printThrowsTags(thrown);
         this.printOverridden(overridden, method);
         super.writer.printSinceTag(method);
         super.writer.printSeeTags(method);
         super.writer.printUsageTags(method);
         super.writer.dlEnd();
         super.writer.ddEnd();
      }
   }
}
