/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.xslt;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Utility class to report simple information about the environment.
 * Simplistic reporting about certain classes found in your JVM may 
 * help answer some FAQs for simple problems.
 *
 * Usage-command line: 
 * <code>java org.apache.xalan.xslt.EnvironmentCheck [-out outFile]</code>
 * Usage-from program: 
 * <code>
 * boolean environmentOK = 
 * (new EnvironmentCheck()).checkEnvironment(yourPrintWriter);
 * </code>
 *
 * Xalan users reporting problems are encouraged to use this class 
 * to see if there are potential problems with their actual 
 * Java environment <b>before</b> reporting a bug.  Note that you 
 * should both check from the JVM/JRE's command line as well as 
 * temporarily calling checkEnvironment() directly from your code, 
 * since the classpath may differ (especially for servlets, etc).
 *
 * Also see http://xml.apache.org/xalan-j/faq.html
 *
 * Note: This class is pretty simplistic: it does a fairly simple 
 * unordered search of the classpath; it only uses Class.forName() 
 * to load things, not actually querying the classloader; so the 
 * results are not necessarily definitive nor will it find all 
 * problems related to environment setup.  Also, you should avoid 
 * calling this in deployed production code, both because it is 
 * quite slow and because it forces classes to get loaded.
 * 
 * @author Shane_Curcuru@lotus.com
 * @version $Id$
 */
public class EnvironmentCheck
{

  /**
   * Command line runnability: checks for [-out outFilename] arg.
   * @param args command line args
   */
  public static void main(String[] args)
  {
    // Default to System.out, autoflushing
    PrintWriter sendOutputTo = new PrintWriter(System.out, true);

    // Read our simplistic input args, if supplied
    for (int i = 0; i < args.length; i++)
    {
      if ("-out".equalsIgnoreCase(args[i]))
      {
        i++;

        if (i < args.length)
        {
          try
          {
            sendOutputTo = new PrintWriter(new FileWriter(args[i], true));
          }
          catch (Exception e)
          {
            System.err.println("# WARNING: -out " + args[i] + " threw "
                               + e.toString());
          }
        }
        else
        {
          System.err.println(
            "# WARNING: -out argument should have a filename, output sent to console");
        }
      }
    }

    EnvironmentCheck app = new EnvironmentCheck();
    app.checkEnvironment(sendOutputTo);
  }

  /**
   * Report on basic environment settings that affect Xalan.
   *
   * Note that this class is not advanced enough to tell you 
   * everything about the environment that affects Xalan, and 
   * sometimes reports errors that will not actually affect 
   * Xalan's behavior.  Currently, it very simplistically 
   * checks the JVM's environment for some basic properties and 
   * logs them out; it will report a problem if it finds a setting 
   * or .jar file that is <i>likely</i> to cause problems.
   *
   * Advanced users can peruse the code herein to help them 
   * investigate potential environment problems found; other users 
   * may simply send the output from this tool along with any bugs 
   * they submit to help us in the debugging process.
   *
   * @param pw PrintWriter to send output to; can be sent to a 
   * file that will look similar to a Properties file; defaults 
   * to System.out if null
   * @return true if your environment appears to have no major 
   * problems; false if potential environment problems found
   */
  public boolean checkEnvironment(PrintWriter pw)
  {

    // Use user-specified output writer if non-null
    if (null != pw)
      outWriter = pw;

    // Setup a hash to store various environment information in
    Hashtable hash = new Hashtable();

    // Call various worker methods to fill in the hash
    //  These are explicitly separate for maintenance and so 
    //  advanced users could call them standalone
    checkJAXPVersion(hash);
    checkProcessorVersion(hash);
    checkParserVersion(hash);
    checkDOMVersion(hash);
    checkSystemProperties(hash);

    // Check for ERROR keys in the hashtable, and print report
    boolean environmentHasErrors = writeEnvironmentReport(hash);

    if (environmentHasErrors)
    {
      // Note: many logMsg calls have # at the start to 
      //  fake a property-file like output
      logMsg("# WARNING: Potential problems found in your environment!");
      logMsg("#    Check any 'ERROR' items above against the Xalan FAQs");
      logMsg("#    to correct potential problems with your classes/jars");
      logMsg("#    http://xml.apache.org/xalan-j/faq.html");
      if (null != outWriter)
        outWriter.flush();
      return false;
    }
    else
    {
      logMsg("# YAHOO! Your environment seems to be OK.");
      if (null != outWriter)
        outWriter.flush();
      return true;
    }
  }

