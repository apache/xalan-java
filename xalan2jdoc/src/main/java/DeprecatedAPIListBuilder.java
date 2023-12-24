package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.RootDoc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeprecatedAPIListBuilder {
   private List deprecatedclasses = new ArrayList();
   private List deprecatedinterfaces = new ArrayList();
   private List deprecatedexceptions = new ArrayList();
   private List deprecatederrors = new ArrayList();
   private List deprecatedfields = new ArrayList();
   private List deprecatedmethods = new ArrayList();
   private List deprecatedconstructors = new ArrayList();

   public DeprecatedAPIListBuilder(RootDoc root) {
      this.buildDeprecatedAPIInfo(root);
   }

   private void buildDeprecatedAPIInfo(RootDoc root) {
      ClassDoc[] classes = root.classes();

      for(int i = 0; i < classes.length; ++i) {
         ClassDoc cd = classes[i];
         if (cd.tags("deprecated").length > 0) {
            if (cd.isOrdinaryClass()) {
               this.deprecatedclasses.add(cd);
            } else if (cd.isInterface()) {
               this.deprecatedinterfaces.add(cd);
            } else if (cd.isException()) {
               this.deprecatedexceptions.add(cd);
            } else {
               this.deprecatederrors.add(cd);
            }
         }

         this.composeDeprecatedList(this.deprecatedfields, cd.fields());
         this.composeDeprecatedList(this.deprecatedmethods, cd.methods());
         this.composeDeprecatedList(this.deprecatedconstructors, cd.constructors());
      }

      this.sortDeprecatedLists();
   }

   private void composeDeprecatedList(List list, MemberDoc[] members) {
      for(int i = 0; i < members.length; ++i) {
         if (members[i].tags("deprecated").length > 0) {
            list.add(members[i]);
         }
      }
   }

   public List getDeprecatedClasses() {
      return this.deprecatedclasses;
   }

   public List getDeprecatedConstructors() {
      return this.deprecatedconstructors;
   }

   public List getDeprecatedErrors() {
      return this.deprecatederrors;
   }

   public List getDeprecatedExceptions() {
      return this.deprecatedexceptions;
   }

   public List getDeprecatedFields() {
      return this.deprecatedfields;
   }

   public List getDeprecatedInterfaces() {
      return this.deprecatedinterfaces;
   }

   public List getDeprecatedMethods() {
      return this.deprecatedmethods;
   }

   private void sortDeprecatedLists() {
      Collections.sort(this.deprecatedclasses);
      Collections.sort(this.deprecatedinterfaces);
      Collections.sort(this.deprecatedexceptions);
      Collections.sort(this.deprecatederrors);
      Collections.sort(this.deprecatedfields);
      Collections.sort(this.deprecatedmethods);
      Collections.sort(this.deprecatedconstructors);
   }
}
