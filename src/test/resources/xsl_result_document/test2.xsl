<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                               
			    version="3.0">					

  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL 3 stylesheet test case, to test xsl:result-document 
       instruction having method="text". -->
  
  <!-- This specification of xsl:output method is not related to 
       xsl:result-document element's 'method' attribute. -->     
  <xsl:output method="text"/>

  <xsl:template match="/">
     <xsl:variable name="txnXmlDocNode" select="doc('transactions.xml')"/>
	 <xsl:result-document href="credits.txt" method="text">
	    <xsl:variable name="creditDetailsCsvStrValue" select="string-join(for $strValue in $txnXmlDocNode/transactions/transaction[@value &gt;= 0]/@value 
	    																																return $strValue, ',')"/>
		<xsl:value-of select="$creditDetailsCsvStrValue"/>
	 </xsl:result-document>
	 <xsl:result-document href="debits.txt" method="text">
	    <xsl:variable name="debitDetailsCsvStrValue" select="string-join(for $strValue in $txnXmlDocNode/transactions/transaction[@value &lt; 0]/@value 
	    																																return $strValue, ',')"/>
		<xsl:value-of select="$debitDetailsCsvStrValue"/>
	 </xsl:result-document>
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
