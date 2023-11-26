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
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Administrative class to keep track of the version number of
 * the Serializer release.
 * <P>This class implements the upcoming standard of having
 * org.apache.project-name.Version.getVersion() be a standard way
 * to get version information.</P>
 * @xsl.usage general
 */
public final class Version
{
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
    // IMPLEMENTATION NOTE: Class.getResourceAsStream uses a *relative* path by
    // default, in contrast to Classloader.getResourceAsStream, which uses an
    // *absolute* one. This is not clearly documented in the JDK, only
    // noticeable by the absence of the word "absolute" in
    // Class.getResourceAsStream javadocs. For more details, see
    // https://www.baeldung.com/java-class-vs-classloader-getresource.
    //
    // Because we expect the properties file to be in the same directory/package
    // as this class, the relative path comes in handy and as a bonus is also
    // relocation-friendly (think Maven Shade).
    try (InputStream fromResource = Version.class.getResourceAsStream("version.properties")) {
      if (fromResource != null) {
        pomProperties.load(fromResource);
        version = pomProperties.getProperty("version", NO_VERSION);
      }
    }
    catch (IOException e) {
      new RuntimeException("Cannot read properties file to extract Xalan version number information: ", e)
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
        "Cannot match Xalan version \"" + version + "\" " +
          "against expected pattern \"" + VERSION_NUMBER_PATTERN + "\""
      );
    }
  }

  /**
   * Get the basic version string for the current Serializer.
   * Version String formatted like
   * <CODE>"<B>Xalan Serializer</B> <B>Java</B> v.r[.dd| <B>D</B>nn]"</CODE>.
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
   * @return String name of product: Serializer.
   */
  public static String getProduct()
  {
    return "Xalan Serializer";
  }

  /**
   * @return String implementation Language: Java.
   */
  public static String getImplementationLanguage()
  {
    return "Java";
  }


  /**
   * @return int Major version number.
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
   * @return int Release Number.
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
   * @return int Maintenance Drop Number.
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
   * @return int Development Drop Number.
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
