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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Isolated("redirecting System.err is not thread-safe")
public class XSLProcessorVersionTest {
  private static final PrintStream originalPrintStream = System.err;
  private static final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
  private static final PrintStream mockPrintStream = new PrintStream(buffer, true);

  @BeforeAll
  public static void beforeAll() {
    System.setErr(mockPrintStream);
  }

  @AfterAll
  public static void afterAll() {
    System.setErr(originalPrintStream);
  }

  @BeforeEach
  public void beforeEach() {
    buffer.reset();
  }

  @ParameterizedTest(name = "{0} -> {1}")
  @MethodSource("testParseVersionNumberArgs")
  public void testParseVersionNumber(
    String inputVersion, boolean matchSuccessful, String parsedVersion,
    int major, int release, int maintenance, int development, boolean snapshot
  ) {
    VersionAccessor.parseVersionNumber(inputVersion);
    assertEquals(parsedVersion, XSLProcessorVersion.getVersion());
    assertEquals(major, XSLProcessorVersion.getMajorVersionNum());
    assertEquals(release, XSLProcessorVersion.getReleaseVersionNum());
    assertEquals(maintenance, XSLProcessorVersion.getMaintenanceVersionNum());
    assertEquals(development, XSLProcessorVersion.getDevelopmentVersionNum());
    assertEquals(snapshot, XSLProcessorVersion.isSnapshot());
    boolean cannotMatchWarningFound = buffer.toString().contains("Cannot match Xalan version");
    assertEquals(matchSuccessful, !cannotMatchWarningFound);
  }

  private static Stream<Arguments> testParseVersionNumberArgs() {
    return Stream.of(
      // Pattern match
      Arguments.of("1.2.3", true, "Xalan Processor Java 1.2.3", 1, 2, 3, 0, false),
      Arguments.of("1.2.D3", true, "Xalan Processor Java 1.2.D3", 1, 2, 0, 3, false),
      Arguments.of("1.2.3-SNAPSHOT", true, "Xalan Processor Java 1.2.3-SNAPSHOT", 1, 2, 3, 0, true),
      Arguments.of("1.2.D03-SNAPSHOT", true, "Xalan Processor Java 1.2.D3-SNAPSHOT", 1, 2, 0, 3, true),
      Arguments.of("0.0.0", true, "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      // No pattern match
      Arguments.of("", false, "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("-1.2.3", false, "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("-1.2.3", false, "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1. 2.3", false, "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3x", false, "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.3-XSNAPSHOT", false, "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3-snapshot", false, "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3-SNAPSHOT-1", false, "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false),
      // Input version null -> cannot happen in class under test, but we know
      // what would happen if it did
      Arguments.of(null, true, "Xalan Processor Java 0.0.0", 0, 0, 0, 0, false)
    );
  }

}
