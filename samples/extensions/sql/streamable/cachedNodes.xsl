<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns:sql="org.apache.xalan.lib.sql.XConnection"
                extension-element-prefixes="sql">

<xsl:output method="html" indent="yes"/>

<xsl:param name="driver" select="'org.enhydra.instantdb.jdbc.idbDriver'"/>
<xsl:param name="datasource" select="'jdbc:idb:../../instantdb/sample.prp'"/>
<xsl:param name="query" select="'SELECT * FROM import1'"/>

<xsl:template match="/">
    <xsl:variable name="db" select="sql:new($driver, $datasource)"/>

    <HTML>
      <HEAD>
        <TITLE>List of products</TITLE>
      </HEAD>
      <BODY>
        <TABLE border="1">
        	<xsl:value-of select="sql:enableCacheNodes($db)" />
          <xsl:variable name="table" select='sql:query($db, $query)'/>
          <TR>
             <xsl:for-each select="$table/row-set/column-header">
               <TH><xsl:value-of select="@column-label"/></TH>
             </xsl:for-each>
          </TR>
          <xsl:apply-templates select="$table/row-set/row"/>
        </TABLE>
      </BODY>
    </HTML>
    <xsl:value-of select="sql:close($db)"/>
</xsl:template>

<xsl:template match="row">
  <TR><xsl:apply-templates select="col"/></TR>
</xsl:template>

<xsl:template match="col">
  <TD><xsl:value-of select="text()"/></TD>
</xsl:template>

</xsl:stylesheet>