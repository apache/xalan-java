<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL 3 stylesheet test case, to test XPath 3.1 function fn:unparsed-text-available.
        The function call fn:unparsed-text-available caches the file contents into memory, 
        after which multiple calls to function fn:unparsed-text fetches file contents from 
        the cache.
    -->

   <xsl:output method="xml" indent="yes"/>      

   <xsl:template match="/">
     <result>
        <xsl:variable name="isFileAvailable" select="unparsed-text-available('file1.txt')" as="xs:boolean"/>
        <xsl:for-each select="1 to 5">
        	<xsl:variable name="fileContents" select="if ($isFileAvailable) then unparsed-text('file1.txt') else ''" as="xs:string"/>                        
        	<xsl:variable name="fileLines" select="tokenize($fileContents, '\r?\n')"/>
        	<file name="file1.txt">
        		<xsl:for-each select="$fileLines">
           			<line><xsl:value-of select="."/></line>
        		</xsl:for-each>
        	</file>
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