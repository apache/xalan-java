<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_f.xml -->
   
   <!-- This example demonstrates, using xsl:analyze-string instruction,
        using the regex pattern flag for case-insensitive string contents 
        matching. -->

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <elem>
         <!-- producing an alternate sequence of, matching and non matching 
              string parts, in order from left to right scan of the input 
              string. -->
         <xsl:analyze-string select="elem" regex="(a|b)" flags="i">
	    <xsl:matching-substring>
	       <delim><xsl:value-of select="."/></delim>   
	    </xsl:matching-substring>
	    <xsl:non-matching-substring>
	       <token><xsl:value-of select="."/></token>
	    </xsl:non-matching-substring>
         </xsl:analyze-string>
      </elem>
   </xsl:template>
   
   <!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the  "License");
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