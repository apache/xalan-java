<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 stylesheet test case, to test XSLT function copy-of -->                

	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
	   <result>
          <xsl:variable name="item1" select="copy-of('hello')"/>
		  <a1 isString="{$item1 instance of xs:string}"><xsl:value-of select="$item1"/></a1>
		  <a2 isXsDateTime="{$item1 instance of xs:dateTime}"><xsl:value-of select="$item1"/></a2>
		  <a3 isXsDateTime="{copy-of(xs:dateTime('2004-10-07T12:30:00')) instance of xs:dateTime}"><xsl:value-of select="copy-of(xs:dateTime('2004-10-07T12:30:00'))"/></a3>
		  
		  <xsl:variable name="seq1" select="copy-of(('hello', 'there'))"/>
		  <b seqLength="{count($seq1)}"><xsl:value-of select="$seq1" separator=","/></b>
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