<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"			
				exclude-result-prefixes="xs math"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath 3.1 functions 
         specified in math: namespace -->				

	<xsl:output method="html" indent="yes"/>
	
	<xsl:param name="math_round_decimal_places" select="10" as="xs:integer"/>

	<xsl:template match="/">
	   <html>
	     <head>		    
			<meta name="viewport" content="width=device-width, initial-scale=1"/>
			<style>
				table, th, td {
				  border: 1px solid black;
				  border-collapse: collapse;
				  margin: 10px;
				  padding: 10px;
				}

				table.center {
				  margin-left: auto; 
				  margin-right: auto;
				}
			</style>
			<title>math:sin, math:cos, math:tan values</title>
		 </head>
		 <body>
		     <br/>
		     <table class="center">
			     <tr>
				   <td style="text-align: center;"><b>Angle (deg)</b></td>
				   <td style="text-align: center;"><b>math:sin</b></td>
				   <td style="text-align: center;"><b>math:cos</b></td>
				   <td style="text-align: center;"><b>math:tan</b></td>
				 </tr>
				 <xsl:for-each select="1 to 360">
					<xsl:variable name="math_sin_value1" select="math:sin((math:pi() div 180) * .)" as="xs:double"/>
					<xsl:variable name="math_cos_value1" select="math:cos((math:pi() div 180) * .)" as="xs:double"/>
					<xsl:variable name="math_tan_value1" select="math:tan((math:pi() div 180) * .)" as="xs:double"/>
					<tr>
					   <td style="text-align: center;"><xsl:value-of select="."/></td>
					   <td style="text-align: center;"><xsl:value-of select="round($math_sin_value1, $math_round_decimal_places)"/></td>
					   <td style="text-align: center;"><xsl:value-of select="round($math_cos_value1, $math_round_decimal_places)"/></td>
					   <td style="text-align: center;"><xsl:value-of select="round($math_tan_value1, $math_round_decimal_places)"/></td>
					</tr>
				 </xsl:for-each>
			 </table>
		 </body>
	   </html>
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