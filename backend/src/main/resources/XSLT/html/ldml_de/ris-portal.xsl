<?xml version="1.0" encoding="UTF-8"?>
<!--
    Transformiert LegalDocML.de-Instanzen nach XHTML 5.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:akn="http://Inhaltsdaten.LegalDocML.de/1.8.2/"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xs xsi akn xhtml" version="3.0"
>
    <xsl:output method="html" indent="yes" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>

    <!-- Include utilities -->
    <xsl:include href="include/hilfsfunktionen.xsl"/>
    <!-- Import base rules to be overridden -->
    <xsl:import href="include/inhalt.xsl"/>

    <!-- Parameters can be modified when calling the template -->
    <xsl:param name="debugging" as="xs:boolean" select="false()"/>
    <xsl:param name="pfad-css-datei" as="xs:string" select="''"/>
    <xsl:param name="durchreichen" as="xs:boolean" select="true()"/>
    <!-- base path to use when generating links -->
    <xsl:param name="dokumentpfad" as="xs:string" select="'/regelungstext-1'"/>
    <!-- base path to use when linking to resources, such as images -->
    <xsl:param name="ressourcenpfad" as="xs:string" select="''" />
    <!-- article eId can be specified to only output a single article -->
    <xsl:param name="article-eid" as="xs:string" select="''"/>
    <xsl:variable name="is-single-article" as="xs:boolean" select="$article-eid != ''"/>
    <!-- override param in inhalt.xsl -->
    <xsl:param name="erlaube-links-in-heading" select="$is-single-article"/>

    <!-- File paths for debug output -->
    <xsl:variable name="quellpfad" select="base-uri(.)"/>
    <xsl:variable name="zielpfad" select="replace($quellpfad, '\.xml', '.html')"/>

    <!-- Overrides for custom class names -->
    <xsl:variable name="textabsatz" as="xs:string" select="'akn-p'"/>
    <xsl:variable name="juristischer-absatz" as="xs:string" select="'akn-paragraph'"/>

    <!-- ******************************************************************************************************* -->

    <!-- suppress output if an article-eid is specified but cannot be found -->
    <xsl:template match="akn:akomaNtoso[$is-single-article and count(//akn:article[@eId=$article-eid] | //akn:attachment[@eId=$article-eid] | //akn:formula[@eId=$article-eid]) = 0]">
        <xsl:message terminate="yes">EID_NOT_FOUND: <xsl:value-of select="$article-eid"/></xsl:message>
    </xsl:template>

    <!-- Entrypoint -->
    <xsl:template match="akn:akomaNtoso">
        <xsl:result-document href="{$zielpfad}">
            <xsl:if test="$debugging">
            <xsl:message>* [Quelle]
                <xsl:value-of select="translate(replace($quellpfad, 'file:/', ''), '/', '\')"/>
            </xsl:message>
            <xsl:message>* [ HTML ]
                <xsl:value-of select="translate(replace($zielpfad, 'file:/', ''), '/', '\')"/>
            </xsl:message>
            </xsl:if>

            <html lang="de" xml:lang="de">
                <head>
                    <meta name="erzeugt" content="{current-dateTime()}"/>
                    <meta name="debugging-information"
                          content="{if ($debugging) then ('enthalten') else ('ausgeblendet')}"/>
                    <xsl:if test="$pfad-css-datei">
                        <link rel="stylesheet" type="text/css" href="{$pfad-css-datei}"/>
                    </xsl:if>
                    <title>
                        <xsl:value-of select="akn:dokumenttitel()"/>&#160;<xsl:value-of select="akn:kurztitel()"/>
                    </title>
                </head>
                <body class="akn-akomaNtoso">
                    <xsl:choose>
                        <xsl:when test="$article-eid=''">
                            <!-- process all articles and other content normally -->
                            <xsl:apply-templates select="@* | node()"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <!-- process only article or attachment indicated by eId -->
                            <xsl:for-each select="//akn:preamble/akn:formula[@eId=$article-eid]">
                                <h2 class="{$einzelvorschrift}">Eingangsformel</h2>
                                <xsl:call-template name="article-with-basic-heading"/>
                            </xsl:for-each>
                            <xsl:for-each select="//akn:article[@eId=$article-eid]">
                                <xsl:call-template name="article-with-basic-heading"/>
                            </xsl:for-each>
                            <xsl:for-each select="//akn:conclusions/akn:formula[@eId=$article-eid]">
                                <h2 class="{$einzelvorschrift}">Schlussformel</h2>
                                <xsl:call-template name="article-with-basic-heading"/>
                            </xsl:for-each>
                            <xsl:for-each select="//akn:attachment[@eId=$article-eid]">
                                <xsl:call-template name="attachment"/>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>

                </body>
            </html>
        </xsl:result-document>
    </xsl:template>

    <xsl:template match="akn:preamble/akn:formula">
        <xsl:sequence select="akn:gliederungskommentar('Eingangsformel (akn:formula)')"/>
        <section class="{$eingangsformel}">
            <xsl:apply-templates select="@*"/>
            <a href="{concat($dokumentpfad, '/', @eId)}">
                <h2 class="{$einzelvorschrift}">
                    <span class="akn-heading">
                        Eingangsformel
                    </span>
                </h2>
            </a>
            <xsl:apply-templates/>
        </section>
    </xsl:template>

    <xsl:template match="akn:article" name="article-with-link-heading">
        <article>
            <xsl:apply-templates select="@*" />
            <!-- wrap h2 in link that points to $dokumentpfad/@eId -->
            <a href="{concat($dokumentpfad, '/', akn:encode-for-uri(@eId))}">
                <xsl:call-template name="num-and-heading-h2"/>
            </a>
            <!-- process remaining elements -->
            <xsl:apply-templates select="node()[not(self::akn:num | self::akn:heading)]"/>
            <!-- output authorialNote and note contents at the end of an article -->
            <xsl:call-template name="authorial-notes-collection"/>
            <xsl:call-template name="notes-collection" />
        </article>
    </xsl:template>

    <xsl:template name="article-with-basic-heading">
        <article>
            <!-- Start processing from the specified article -->
            <xsl:apply-templates select="@*" />
            <xsl:call-template name="num-and-heading-h2"/>
            <!-- process remaining elements -->
            <xsl:apply-templates select="node()[not(self::akn:num | self::akn:heading)]"/>

            <!-- output authorialNote and note contents at the end of an article -->
            <xsl:call-template name="authorial-notes-collection"/>
            <xsl:call-template name="notes-collection" />
        </article>
    </xsl:template>

    <xsl:template name="num-and-heading-h2">
        <h2 class="{$einzelvorschrift}">
            <xsl:apply-templates select="akn:num"/>
            <xsl:if test="normalize-space(akn:heading)">
                <xsl:text> </xsl:text>
                <span class="akn-heading">
                    <xsl:apply-templates select="akn:heading/@* | akn:heading/node()"/>
                </span>
            </xsl:if>
        </h2>
    </xsl:template>

    <xsl:template match="akn:act">
        <div class="akn-act">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="akn:body">
        <div class="akn-body">
            <xsl:apply-templates />
        </div>
    </xsl:template>

    <!-- Custom handling for num elements: add any contents of marker as @data-marker -->
    <xsl:template match="akn:num">
        <span class="akn-num">
            <xsl:apply-templates select="@*"/>
            <xsl:if test="akn:marker">
                <xsl:attribute name="data-marker">
                    <xsl:value-of select="normalize-space(akn:marker/@name)" />
                </xsl:attribute>
            </xsl:if>
            <xsl:sequence select="akn:inlinekommentar('Zählbezeichnung (akn:num)')"/>
            <xsl:apply-templates select="node()[not(self::marker)]" />
        </span>
    </xsl:template>

    <!--
        #########################
        ### Table of Contents ###
        #########################
    -->

    <!-- Template to process akn:toc -->
    <xsl:template match="akn:toc">
        <!-- Enclose tocItems in a div -->
        <div class="official-toc">
            <xsl:apply-templates select="akn:tocItem"/>
        </div>
    </xsl:template>

    <!-- Template to process each akn:tocItem -->
    <xsl:template match="akn:tocItem">
        <div class="level-{@level}">
            <!-- Convert akn:span elements to HTML spans, adding a space before the second element if it exists -->
            <xsl:apply-templates select="akn:span[1]"/>
            <xsl:if test="akn:span[2]">
                <xsl:text> </xsl:text>
            </xsl:if>
            <xsl:apply-templates select="akn:span[2]"/>
        </div>
    </xsl:template>

    <!-- Regelungstext-Schluss: Container
        remove hr break from conclusions container
    -->
    <xsl:template match = "akn:conclusions">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="akn:conclusions/akn:formula">

        <xsl:sequence select="akn:gliederungskommentar('Regelungstext-Schluss (akn:conclusions)')"/>
        <section class="{$regelungstext-schluss}">
            <xsl:apply-templates select="@*"/>
            <a href="{concat($dokumentpfad, '/', @eId)}">
                <h2 class="{$einzelvorschrift}">
                    <span class="akn-heading">
                        Schlussformel
                    </span>
                </h2>
            </a>
            <xsl:apply-templates/>
        </section>
    </xsl:template>

    <!--
        ###################
        ### Attachments ###
        ###################
    -->

    <!-- Handle individual attachment -->
    <xsl:template match="akn:attachment" name="attachment">
        <div class="akn-attachment" id="{@eId}">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <!-- Handle document references -->
    <xsl:template match="akn:documentRef">
        <div class="included-document" data-source="{@href}">
            <xsl:try>
                <!-- Include referenced document -->
                <xsl:apply-templates select="document(@href)/akn:akomaNtoso/*">
                    <xsl:with-param name="attachment-eId" select="../@eId" tunnel="yes"/>
                </xsl:apply-templates>
                <xsl:catch>
                    <xsl:message terminate="yes">DOCUMENT_REF_NOT_FOUND: <xsl:value-of select="@href"/></xsl:message>
                </xsl:catch>
            </xsl:try>
        </div>
    </xsl:template>

    <!-- Handle titles of attachments -->
    <xsl:template match="akn:doc/akn:preface/akn:block/akn:docTitle" >
        <xsl:param name="attachment-eId" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="not($is-single-article)">
                <a href="{concat($dokumentpfad, '/', akn:encode-for-uri($attachment-eId))}">
                    <h2 class="{$einzelvorschrift}">
                        <xsl:apply-templates/>
                    </h2>
                </a>
            </xsl:when>
            <xsl:otherwise>
                <h2 class="{$einzelvorschrift}">
                    <xsl:apply-templates/>
                </h2>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Insert space between attachment number and "Bezug" -->
    <xsl:template
            match="akn:inline[@refersTo='anlageregelungstext-num' and following-sibling::akn:inline[@refersTo='anlageregelungstext-bezug']]">
        <span id="{@eId}" class="anlageregelungstext-num"><xsl:value-of select="text()"/></span>
        <xsl:text> </xsl:text>
    </xsl:template>

    <!-- Handle attachments without preface (and therefore lacking a title) -->
    <xsl:template match="akn:doc[not(akn:preface)]">
        <xsl:param name="attachment-eId" tunnel="yes"/>
        <div class="akn-doc" data-name="offene-struktur">
        <xsl:choose>
            <xsl:when test="not($is-single-article)">
                <a href="{concat($dokumentpfad, '/', akn:encode-for-uri($attachment-eId))}">
                    <h2 class="{$einzelvorschrift}">
                        Anlage
                    </h2>
                </a>
            </xsl:when>
            <xsl:otherwise>
                <h2 class="{$einzelvorschrift}">
                    Anlage
                </h2>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates />
        </div>
    </xsl:template>

    <!--
        ###################
        ### Images ###
        ###################
    -->

</xsl:stylesheet>
