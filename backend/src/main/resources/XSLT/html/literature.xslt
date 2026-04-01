<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:akn="http://docs.oasis-open.org/legaldocml/ns/akn/3.0"
				xmlns:ris="http://ldml.neuris.de/literature/unselbstaendig/meta/"
				exclude-result-prefixes="akn ris">

	<xsl:output method="html" encoding="UTF-8" indent="yes"/>

	<xsl:strip-space elements="*"/>

	<xsl:variable name="titles" as="node()*" select="(//akn:FRBRWork/akn:FRBRalias[@name='haupttitel']/@value
									| //akn:FRBRWork/akn:FRBRalias[@name='dokumentarischerTitel']/@value
									| //akn:FRBRWork/akn:FRBRalias[@name='haupttitelZusatz']/@value)"/>

	<xsl:template match="akn:doc">
		<html>
			<head>
				<title>
					<xsl:value-of select="$titles[1]" />
				</title>
			</head>
			<body>
				<xsl:apply-templates select="//akn:FRBRWork"/>
				<xsl:apply-templates select="//*[local-name()='gliederung']"/>
				<xsl:apply-templates select="//akn:mainBody"/>
				<xsl:apply-templates select="//akn:otherReferences[@source='active']"/>
				<xsl:apply-templates select="//akn:otherReferences[@source='passive']"/>
			</body>
		</html>
	</xsl:template>


	<!-- render titles -->
	<xsl:template match="akn:FRBRWork">
		<xsl:if test="$titles[1]">
			<h1><xsl:value-of select="$titles[1]"/></h1>
		</xsl:if>
		<xsl:if test="$titles[2]">
			<section id="zusaetzliche-titel">
				<h2>Zusätzliche Titel</h2>
				<xsl:for-each select="$titles[position() > 1]">
					<p><xsl:value-of select="."/></p>
				</xsl:for-each>
			</section>
		</xsl:if>
	</xsl:template>

	<!-- Outline -->
	<xsl:template match="*[local-name()='gliederung']">
		<section id="gliederung">
			<h2>Gliederung</h2>
			<ul>
				<xsl:apply-templates/>
			</ul>
		</section>
	</xsl:template>

	<xsl:template match="*[local-name()='gliederungEntry']">
		<li><xsl:value-of select="."/></li>
	</xsl:template>

	<!-- Short Report -->
	<!-- if there is no short report the main body only contains an empty hcontainer -->
	<xsl:template match="akn:mainBody">
		<xsl:if test="not(akn:hcontainer)">
			<section id="kurzreferat">
				<h2>Kurzreferat</h2>
				<xsl:apply-templates />
			</section>
		</xsl:if>
	</xsl:template>

	<!-- Active references -->
	<xsl:template match="akn:otherReferences[@source='active']">
		<section id="dieser-beitrag-zitiert">
			<h2>Dieser Beitrag zitiert</h2>
			<xsl:call-template name="references"/>
		</section>
	</xsl:template>

	<!-- Passive references -->
	<xsl:template match="akn:otherReferences[@source='passive']">
		<section id="dieser-beitrag-wird-zitiert">
			<h2>Dieser Beitrag wird zitiert</h2>
			<xsl:call-template name="references"/>
		</section>
	</xsl:template>

	<!-- active and passive contents references -->
	<xsl:template name="references" >
		<!-- Rechtsprechung -->
		<xsl:if test="akn:implicitReference/ris:caselawReference">
			<h3>Rechtsprechung</h3>
			<ul>
			<xsl:for-each select="akn:implicitReference[ris:caselawReference]">
				<li><xsl:value-of select="@showAs" /></li>
			</xsl:for-each>
			</ul>
		</xsl:if>

		<!-- Verwaltungsvorschriften -->
		<xsl:if test="akn:implicitReference/ris:verwaltungsvorschriftReference">
			<h3>Verwaltungsvorschriften</h3>
			<ul>
			<xsl:for-each select="akn:implicitReference[ris:verwaltungsvorschriftReference]">
				<li><xsl:value-of select="@showAs" /></li>
			</xsl:for-each>
			</ul>
		</xsl:if>

		<!-- Literaturnachweise -->
		<xsl:if test="akn:implicitReference[ris:unselbstaendigeLiteraturReference or ris:selbstaendigeLiteraturReference]">
			<h3>Literaturnachweise</h3>
			<ul>
			<xsl:for-each select="akn:implicitReference[ris:unselbstaendigeLiteraturReference or ris:selbstaendigeLiteraturReference]">
				<li><xsl:value-of select="@showAs" /></li>
			</xsl:for-each>
			</ul>
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
