<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_c.xml -->
   
   <!-- This XSLT stylesheet does the work, what XPath 3.1 quantified expressions 
        (i.e, as per the following XPath 3.1 grammar fragment. these XPath expressions 
        evaluate to a boolean result) could do.
   
        QuantifiedExpr ::= ("some" | "every") "$" VarName "in" ExprSingle ("," "$" 
                                        VarName "in" ExprSingle)* "satisfies" ExprSingle
                                        
        This stylesheet example, illustrates the ability of XSLT named template 
        ("isXmlElementE1eXistsWithinAllElementsA" within this example) to function as a 
        re-usable fragment of XSLT code.
   -->

   <xsl:output method="text"/>

   <xsl:template match="/elem">      
      <xsl:variable name="result1">
         <xsl:call-template name="isXmlElementE1eXistsWithinAllElementsA"/>
      </xsl:variable>
      
      <xsl:if test="$result1">
         <xsl:text>check 1 : all the xml elements '(test1 | test2)/a' contain an xml element 'e1'</xsl:text>
      </xsl:if>
      
      <xsl:text>&#xa;</xsl:text>
      
      <xsl:variable name="result2">
         <xsl:call-template name="isXmlElementE1eXistsWithinAllElementsA"/>
      </xsl:variable>
            
      <xsl:if test="$result2">
         <xsl:text>check 2 : all the xml elements '(test1 | test2)/a' contain an xml element 'e1'</xsl:text>
      </xsl:if>
   </xsl:template>
   
   <!-- an XSLT named template -->
   <xsl:template name="isXmlElementE1eXistsWithinAllElementsA">
      <xsl:value-of select="count((test1 | test2)/a) = count((test1 | test2)/a[e1])"/>
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