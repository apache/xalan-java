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
package org.apache.xml.serializer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VersionTest {
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
    }
  }

  @ParameterizedTest(name = "{0} -> {1}")
  @MethodSource("testParseVersionNumberArgs")
  public void testParseVersionNumber(
    String inputVersion, String parsedVersion,
    int major, int release, int maintenance, int development, boolean snapshot) {
    Version.parseVersionNumber(inputVersion);
    assertEquals(parsedVersion, Version.getVersion());
    assertEquals(major, Version.getMajorVersionNum());
    assertEquals(release, Version.getReleaseVersionNum());
    assertEquals(maintenance, Version.getMaintenanceVersionNum());
    assertEquals(development, Version.getDevelopmentVersionNum());
    assertEquals(snapshot, Version.isSnapshot());
  }

  private static Stream<Arguments> testParseVersionNumberArgs() {
    return Stream.of(
      // Pattern match
      Arguments.of("1.2.3", "Xalan Serializer Java 1.2.3", 1, 2, 3, 0, false),
      Arguments.of("1.2.D3", "Xalan Serializer Java 1.2.D3", 1, 2, 0, 3, false),
      Arguments.of("1.2.3-SNAPSHOT", "Xalan Serializer Java 1.2.3-SNAPSHOT", 1, 2, 3, 0, true),
      Arguments.of("1.2.D03-SNAPSHOT", "Xalan Serializer Java 1.2.D3-SNAPSHOT", 1, 2, 0, 3, true),
      // No pattern match
      Arguments.of("", "Xalan Serializer Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("-1.2.3", "Xalan Serializer Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("-1.2.3", "Xalan Serializer Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1. 2.3", "Xalan Serializer Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3x", "Xalan Serializer Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.3-XSNAPSHOT", "Xalan Serializer Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3-snapshot", "Xalan Serializer Java 0.0.0", 0, 0, 0, 0, false),
      Arguments.of("1.2.D3-SNAPSHOT-1", "Xalan Serializer Java 0.0.0", 0, 0, 0, 0, false),
      // Input version null
      Arguments.of(null, "Xalan Serializer Java 0.0.0", 0, 0, 0, 0, false)
    );
  }

}
