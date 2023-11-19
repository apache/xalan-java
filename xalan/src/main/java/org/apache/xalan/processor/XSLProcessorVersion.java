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
package org.apache.xalan.processor;

import org.apache.xalan.Version;

/**
 * Administrative class to keep track of the version number of
 * the Xalan Processor release.
 * <P>See also: org/apache/xalan/res/XSLTInfo.properties</P>
 * @deprecated To be replaced by org.apache.xalan.Version.getVersion()
 * @xsl.usage general
 */
public class XSLProcessorVersion extends Version
{

  /**
   * Get the basic version string for the current Xalan release.
   * Version String formatted like
   * <CODE>"<B>Xalan Processor</B> <B>Java</B> v.r[.dd| <B>D</B>nn]"</CODE>.
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
   * Name of product: Xalan Processor.
   */
  public static String getProduct()
  {
    return "Xalan Processor";
  }
}