  /**
   * Dump a basic Xalan environment report 
   *
   * This dumps a simple header and then each of the entries in 
   * the Hashtable to our PrintWriter; it does special processing 
   * for entries that are .jars found in the classpath.  
   *
   * @param h Hashtable of items to report on; presumably
   * filled in by our various check*() methods
   * @return true if your environment appears to have no major 
   * problems; false if potential environment problems found
   */
  protected boolean writeEnvironmentReport(Hashtable h)
  {

    if (null == h)
    {
      logMsg("# ERROR: writeEnvironmentReport called with null Hashtable");
      return false;
    }

    boolean errors = false;

    logMsg(
      "#---- BEGIN writeEnvironmentReport($Revision$): Useful properties found: ----");

    // Fake the Properties-like output
    for (Enumeration enum = h.keys(); 
         enum.hasMoreElements();
        /* no increment portion */
        )
    {
      Object key = enum.nextElement();
      try
      {
        //@todo ensure all keys are Strings
        String keyStr = (String) key;

        if (keyStr.startsWith(FOUNDCLASSES))
        {
          Vector v = (Vector) h.get(keyStr);
          errors |= logFoundJars(v, keyStr);
        }
        else if (keyStr.startsWith(ERROR))
        {
          errors = true;
          logMsg(keyStr + "=" + h.get(keyStr));
        }
        else
        {
          logMsg(keyStr + "=" + h.get(keyStr));
        }
      }
      catch (Exception e)
      {
        logMsg("Reading-" + key + "= threw: " + e.toString());
      }
    }

    logMsg(
      "#----- END writeEnvironmentReport: Useful properties found: -----");

    return errors;
  }

  /** Prefixed to hash keys that signify potential problems.  */
  public static final String ERROR = "ERROR.";

  /** Prefixed to hash keys that signify version numbers.  */
  public static final String VERSION = "version.";

  /** Prefixed to hash keys that signify .jars found in classpath.  */
  public static final String FOUNDCLASSES = "foundclasses.";

  /** Marker that a class or .jar was found.  */
  public static final String CLASS_PRESENT = "present-unknown-version";

  /** Marker that a class or .jar was not found.  */
  public static final String CLASS_NOTPRESENT = "not-present";

  /** Listing of common .jar files that include Xalan-related classes.  */
  public String[] jarNames =
  {
    "xalan.jar", "xalansamples.jar", "xalanj1compat.jar", "xalanservlet.jar",
    "xerces.jar", 
    "testxsl.jar", 
    "crimson.jar", 
    "jaxp.jar", "parser.jar", "dom.jar", "sax.jar", "xml.jar",
    /* below are .jars associated with XSLTC for now */
    "BCEL.jar", "java_cup.jar", "JLex.jar", "runtime.jar", "xsltc.jar"

    /* @todo add other jars that commonly include either
     * SAX, DOM, or JAXP interfaces in them
     */
  };

