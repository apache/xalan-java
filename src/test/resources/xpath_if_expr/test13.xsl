<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
   
    <!-- An XSLT stylesheet test case, to test the XPath 3.1 
         "if" expression.
         
         The XPath "if" expressions used within this stylesheet,
         are conceptually borrowed from W3C XSLT 3.0 test 
         suite. -->                 
                
    <xsl:output method="xml" indent="yes"/>                
                
    <xsl:variable name="nameValSeq" select="('Mike', 'Joseph', 'Gary')"/>                
        
    <xsl:template match="/">
       <result>
          <xsl:for-each select="$nameValSeq">
             <data>
                <one>
                   <xsl:value-of select="if (current-date() gt xs:date('2000-12-31')) 
                                                                    then upper-case(.) else lower-case(.)"/>
                </one>
                <two>
	           <xsl:value-of select="if (current-date() gt xs:date('2501-12-31')) 
	                                                                then upper-case(.) else lower-case(.)"/>
                </two>
             </data>
          </xsl:for-each>
       </result>
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