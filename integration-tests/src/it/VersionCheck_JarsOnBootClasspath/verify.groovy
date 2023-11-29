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

// Replace leading zeroes in dev drop version number, e.g. 'D07'  to 'D7',
// because this is what the version classes do, too
projectVersion = projectVersion.replaceAll("\\.D0*", ".D")

def buildLogLines = new File(basedir, "build.log").readLines()
def expectedLogLines = [
  'Class org.apache.xalan.Version is on boot classpath',
  'Class org.apache.xalan.processor.XSLProcessorVersion is on boot classpath',
  'Class org.apache.xml.serializer.Version is on boot classpath',
  "Xalan Java $projectVersion",
  "Xalan Processor Java $projectVersion",
  "Xalan Serializer Java $projectVersion"
]

// Find first expected log output line
def index = buildLogLines.indexOf(expectedLogLines[0])
assert index > 0 : "First expected log line not found"
// Verify that all expected log lines exist in the expected order
assert buildLogLines[index..index + 5] == expectedLogLines : "Expected log lines not found"