  /**
   * Fillin hash with info about SystemProperties.  
   *
   * Logs java.class.path and other likely paths; then attempts 
   * to search those paths for .jar files with Xalan-related classes.
   *
   * @param h Hashtable to put information in
   * @see #jarNames
   * @see #checkPathForJars(String, String[])
   */
  protected void checkSystemProperties(Hashtable h)
  {

    if (null == h)
      h = new Hashtable();

    // Grab java version for later use
    try
    {
      String javaVersion = System.getProperty("java.version");

      h.put("java.version", javaVersion);
    }
    catch (SecurityException se)
    {

      // For applet context, etc.
      h.put(
        ERROR + "java.version",
        "WARNING: SecurityException thrown accessing system version properties");
    }

    // Printout jar files on classpath(s) that may affect operation
    //  Do this in order
    try
    {

      // This is present in all JVM's
      String cp = System.getProperty("java.class.path");

      h.put("java.class.path", cp);

      Vector classpathJars = checkPathForJars(cp, jarNames);

      if (null != classpathJars)
        h.put(FOUNDCLASSES + "java.class.path", classpathJars);

      // Also check for JDK 1.2+ type classpaths
      String othercp = System.getProperty("sun.boot.class.path");

      if (null != othercp)
      {
        h.put("sun.boot.class.path", othercp);

        classpathJars = checkPathForJars(othercp, jarNames);

        if (null != classpathJars)
          h.put(FOUNDCLASSES + "sun.boot.class.path", classpathJars);
      }

      othercp = System.getProperty("java.ext.dirs");
      if (null != othercp)
      {
        h.put("java.ext.dirs", othercp);

        // Check the whole extensions directory for *.jar
        classpathJars = checkDirForJars(othercp, jarNames);

        if (null != classpathJars)
          h.put(FOUNDCLASSES + "java.ext.dirs", classpathJars);
      }
    }
    catch (SecurityException se2)
    {
      // For applet context, etc.
      h.put(
        ERROR + "java.class.path.ext.dirs",
        "WARNING: SecurityException thrown accessing system classpath properties");
    }
  }

  /**
   * Print out report of .jars found in a classpath. 
   *
   * Takes the information encoded from a checkPathForJars() 
   * call and dumps it out to our PrintWriter.
   *
   * @param v Vector of Hashtables of .jar file info
   * @param desc description to print out in header
   *
   * @return false if OK, true if any .jars were reported 
   * as having errors
   * @see #checkPathForJars(String, String[])
   * @see #checkDirForJars(String, String[])
   */
  protected boolean logFoundJars(Vector v, String desc)
  {

    if ((null == v) || (v.size() < 1))
      return false;

    boolean errors = false;

    logMsg("#---- BEGIN Listing XML-related jars in: " + desc + " ----");

    for (int i = 0; i < v.size(); i++)
    {
      Hashtable subhash = (Hashtable) v.elementAt(i);

      for (Enumeration enum = subhash.keys(); enum.hasMoreElements();

      /* no increment portion */
      )
      {
        Object key = enum.nextElement();

        try
        {

          //@todo ensure all keys are Strings
          String keyStr = (String) key;
          if (keyStr.startsWith(ERROR))
          {
            errors = true;
          }
          logMsg(keyStr + "=" + subhash.get(keyStr));

        }
        catch (Exception e)
        {
          errors = true;
          logMsg("Reading-" + key + "= threw: " + e.toString());
        }
      }
    }

    logMsg("#----- END Listing XML-related jars in: " + desc + " -----");

    return errors;
  }

  /**
   * Cheap-o listing of specified .jars found in the classpath. 
   *
   * cp should be separated by the usual File.pathSeparator.  We 
   * then do a simplistic search of the path for any requested 
   * .jar filenames, and return a listing of their names and 
   * where (apparently) they came from.
   *
   * @param cp classpath to search
   * @param jars array of .jar base filenames to look for
   *
   * @return Vector of Hashtables filled with info about found .jars
   * @see #jarNames
   * @see #checkDirForJars(String, String[])
   * @see #logFoundJars(Vector, String)
   * @see #getApparentVersion(String, long)
   */
  protected Vector checkPathForJars(String cp, String[] jars)
  {

    if ((null == cp) || (null == jars) || (0 == cp.length())
            || (0 == jars.length))
      return null;

    Vector v = new Vector();
    StringTokenizer st = new StringTokenizer(cp, File.pathSeparator);

    while (st.hasMoreTokens())
    {

      // Look at each classpath entry for each of our requested jarNames
      String filename = st.nextToken().toLowerCase();

      for (int i = 0; i < jars.length; i++)
      {
        if (filename.indexOf(jars[i]) > -1)
        {
          File f = new File(filename);

          if (f.exists())
          {

            // If any requested jarName exists, report on 
            //  the details of that .jar file
            try
            {
              Hashtable h = new Hashtable(5);

              //h.put(jars[i] + ".jarname", jars[i]);
              // h.put(jars[i] + ".lastModified", String.valueOf(f.lastModified()));
              h.put(jars[i] + ".path", f.getAbsolutePath());
              h.put(jars[i] + ".apparent.version",
                    getApparentVersion(jars[i], f.length()));
              v.addElement(h);
            }
            catch (Exception e)
            {

              /* no-op, don't add it  */
            }
          }
          else
          {
            logMsg("# Warning: Classpath entry: " + filename
                   + " does not exist.");
          }
        }
      }
    }

    return v;
  }

