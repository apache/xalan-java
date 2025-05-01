<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"               
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org --> 			    
				
  <!-- use with composite_grouping_key_test1.xml -->
  
  <!-- An XSL 3 stylesheet test case, to test xsl:for-each-group 
       instruction's 'composite' attribute. 
       
       An XSL stylesheet algorithm for this test case, has been
       borrowed from XSLT 3.0 spec.
  -->				

  <xsl:output method="xml" indent="yes"/>				
  
  <xsl:template match="/">
	 <result>
	   <xsl:for-each-group select="cities/city" 
						   group-by="@name, @country"
						   composite="yes">
         <p>
		    <xsl:value-of select="current-grouping-key()[1] || ', ' ||
                                  current-grouping-key()[2] || ': ' || 
                                  avg(current-group()/@pop)"/>
		 </p>
       </xsl:for-each-group>
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
