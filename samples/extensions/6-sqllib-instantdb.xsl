<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns:sql="org.apache.xalan.lib.sql.XConnection"
                extension-element-prefixes="sql">
  <xsl:output method="html" indent="yes"/>
  <xsl:param name="query" select="'SELECT * FROM import1'"/>
 
  <xsl:template match="/">
    <!-- 1. Make the connection -->
    <xsl:variable name="products"
                  select="sql:new('com.lutris.instantdb.jdbc.idbDriver',
                                'jdbc:idb:./instantdb/sample.prp')"/>
    <!--2. Execute the query -->
    <xsl:variable name="table" select='sql:query($products, $query)'/>
    <HTML>
      <HEAD>
        <TITLE>Products</TITLE>
      </HEAD>
      <BODY>
        <TABLE border="1">
          <TR>
          <!-- Get column-label attribute from each column-header-->
          <xsl:for-each select="$table/sql/metadata/column-header">
            <TH><xsl:value-of select="@column-label"/></TH>
          </xsl:for-each>
          </TR>
          <xsl:apply-templates select="$table/sql/row-set/row"/>
          <xsl:text>&#10;</xsl:text>
        </TABLE>
      </BODY>
    </HTML> 
    <!-- 3. Close the connection -->
    <xsl:value-of select="sql:close($products)"/>
  </xsl:template>

  <xsl:template match="row">
        <TR>
          <xsl:apply-templates select="col"/>
        </TR>
  </xsl:template>

  <xsl:template match="col">
    <TD>
      <!-- Here is the column data -->
      <xsl:value-of select="text()"/>
    </TD>
  </xsl:template>

</xsl:stylesheet>