  /**
   * Cheap-o listing of specified .jars found in a directory. 
   *
   * cp should be a directory name, presumably java.ext.dirs.
   *
   * @param cp name of directory to look in for *.jar
   * @param jars array of .jar base filenames to look for
   *
   * @return Vector of Hashtables filled with info about found .jars
   * @see #jarNames
   * @see #checkPathForJars(String, String[])
   * @see #logFoundJars(Vector, String)
   * @see #getApparentVersion(String, long)
   */
  protected Vector checkDirForJars(String cp, String[] jars)
  {

    if ((null == cp) || (null == jars) || (0 == cp.length())
            || (0 == jars.length))
      return null;

    File extDir = new File(cp);
    if (!extDir.exists())
      return null;

    Vector v = new Vector();
    String foundJars[] = extDir.list(new JarFileFilter());
    for (int j = 0; j < foundJars.length; j++)
    {
      // Look at each .jar entry for each of our requested jarNames
      String filename = foundJars[j];

      for (int i = 0; i < jars.length; i++)
      {
        if (filename.equalsIgnoreCase(jars[i]))
        {
          File f = new File(extDir, filename);

          if (f.exists())
          {

            // If any requested jarName exists, report on 
            //  the details of that .jar file
            try
            {
              Hashtable h = new Hashtable(5);

              //h.put(jars[i] + ".jarname", jars[i]);
              // h.put(jars[i] + ".lastModified", String.valueOf(f.lastModified()));
              h.put(jars[i] + ".path", f.getAbsolutePath());
              h.put(jars[i] + ".apparent.version",
                    getApparentVersion(jars[i], f.length()));
              v.addElement(h);
            }
            catch (Exception e)
            {

              /* no-op, don't add it  */
            }
          }
          else
          {
            logMsg("# Warning: Classpath entry: " + filename
                   + " does not exist.");
          }
        }
      }
    }

    return v;
  }

