<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, that tests an XPath 3.1 inline 
       function expression calling a stylesheet 'xsl:function' function. 
  -->                
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:variable name="func1" select="function($a as xs:string) as xs:string { fn0:getMesg($a) }"/>
  
  <xsl:template match="/">     
     <result>
        <xsl:value-of select="$func1('world')"/>   
     </result>
  </xsl:template>
  
  <xsl:function name="fn0:getMesg" as="xs:string">
     <xsl:param name="mesg" as="xs:string"/>
    
     <xsl:sequence select="'hello ' || $mesg"/>
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