<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL test case to test, representing numeric literals and 
        arithmetic operations on them. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <result>
	    <xsl:variable name="num1" select="12388889999999999999999999999999999999999999999999999999999999977777777777777777777777777777"/>
		<xsl:variable name="num2" select="12388889999999999999999999999999999999999999999999999999999999977777777777777777777777777777777.5"/>
		<xsl:variable name="num3" select="0.6"/>
        <xsl:variable name="num4" select="2E3"/>		
	    <one>           
		   <xsl:value-of select="$num1"/>
		</one>
        <two>           
		   <xsl:value-of select="$num2"/>
		</two>		
		<three>           
		   <xsl:value-of select="$num4"/>
		</three>		
		<sum>
		  <xsl:value-of select="$num1 + $num2"/>
		</sum>
		<diff>
		  <xsl:value-of select="$num2 - $num3"/>
		</diff>
		<sum>
		  <xsl:value-of select="$num3 + $num4"/>
		</sum>		
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
