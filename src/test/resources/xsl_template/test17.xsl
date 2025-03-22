<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->

  <!--  An XSL 3 stylesheet test case, to test xsl:template match 
        with pattern ".", and ".[...]" (i.e, XPath string "." 
        followed by an XPath predicate.
        
        This XSL stylesheet example's algorithm has been borrowed
        from XSLT 3.0 spec.         
  -->			     

  <xsl:output method="html"/>

  <xsl:template match="/">
      <html>
	     <head>
		   <title>XSL transformation result</title>
		 </head>
		 <body>		   
		   <br/><xsl:apply-templates select="unparsed-text-lines('input.txt')"/>
		 </body>
	  </html>
  </xsl:template>
  
  <xsl:template match=".[starts-with(., '==')]">
    <h1><xsl:value-of select="replace(., '==', '')"/></h1>
  </xsl:template>
  
  <xsl:template match=".[starts-with(., '::')]">
    <h2><xsl:value-of select="replace(., '::', '')"/></h2>
  </xsl:template>

  <xsl:template match=".">
    <h3><xsl:value-of select="."/></h3>
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
