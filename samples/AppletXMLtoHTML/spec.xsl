<?xml version="1.0" encoding="ISO-8859-1" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>
  
  <xsl:template match="spec">
    <html>
      <head>
        <title>
          <xsl:value-of select="title"/>
        </title>
      </head>
      <body>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="spec/title">
    <h1><xsl:apply-templates/></h1>
  </xsl:template>

  <xsl:template match="frontmatter">
    <p><b>Edit Date: </b><xsl:value-of select="./pubdate"/></p>
    <p><b>Author: </b>
      <xsl:element name="a">
        <xsl:attribute name="href">
          <xsl:value-of select="concat('mailto:', author/address/email)"/>
        </xsl:attribute>
        <xsl:value-of select="concat(author/firstname,' ', author/surname, ', ', ./author/orgname)"/>
      </xsl:element>
    </p>    
  </xsl:template>
  
  <xsl:template match="spec/*/title">
    <h2>
      <xsl:choose>
        <xsl:when test="@id">
          <a name="@id">
            <xsl:apply-templates/>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </h2>
  </xsl:template>
  
  <xsl:template name="apply-id-templates">
    <xsl:choose>
      <xsl:when test="@id">
        <a name="{@id}">
          <xsl:apply-templates/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="spec/*/*/title">
    <h3>
      <xsl:call-template name="apply-id-templates"/>
    </h3>
  </xsl:template>
  
  <xsl:template match="para">
    <p><xsl:apply-templates/></p>
  </xsl:template>

  <xsl:template match="variablelist">
    <ul>
    <xsl:for-each select="varlistentry">
      <li>
        <p><b><xsl:apply-templates select="term"/></b><br/>
        <xsl:apply-templates select="listitem"/></p>
      </li>
    </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="orderedlist">
    <ol>
    <xsl:for-each select="listitem">
      <li><xsl:apply-templates/></li>
    </xsl:for-each>
    </ol>
  </xsl:template>

  <xsl:template match="patterns">
    <h2><xsl:value-of select="@module"/><xsl:text> </xsl:text>Patterns</h2>
    <ul>
      <xsl:for-each select="pattern">
        <p>
          <b>
            <xsl:for-each select="pattern-name">
              <xsl:call-template name="apply-id-templates"/>
            </xsl:for-each>
          </b>
          <br/>
        <xsl:apply-templates select="*[name() != 'pattern-name']"/></p>
      </xsl:for-each>
    </ul>
  </xsl:template>
  
  <xsl:template match="pattern/intent">
    <br/><i>Intent: </i><xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="pattern/responsibilities">
    <br/><i>Responsibilities: </i><xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="pattern/potential-alternate-name">
    <br/><i>Potential alternate name: </i><xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="pattern/thread-safety">
    <br/><i>Thread safety: </i><xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="pattern/notes">
    <br/><i>Notes: </i><xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="programlisting">
    <code>
    <pre>
      <xsl:apply-templates/>
    </pre>
    </code>
  </xsl:template>
  
  <xsl:template match="link">
    <A href="#{@linkend}">
      <xsl:apply-templates/>
    </A>
  </xsl:template>
  <xsl:template match="ulink">
    <A href="{@url}">
      <xsl:apply-templates/>
    </A>
  </xsl:template>

  <xsl:template match="termref">
    <xsl:choose>
      <xsl:when test="@link-url">
        <A href="#{@link-url}">
          <xsl:value-of select="."/>
        </A>
      </xsl:when>
      <xsl:otherwise>
        <U><xsl:value-of select="."/></U>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>

