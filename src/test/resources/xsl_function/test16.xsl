<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, that tests XPath boolean binary 
       operators 'and' and 'or', when the operands to these XPath 
       operators are stylesheet function calls. 
  -->                
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">     
     <result>
        <andTest>
           <one><xsl:value-of select="fn0:isInteger('abc') and fn0:isInteger('pqr')"/></one>
           <two><xsl:value-of select="fn0:isInteger('abc') and fn0:isInteger(5)"/></two>
           <three><xsl:value-of select="fn0:isInteger(5) and fn0:isInteger('abc')"/></three>
           <four><xsl:value-of select="fn0:isInteger(7) and fn0:isInteger(5)"/></four>
        </andTest>
        <orTest>
           <one><xsl:value-of select="fn0:isInteger('abc') or fn0:isInteger('pqr')"/></one>
           <two><xsl:value-of select="fn0:isInteger('abc') or fn0:isInteger(5)"/></two>
           <three><xsl:value-of select="fn0:isInteger(5) or fn0:isInteger('abc')"/></three>
           <four><xsl:value-of select="fn0:isInteger(7) or fn0:isInteger(5)"/></four>
        </orTest>
     </result>
  </xsl:template>
  
  <!-- A stylesheet function that checks whether, an argument value
       passed to this function is of type xs:integer.
  -->
  <xsl:function name="fn0:isInteger" as="xs:boolean">
    <xsl:param name="a" as="item()"/>
    
    <xsl:sequence select="$a instance of xs:integer"/>
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