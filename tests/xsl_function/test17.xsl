<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs math fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, that tests XPath arithmetic binary 
       operators '+' and '-', when the operands to these XPath operators 
       are stylesheet function calls. 
  -->                
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">     
     <result>
        <plusTest>
           <xsl:value-of select="fn0:echoArg(2) + fn0:echoArg(5)"/>
        </plusTest>
        <minusTest>
           <xsl:value-of select="fn0:echoArg(7) - fn0:echoArg(3)"/>
        </minusTest>
        <sqrt1>
           <xsl:value-of select="math:sqrt(fn0:echoArg(2) + fn0:echoArg(5))"/>
        </sqrt1>
        <sqrt2>
           <xsl:value-of select="math:sqrt(fn0:echoArg(7) - fn0:echoArg(3))"/>
        </sqrt2>
     </result>
  </xsl:template>
  
  <!-- A stylesheet function, that returns a xs:double value, which is
       equal to the value passed to this function as an argument.
  -->
  <xsl:function name="fn0:echoArg" as="xs:double">
    <xsl:param name="a" as="xs:double"/>
    
    <xsl:sequence select="$a"/>
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