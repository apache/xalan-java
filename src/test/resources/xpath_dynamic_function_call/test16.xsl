<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
                
    <!-- This XSLT stylesheet test case, tests function recursion,
         for an XPath 3.1 dynamic function call.
        
         This stylesheet, specifies an XPath inline function expression
         to calculate factorial of a numerical integer value. 
    -->                
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:variable name="factorial" select="function($num) {if ($num = 0) then 1 else ($num * $factorial($num - 1))}"/>
    
    <xsl:template match="/">       
       <factorial>
          <xsl:for-each select="0 to 10">
             <xsl:variable name="num" select="."/>
             <inp val="{$num}">
                <result>               
                   <xsl:value-of select="$factorial($num)"/>
                </result>
             </inp>
          </xsl:for-each>
       </factorial> 
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