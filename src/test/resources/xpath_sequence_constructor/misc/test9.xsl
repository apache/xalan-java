<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- XSL stylesheet test case, to test concatenation of two or more 
       XPath 'if' expressions. -->                 
   
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <result>
	    <xsl:variable name="v1" select="1"/>
		<xsl:variable name="v2" select="2"/>
		<xsl:variable name="v3" select="3"/>
		<xsl:value-of select="if ($v2 &gt; $v1 or $v2 &gt; $v3) then 'hello' else (),
		                      if ($v2 &gt; $v1 and $v3 &lt; ($v2 + 2)) then 'there' else 'nice',
			   				  if ($v2 = ($v1 + 1)) then 'A' else (),
							  if ($v3 = ($v2 + 1)) then 'B' else (),
							  if ($v2 = ($v1 + 1)) then 'C' else (),
							  if ($v3 = ($v2 + 1)) then 'D' else ()"/>
        
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