  /**
   * Cheap-o method to determine the product version of a .jar.   
   *
   * Currently does a lookup into a local table of some recent 
   * shipped Xalan builds to determine where the .jar probably 
   * came from.  Note that if you recompile Xalan or Xerces 
   * yourself this will likely report a potential error, since 
   * we can't certify builds other than the ones we ship.
   * Only reports against selected posted Xalan-J builds.
   *
   * //@todo actually look up version info in manifests
   *
   * @param jarName base filename of the .jarfile
   * @param jarSize size of the .jarfile
   *
   * @return String describing where the .jar file probably 
   * came from
   */
  protected String getApparentVersion(String jarName, long jarSize)
  {

    // Lookup in a manual table of known .jar sizes; 
    //  only includes shipped versions of certain projects
    Hashtable jarVersions = new Hashtable();

    // key=jarsize, value=jarname ' from ' distro name
    // Note assumption: two jars will not have the same size!
    // Note: hackish Hashtable, this could use improvement
    jarVersions.put(new Long(440237), "xalan.jar from xalan-j_1_2");
    jarVersions.put(new Long(436094), "xalan.jar from xalan-j_1_2_1");
    jarVersions.put(new Long(426249), "xalan.jar from xalan-j_1_2_2");
    jarVersions.put(new Long(702536), "xalan.jar from xalan-j_2_0_0");
    jarVersions.put(new Long(720930), "xalan.jar from xalan-j_2_0_1");
    jarVersions.put(new Long(732330), "xalan.jar from xalan-j_2_1_0");
    jarVersions.put(new Long(857171), "xalan.jar from lotusxsl-j_1_0_1");
    jarVersions.put(new Long(802165), "xalan.jar from lotusxsl-j_2_0_0");
    jarVersions.put(new Long(424490), "xalan.jar from Xerces Tools releases - ERROR:DO NOT USE!");

    jarVersions.put(new Long(1498679), "xerces.jar from xalan-j_1_2 from xerces-1_2_0.bin");
    jarVersions.put(new Long(1484896), "xerces.jar from xalan-j_1_2_1 from xerces-1_2_1.bin");
    jarVersions.put(new Long(804460),  "xerces.jar from xalan-j_1_2_2 from xerces-1_2_2.bin");
    jarVersions.put(new Long(1499244), "xerces.jar from xalan-j_2_0_0 from xerces-1_2_3.bin");
    jarVersions.put(new Long(1605266), "xerces.jar from xalan-j_2_0_1 from xerces-1_3_0.bin");
    jarVersions.put(new Long(904030),  "xerces.jar from xalan-j_2_1_0 from xerces-1_4_0.bin");
    jarVersions.put(new Long(1190776), "xerces.jar from lotusxsl_1_0_1 apparently-from xerces-1_0_3.bin");
    jarVersions.put(new Long(1489400), "xerces.jar from lotusxsl-j_2_0_0 from XML4J-3_1_1");

    jarVersions.put(new Long(37485), "xalanj1compat.jar from xalan-j_2_0_0");
    jarVersions.put(new Long(38100), "xalanj1compat.jar from xalan-j_2_0_1");

    jarVersions.put(new Long(18779), "xalanservlet.jar from xalan-j_2_0_0");
    jarVersions.put(new Long(21453), "xalanservlet.jar from xalan-j_2_0_1");

    // For those who've downloaded JAXP from sun
    jarVersions.put(new Long(5618), "jaxp.jar from jaxp1.0.1");
    jarVersions.put(new Long(136133), "parser.jar from jaxp1.0.1");
    jarVersions.put(new Long(28404), "jaxp.jar from jaxp-1.1");
    jarVersions.put(new Long(187162), "crimson.jar from jaxp-1.1");
    jarVersions.put(new Long(801714), "xalan.jar from jaxp-1.1");

    // jakarta-ant: since many people use ant these days
    jarVersions.put(new Long(5537), "jaxp.jar from jakarta-ant-1.3 or 1.2");
    jarVersions.put(new Long(136198),
                    "parser.jar from jakarta-ant-1.3 or 1.2");

    // XSLTC integration jars checked-in Apr-01
    jarVersions.put(new Long(320367), "BCEL.jar from XSLTC integration Apr-01");
    jarVersions.put(new Long(61975), "java_cup.jar from XSLTC integration Apr-01");
    jarVersions.put(new Long(54603), "JLex.jar from XSLTC integration Apr-01");
    jarVersions.put(new Long(7779), "runtime.jar from XSLTC integration Apr-01");
    jarVersions.put(new Long(129139), "xml.jar from XSLTC integration Apr-01");

    // If we found a matching size and it's for our 
    //  jar, then return it's description
    String foundSize = (String) jarVersions.get(new Long(jarSize));

    if ((null != foundSize) && (foundSize.startsWith(jarName)))
    {
      return foundSize;
    }
    else
    {
      if ("xerces.jar".equalsIgnoreCase(jarName)
              || "xalan.jar".equalsIgnoreCase(jarName))
      {

        // For xalan.jar and xerces.jar, which we ship together:
        // The jar is not from a shipped copy of xalan-j, so 
        //  it's up to the user to ensure that it's compatible
        return jarName + " potential-ERROR " + CLASS_PRESENT;
      }
      else
      {

        // Otherwise, it's just a jar we don't have the version info calculated for
        return jarName + " " + CLASS_PRESENT;
      }
    }
  }

