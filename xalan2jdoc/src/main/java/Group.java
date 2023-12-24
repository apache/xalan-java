package xalanjdoc;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.PackageDoc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Group {
   private static Map regExpGroupMap = new HashMap();
   private static List sortedRegExpList = new ArrayList();
   private static List groupList = new ArrayList();
   private static Map pkgNameGroupMap = new HashMap();

   static List asList(Object[] arr) {
      List list = new ArrayList();

      for(int i = 0; i < arr.length; ++i) {
         list.add(arr[i]);
      }

      return list;
   }

   public static boolean checkPackageGroups(String groupname, String pkgNameFormList, DocErrorReporter reporter) {
      StringTokenizer strtok = new StringTokenizer(pkgNameFormList, ":");
      if (groupList.contains(groupname)) {
         reporter.printError(getText("doclet.Groupname_already_used", groupname));
         return false;
      } else {
         groupList.add(groupname);

         while(strtok.hasMoreTokens()) {
            String id = strtok.nextToken();
            if (id.length() == 0) {
               reporter.printError(getText("doclet.Error_in_packagelist", groupname, pkgNameFormList));
               return false;
            }

            if (id.endsWith("*")) {
               id = id.substring(0, id.length() - 1);
               if (foundGroupFormat(regExpGroupMap, id, reporter)) {
                  return false;
               }

               regExpGroupMap.put(id, groupname);
               sortedRegExpList.add(id);
            } else {
               if (foundGroupFormat(pkgNameGroupMap, id, reporter)) {
                  return false;
               }

               pkgNameGroupMap.put(id, groupname);
            }
         }

         Collections.sort(sortedRegExpList, new Group.MapKeyComparator());
         return true;
      }
   }

   static boolean foundGroupFormat(Map map, String pkgFormat, DocErrorReporter reporter) {
      if (map.containsKey(pkgFormat)) {
         reporter.printError(getText("doclet.Same_package_name_used", pkgFormat));
         return true;
      } else {
         return false;
      }
   }

   public static List getGroupList() {
      return groupList;
   }

   static List getPkgList(Map map, String groupname) {
      List list = (List)map.get(groupname);
      if (list == null) {
         list = new ArrayList();
         map.put(groupname, list);
      }

      return list;
   }

   private static String getText(String text) {
      Standard.configuration();
      return ConfigurationStandard.standardmessage.getText(text);
   }

   private static String getText(String text, String arg) {
      Standard.configuration();
      return ConfigurationStandard.standardmessage.getText(text, arg);
   }

   private static String getText(String text, String arg1, String arg2) {
      Standard.configuration();
      return ConfigurationStandard.standardmessage.getText(text, arg1, arg2);
   }

   public static Map groupPackages(PackageDoc[] packages) {
      Map groupPackageMap = new HashMap();
      String defaultGroupName = pkgNameGroupMap.isEmpty() && regExpGroupMap.isEmpty() ? getText("doclet.Packages") : getText("doclet.Other_Packages");
      if (!groupList.contains(defaultGroupName)) {
         groupList.add(defaultGroupName);
      }

      for(int i = 0; i < packages.length; ++i) {
         PackageDoc pkg = packages[i];
         String pkgName = pkg.name();
         String groupName = (String)pkgNameGroupMap.get(pkgName);
         if (groupName == null) {
            groupName = regExpGroupName(pkgName);
         }

         if (groupName == null) {
            groupName = defaultGroupName;
         }

         getPkgList(groupPackageMap, groupName).add(pkg);
      }

      return groupPackageMap;
   }

   static String regExpGroupName(String pkgName) {
      for(int j = 0; j < sortedRegExpList.size(); ++j) {
         String regexp = (String)sortedRegExpList.get(j);
         if (pkgName.startsWith(regexp)) {
            return (String)regExpGroupMap.get(regexp);
         }
      }

      return null;
   }

   private static class MapKeyComparator implements Comparator {
      MapKeyComparator() {
      }

      public int compare(Object key1, Object key2) {
         return ((String)key2).length() - ((String)key1).length();
      }
   }
}
