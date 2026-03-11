<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:f="f"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- This stylesheet, is a subset of an XSL stylesheet from W3C 
        XSLT 3.0 test case attr/as/as-0152. -->                

   <xsl:function name="f:promote" as="xs:double">
      <xsl:param name="p"/>
      <xsl:sequence select="$p"/>
   </xsl:function>
   
   <xsl:param name="base" select="0" as="xs:integer"/>
   
   <xsl:template name="xsl:initial-template">
      <out>
		 <xsl:variable name="var1" select="f:promote(xs:untypedAtomic($base || '1.123'))"/>
		 <a result="{$var1 instance of xs:double}"/>
		 <b result="{$var1 instance of xs:double*}"/>
      </out>
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
   
</xsl:transform>