  /**
   * Report version information about JAXP interfaces.
   *
   * Currently distinguishes between JAXP 1.0.1 and JAXP 1.1, 
   * and not found.
   *
   * @param h Hashtable to put information in
   */
  protected void checkJAXPVersion(Hashtable h)
  {

    if (null == h)
      h = new Hashtable();

    final Class noArgs[] = new Class[0];
    Class clazz = null;

    try
    {
      final String JAXP1_CLASS = "javax.xml.parsers.DocumentBuilder";
      final String JAXP11_METHOD = "getDOMImplementation";

      clazz = Class.forName(JAXP1_CLASS);

      Method method = clazz.getMethod(JAXP11_METHOD, noArgs);

      // If we succeeded, we at least have JAXP 1.1 available
      h.put(VERSION + "JAXP", "1.1");
    }
    catch (Exception e)
    {
      if (null != clazz)
      {

        // We must have found the class itself, just not the 
        //  method, so we (probably) have JAXP 1.0.1
        h.put(ERROR + VERSION + "JAXP", "1.0.1");
      }
      else
      {

        // We couldn't even find the class, and don't have 
        //  any JAXP support at all
        h.put(ERROR + VERSION + "JAXP", "none");
      }
    }
  }

  /**
   * Report product version information from Xalan-J.
   *
   * Looks for version info in xalan.jar from Xalan-J products.
   *
   * @param h Hashtable to put information in
   */
  protected void checkProcessorVersion(Hashtable h)
  {

    if (null == h)
      h = new Hashtable();

    try
    {
      final String XALAN1_VERSION_CLASS =
        "org.apache.xalan.xslt.XSLProcessorVersion";
      Class clazz = Class.forName(XALAN1_VERSION_CLASS);

      // Found Xalan-J 1.x, grab it's version fields
      StringBuffer buf = new StringBuffer();
      Field f = clazz.getField("PRODUCT");

      buf.append(f.get(null));
      buf.append(';');

      f = clazz.getField("LANGUAGE");

      buf.append(f.get(null));
      buf.append(';');

      f = clazz.getField("S_VERSION");

      buf.append(f.get(null));
      buf.append(';');
      h.put(VERSION + "xalan1", buf.toString());
    }
    catch (Exception e1)
    {
      h.put(VERSION + "xalan1", CLASS_NOTPRESENT);
    }

    try
    {
      final String XALAN2_VERSION_CLASS =
        "org.apache.xalan.processor.XSLProcessorVersion";
      Class clazz = Class.forName(XALAN2_VERSION_CLASS);

      // Found Xalan-J 2.x, grab it's version fields
      StringBuffer buf = new StringBuffer();
      Field f = clazz.getField("PRODUCT");

      buf.append(f.get(null));
      buf.append(";");

      f = clazz.getField("LANGUAGE");

      buf.append(f.get(null));
      buf.append(";");

      f = clazz.getField("S_VERSION");

      buf.append(f.get(null));
      buf.append(";");
      h.put(VERSION + "xalan2", buf.toString());
    }
    catch (Exception e2)
    {
      h.put(VERSION + "xalan2", CLASS_NOTPRESENT);
    }
  }

  /**
   * Report product version information from common parsers.
   *
   * Looks for version info in xerces.jar/crimson.jar.
   *
   * //@todo actually look up version info in crimson manifest
   *
   * @param h Hashtable to put information in
   */
  protected void checkParserVersion(Hashtable h)
  {

    if (null == h)
      h = new Hashtable();

    try
    {
      final String XERCES1_VERSION_CLASS =
        "org.apache.xerces.framework.Version";
      Class clazz = Class.forName(XERCES1_VERSION_CLASS);

      // Found Xerces-J 1.x, grab it's version fields
      Field f = clazz.getField("fVersion");
      String parserVersion = (String) f.get(null);

      h.put(VERSION + "xerces", parserVersion);
    }
    catch (Exception e)
    {

      // This isn't necessarily an error, since the user might 
      //  be using some other parser
      h.put(VERSION + "xerces", CLASS_NOTPRESENT);
    }

    try
    {
      final String CRIMSON_CLASS = "org.apache.crimson.Parser2";
      Class clazz = Class.forName(CRIMSON_CLASS);

      //@todo determine specific crimson version
      h.put(VERSION + "crimson", CLASS_PRESENT);
    }
    catch (Exception e)
    {
      h.put(VERSION + "crimson", CLASS_NOTPRESENT);
    }
  }

