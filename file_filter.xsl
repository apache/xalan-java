<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
                version="3.0">
				
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL stylesheet to filter specific lines from a given file. -->				
				
   <xsl:output method="text"/>				
				
   <xsl:template match="/">
      <xsl:variable name="fileList" select="unparsed-text-lines('file.lst')" as="xs:string*"/>
	  <xsl:for-each select="$fileList[not(contains(., 'org\apache\xalan\tests\') or 
	                                      contains(., 'org\apache\xml\res\') or 
										  contains(., 'org\apache\xalan\res\') or
										  contains(., 'org\apache\xpath\res\') or
										  contains(., 'org\apache\xml\utils\res\') or 
										  contains(., 'org\apache\xalan\xsltc\'))]">
	     <xsl:value-of select="."/><xsl:text>&#xa;</xsl:text>
	  </xsl:for-each>
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