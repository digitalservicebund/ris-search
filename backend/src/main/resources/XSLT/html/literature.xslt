<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
				xmlns:ris="http://ldml.neuris.de/literature/metadata/"
				exclude-result-prefixes="akn ris">

	<xsl:output method="html"/>

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

	<!-- Main Title-->
	<xsl:template match="akn:FRBRalias[@name='haupttitel']">
		<h1><xsl:value-of select="@value"/></h1>
	</xsl:template>

	<xsl:template match="akn:FRBRalias[@name='dokumentarischerTitel']">
		<h3><xsl:value-of select="@value"/></h3>
	</xsl:template>

	<!-- Outline -->
	<xsl:template match="ris:gliederung">
		<h2>Gliederung</h2>
		<ul>
			<xsl:apply-templates/>
		</ul>
	</xsl:template>

	<xsl:template match="ris:gliederungEntry">
		<li><xsl:value-of select="."/></li>
	</xsl:template>

	<!-- Short Report -->
	<xsl:template match="akn:mainBody">
		<h2>Kurzrefarat</h2>
		<xsl:apply-templates />
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

	<xsl:template match="akn:a">
		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="@href"/>
			</xsl:attribute>
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
