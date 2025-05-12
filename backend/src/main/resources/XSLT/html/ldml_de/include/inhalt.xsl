<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:akn="http://Inhaltsdaten.LegalDocML.de/1.7.2/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:ris="http://MetadatenRIS.LegalDocML.de/1.7.2/" exclude-result-prefixes="xs xsi akn xhtml ris" version="2.0">
    <!-- *******************************************************************************************************
         NeuRIS / Carl Gödecken / 2025-01-15 - 1.7.2
         ******************************************************************************************************* -->

    <!-- *******************************************************************************************************
         Globale Variablen
         ******************************************************************************************************* -->
    <!-- Literale für CSS-Klassen bzw. IDs -->
    <xsl:variable name="dokumentenkopf" as="xs:string" select="'dokumentenkopf'"/>
    <xsl:variable name="dokumententitel" as="xs:string" select="'dokumententitel'"/>
    <xsl:variable name="titel" as="xs:string" select="'titel'"/>
    <xsl:variable name="kurztitel" as="xs:string" select="'kurztitel'"/>
    <xsl:variable name="ausfertigungsdatum" as="xs:string" select="'ausfertigungsdatum'"/>
    <xsl:variable name="eingangsformel" as="xs:string" select="'eingangsformel'"/>
    <xsl:variable name="absatztext" as="xs:string" select="'absatztext'"/>
    <xsl:variable name="regelungstext-hauptteil" as="xs:string" select="'regelungstext-hauptteil'"/>
    <xsl:variable name="einzelvorschrift" as="xs:string" select="'einzelvorschrift'"/>
    <xsl:variable name="juristischer-absatz" as="xs:string" select="'juristischer-absatz'"/>
    <xsl:variable name="untergliederung" as="xs:string" select="'untergliederung'"/>
    <xsl:variable name="textabsatz" as="xs:string" select="'textabsatz'"/>
    <xsl:variable name="einleitender-text" as="xs:string" select="'einleitender-text'"/>
    <xsl:variable name="untergliederungspunkt" as="xs:string" select="'untergliederungspunkt'"/>
    <xsl:variable name="regelungstext-schluss" as="xs:string" select="'regelungstext-schluss'"/>
    <xsl:variable name="signatur" as="xs:string" select="'signatur'"/>
    <xsl:variable name="funktionsbezeichnung" as="xs:string" select="'funktionsbezeichnung'"/>
    <xsl:variable name="person" as="xs:string" select="'person'"/>
    <xsl:variable name="trennlinie" as="xs:string" select="'trennlinie'"/>
    <xsl:variable name="fussnoten" as="xs:string" select="'fussnoten'"/>
    <xsl:variable name="nichtamtliche-fussnoten" as="xs:string" select="'nichtamtliche-fussnoten'"/>
    <xsl:variable name="fussnote" as="xs:string" select="'fussnote'"/>
    <xsl:variable name="marker" as="xs:string" select="'marker'"/>
    <xsl:variable name="rueckverweis" as="xs:string" select="'rueckverweis'"/>
    <xsl:param name="erlaube-links-in-heading" as="xs:boolean" select="false()"/>
    <xsl:param name="ressourcenpfad" as="xs:string" select="''"/>

    <!-- ******************************************************************************************************* -->

    <!--
        Ignorieren
        ##########
        (AKN-Elemente, die keine Entsprechung in HTML haben sollen, müssen aktiv "nicht-behandelt" werden ("Durchreich-Template"),
        weil es sonst wegen der generischen Attributtemplates (betreffs @eId usw.) zu Fehlern kommt.)
    -->
    <xsl:template match="akn:act">
        <xsl:apply-templates select="node() | @*"/>
    </xsl:template>

    <xsl:template match="akn:wrapUp">
        <div class="akn-wrapUp">
            <xsl:apply-templates select="node() | @*"/>
        </div>
    </xsl:template>

    <!-- Komplett leere Art- und Zählbezeichnungen, die aus Schema-Gründen auch bei Einzelvorschriften angegeben werden müssen,
         die nicht weiter untergliedert sind und deshalb keine "Unternummerierung" nach "§ 1" besitzen, sollen keine Auswirkung haben. -->
    <xsl:template match="akn:num[not(node())]"/>

    <!-- Satzende-Marker werden vorerst ignoriert -->
    <xsl:template match="akn:marker[@refersTo='satzende']" />



    <!--
        Identity Transforms
        ###################
    -->

    <!-- Info zu AKN-Element annotieren -->
    <xsl:template match="*">
        <!-- TODO:
                <xsl:attribute name="data-akn_element" select="local-name(.)"/>
                <xsl:apply-templates select="node() | @*"/>
        -->
        <xsl:if test="$debugging">
            <xsl:variable name="warnhinweis" as="xs:string" select="concat(' TODO: Kein Template-Match für &lt;akn:', local-name(.), '&gt; ')"/>
            <xsl:comment select="$warnhinweis"/>
            <mark class="unimplementiert">
                <xsl:value-of select="concat($warnhinweis, 'bei @eId=&quot;', @eId, '&quot;')"/>
            </mark>
        </xsl:if>
        <xsl:if test="$durchreichen and node()">
            <span>
                <xsl:attribute name="class">
                    <xsl:value-of select="string-join((string-join(('akn-', local-name()), ''), @class)[. != ''], ' ')" />
                </xsl:attribute>
                <xsl:apply-templates select="@*[name()!='class']" />
                <xsl:apply-templates select="node()" />
            </span>
        </xsl:if>
    </xsl:template>

    <!-- Elemente mit den folgenden Tags werden als <div /> durchgereicht, wobei sie den local-name mit akn-Präfix
     als @class erhalten, also <akn:doc /> zu <div class="akn-doc" /> -->
    <xsl:template match="akn:attachments | akn:attachment | akn:doc | akn:note">
        <div>
            <xsl:attribute name="class">
                <xsl:value-of select="string-join((string-join(('akn-', local-name()), ''), @class)[. != ''], ' ')" />
            </xsl:attribute>
            <xsl:apply-templates select="@*[name()!='class']" />
            <xsl:apply-templates select="node()" />
        </div>
    </xsl:template>

    <!-- Der Hauptteil einer Anlage wird als div ausgegeben, eventuelle Fußnoten folgen am Ende -->
    <xsl:template match="akn:mainBody">
        <div class="{string-join((string-join(('akn-', local-name()), ''), @class)[. != ''], ' ')}">
            <xsl:apply-templates select="@*[name()!='class']" />
            <xsl:apply-templates select="node()" />
        </div>
        <!-- Der Inhalt von Fußnoten (authorialNote) wird am Ende der Einzelvorschrift ausgegeben -->
        <xsl:call-template name="authorial-notes-collection"/>
        <xsl:call-template name="notes-collection" />
    </xsl:template>

    <xsl:template match="akn:blockList">
        <ol class="akn-blockList">
            <xsl:apply-templates select="@*[name()!='class']" />
            <xsl:apply-templates select="node()" />
        </ol>
    </xsl:template>

    <xsl:template match="akn:item">
        <li class="akn-item">
            <xsl:apply-templates select="@*[name()!='class']" />
            <xsl:apply-templates select="akn:num" />
            <div class="content">
                <xsl:apply-templates select="*[not(self::akn:num)]" />
            </div>
        </li>
    </xsl:template>

    <!-- Das akn:longtitle/akn:p nicht in die Zielstruktur übernehmen
         (weil in HTML p nicht mehr geschachtelt werden darf) -->
    <xsl:template match="akn:p[parent::akn:longTitle]">
        <!-- hier kein <p> -->
        <xsl:apply-templates select="node() | @*"/>
        <!-- hier kein </p> -->
    </xsl:template>

    <!--
        Attribute
        ###################
    -->

    <!-- @refersTo zu @class in HTML -->
    <xsl:template match="@refersTo" priority="3">
        <xsl:attribute name="class" select="."/>
    </xsl:template>

    <!-- @eIds zu @id in HTML -->
    <xsl:template match="@eId" priority="2">
        <xsl:attribute name="id">
            <xsl:value-of select="akn:encode-for-uri(.)" />
        </xsl:attribute>
    </xsl:template>

    <!-- @style und @class unverändert übernehmen -->
    <xsl:template match="@class | @style" priority="2">
        <xsl:attribute name="{local-name()}" select="."/>
    </xsl:template>

    <!-- ignorieren: Namespace-deklarationen -->
    <xsl:template match="@xsi:schemaLocation"/>

    <!-- ignorieren: GUIDs -->
    <xsl:template match="@GUID"/>

    <!-- ignorieren: Alle Attribute mit Wert "Attributsemantik noch undefiniert" -->
    <xsl:template match="@*[. = 'attributsemantik-noch-undefiniert']" priority="1"/>

    <!-- Valide HTML-Attribute kopieren -->
    <xsl:template match="@colspan | @headers | @rowspan | @abbr | @align | @axis | @bgcolor | @char | @charoff | @height | @scope | @valign | @width | @style | @class | @href">
        <xsl:copy/>
    </xsl:template>

    <!-- Übrige Attribute mit data-Präfix übernehmen -->
    <xsl:template match="@*">
        <xsl:attribute name="data-{replace(name(), ':', '-')}"><xsl:value-of select="." /></xsl:attribute>
    </xsl:template>

    <!--
        AKN-Metadaten
        #############
    -->

    <!-- ignorieren: Metadatenblock -->
    <xsl:template match="akn:meta"/>

    <!--
        Dokumentenkopf (akn:preface)
        ############################
    -->
    <!-- Dokumentenkopf: Container -->
    <xsl:template match="akn:preface[node()]">
        <xsl:sequence select="akn:gliederungskommentar('Dokumentenkopf (akn:preface)')"/>
        <section class="{$dokumentenkopf}">
            <xsl:apply-templates select="node() | @*"/>
            <xsl:call-template name="authorial-notes-collection"/>
            <xsl:call-template name="notes-collection" />
        </section>

            <xsl:apply-templates select="/akn:akomaNtoso/akn:act/akn:meta/akn:proprietary" />
    </xsl:template>

    <!-- Dokumentenkopf :: Titel & Kurztitel :: Container -->
    <xsl:template match="akn:longTitle">
        <xsl:sequence select="akn:gliederungskommentar('Titel &amp; Kurztitel (akn:longTitle)')"/>
        <div class="{$dokumententitel}">
            <xsl:apply-templates select="node() | @*"/>
        </div>
    </xsl:template>

    <!-- Dokumentenkopf :: Titel -->
    <xsl:template match="akn:docTitle[ancestor::akn:longTitle]">
        <xsl:sequence select="akn:gliederungskommentar('Titel (akn:title)')"/>
        <h1 class="{$titel}">
            <xsl:apply-templates select="node() | @*"/>
        </h1>
    </xsl:template>

    <!-- Dokumentenkopf :: Kurztitel -->
    <xsl:template match="akn:shortTitle[ancestor::akn:longTitle]">
        <xsl:sequence select="akn:gliederungskommentar('Kurztitel (akn:shortTitle)')"/>
        <h1 class="{$kurztitel}">
            <xsl:apply-templates select="node() | @*"/>
        </h1>
    </xsl:template>

    <!-- Dokumentenkopf :: Kurztitel -->
    <xsl:template match="akn:inline">
        <span>
            <xsl:apply-templates select="node() | @*"/>
        </span>
    </xsl:template>

    <!-- Dokumentenkopf :: Ausfertigungsdatum -->
    <xsl:template match="akn:block[parent::akn:preface]">
        <xsl:sequence select="akn:gliederungskommentar('Datumscontainer (akn:block innerhalb von akn:preface)')"/>
        <div>
            <xsl:apply-templates select="node() | @*"/>
        </div>
    </xsl:template>

    <!-- Dokumentenkopf :: Container -->
    <xsl:template match="akn:container">
        <div class="akn-container">
            <xsl:apply-templates select="@*[name()!='class' and name()!='refersTo']" />
            <xsl:apply-templates select="node()" />
        </div>
    </xsl:template>

    <!-- Datumsangaben -->
    <xsl:template match="akn:date">
        <span>
            <xsl:apply-templates select="@*"/>
            <xsl:sequence select="akn:inlinekommentar('Datum (akn:date)')"/>
            <xsl:apply-templates select="node()"/>
        </span>
    </xsl:template>

    <!--
        Eingangsformel (akn:preamble)
        #############################
    -->

    <!-- Eingangsformel: Container -->
    <xsl:template match="akn:preamble[node()]">
        <xsl:sequence select="akn:gliederungskommentar('Eingangsformel (akn:preamble)')"/>
        <section class="{$eingangsformel}">
            <xsl:apply-templates select="node() | @*"/>
        </section>
    </xsl:template>

    <!-- Eingangsformel :: Eingangsformeltext -->
    <xsl:template match="akn:formula">
        <xsl:sequence select="akn:gliederungskommentar('Eingangs- oder Schlussformel (akn:formula)')"/>
        <div>
            <xsl:apply-templates select="node() | @*"/>
        </div>
    </xsl:template>

    <!--
        Regelungstext-Hauptteil
        #######################
    -->

    <!-- Regelungstext-Hauptteil: Container -->
    <xsl:template match="akn:body">
        <xsl:sequence select="akn:gliederungskommentar('Regelungstext-Hauptteil (akn:body)')"/>
        <section class="{$regelungstext-hauptteil}">
            <xsl:apply-templates/>
        </section>
    </xsl:template>

    <!-- Regelungstext-Hauptteil :: Einzelvorschrift: Container  -->
    <xsl:template match="akn:article">
        <xsl:sequence select="akn:gliederungskommentar(concat('Einzelvorschrift: ', akn:num/text(), ' (akn:article)'))"/>
        <article class="{$einzelvorschrift}">
            <xsl:apply-templates/>
            <!-- Der Inhalt von Fußnoten (authorialNote) wird am Ende der Einzelvorschrift ausgegeben -->
            <xsl:call-template name="authorial-notes-collection"/>
            <xsl:call-template name="notes-collection" />
        </article>
    </xsl:template>

    <!-- Einzelvorschrift :: Art und Zählbezeichnung -->
    <xsl:template match="akn:num[parent::akn:article]">
        <xsl:sequence select="akn:gliederungskommentar('Art- und Zählbezeichnung (akn:num in akn:article)')"/>
        <h2 class="{$einzelvorschrift}">
            <xsl:value-of select="text()"/>
        </h2>
    </xsl:template>

    <!-- Einzelvorschrift :: Überschrift -->
    <xsl:template match="akn:heading[parent::akn:article]">
        <xsl:sequence select="akn:gliederungskommentar('Überschrift (akn:heading)')"/>
        <h2 class="{$einzelvorschrift}">
            <xsl:value-of select="text()"/>
        </h2>
    </xsl:template>

    <!-- Einzelvorschrift :: Juristischer Absatz -->
    <xsl:template match="akn:paragraph">
        <xsl:sequence select="akn:gliederungskommentar('Juristischer Absatz (akn:paragraph)')"/>
        <section class="{$juristischer-absatz}">
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </section>
    </xsl:template>

    <!-- Einzelvorschrift :: Juristischer Absatz :: Zählbezeichnung -->
    <xsl:template match="akn:num[parent::akn:paragraph and string-length(text()) gt 0]">
        <span>
            <xsl:apply-templates select="@*"/>
            <xsl:sequence select="akn:inlinekommentar('Zählbezeichnung (akn:num in akn:paragraph)')"/>
            <xsl:apply-templates select="node()"/>
        </span>
    </xsl:template>

    <!-- Einzelvorschrift :: Juristischer Absatz :: Inhaltscontainer -->
    <xsl:template match="akn:content">
        <xsl:sequence select="akn:gliederungskommentar('Juristischer Absatz: Inhalt (akn:content)')"/>
        <div class="akn-content">
            <xsl:apply-templates select="node() | @*"/>
        </div>
    </xsl:template>

    <!-- Textabsatz -->
    <xsl:template match="akn:p[parent::akn:content and not(ancestor::akn:point)]">
        <p class="{$textabsatz}">
            <xsl:apply-templates select="node() | @*"/>
        </p>
    </xsl:template>

    <!-- Vorformatierter Text -->
    <xsl:template match="akn:p[@class='pre']">
        <pre>
            <xsl:apply-templates select="node() | @*" />
        </pre>
    </xsl:template>

    <!-- Sonstiger Textabsatz -->
    <xsl:template match="akn:p">
        <p>
            <xsl:apply-templates select="node() | @*"/>
        </p>
    </xsl:template>

    <!--
        Untergliederung eines juristischen Absatzes
        ###########################################
    -->
    <!-- Untergliederung juristischer Absatz: Container -->
    <xsl:template match="akn:list">
        <xsl:sequence select="akn:gliederungskommentar('Untergliederung eines juristischen Absatzes (akn:list)')"/>

        <div class="{$untergliederung}">
            <!-- Falls vorhanden, einführenden Text der unordered list voranstellen -->
            <xsl:apply-templates select="akn:intro"/>

            <!-- Eigentliche Untergliederung -->
            <xsl:sequence select="akn:gliederungskommentar('Eigentliche Untergliederung (akn:list ohne akn:intro)')"/>
            <ul class="juristischer-absatz-untergliederung">
                <xsl:apply-templates select="@*"/>
                <xsl:apply-templates select="*[not(local-name() = 'intro')]"/>
            </ul>
        </div>
    </xsl:template>

    <!-- Untergliederung juristischer Absatz :: Einleitender Text -->
    <xsl:template match="akn:intro[parent::akn:list]">
        <xsl:sequence select="akn:gliederungskommentar('Einleitender Text vor Untergliederung (akn:intro)')"/>
        <div class="{$einleitender-text}">
            <xsl:apply-templates select="node() | @*"/>
        </div>
    </xsl:template>

    <!-- Untergliederung juristischer Absatz :: Untergliederungselement (= Aufzählungspunkt) -->
    <xsl:template match="akn:point[parent::akn:list]">
        <xsl:sequence select="akn:gliederungskommentar(concat('Juristischer Absatz: Untergliederungspunkt: ', akn:num/text(), ' (akn:point)'))"/>
        <li data-aufzählungsliteral="{akn:num/text()}">
            <xsl:apply-templates select="node() | @*"/>
        </li>
    </xsl:template>

    <!-- Untergliederung juristischer Absatz :: Untergliederungselement :: Aufzählungsliteral -->
    <xsl:template match="akn:num[parent::akn:point]"><!-- bereits qua akn:point-template verarbeitet (pull statt push) --></xsl:template>

    <!-- Untergliederung juristischer Absatz :: Untergliederungselement :: Aufzählungstext-->
    <xsl:template match="akn:p[parent::akn:content and ancestor::akn:point]">
        <xsl:value-of select="text()"/>
    </xsl:template>

    <!--
        Regelungstext-Schluss
        #####################
    -->
    <!-- Regelungstext-Schluss: Container -->
    <xsl:template match="akn:conclusions">
        <xsl:sequence select="akn:gliederungskommentar('Regelungstext-Schluss (akn:conclusions)')"/>
        <hr class="{$trennlinie}"/>
        <section class="{$regelungstext-schluss}">
            <xsl:apply-templates select="node() | @*"/>
        </section>
    </xsl:template>

    <!-- Regelungstext-Schluss :: Signaturblock -->
    <xsl:template match="akn:blockContainer">
        <xsl:sequence select="akn:gliederungskommentar('Signaturblock (akn:blockContainer innerhalb von akn:conclusions)')"/>
        <div>
            <xsl:apply-templates select="node() | @*"/>
        </div>
    </xsl:template>

    <!-- Regelungstext-Schluss :: Signaturblock :: Signatur -->
    <xsl:template match="akn:signature">
        <xsl:sequence select="akn:gliederungskommentar('Signatur (akn:signature)')"/>
        <div class="{$signatur}">
            <xsl:apply-templates select="node() | @*"/>
        </div>
    </xsl:template>

    <!-- Regelungstext-Schluss :: Signaturblock :: Signatur :: Funktionsbezeichnung -->
    <xsl:template match="akn:role">
        <p class="{$funktionsbezeichnung}">
            <xsl:apply-templates select="@*"/>
            <xsl:sequence select="akn:inlinekommentar('Funktionsbezeichnung (akn:role)')"/>
            <xsl:apply-templates select="node()"/>
        </p>
    </xsl:template>

    <!-- Regelungstext-Schluss :: Signaturblock :: Personenname -->
    <xsl:template match="akn:person">
        <p class="{$person}">
            <xsl:apply-templates select="@*"/>
            <xsl:sequence select="akn:inlinekommentar('Person (akn:person)')"/>
            <xsl:apply-templates select="node()"/>
        </p>
    </xsl:template>

    <!--
       Fußnoten
       #####################
    -->
    <!-- An der Stelle ihres Vorkommens wird zu einer authorialNote nur der @marker selbst ausgegeben, der Inhalt
    hingegen kann an passender Stelle mit authorial-notes-collection ausgegeben werden -->
    <xsl:template match="akn:authorialNote">
        <a href="{concat('#', akn:encode-for-uri(@eId))}">
            <sup>
                <xsl:value-of select="@marker"/>
            </sup>
        </a>
    </xsl:template>

    <!-- AuthorialNotes, die in heading-Elementen vorkommen, werden nicht als Links ausgegeben. Diese Regel
     ist spezifischer als die obige. -->
    <xsl:template match="akn:authorialNote[(parent::akn:heading or parent::akn:docTitle) and not($erlaube-links-in-heading)]">
        <sup>
            <xsl:value-of select="@marker"/>
        </sup>
    </xsl:template>

    <xsl:template match="akn:ref">
        <a>
            <xsl:apply-templates select="node() | @*"/>
        </a>
    </xsl:template>

    <!--
    Sammle alle Fußnoten, die nicht explizit über das Attribut placementBase über einen akn:marker ausgegeben
    werden.

    Siehe auch `<xsl:template match="akn:marker[@refersTo='positionierung-fussnote']">`.
    -->
    <xsl:template name="authorial-notes-collection">
        <xsl:if test=".//akn:authorialNote[not(@placementBase)]">
            <ol class="{$fussnoten}">
                <xsl:for-each select=".//akn:authorialNote[not(@placementBase)]">
                    <li id="{akn:encode-for-uri(@eId)}" class="{$fussnote}">
                        <xsl:call-template name="authorial-note-content"/>
                    </li>
                </xsl:for-each>
            </ol>
        </xsl:if>
    </xsl:template>

    <!--
    Sammle im Kontext des aktuellen Elements alle dazugehörigen nichtamtlichen Fußnoten.
    Diese befinden sich nicht im Fließtext, sondern in `akn:meta/akn:notes`.
    Die Zuordnung erfolgt über @placementBase.
    -->
    <xsl:template name="notes-collection">
        <xsl:variable name="thisId" select="@eId" />
        <xsl:variable name="nichtamtliche-fussnoten-elemente" select="//akn:akomaNtoso/akn:act/akn:meta/akn:notes/akn:note[@placementBase=concat('#', $thisId)]" />
        <xsl:if test="$nichtamtliche-fussnoten-elemente">
            <ul class="{$nichtamtliche-fussnoten}">
                <xsl:for-each select="$nichtamtliche-fussnoten-elemente">
                    <li id="{akn:encode-for-uri(@eId)}" class="{$fussnote}">
                        <xsl:apply-templates />
                    </li>
                </xsl:for-each>
            </ul>
        </xsl:if>
    </xsl:template>

    <!--
    Gibt den Marker (z.B. "*") und den Inhalt einer Fußnote aus, gefolgt von einem Rückverweis.
    -->
    <xsl:template name="authorial-note-content">
        <xsl:variable name="parent-id" select="../@eId"/>
        <span class="{$marker}">
            <xsl:value-of select="@marker"/>
        </span>
        <p>
            <xsl:value-of select="."/><xsl:text> </xsl:text>
            <a class="{$rueckverweis}" aria-label="Zurück zum Inhalt" href="{concat('#', akn:encode-for-uri($parent-id))}">↑</a>
        </p>
    </xsl:template>

    <!--
    Verarbeitung von Markern, die zur Platzierung des Inhalts einer Fußnote verwendet werden.
    -->
    <xsl:template match="akn:marker[@refersTo='positionierung-fussnote']">
        <xsl:sequence select="akn:inlinekommentar('Explizit platzierte Fußnote')"/>

        <!--
        Zu jedem Marker mit Attribut refersTo="positionierung-fussnote" sollte eine akn:authorialNote bestehen,
        welche mit dem Attribut placementBase auf diesen Marker verweisen (mit vorangestelltem #).
        -->
        <xsl:variable name="marker-eId" select="concat('#', @eId)"/>

        <xsl:for-each select="//akn:authorialNote[@placementBase=$marker-eId]">
            <div id="{akn:encode-for-uri(@eId)}" class="{$fussnote}">
                <xsl:call-template name="authorial-note-content"/>
            </div>
        </xsl:for-each>
    </xsl:template>

    <!--
    Proprietäre Metadaten
    -->
    <xsl:template match="akn:proprietary">
        <xsl:sequence select="akn:gliederungskommentar('Proprietäre Metadaten (akn:proprietary)')"/>
        <section class="akn-proprietary">
            <xsl:apply-templates />
        </section>
    </xsl:template>

    <xsl:template match="ris:legalDocML.de_metadaten">
        <dl>
            <xsl:apply-templates select="@* | node()[not(self::ris:standangabe)]" />
            <xsl:if test="./ris:standangabe">
            <dt>Standangabe</dt>
            <!-- Process all standangabe elements to create dd elements -->
            <xsl:apply-templates select="./ris:standangabe"/>
            </xsl:if>
        </dl>
    </xsl:template>

    <xsl:template match="ris:legalDocML.de_metadaten/ris:vollzitat">
        <dt>Vollzitat</dt>
        <dd class="ris-vollzitat">
            <xsl:value-of select="."/>
        </dd>
    </xsl:template>
    <xsl:template match="ris:standangabe">
        <dd class="ris-standangabe">
            <xsl:apply-templates select="@*" />
            <xsl:value-of select="."/>
        </dd>
    </xsl:template>

    <!--
        Bilder
        ######
    -->
    <xsl:template match="akn:img">
        <img src="{concat($ressourcenpfad, @src)}">
              <xsl:apply-templates select="@*[local-name() != 'src']" />
        </img>
    </xsl:template>

    <!--
        An diversen Stellen wiederverwendete Angaben
        ############################################
    -->
    <!-- Ort -->
    <xsl:template match="akn:location">
        <span>
            <xsl:apply-templates select="@*"/>
            <xsl:sequence select="akn:inlinekommentar('Ort (akn:location)')"/>
            <xsl:apply-templates select="node()"/>
        </span>
    </xsl:template>

    <!--
        Elemente aus LegalDocML.de, welche eine direkte Entsprechung in HTML haben, werden mit
        den entsprechenden Attributen übernommen
        ############################################
    -->
    <xsl:template match="akn:a | akn:abbr | akn:b | akn:br | akn:caption | akn:i | akn:li | akn:ol | akn:sub | akn:sup | akn:table | akn:td | akn:th | akn:tr | akn:u | akn:ul">
        <xsl:element name="{local-name()}">
            <!--  LegalDocML.de-Attribute, die in HTML ebenfalls existieren, werden ebenfalls übernommen  -->
            <xsl:apply-templates select="node() | @*" />
        </xsl:element>
    </xsl:template>



    </xsl:stylesheet>
