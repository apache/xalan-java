package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;

public class SerialMethodSubWriter extends MethodSubWriter {
   SerialMethodSubWriter(SubWriterHolderWriter writer) {
      super(writer);
   }

   public ProgramElementDoc[] members(ClassDoc cd) {
      return this.eligibleMembers(cd.serializationMethods());
   }

   protected void printHeader(ClassDoc cd) {
      super.writer.anchor("serialized_methods");
      super.writer.printTableHeadingBackground(super.writer.getText("doclet.Serialized_Form_methods"));
      super.writer.p();
      if (cd.isSerializable() && !cd.isExternalizable() && this.members(cd).length == 0) {
         String msg = super.writer.getText("doclet.Serializable_no_customization");
         super.writer.print(msg);
         super.writer.p();
      }
   }

   protected void printMember(ClassDoc cd, ProgramElementDoc member) {
      ExecutableMemberDoc emd = (ExecutableMemberDoc)member;
      String name = emd.name();
      this.printHead(emd);
      this.printFullComment(emd);
   }

   public void printMembers(ClassDoc cd) {
      if (this.members(cd).length > 0) {
         super.printMembers(cd);
      }
   }

   protected void printSerialDataTag(Tag[] serialData) {
      if (serialData != null && serialData.length > 0) {
         super.writer.dt();
         super.writer.boldText("doclet.SerialData");
         super.writer.dd();

         for(int i = 0; i < serialData.length; ++i) {
            super.writer.print(serialData[i].text());
         }
      }
   }

   protected void printTags(ProgramElementDoc member) {
      MethodDoc method = (MethodDoc)member;
      Tag[] serialData = method.tags("serialData");
      Tag[] sinces = method.tags("since");
      Tag[] usages = member.tags("xsl.usage");
      SeeTag[] sees = method.seeTags();
      if (serialData.length + sees.length + sinces.length > 0) {
         super.writer.dd();
         super.writer.dl();
         this.printSerialDataTag(serialData);
         super.writer.printSinceTag(method);
         super.writer.printSeeTags(method);
         super.writer.printUsageTags(method);
         super.writer.dlEnd();
         super.writer.ddEnd();
      } else if (method.name().compareTo("writeExternal") == 0) {
         Standard.configuration();
         ConfigurationStandard.standardmessage.warning("doclet.MissingSerialDataTag", method.containingClass().qualifiedName(), method.name());
      }
   }
}
