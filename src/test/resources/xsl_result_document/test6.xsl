<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
			    version="3.0">					
  
  <!-- Author: mukulg@apache.org -->
       
  <!-- use with transactions.xml -->
  
  <!-- An XSL 3 stylesheet test case, to test xsl:result-document 
       instruction having method="html". -->
  
  <xsl:output method="html"/>    

  <xsl:template match="/">
     <!-- This xsl:result-document instruction, transforms information from 
	      an XML document file transactions.xml (which is the primary XML 
		  document source of this XSL transformation) to an HTML document 
		  file with file name specified by this xsl:result-document element's 
		  attribute "href". --> 
     <xsl:result-document href="transactions.html" method="html">
		<html>
		  <head>
		    <title>Transaction information</title>
		  </head>
		  <body>
		     <br/>
		     <table align="center">
                <tr>
                   <td style="text-align: center;"><b>Sl. no</b></td>
				   <td>&#160;</td>
				   <td style="text-align: center;"><b>Transaction value</b></td>
				</tr>				
				<xsl:for-each select="transactions/transaction">
			       <tr>
				      <td style="text-align: center;"><xsl:value-of select="position() || '.'"/></td>
					  <td>&#160;</td>
					  <td style="text-align: center;"><xsl:value-of select="@value"/></td>
				   </tr>
			    </xsl:for-each>
			 </table>
		  </body>
		</html>
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
