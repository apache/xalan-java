<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test1_a.xml -->
    
    <!-- An XSLT stylesheet test case, to test XPath 3.1 
         fn:deep-equal function.
    -->              

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
       <out>
          <true>
             <xsl:value-of select="deep-equal(//a[1]/@a, //a[2]/@a)"/>
          </true>
          <false>
             <xsl:value-of select="deep-equal(//a[1]/@a, //b[1]/@a)" />
          </false>
          <false>
             <xsl:value-of select="deep-equal(//a[1]/@a, //c[1]/@c)" />
          </false>
       </out>
    </xsl:template>
    
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
