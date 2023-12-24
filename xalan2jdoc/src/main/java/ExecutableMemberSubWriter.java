package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;

public abstract class ExecutableMemberSubWriter extends AbstractSubWriter {
   ExecutableMemberSubWriter(SubWriterHolderWriter writer) {
      super(writer);
   }

   protected MethodDoc findMethod(ClassDoc cd, MethodDoc method) {
      MethodDoc[] methods = cd.methods();

      for(int i = 0; i < methods.length; ++i) {
         if (method.name().equals(methods[i].name()) && method.signature().equals(methods[i].signature())) {
            return methods[i];
         }
      }

      return null;
   }

   protected int getReturnTypeLength(ExecutableMemberDoc member) {
      if (member instanceof MethodDoc) {
         MethodDoc method = (MethodDoc)member;
         Type rettype = method.returnType();
         ClassDoc cd = rettype.asClassDoc();
         return cd == null
            ? rettype.typeName().length() + rettype.dimension().length()
            : rettype.dimension().length() + (super.writer.isCrossClassIncluded(cd) ? cd.name().length() : cd.qualifiedName().length());
      } else {
         return -1;
      }
   }

   protected MethodDoc implementedMethod(MethodDoc method, ClassDoc[] intfacs) {
      for(int i = 0; i < intfacs.length; ++i) {
         MethodDoc found = this.findMethod(intfacs[i], method);
         if (found != null) {
            return found;
         }
      }

      return null;
   }

   protected ClassDoc implementsMethodInIntfac(MethodDoc method, ClassDoc[] intfacs) {
      for(int i = 0; i < intfacs.length; ++i) {
         MethodDoc[] methods = intfacs[i].methods();
         if (methods.length > 0) {
            for(int j = 0; j < methods.length; ++j) {
               if (methods[j].name().equals(method.name()) && methods[j].signature().equals(method.signature())) {
                  return intfacs[i];
               }
            }
         }
      }

      return null;
   }

   protected String name(ProgramElementDoc member) {
      return member.name() + "()";
   }

   protected void printBodyHtmlEnd(ClassDoc cd) {
   }

   protected void printDeprecatedLink(ProgramElementDoc member) {
      ExecutableMemberDoc emd = (ExecutableMemberDoc)member;
      super.writer.printClassLink(emd.containingClass(), emd.name() + emd.signature(), emd.qualifiedName() + emd.flatSignature());
   }

   protected void printExceptions(ExecutableMemberDoc member) {
      ClassDoc[] except = member.thrownExceptions();
      if (except.length > 0) {
         int retlen = this.getReturnTypeLength(member);
         String indent = this.makeSpace(this.modifierString(member).length() + member.name().length() + retlen - 4);
         super.writer.print('\n');
         super.writer.print(indent);
         super.writer.print("throws ");
         indent = indent + "       ";
         super.writer.printClassLink(except[0]);

         for(int i = 1; i < except.length; ++i) {
            super.writer.print(",\n");
            super.writer.print(indent);
            super.writer.printClassLink(except[i]);
         }
      }
   }

   protected void printImplementsInfo(MethodDoc method) {
      ClassDoc[] implIntfacs = method.containingClass().interfaces();
      if (implIntfacs.length > 0) {
         MethodDoc implementedMeth = this.implementedMethod(method, implIntfacs);
         if (implementedMeth != null) {
            ClassDoc intfac = implementedMeth.containingClass();
            String methlink = "";
            String intfaclink = super.writer.getClassLink(intfac);
            super.writer.dt();
            super.writer.boldText("doclet.Specified_By");
            super.writer.dd();
            methlink = super.writer.getDocLink(implementedMeth, implementedMeth.name());
            super.writer.printText("doclet.in_interface", methlink, intfaclink);
         }
      }
   }

   protected void printInheritedSummaryLink(ClassDoc cd, ProgramElementDoc member) {
      ExecutableMemberDoc emd = (ExecutableMemberDoc)member;
      String name = emd.name();
      super.writer.printClassLink(cd, name + emd.signature(), name, false);
   }

   protected void printMember(ProgramElementDoc member) {
      ExecutableMemberDoc emd = (ExecutableMemberDoc)member;
      String name = emd.name();
      super.writer.anchor(name + emd.signature());
      this.printHead(emd);
      this.printSignature(emd);
      this.printFullComment(emd);
   }

   protected void printParam(Parameter param) {
      this.printTypedName(param.type(), param.name());
   }

   protected void printParamTags(ParamTag[] params) {
      if (params.length > 0) {
         super.writer.dt();
         super.writer.boldText("doclet.Parameters");

         for(int i = 0; i < params.length; ++i) {
            ParamTag pt = params[i];
            super.writer.dd();
            super.writer.code();
            this.print(pt.parameterName());
            super.writer.codeEnd();
            this.print(" - ");
            super.writer.printInlineComment(pt);
         }
      }
   }

   protected void printParameters(ExecutableMemberDoc member) {
      int paramstart = 0;
      this.print('(');
      Parameter[] params = member.parameters();
      String indent = this.makeSpace(super.writer.displayLength);

      while(paramstart < params.length) {
         Parameter param = params[paramstart++];
         if (!param.name().startsWith("this$")) {
            this.printParam(param);
            break;
         }
      }

      for(int i = paramstart; i < params.length; ++i) {
         super.writer.print(',');
         super.writer.print('\n');
         super.writer.print(indent);
         this.printParam(params[i]);
      }

      super.writer.print(')');
   }

   protected void printSignature(ExecutableMemberDoc member) {
      super.writer.displayLength = 0;
      super.writer.pre();
      this.printModifiers(member);
      this.bold(member.name());
      this.printParameters(member);
      this.printExceptions(member);
      super.writer.preEnd();
   }

   protected void printSummaryLink(ClassDoc cd, ProgramElementDoc member) {
      ExecutableMemberDoc emd = (ExecutableMemberDoc)member;
      ClassDoc mcd = member.containingClass();
      String name = emd.name();
      super.writer.bold();
      if (mcd != cd) {
         super.writer.print(mcd.name() + ".");
      }

      super.writer.printClassLink(mcd, name + emd.signature(), name, false);
      super.writer.boldEnd();
      super.writer.displayLength = name.length();
      this.printParameters(emd);
   }

   protected void printThrowsTags(ThrowsTag[] thrown) {
      if (thrown.length > 0) {
         super.writer.dt();
         super.writer.boldText("doclet.Throws");

         for(int i = 0; i < thrown.length; ++i) {
            ThrowsTag tt = thrown[i];
            super.writer.dd();
            ClassDoc cd = tt.exception();
            if (cd == null) {
               super.writer.print(tt.exceptionName());
            } else {
               super.writer.printClassLink(cd);
            }

            this.print(" - ");
            super.writer.printInlineComment(tt);
         }
      }
   }
}
