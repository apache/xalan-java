/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.xalan;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Administrative class to keep track of the version number of
 * the Xalan release.
 * <P>This class implements the upcoming standard of having
 * org.apache.project-name.Version.getVersion() be a standard way
 * to get version information.  This class will replace the older
 * org.apache.xalan.processor.Version class.</P>
 * <P>See also: org/apache/xalan/res/XSLTInfo.properties for
 * information about the version of the XSLT spec we support.</P>
 * @xsl.usage general
 */
public class Version
{
  private static final String POM_PROPERTIES_JAR = "META-INF/maven/xalan/xalan/pom.properties";
  private static final String POM_PROPERTIES_FILE_SYSTEM = "xalan/target/maven-archiver/pom.properties";
  private static final String VERSION_NUMBER_PATTERN = "^(\\d+)[.](\\d+)[.](D)?(\\d+)(-SNAPSHOT)?$";
  private static final String NO_VERSION = "0.0.0";

  private static String version = NO_VERSION;
  private static int majorVersionNum;
  private static int releaseVersionNum;
  private static int maintenanceVersionNum;
  private static int developmentVersionNum;

  private static boolean snapshot;

  static {
    readProperties();
    parseVersionNumber();
  }

  private static void readProperties() {
    Properties pomProperties = new Properties();
    try (InputStream fromJar = Version.class.getResourceAsStream(POM_PROPERTIES_JAR)) {
      if (fromJar != null) {
        pomProperties.load(fromJar);
        version = pomProperties.getProperty("version", NO_VERSION);
      }
      else {
        try (FileInputStream fromFileSystem = new FileInputStream(POM_PROPERTIES_FILE_SYSTEM)) {
          pomProperties.load(fromFileSystem);
          version = pomProperties.getProperty("version", NO_VERSION);
        }
      }
    }
    catch (IOException e) {
      new RuntimeException("Cannot read properties file to extract version number information: ", e)
        .printStackTrace();
    }
  }

  private static void parseVersionNumber() {
    Matcher matcher = Pattern.compile(VERSION_NUMBER_PATTERN).matcher(version);
    if (matcher.find()) {
      majorVersionNum = Integer.parseInt(matcher.group(1));
      releaseVersionNum = Integer.parseInt(matcher.group(2));
      if (matcher.group(3) == null) {
        maintenanceVersionNum = Integer.parseInt(matcher.group(4));
      }
      else {
        developmentVersionNum = Integer.parseInt(matcher.group(4));
      }
      snapshot = matcher.group(5) != null && !matcher.group(5).isEmpty();
    }
    else {
      System.err.println(
        "Cannot match version \"" + version + "\" " +
          "against expected pattern \"" + VERSION_NUMBER_PATTERN + "\""
      );
    }
  }

  /**
   * Get the basic version string for the current Xalan release.
   * Version String formatted like
   * <CODE>"<B>Xalan</B> <B>Java</B> v.r[.dd| <B>D</B>nn]"</CODE>.
   *
   * @return String denoting our current version
   */
  public static String getVersion()
  {
     return getProduct()+" "+getImplementationLanguage()+" "
           +getMajorVersionNum()+"."+getReleaseVersionNum()+"."
           +( (getDevelopmentVersionNum() > 0) ?
               ("D"+getDevelopmentVersionNum()) : (""+getMaintenanceVersionNum()))
           +(isSnapshot() ? "-SNAPSHOT" :"");
  }

  /**
   * Print the processor version to the command line.
   *
   * @param argv command line arguments, unused.
   */
  public static void main(String argv[])
  {
    System.out.println(getVersion());
  }

  /**
   * Name of product: Xalan.
   */
  public static String getProduct()
  {
    return "Xalan";
  }

  /**
   * Implementation Language: Java.
   */
  public static String getImplementationLanguage()
  {
    return "Java";
  }


  /**
   * Major version number.
   * Version number. This changes only when there is a
   *          significant, externally apparent enhancement from
   *          the previous release. 'n' represents the n'th
   *          version.
   *
   *          Clients should carefully consider the implications
   *          of new versions as external interfaces and behaviour
   *          may have changed.
   */
  public static int getMajorVersionNum()
  {
    return majorVersionNum;
  }

  /**
   * Release Number.
   * Release number. This changes when:
   *            -  a new set of functionality is to be added, eg,
   *               implementation of a new W3C specification.
   *            -  API or behaviour change.
   *            -  its designated as a reference release.
   */
  public static int getReleaseVersionNum()
  {
    return releaseVersionNum;
  }

  /**
   * Maintenance Drop Number.
   * Optional identifier used to designate maintenance
   *          drop applied to a specific release and contains
   *          fixes for defects reported. It maintains compatibility
   *          with the release and contains no API changes.
   *          When missing, it designates the final and complete
   *          development drop for a release.
   */
  public static int getMaintenanceVersionNum()
  {
    return maintenanceVersionNum;
  }

  /**
   * Development Drop Number.
   * Optional identifier designates development drop of
   *          a specific release. D01 is the first development drop
   *          of a new release.
   *
   *          Development drops are works in progress towards a
   *          compeleted, final release. A specific development drop
   *          may not completely implement all aspects of a new
   *          feature, which may take several development drops to
   *          complete. At the point of the final drop for the
   *          release, the D suffix will be omitted.
   *
   *          Each 'D' drops can contain functional enhancements as
   *          well as defect fixes. 'D' drops may not be as stable as
   *          the final releases.
   */
  public static int getDevelopmentVersionNum()
  {
    return developmentVersionNum;
  }

  /**
   * Snapshot flag.
   * Specifies whether the version number has a "-SNAPSHOT" suffix,
   *          which by Maven/Gradle conventions designates a
   *          development version.
   */
  public static boolean isSnapshot()
  {
    return snapshot;
  }
}
