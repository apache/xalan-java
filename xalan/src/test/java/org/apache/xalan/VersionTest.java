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
package org.apache.xalan;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Isolated("redirecting System.err is not thread-safe")
public class VersionTest {
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

  @ParameterizedTest(name = "{0} -> {2}")
  @MethodSource("testReadPropertiesArgs")
  public void testReadProperties(String ignoredName, String properties, String version) {
    try (MockedStatic<Version> versionMock = Mockito.mockStatic(Version.class, Mockito.CALLS_REAL_METHODS)) {
      versionMock
        .when(Version::getPropertiesStream)
        .thenReturn(new ByteArrayInputStream(properties.getBytes()));
      assertEquals(version, Version.readVersionNumber());
    }
  }

  private static Stream<Arguments> testReadPropertiesArgs() {
    return Stream.of(
      Arguments.of("single line without line feed", "version=1.2.3", "1.2.3"),
      Arguments.of("single line with line feed", "version=4.5.D6-SNAPSHOT\n", "4.5.D6-SNAPSHOT"),
      Arguments.of("multiple lines with version number", "foo=bar\nversion=7.8.9\nbaz=zot\n", "7.8.9"),
      Arguments.of("single line without version number", "verXion=7.8.9\n", "0.0.0"),
      Arguments.of("multiple lines without version number", "foo=bar\nverXion=7.8.9\nbaz=zot\n", "0.0.0")
    );
  }

  @Test
  public void testCannotReadProperties() {
    try (MockedStatic<Version> versionMock = Mockito.mockStatic(Version.class, Mockito.CALLS_REAL_METHODS)) {
      versionMock
        .when(Version::getPropertiesStream)
        .thenThrow(NullPointerException.class);
      assertEquals("0.0.0", Version.readVersionNumber());
      assertTrue(buffer.toString().contains("RuntimeException: Cannot read properties file"));
    }
  }

  @ParameterizedTest(name = "{0} -> {1}")
  @MethodSource("testParseVersionNumberArgs")
  public void testParseVersionNumber(
    String inputVersion, boolean matchSuccessful, String parsedVersion,
    int major, int release, int maintenance, int development, boolean snapshot
  ) {
    Version.parseVersionNumber(inputVersion);
    assertEquals(parsedVersion, Version.getVersion());
    assertEquals(major, Version.getMajorVersionNum());
    assertEquals(release, Version.getReleaseVersionNum());
    assertEquals(maintenance, Version.getMaintenanceVersionNum());
    assertEquals(development, Version.getDevelopmentVersionNum());
    assertEquals(snapshot, Version.isSnapshot());
    boolean cannotMatchWarningFound = buffer.toString().contains("Cannot match Xalan version");
    assertEquals(matchSuccessful, !cannotMatchWarningFound);
  }

  private static Stream<Arguments> testParseVersionNumberArgs() {
    return Stream.of(
      // Pattern match
      Arguments.of("1.2.3", true, "Xalan Java 1.2.3", 1, 2, 3, 0, false),
      Arguments.of("1.2.D3", true, "Xalan Java 1.2.D3", 1, 2, 0, 3, false),
      Arguments.of("1.2.3-SNAPSHOT", true, "Xalan Java 1.2.3-SNAPSHOT", 1, 2, 3, 0, true),
      Arguments.of("1.2.D03-SNAPSHOT", true, "Xalan Java 1.2.D3-SNAPSHOT", 1, 2, 0, 3, true),
      Arguments.of("0.0.0", true, "Xalan Java 0.0.0", 0, 0, 0, 0, false),
      // No pattern match
      Arguments.of("", false, "Xalan Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("-1.2.3", false, "Xalan Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("-1.2.3", false, "Xalan Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1. 2.3", false, "Xalan Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3x", false, "Xalan Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.3-XSNAPSHOT", false, "Xalan Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3-snapshot", false, "Xalan Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3-SNAPSHOT-1", false, "Xalan Java 0.0.0", 0, 0, 0, 0, false),
      // Input version null -> cannot happen in class under test, but we know
      // what would happen if it did
      Arguments.of(null, true, "Xalan Java 0.0.0", 0, 0, 0, 0, false)
    );
  }

}
