<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns0="http://my.uri/"               
                exclude-result-prefixes="ns0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, to test serialization of a
       sequence of atomic values produced by recursion, and also 
       serialization of another sequence of atomic values produced 
       via an XPath sequence literal expression.
       
       The stylesheet recursive function used within this stylesheet
       has been borrowed from W3C XSLT 3.0 test suite, and output
       of that function has been adjusted for XalanJ implementation 
       (which we believe, is compliant to XSLT 3.0 specification).
  -->
                
  <xsl:output method="xml" indent="yes"/>                

  <xsl:template match="/">
    <result>
       <one>
          <xsl:value-of select="ns0:func1(12)"/>
       </one>
       <two>
          <xsl:value-of select="('a', 'b', 'c', 'd', 'e')"/>
       </two>
    </result> 
  </xsl:template>
  
  <xsl:function name="ns0:func1">
     <xsl:param name="x"/>
     <xsl:sequence select="if ($x = 0) then () else ($x, ns0:func1($x - 1))"/>
  </xsl:function>
  
  <!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the "License");
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