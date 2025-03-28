<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                               
			    version="3.0">
			    
   <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL 3 stylesheet test case, to test xsl:fork 
       instruction having multiple xsl:sequence child elements.
       This stylesheet has xsl:fork instruction that is contained within, 
	   xsl:source-document instruction that runs in non-streaming mode.
	   
	   An XSL transformation algorithm implemented within this stylesheet, 
	   is borrowed from XSLT 3.0 spec.
  -->			    									

  <xsl:output method="xml" omit-xml-declaration="yes"/>

  <xsl:template match="/">
	 <xsl:source-document href="transactions.xml">
	    <xsl:fork>		      
		    <xsl:sequence>
			   <xsl:result-document href="credits.xml">
				  <credits>
					 <xsl:for-each select="transactions/transaction[@value &gt;= 0]">
						<xsl:copy-of select="."/>
					 </xsl:for-each>
				  </credits>
			   </xsl:result-document>
		   </xsl:sequence>			 			 
		   <xsl:sequence>
			   <xsl:result-document href="debits.xml">
				  <debits>
					 <xsl:for-each select="transactions/transaction[@value &lt; 0]">
						<xsl:copy-of select="."/>
					 </xsl:for-each>
				  </debits>
			   </xsl:result-document>
		   </xsl:sequence>             			 
		</xsl:fork>
	 </xsl:source-document>
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
