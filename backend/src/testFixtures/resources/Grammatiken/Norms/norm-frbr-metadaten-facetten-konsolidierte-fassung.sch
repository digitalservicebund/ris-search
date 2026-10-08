<!--

Diese Datei ist Bestandteil der Schematronregeln des Standards LegalDocML.de 1.9 (03.11.2025).
Sie wird inkludiert von der Schematron-Hauptdatei.

                
********************************* Hinweis zur Lizensierung ***********************************
 2025 Copyright © 2021–2025 Bundesministerium des Innern und für Heimat,
 Referat DG II 6, Maßnahmen Enterprise Resource Management und Elektronische Verwaltungsarbeit

      Lizenz: CC-BY-3.0 (Creative Commons Namensnennung 3.0)
 Information: https://creativecommons.org/licenses/by/3.0/legalcode.de
**********************************************************************************************



Dieses Dokument ist eine Bearbeitung (Fork) des Originalwerks und die Änderungen stehen unter derselben Lizenz (CC-BY-3.0), sofern nicht anders angegeben.

-->
<sch:pattern xmlns:sch="http://purl.oclc.org/dsdl/schematron">
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href"
  >
    <sch:assert
      id="SCH-KONSF-hrefLiterals.expression.FRBRauthor"
      test="if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</sch:assert>
  </sch:rule>
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRWork/akn:FRBRauthor/@href"
  >
    <sch:assert
      id="SCH-KONSF-hrefLiterals.work.FRBRauthor"
      test="if ($ist-konsolidierte-fassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</sch:assert>
  </sch:rule>
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRExpression/akn:FRBRdate/@name"
  >
    <sch:assert
      id="SCH-KONSF-nameLiterals.expression.FRBRdate"
      test="if ($ist-konsolidierte-fassung) then (. = ('ausfertigung', 'ausfertigung-aenderung')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "ausfertigung" und "ausfertigung-aenderung".</sch:assert>
  </sch:rule>
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRExpression/akn:FRBRthis/@value"
  >
    <sch:assert
      id="SCH-KONS-valueLiterals.expression.FRBRthis"
      test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/[a-zöäüß\-]+-\d+$')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/[a-zöäüß\-]+-\d+" entsprechen.</sch:assert>
  </sch:rule>
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRExpression/akn:FRBRuri/@value"
  >
    <sch:assert
      id="SCH-KONS-valueLiterals.expression.FRBRuri"
      test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}$')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}" entsprechen.</sch:assert>
  </sch:rule>
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value"
  >
    <sch:assert
      id="SCH-KONS-valueLiterals.manifestation.FRBRthis"
      test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</sch:assert>
  </sch:rule>
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value"
  >
    <sch:assert
      id="SCH-KONS-valueLiterals.manifestation.FRBRuri"
      test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</sch:assert>
  </sch:rule>
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRWork/akn:FRBRname/@value"
  >
    <sch:assert
      id="SCH-KONSF-valueLiterals.work.FRBRname"
      test="if ($ist-konsolidierte-fassung) then (. = ('bgbl', 'bgbl-1', 'bgbl-2', 'banz-at', 'banz', 'ebanz', 'vkbl', 'hist')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "bgbl", "bgbl-1", "bgbl-2", "banz-at", "banz", "ebanz", "vkbl" sowie "hist".</sch:assert>
  </sch:rule>
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRWork/akn:FRBRnumber/@value"
  >
    <sch:assert
      id="SCH-KONS-valueLiterals.work.FRBRnumber"
      test="if ($ist-konsolidierte-fassung) then (matches(., '^[-a-zäöüß0-9]+$')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "[-a-zäöüß0-9]+" entsprechen.</sch:assert>
  </sch:rule>
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value"
  >
    <sch:assert
      id="SCH-KONS-valueLiterals.work.FRBRsubtype"
      test="if ($ist-konsolidierte-fassung) then (matches(., '^(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)$')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "(anlage-regelungstext|rechtsetzungsdokument|regelungstext|vereinbarung-verkuendung|sonstiger-veroeffentlichungstext|sonstiges-teildokument)(-[0-9]+)" entsprechen.</sch:assert>
  </sch:rule>
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRWork/akn:FRBRthis/@value"
  >
    <sch:assert
      id="SCH-KONS-valueLiterals.work.FRBRthis"
      test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/[a-zöäüß\-]+-\d+$')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+/[a-zöäüß\-]+-\d+" entsprechen.</sch:assert>
  </sch:rule>
  <sch:rule
    role="error"
    context="akn:identification/akn:FRBRWork/akn:FRBRuri/@value"
  >
    <sch:assert
      id="SCH-KONS-valueLiterals.work.FRBRuri"
      test="if ($ist-konsolidierte-fassung) then (matches(., '^https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+$')) else true()"
    >In der konsolidierten Fassung ist das Literal "<sch:value-of
      select="."
    />" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "https://www.ris.bund.de/eli/bund/[-a-z0-9]+/\d{4}/[-a-z0-9äöüß]+" entsprechen.</sch:assert>
  </sch:rule>
</sch:pattern>
