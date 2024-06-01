<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                version="3.0"
                exclude-result-prefixes="map">
                
  <!-- Author: mukulg@apache.org -->                
                
  <!-- XSL stylesheet, for jira issue XALANJ-2738. Adapted from, XSL stylesheet 
       provided by Martin Honnen. -->
       
  <!-- use with xml-sample-with-json-data2.xml -->                       

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/root">
    <result>
       <xsl:variable name="map_for_each_result" select="map:for-each(parse-json(data), function($k, $v) { $k || ' : ' || $v })"/>
       <key name="foo" isPresentInResult="{contains($map_for_each_result, 'foo :')}"/>
       <key name="data" isPresentInResult="{contains($map_for_each_result, 'data :')}"/>
    </result>
  </xsl:template>
  
  <!--
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
   -->

</xsl:stylesheet>
