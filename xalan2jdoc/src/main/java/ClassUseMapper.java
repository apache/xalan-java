package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.ClassTree;
import com.sun.tools.doclets.DocletAbortException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ClassUseMapper {
   private final ClassTree classtree;
   public Map classToPackage = new HashMap();
   public Map classToClass = new HashMap();
   public Map classToSubclass = new HashMap();
   public Map classToSubinterface = new HashMap();
   public Map classToImplementingClass = new HashMap();
   public Map classToField = new HashMap();
   public Map classToMethodReturn = new HashMap();
   public Map classToMethodArgs = new HashMap();
   public Map classToMethodThrows = new HashMap();
   public Map classToConstructorArgs = new HashMap();
   public Map classToConstructorThrows = new HashMap();

   private ClassUseMapper(RootDoc root, ClassTree classtree) {
      this.classtree = classtree;
      Iterator it = classtree.baseclasses().iterator();

      while(it.hasNext()) {
         this.subclasses((ClassDoc)it.next());
      }

      Iterator itx = classtree.baseinterfaces().iterator();

      while(itx.hasNext()) {
         this.implementingClasses((ClassDoc)itx.next());
      }

      ClassDoc[] classes = root.classes();

      for(int i = 0; i < classes.length; ++i) {
         ClassDoc cd = classes[i];
         FieldDoc[] fields = cd.fields();

         for(int j = 0; j < fields.length; ++j) {
            FieldDoc fd = fields[j];
            ClassDoc tcd = fd.type().asClassDoc();
            if (tcd != null) {
               this.add(this.classToField, tcd, fd);
            }
         }

         ConstructorDoc[] cons = cd.constructors();

         for(int j = 0; j < cons.length; ++j) {
            this.mapExecutable(cons[j]);
         }

         MethodDoc[] meths = cd.methods();

         for(int j = 0; j < meths.length; ++j) {
            MethodDoc md = meths[j];
            this.mapExecutable(md);
            ClassDoc tcd = md.returnType().asClassDoc();
            if (tcd != null) {
               this.add(this.classToMethodReturn, tcd, md);
            }
         }
      }
   }

   private void add(Map map, ClassDoc cd, ProgramElementDoc ref) {
      this.refList(map, cd).add(ref);
      this.packageSet(cd).add(ref.containingPackage());
      this.classSet(cd).add(ref instanceof MemberDoc ? ((MemberDoc)ref).containingClass() : ref);
   }

   private void addAll(Map map, ClassDoc cd, Collection refs) {
      if (refs != null) {
         this.refList(map, cd).addAll(refs);
         Set pkgSet = this.packageSet(cd);
         Set clsSet = this.classSet(cd);

         for(ProgramElementDoc pedoc : refs) {
            pkgSet.add(pedoc.containingPackage());
            clsSet.add(pedoc instanceof MemberDoc ? ((MemberDoc)pedoc).containingClass() : pedoc);
         }
      }
   }

   private Set classSet(ClassDoc cd) {
      Set clsSet = (Set)this.classToClass.get(cd);
      if (clsSet == null) {
         clsSet = new TreeSet();
         this.classToClass.put(cd, clsSet);
      }

      return clsSet;
   }

   public static void generate(RootDoc root, ClassTree classtree) throws DocletAbortException {
      ClassUseMapper mapper = new ClassUseMapper(root, classtree);
      ClassDoc[] classes = root.classes();

      for(int i = 0; i < classes.length; ++i) {
         ClassUseWriter.generate(mapper, classes[i]);
      }

      PackageDoc[] pkgs = Standard.configuration().packages;

      for(int i = 0; i < pkgs.length; ++i) {
         PackageUseWriter.generate(mapper, pkgs[i]);
      }
   }

   private Collection implementingClasses(ClassDoc cd) {
      Collection ret = (List)this.classToImplementingClass.get(cd);
      if (ret == null) {
         ret = new TreeSet();
         List impl = this.classtree.implementingclasses(cd);
         if (impl != null) {
            ret.addAll(impl);
            Iterator it = impl.iterator();

            while(it.hasNext()) {
               ret.addAll(this.subclasses((ClassDoc)it.next()));
            }
         }

         Iterator it = this.subinterfaces(cd).iterator();

         while(it.hasNext()) {
            ret.addAll(this.implementingClasses((ClassDoc)it.next()));
         }

         this.addAll(this.classToImplementingClass, cd, ret);
      }

      return ret;
   }

   private void mapExecutable(ExecutableMemberDoc em) {
      Parameter[] params = em.parameters();
      boolean isConstructor = em.isConstructor();
      List classArgs = new ArrayList();

      for(int k = 0; k < params.length; ++k) {
         ClassDoc pcd = params[k].type().asClassDoc();
         if (pcd != null && !classArgs.contains(pcd)) {
            this.add(isConstructor ? this.classToConstructorArgs : this.classToMethodArgs, pcd, em);
            classArgs.add(pcd);
         }
      }

      ClassDoc[] thr = em.thrownExceptions();

      for(int k = 0; k < thr.length; ++k) {
         this.add(isConstructor ? this.classToConstructorThrows : this.classToMethodThrows, thr[k], em);
      }
   }

   private Set packageSet(ClassDoc cd) {
      Set pkgSet = (Set)this.classToPackage.get(cd);
      if (pkgSet == null) {
         pkgSet = new TreeSet();
         this.classToPackage.put(cd, pkgSet);
      }

      return pkgSet;
   }

   private List refList(Map map, ClassDoc cd) {
      List list = (List)map.get(cd);
      if (list == null) {
         list = new ArrayList();
         map.put(cd, list);
      }

      return list;
   }

   private Collection subclasses(ClassDoc cd) {
      Collection ret = (Collection)this.classToSubclass.get(cd);
      if (ret == null) {
         ret = new TreeSet();
         List subs = this.classtree.subclasses(cd);
         if (subs != null) {
            ret.addAll(subs);
            Iterator it = subs.iterator();

            while(it.hasNext()) {
               ret.addAll(this.subclasses((ClassDoc)it.next()));
            }
         }

         this.addAll(this.classToSubclass, cd, ret);
      }

      return ret;
   }

   private Collection subinterfaces(ClassDoc cd) {
      Collection ret = (Collection)this.classToSubinterface.get(cd);
      if (ret == null) {
         ret = new TreeSet();
         List subs = this.classtree.subinterfaces(cd);
         if (subs != null) {
            ret.addAll(subs);
            Iterator it = subs.iterator();

            while(it.hasNext()) {
               ret.addAll(this.subinterfaces((ClassDoc)it.next()));
            }
         }

         this.addAll(this.classToSubinterface, cd, ret);
      }

      return ret;
   }
}
