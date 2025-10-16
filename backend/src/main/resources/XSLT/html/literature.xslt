<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
				exclude-result-prefixes="akn">

	<xsl:output method="html" encoding="UTF-8" indent="yes"/>

	<xsl:strip-space elements="*"/>

	<xsl:template match="akn:doc">
		<html>
			<head>
				<title>
					<xsl:value-of select="//akn:FRBRalias[@name='haupttitel']/@value"/>
				</title>
			</head>
			<body>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>

	<!-- always render main title if it exists -->
	<xsl:template match="akn:FRBRalias[@name='haupttitel']">
		<h1 id="title"><xsl:value-of select="@value"/></h1>
	</xsl:template>

	<!-- render documentary title differently based on main title existence -->
	<xsl:template match="akn:FRBRalias[@name='dokumentarischerTitel']">
		<xsl:variable name="haupttitelExists" select="../akn:FRBRalias[@name='haupttitel']/@value"/>
		<xsl:choose>
			<xsl:when test="$haupttitelExists">
				<h3 id="dokumentarischerTitel"><xsl:value-of select="@value"/></h3>
			</xsl:when>
			<xsl:otherwise>
				<h1 id="title"><xsl:value-of select="@value"/></h1>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Outline -->
	<xsl:template match="*[local-name()='gliederung']">
		<h2>Gliederung</h2>
		<ul>
			<xsl:apply-templates/>
		</ul>
	</xsl:template>

	<xsl:template match="*[local-name()='gliederungEntry']">
		<li><xsl:value-of select="."/></li>
	</xsl:template>

	<!-- Short Report -->
	<!-- if there is no short report the main body only contains an empty hcontainer -->
	<xsl:template match="akn:mainBody">
		<xsl:if test="not(akn:hcontainer)">
			<h2>Kurzreferat</h2>
			<xsl:apply-templates />
		</xsl:if>
	</xsl:template>

	<!--***************************************************************************************-->
	<!--Html transformation-->
	<!--***************************************************************************************-->

	<!-- akn html elements with same name -->
	<xsl:template match="akn:p|akn:div|akn:span|akn:sub|akn:sup">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="akn:br">
		<br />
	</xsl:template>

	<xsl:template match="akn:a[@href]">
		<a href="{@href}">
			<xsl:apply-templates/>
		</a>
	</xsl:template>

	<xsl:template match="akn:inline[@name='em']">
		<em><xsl:apply-templates/></em>
	</xsl:template>

	<xsl:template match="akn:inline[@name='strong']">
		<strong><xsl:apply-templates/></strong>
	</xsl:template>

	<xsl:template match="akn:mainBody//text()">
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="text()"/>
</xsl:stylesheet>
