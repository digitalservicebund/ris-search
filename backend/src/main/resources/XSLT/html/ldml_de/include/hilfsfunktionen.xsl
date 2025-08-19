<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:akn="http://Inhaltsdaten.LegalDocML.de/1.8.2/" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:math="http://www.w3.org/2005/xpath-functions/math" exclude-result-prefixes="xs math akn" version="3.0">

    <!--
        ****************************************************************************************************************************
        Diese Hilfsfunktionen und -Templates werden inkludiert vom Haupt-Skript (bgbl.xsl oder ris-portal.xsl).
        Die dort deklarierte globale Variable $dokument beinhaltet die LegalDocML.de-Instanz als Baum mit der Wurzel akn:akomaNtoso.

        Pflegeorganisation LegalDocML.de / Philipp Koch / 2024-11-20 - 1.7.1
                                  NeuRIS / Carl Gödecken / 2025-01-15 - 1.8.2
        ****************************************************************************************************************************
    -->

    <!--
        #################
        ### Technisch ###
        #################
    -->

    <!-- Fügt eine Leerzeile und einen Kommentar ins Ausgabedokument ein, sofern Debugging eingeschaltet ist -->
    <xsl:function name="akn:gliederungskommentar" as="node()*">
        <xsl:param name="gliederungsüberschrift" as="xs:string"/>

        <xsl:if test="$debugging (: global parametrisiert beim Saxon-Aufruf :)">
            <xsl:text>

            </xsl:text>
            <xsl:comment select="concat(' ', $gliederungsüberschrift, ' ')"/>
        </xsl:if>
    </xsl:function>

    <xsl:function name="akn:inlinekommentar" as="comment()?">
        <xsl:param name="text" as="xs:string" required="yes"/>

        <xsl:if test="$debugging (: global parametrisiert beim Saxon-Aufruf :)">
            <xsl:comment select="concat(' ', normalize-space($text), ' ')"/>
        </xsl:if>
    </xsl:function>

    <!-- Ersetzt Umlaute -->
    <xsl:function name="akn:encode-for-uri" as="xs:string">
        <xsl:param name="uri" as="xs:string" />
        <xsl:variable name="uri" select="replace($uri, 'ä', 'ae')" />
        <xsl:variable name="uri" select="replace($uri, 'ö', 'oe')" />
        <xsl:variable name="uri" select="replace($uri, 'ü', 'ue')" />
        <xsl:value-of select="$uri" />
    </xsl:function>


    <!--
        ################
        ### Fachlich ###
        ################
    -->

    <!-- Dokumentbaum -->
    <xsl:variable name="dokument" as="element(akn:akomaNtoso)" select="/akn:akomaNtoso"/>


    <!-- Dokumentitel ("Gesetz zur Schlumpfjagd") -->
    <xsl:function name="akn:dokumenttitel" as="xs:string">
        <xsl:sequence select="normalize-space(string-join($dokument/akn:act/akn:preface/akn:longTitle/akn:p/akn:docTitle//text(), ' '))"/>
    </xsl:function>

    <!-- Kurztitel des Dokuments ("(SchlJG)"); gibt den Kurzbezeichner, von Klammern umschlossen, zurück, oder einen leeren String -->
    <xsl:function name="akn:kurztitel" as="xs:string?">
        <xsl:sequence>
            <xsl:variable name="kurztitel" as="xs:string?" select="$dokument/akn:act/akn:preface/akn:longTitle/akn:p/akn:shortTitle/akn:inline"/>
            <xsl:choose>
                <xsl:when test="$kurztitel">
                    <xsl:sequence select="concat('(', $kurztitel, ')')"/>
                </xsl:when>
                <xsl:otherwise><!-- nichts --></xsl:otherwise>
            </xsl:choose>
        </xsl:sequence>
    </xsl:function>

    <!-- Gibt die ELI-Komponenten auf Work-Ebene als Sequenz von Strings zurück -->
    <xsl:function name="akn:eli-komponenten" as="xs:string*">
        <xsl:sequence select="
                (: z. B. 'eli/bund/bgbl-1/2024/301' :)
                tokenize($dokument/akn:act/akn:meta/akn:identification/akn:FRBRWork/akn:FRBRuri/@value, '/')"/>
    </xsl:function>

</xsl:stylesheet>
