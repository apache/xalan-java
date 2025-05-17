<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"    
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- XSL stylesheet test case, to test XPath 'for' and other 
         such expressions contained within '(' & ')'. -->                
	
	<xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">
	   <result>
		  <one><xsl:value-of select="(for $x in 1 to 5 return $x)"/></one>
		  <two><xsl:value-of select="(let $x := 1 return $x + 3)"/></two>
		  <xsl:variable name="seq1" select="(false(), false())"/>
		  <xsl:variable name="seq2" select="(false(), true())"/>
		  <three><xsl:value-of select="(some $x in $seq1 satisfies ($x eq true()))"/>, <xsl:value-of select="(some $x in $seq2 satisfies ($x eq true()))"/></three>
		  <xsl:variable name="seq3" select="(false(), true())"/>
		  <xsl:variable name="seq4" select="(true(), true())"/>
		  <four><xsl:value-of select="(every $x in $seq3 satisfies ($x eq true()))"/>, <xsl:value-of select="(every $x in $seq4 satisfies ($x eq true()))"/></four>
		  <five><xsl:value-of select="(if (true()) then 'abc' else 'pqr')"/>, <xsl:value-of select="(if (false()) then 'abc' else 'pqr')"/></five>
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