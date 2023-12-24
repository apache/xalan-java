package xalanjdoc;

import com.sun.javadoc.DocErrorReporter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Extern {
   private static Map packageMap;
   final String packageName;
   final String path;
   final boolean relative;

   Extern(String packageName, String path, boolean relative) {
      this.packageName = packageName;
      this.path = path;
      this.relative = relative;
      if (packageMap == null) {
         packageMap = new HashMap();
      }

      packageMap.put(packageName, this);
   }

   static String adjustEndFileSeparator(String url) {
      String filesep = isRelativePath(url) ? File.separator : "/";
      if (!url.endsWith(filesep)) {
         url = url + filesep;
      }

      return url;
   }

   static String composeExternPackageList(String url, String pkglisturl) {
      url = adjustEndFileSeparator(url);
      pkglisturl = adjustEndFileSeparator(pkglisturl);
      return !pkglisturl.startsWith("http://") && !pkglisturl.startsWith("file:")
         ? readFileComposeExternPackageList(url, pkglisturl)
         : fetchURLComposeExternPackageList(url, pkglisturl);
   }

   static String fetchURLComposeExternPackageList(String urlpath, String pkglisturlpath) {
      String link = pkglisturlpath + "package-list";

      try {
         boolean relative = isRelativePath(urlpath);
         readPackageList(new URL(link).openStream(), urlpath, relative);
         return null;
      } catch (MalformedURLException var4) {
         return getText("doclet.MalformedURL", link);
      } catch (IOException var5) {
         return getText("doclet.URL_error", link);
      }
   }

   public static Extern findPackage(String pkgName) {
      return packageMap == null ? null : (Extern)packageMap.get(pkgName);
   }

   private static String getText(String msg) {
      Standard.configuration();
      return ConfigurationStandard.standardmessage.getText(msg);
   }

   private static String getText(String prop, String link) {
      Standard.configuration();
      return ConfigurationStandard.standardmessage.getText(prop, link);
   }

   static boolean isRelativePath(String url) {
      return (url.startsWith("http://") || url.startsWith("file:")) ^ true;
   }

   static String readFileComposeExternPackageList(String urlpath, String relpath) {
      String link = relpath + "package-list";

      try {
         File file = new File(link);
         if (file.exists() && file.canRead()) {
            boolean relative = isRelativePath(urlpath);
            readPackageList(new FileInputStream(file), urlpath, relative);
            return null;
         } else {
            return getText("doclet.File_error", link);
         }
      } catch (FileNotFoundException var5) {
         return getText("doclet.File_error", link);
      } catch (IOException var6) {
         return getText("doclet.File_error", link);
      }
   }

   static void readPackageList(InputStream input, String path, boolean relative) throws IOException {
      InputStreamReader in = new InputStreamReader(input);
      StringBuffer strbuf = new StringBuffer();

      int c;
      try {
         while((c = in.read()) >= 0) {
            char ch = (char)c;
            if (ch != '\n' && ch != '\r') {
               strbuf.append(ch);
            } else if (strbuf.length() > 0) {
               String packname = strbuf.toString();
               String packpath = path + packname.replace('.', '/') + '/';
               new Extern(packname, packpath, relative);
               strbuf.setLength(0);
            }
         }
      } finally {
         input.close();
      }
   }

   public String toString() {
      return this.packageName + (this.relative ? " -> " : " => ") + this.path;
   }

   public static boolean url(String url, String pkglisturl, DocErrorReporter reporter) {
      if (packageMap != null) {
         reporter.printError(getText("doclet.link_option_twice"));
         return false;
      } else {
         String errMsg = composeExternPackageList(url, pkglisturl);
         if (errMsg != null) {
            reporter.printError(errMsg);
            return false;
         } else {
            return true;
         }
      }
   }
}
