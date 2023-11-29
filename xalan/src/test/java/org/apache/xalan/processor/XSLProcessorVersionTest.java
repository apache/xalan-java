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
package org.apache.xalan.processor;

import org.apache.xalan.VersionAccessor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XSLProcessorVersionTest {

  @ParameterizedTest(name = "{0} -> {1}")
  @MethodSource("testParseVersionNumberArgs")
  public void testParseVersionNumber(
    String inputVersion, String parsedVersion,
    int major, int release, int maintenance, int development, boolean snapshot) {
    VersionAccessor.parseVersionNumber(inputVersion);
    assertEquals(parsedVersion, XSLProcessorVersion.getVersion());
    assertEquals(major, XSLProcessorVersion.getMajorVersionNum());
    assertEquals(release, XSLProcessorVersion.getReleaseVersionNum());
    assertEquals(maintenance, XSLProcessorVersion.getMaintenanceVersionNum());
    assertEquals(development, XSLProcessorVersion.getDevelopmentVersionNum());
    assertEquals(snapshot, XSLProcessorVersion.isSnapshot());
  }

  private static Stream<Arguments> testParseVersionNumberArgs() {
    return Stream.of(
      // Pattern match
      Arguments.of("1.2.3", "Xalan Processor Java 1.2.3", 1, 2, 3, 0, false),
      Arguments.of("1.2.D3", "Xalan Processor Java 1.2.D3", 1, 2, 0, 3, false),
      Arguments.of("1.2.3-SNAPSHOT", "Xalan Processor Java 1.2.3-SNAPSHOT", 1, 2, 3, 0, true),
      Arguments.of("1.2.D03-SNAPSHOT", "Xalan Processor Java 1.2.D3-SNAPSHOT", 1, 2, 0, 3, true),
      // No pattern match
      Arguments.of("", "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("-1.2.3", "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("-1.2.3", "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1. 2.3", "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3x", "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.3-XSNAPSHOT", "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3-snapshot", "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3-SNAPSHOT-1", "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      // Input version null
      Arguments.of(null, "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false)
    );
  }

}
