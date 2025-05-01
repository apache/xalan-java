<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
   
  <!-- An XSLT stylesheet test case, that uses xsl:iterate 
       having xsl:param, to do primitive mathematical analysis 
       on a sequence of numbers.  -->                 

  <xsl:output method="xml" indent="yes"/>
  
  <xsl:variable name="x" select="1024"/>  
  <xsl:variable name="y" select="1520"/>
  
  <xsl:variable name="numberList">
     <xsl:for-each select="$x to $y">
       <val><xsl:value-of select="."/></val>
     </xsl:for-each>
  </xsl:variable>
  
  <xsl:template match="/">
     <result startNum="{$x}" endNum="{$y}">
        <xsl:iterate select="$numberList/val">
           <xsl:param name="count" select="0"/>
           <xsl:on-completion select="concat('Total numbers within range : ', $count)"/>                       
           <xsl:next-iteration>
              <xsl:with-param name="count" select="$count + 1"/>
           </xsl:next-iteration>        
        </xsl:iterate>
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
