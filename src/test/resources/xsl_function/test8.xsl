<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:ns0="http://ns0"
                exclude-result-prefixes="xs ns0"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSLT stylesheet test case, to test a stylesheet
         function defined with an XSL element xsl:function.
    -->                 
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">       
       <result>
          <one>
             <xsl:value-of select="ns0:func1(6, 5, xs:boolean('true'), xs:boolean('false'))"/>
          </one>
          <two>
	         <xsl:value-of select="ns0:func1(2, 5, xs:boolean('true'), xs:boolean('false'))"/>
          </two>
       </result>
    </xsl:template>
    
    <xsl:function name="ns0:func1" as="xs:boolean">
       <xsl:param name="val1" as="xs:integer"/>
       <xsl:param name="val2" as="xs:integer"/>
       <xsl:param name="a" as="xs:boolean"/>
       <xsl:param name="b" as="xs:boolean"/>
       
       <xsl:value-of select="if ($val1 gt $val2) then ($a and $b) else ($a or $b)"/>
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