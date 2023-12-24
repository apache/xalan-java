package xalanjdoc;

import com.sun.javadoc.PackageDoc;
import java.io.IOException;
import java.util.Arrays;

public abstract class AbstractPackageIndexWriter extends HtmlStandardWriter {
   protected PackageDoc[] packages;

   public AbstractPackageIndexWriter(String filename) throws IOException {
      super(filename);
      this.packages = Standard.configuration().packages;
   }

   protected void generateIndex() {
      this.printIndexContents(this.packages, "doclet.Package_Summary");
   }

   protected void generatePackageIndexFile() throws IOException {
      this.printHeader(this.getText("doclet.Window_Overview", Standard.configuration().windowtitle));
      this.printNavigationBarHeader();
      this.printOverviewHeader();
      this.generateIndex();
      this.printOverview();
      this.printNavigationBarFooter();
      this.printBodyHtmlEnd();
   }

   protected void navLinkContents() {
      this.navCellRevStart();
      this.fontStyle("NavBarFont1Rev");
      this.boldText("doclet.Overview");
      this.fontEnd();
      this.navCellEnd();
   }

   protected void printAllClassesPackagesLink() {
   }

   protected void printConfigurationTitle() {
      if (Standard.configuration().doctitle.length() > 0) {
         this.center();
         this.h2();
         this.print(Standard.configuration().doctitle);
         this.h2End();
         this.centerEnd();
      }
   }

   protected void printIndexContents(PackageDoc[] packages, String text) {
      if (packages.length > 0) {
         Arrays.sort((Object[])packages);
         this.printIndexHeader(text);
         this.printAllClassesPackagesLink();

         for(int i = 0; i < packages.length; ++i) {
            PackageDoc packagedoc = packages[i];
            this.printIndexRow(packagedoc);
         }

         this.printIndexFooter();
      }
   }

   protected abstract void printIndexFooter();

   protected abstract void printIndexHeader(String var1);

   protected abstract void printIndexRow(PackageDoc var1);

   protected abstract void printNavigationBarFooter();

   protected abstract void printNavigationBarHeader();

   protected void printOverview() throws IOException {
   }

   protected abstract void printOverviewHeader();
}
