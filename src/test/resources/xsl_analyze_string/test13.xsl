<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_h.xml -->
   
   <!-- XSLT stylesheet to transform, date with string format '23 March 2002' 
        to the string format '2002-03-23'.
        Note the use of, double curly braces within xsl:analyze-string's regex,
        because the regex attribute is an avt.
        This stylesheet, transforms sequence of xml 'date' elements. -->

   <xsl:output method="xml" indent="yes"/>
     
   <xsl:variable name="months" select="tokenize('January February March April May June July August September October November December','\s+')"/>

   <xsl:template match="/elem">
      <result>
         <xsl:for-each select="date">
             <date>      
                 <xsl:analyze-string select="normalize-space(.)" regex="([0-9]{{1,2}})\s([A-Z][a-z]+)\s([0-9]{{4}})">          
                     <xsl:matching-substring>              
                         <xsl:value-of select="regex-group(3)"/>
                         <xsl:text>-</xsl:text>
                         <xsl:variable name="monthNum">
                            <xsl:value-of select="index-of($months, regex-group(2))"/>
                         </xsl:variable>
                         <xsl:choose>
                            <xsl:when test="$monthNum &lt; 10">
                               <xsl:value-of select="concat('0',$monthNum)"/>
                            </xsl:when>
                            <xsl:otherwise>
                               <xsl:value-of select="$monthNum"/>
                            </xsl:otherwise>
                         </xsl:choose>
                         <xsl:text>-</xsl:text>
                         <xsl:value-of select="regex-group(1)"/>
                     </xsl:matching-substring>
                 </xsl:analyze-string>
             </date>
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