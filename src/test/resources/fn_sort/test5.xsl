<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_c.xml -->
   
   <!-- An XSLT stylesheet test case, to test XPath 3.1 fn:sort function,
        by reading input data from an XML external source document.
        
        The fn:sort function call result, produced by this stylesheet,
        is in the descending order (due to the fact that, fn:sort function
        call specifies an XPath sort key expression as -1 * number(..)). 
        
        This stylesheet example, post processes the result of fn:sort
        function call, by the xsl:iterate instruction.
   -->                

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/document">
      <document>
        <xsl:iterate select="sort((a | data/@*), (), function($val) { -1 * number($val) })">
          <val>
            <xsl:value-of select="."/>
          </val>        
        </xsl:iterate>
      </document>
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