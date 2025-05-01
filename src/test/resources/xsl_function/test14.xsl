<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, to test xsl:function instruction.
       We've more than one function definition within this 
       stylesheet. Within this stylesheet example, the function calls
       are chained.
  -->             
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">     
     <result>
       <one>
         <xsl:value-of select="fn0:f1(7)"/>
       </one>
       <two>
         <xsl:value-of select="round(fn0:f1(3.1),2)"/>
       </two>
     </result>
  </xsl:template>
  
  <!-- This stylesheet function, calls the function fn0:sqr. -->
  <xsl:function name="fn0:f1" as="xs:double">
    <xsl:param name="a" as="xs:double"/>
    <xsl:sequence select="fn0:sqr($a + 1)"/>
  </xsl:function>
  
  <!-- A stylesheet function, calculating the square 
       of a xs:double value.
  -->
  <xsl:function name="fn0:sqr" as="xs:double">
    <xsl:param name="a" as="xs:double"/>
    <xsl:sequence select="$a * $a"/>
  </xsl:function>
  
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