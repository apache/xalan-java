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
       <xsl:value-of select="fn0:f1()"/>
     </result>
  </xsl:template>
  
  <!-- This stylesheet function, calls another stylesheet function. -->
  <xsl:function name="fn0:f1" as="xs:integer">
    <xsl:sequence select="fn0:f2()"/>
  </xsl:function>
  
  <!-- This stylesheet function, calls another stylesheet function. -->
  <xsl:function name="fn0:f2" as="xs:integer">
    <xsl:sequence select="fn0:f3()"/>
  </xsl:function>
  
  <xsl:function name="fn0:f3" as="xs:integer">
    <xsl:sequence select="12"/>
  </xsl:function>
  
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