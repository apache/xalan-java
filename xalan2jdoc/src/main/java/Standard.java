package xalanjdoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.ClassTree;
import com.sun.tools.doclets.DocletAbortException;
import com.sun.tools.doclets.HtmlDocWriter;
import com.sun.tools.doclets.IndexBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Standard {
   protected String classFileName(ClassDoc cd) {
      return cd.qualifiedName() + ".html";
   }

   public static ConfigurationStandard configuration() {
      if (HtmlDocWriter.configuration == null) {
         HtmlDocWriter.configuration = new ConfigurationStandard();
      }

      return (ConfigurationStandard)HtmlDocWriter.configuration;
   }

   public static void copyFile(File destfile, File srcfile) throws DocletAbortException, IOException {
      byte[] bytearr = new byte[512];
      int len = 0;
      FileInputStream input = new FileInputStream(srcfile);
      FileOutputStream output = new FileOutputStream(destfile);

      try {
         while((len = input.read(bytearr)) != -1) {
            output.write(bytearr, 0, len);
         }
      } catch (FileNotFoundException var11) {
         throw new DocletAbortException();
      } catch (SecurityException var12) {
         throw new DocletAbortException();
      } finally {
         input.close();
         output.close();
      }
   }

   protected void generateClassCycle(ClassDoc[] arr, ClassTree classtree, boolean nopackage) throws DocletAbortException {
      Arrays.sort((Object[])arr);

      for(int i = 0; i < arr.length; ++i) {
         if (!configuration().nodeprecated || arr[i].tags("deprecated").length <= 0) {
            ClassDoc prev = i == 0 ? null : arr[i - 1];
            ClassDoc curr = arr[i];
            ClassDoc next = i + 1 == arr.length ? null : arr[i + 1];
            ClassWriter.generate(curr, prev, next, classtree, nopackage);
         }
      }
   }

   protected void generateClassFiles(RootDoc root, ClassTree classtree) throws DocletAbortException {
      ClassDoc[] classes = root.specifiedClasses();
      List incl = new ArrayList();

      for(int i = 0; i < classes.length; ++i) {
         ClassDoc cd = classes[i];
         if (cd.isIncluded()) {
            incl.add(cd);
         }
      }

      ClassDoc[] inclClasses = new ClassDoc[incl.size()];

      for(int i = 0; i < inclClasses.length; ++i) {
         inclClasses[i] = (ClassDoc)incl.get(i);
      }

      this.generateClassCycle(inclClasses, classtree, true);
      PackageDoc[] packages = configuration().packages;

      for(int i = 0; i < packages.length; ++i) {
         PackageDoc pkg = packages[i];
         this.generateClassCycle(pkg.interfaces(), classtree, false);
         this.generateClassCycle(pkg.ordinaryClasses(), classtree, false);
         this.generateClassCycle(pkg.exceptions(), classtree, false);
         this.generateClassCycle(pkg.errors(), classtree, false);
      }
   }

   public static int optionLength(String option) {
      return configuration().optionLength(option);
   }

   protected void performCopy(String configdestdir, String filename) throws DocletAbortException {
      try {
         String destdir = configdestdir.length() > 0 ? configdestdir + File.separatorChar : "";
         if (filename.length() > 0) {
            File helpstylefile = new File(filename);
            String parent = helpstylefile.getParent();
            String helpstylefilename = parent == null ? filename : filename.substring(parent.length() + 1);
            File desthelpfile = new File(destdir + helpstylefilename);
            if (!desthelpfile.getCanonicalPath().equals(helpstylefile.getCanonicalPath())) {
               configuration();
               ConfigurationStandard.standardmessage.notice("doclet.Copying_File_0_To_File_1", helpstylefile.toString(), desthelpfile.toString());
               copyFile(desthelpfile, helpstylefile);
            }
         }
      } catch (IOException var8) {
         configuration();
         ConfigurationStandard.standardmessage.error("doclet.perform_copy_exception_encountered", var8.toString());
         throw new DocletAbortException();
      }
   }

   public static boolean start(RootDoc root) throws IOException {
      try {
         configuration().setOptions(root);
         new Standard().startGeneration(root);
         return true;
      } catch (DocletAbortException var2) {
         var2.printStackTrace();
         return false;
      }
   }

   protected void startGeneration(RootDoc root) throws DocletAbortException {
      if (root.classes().length == 0) {
         configuration();
         ConfigurationStandard.standardmessage.notice("doclet.No_Public_Classes_To_Document");
      } else {
         String configdestdir = configuration().destdirname;
         String confighelpfile = configuration().helpfile;
         String configstylefile = configuration().stylesheetfile;
         boolean nodeprecated = configuration().nodeprecated;
         this.performCopy(configdestdir, confighelpfile);
         this.performCopy(configdestdir, configstylefile);
         ClassTree classtree = new ClassTree(root, nodeprecated);
         if (configuration().classuse) {
            ClassUseMapper.generate(root, classtree);
         }

         IndexBuilder indexbuilder = new IndexBuilder(root, nodeprecated);
         PackageDoc[] packages = configuration().packages;
         if (configuration().createtree) {
            TreeWriter.generate(classtree);
         }

         if (configuration().createindex) {
            if (configuration().splitindex) {
               SplitIndexWriter.generate(indexbuilder);
            } else {
               SingleIndexWriter.generate(indexbuilder);
            }
         }

         if (!configuration().nodeprecatedlist && !nodeprecated) {
            DeprecatedListWriter.generate(root);
         }

         AllClassesFrameWriter.generate(new IndexBuilder(root, nodeprecated, true));
         FrameOutputWriter.generate();
         PackagesFileWriter.generate();
         if (configuration().createoverview) {
            PackageIndexWriter.generate(root);
         }

         if (packages.length > 1) {
            PackageIndexFrameWriter.generate();
         }

         for(int i = 0; i < packages.length; ++i) {
            PackageDoc prev = i == 0 ? null : packages[i - 1];
            PackageDoc packagedoc = packages[i];
            PackageDoc next = i + 1 == packages.length ? null : packages[i + 1];
            PackageWriter.generate(packages[i], prev, next);
            PackageTreeWriter.generate(packages[i], prev, next, nodeprecated);
            PackageFrameWriter.generate(packages[i]);
         }

         this.generateClassFiles(root, classtree);
         SerializedFormWriter.generate(root);
         PackageListWriter.generate(root);
         HelpWriter.generate();
         StylesheetWriter.generate();
      }
   }

   public static boolean validOptions(String[][] options, DocErrorReporter reporter) throws IOException {
      return configuration().validOptions(options, reporter);
   }
}
