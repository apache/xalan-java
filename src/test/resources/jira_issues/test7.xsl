<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"			    
			    xmlns:xs="http://www.w3.org/2001/XMLSchema"
			    exclude-result-prefixes="xs" 
				version="3.0">
				
  <!-- Author: mukulg@apache.org -->				
				
  <!-- use with test6.xml -->

  <!-- An XSL stylesheet test case, for jira issue XALANJ-2800 (reported by 
       Martin Honnen) -->  

  <xsl:output method="xml" indent="yes"/>

  <xsl:key name="car-by-color" match="cars/car" use="@color"/>
     
  <xsl:key name="lot-for-car" match="lot" use="car/@vin"/>
   
  <xsl:template match="lot">
      <lot id="{@id}"/>
  </xsl:template>
  
  <xsl:template match="/">
      <set>
          <problem>
              <question>Which lots have red cars?</question>
              <answer>
                 <xsl:apply-templates select="key('car-by-color', 'red') ! key('lot-for-car', @vin)"/>
              </answer>
          </problem>
      </set>
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
