<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
    
    <!-- An XSLT stylesheet test case, to test the sequence type
         declaration attribute "as" on an xsl:variable instruction.
         
         Within this stylesheet example, a numeric string is specified
         as child content of an xsl:variable instruction, which is cast
         to the type xs:integer when the variable is evaluated.
         
         We emit the value of variable once by xsl:copy-of instruction,
         and then by xsl:value-of instruction.   
    -->                
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">       
       <result>
         <xsl:variable name="var1" as="xs:integer">123</xsl:variable>	 
	     <one>
	        <xsl:copy-of select="$var1"/>
	     </one>
	     <two>
	        <xsl:value-of select="$var1"/>
	     </two>
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