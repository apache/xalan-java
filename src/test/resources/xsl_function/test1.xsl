<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn1="http://test1"                
                exclude-result-prefixes="xs fn1"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSLT stylesheet test case, to test a stylesheet
         function defined with an XSL element xsl:function.
    -->               
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">       
       <result>
          <xsl:copy-of select="fn1:sum(2, 10) + 12"/>  
       </result>
    </xsl:template>
    
    <!-- A function, doing a sum of two argument values. -->
    <xsl:function name="fn1:sum" as="xs:double">
       <xsl:param name="a1" as="xs:double"/>
       <xsl:param name="a2" as="xs:double"/>
       
       <xsl:value-of select="$a1 + $a2"/>
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