  /**
   * Report version info from DOM interfaces. 
   *
   * Currently distinguishes between pre-DOM level 2, the DOM 
   * level 2 working draft, the DOM level 2 final draft, 
   * and not found.
   *
   * @param h Hashtable to put information in
   */
  protected void checkDOMVersion(Hashtable h)
  {

    if (null == h)
      h = new Hashtable();

    final String DOM_LEVEL2_CLASS = "org.w3c.dom.Document";
    final String DOM_LEVEL2_METHOD = "createElementNS";  // String, String
    final String DOM_LEVEL2WD_CLASS = "org.w3c.dom.Node";
    final String DOM_LEVEL2WD_METHOD = "supported";  // String, String
    final String DOM_LEVEL2FD_CLASS = "org.w3c.dom.Node";
    final String DOM_LEVEL2FD_METHOD = "isSupported";  // String, String
    final Class twoStringArgs[] = { java.lang.String.class,
                                    java.lang.String.class };

    try
    {
      Class clazz = Class.forName(DOM_LEVEL2_CLASS);
      Method method = clazz.getMethod(DOM_LEVEL2_METHOD, twoStringArgs);

      // If we succeeded, we have loaded interfaces from a 
      //  level 2 DOM somewhere
      h.put(VERSION + "DOM", "2.0");

      try
      {

        // Check for the working draft version, which is 
        //  commonly found, but won't work anymore
        clazz = Class.forName(DOM_LEVEL2WD_CLASS);
        method = clazz.getMethod(DOM_LEVEL2WD_METHOD, twoStringArgs);

        h.put(ERROR + VERSION + "DOM.draftlevel", "2.0wd");
      }
      catch (Exception e2)
      {
        try
        {

          // Check for the final draft version as well
          clazz = Class.forName(DOM_LEVEL2FD_CLASS);
          method = clazz.getMethod(DOM_LEVEL2FD_METHOD, twoStringArgs);

          h.put(VERSION + "DOM.draftlevel", "2.0fd");
        }
        catch (Exception e3)
        {
          h.put(ERROR + VERSION + "DOM.draftlevel", "2.0unknown");
        }
      }
    }
    catch (Exception e)
    {
      h.put(ERROR + VERSION + "DOM",
            "ERROR attempting to load DOM level 2 class: " + e.toString());
    }

    //@todo load an actual DOM implmementation and query it as well
    //@todo load an actual DOM implmementation and check if 
    //  isNamespaceAware() == true, which is needed to parse 
    //  xsl stylesheet files into a DOM
  }

  /** Simple PrintWriter we send output to; defaults to System.out.  */
  protected PrintWriter outWriter = new PrintWriter(System.out, true);

  /**
   * Bottleneck output: calls outWriter.println(s).  
   * @param s String to print
   */
  protected void logMsg(String s)
  {
    outWriter.println(s);
  }

  private class JarFileFilter implements FilenameFilter
  {
    /**
     * Returns true for *.jar files
     * @param dir the directory in which the file was found.
     * @param name the name of the file.
     * @return <code>true</code> if the name should be included in the file list; <code>false</code> otherwise.
     * @since JDK1.0
     */
    public boolean accept(File dir, String name)
    {
      // Shortcuts for bogus filenames and dirs
      if (name == null || dir == null)
        return false;
      return name.toLowerCase().endsWith("jar");
    }
}


}
