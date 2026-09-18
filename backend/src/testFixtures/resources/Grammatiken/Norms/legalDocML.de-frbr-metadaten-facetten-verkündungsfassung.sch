<?xml version="1.0" encoding="UTF-8"?>
<!--

Diese Datei ist Bestandteil der Schematronregeln des Standards LegalDocML.de 1.8.2 (07.08.2025).
Sie wird inkludiert von der Schematron-Hauptdatei.

                
********************************* Hinweis zur Lizensierung ***********************************
 2025 Copyright © 2021–2025 Bundesministerium des Innern und für Heimat,
 Referat DG II 6, Maßnahmen Enterprise Resource Management und Elektronische Verwaltungsarbeit

      Lizenz: CC-BY-3.0 (Creative Commons Namensnennung 3.0)
 Information: https://creativecommons.org/licenses/by/3.0/legalcode.de
**********************************************************************************************

-->
<sch:pattern xmlns:sch="http://purl.oclc.org/dsdl/schematron">
   <sch:rule role="error"
             context="akn:identification/akn:FRBRExpression/akn:FRBRauthor/@href">
      <sch:assert id="SCH-VERKF-hrefLiterals.expression.FRBRauthor"
                  test="if ($ist-verkündungsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRauthor/@href">
      <sch:assert id="SCH-VERKF-hrefLiterals.work.FRBRauthor"
                  test="if ($ist-verkündungsfassung) then (. = ('recht.bund.de/institution/bundesregierung', 'recht.bund.de/institution/bundeskanzler', 'recht.bund.de/institution/bundespraesident')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "recht.bund.de/institution/bundesregierung", "recht.bund.de/institution/bundeskanzler" sowie "recht.bund.de/institution/bundespraesident".</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRExpression/akn:FRBRdate/@name">
      <sch:assert id="SCH-VERKF-nameLiterals.expression.FRBRdate"
                  test="if ($ist-verkündungsfassung) then (. = ('verkuendung', 'verkuendung-unbestimmtes-inkrafttreten', 'aenderung', 'aenderung-unbestimmtes-inkrafttreten', 'berichtigung')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "verkuendung", "verkuendung-unbestimmtes-inkrafttreten", "aenderung", "aenderung-unbestimmtes-inkrafttreten" sowie "berichtigung".</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRExpression/akn:FRBRthis/@value">
      <sch:assert id="SCH-VERK-valueLiterals.expression.FRBRthis"
                  test="if ($ist-verkündungsfassung) then (matches(., '^eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/[a-zöäüß\-]+-\d+$')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/[a-zöäüß\-]+-\d+" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRExpression/akn:FRBRuri/@value">
      <sch:assert id="SCH-VERK-valueLiterals.expression.FRBRuri"
                  test="if ($ist-verkündungsfassung) then (matches(., '^eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}$')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRManifestation/akn:FRBRthis/@value">
      <sch:assert id="SCH-VERK-valueLiterals.manifestation.FRBRthis"
                  test="if ($ist-verkündungsfassung) then (matches(., '^eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRManifestation/akn:FRBRuri/@value">
      <sch:assert id="SCH-VERK-valueLiterals.manifestation.FRBRuri"
                  test="if ($ist-verkündungsfassung) then (matches(., '^eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+$')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))/\d{4}-\d{2}-\d{2}/\d+/[a-z]{3}/\d{4}-\d{2}-\d{2}/[a-zöäüß\-]+-\d+\.[a-zöäüß]+" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRname/@value">
      <sch:assert id="SCH-VERKF-valueLiterals.work.FRBRname"
                  test="if ($ist-verkündungsfassung) then (. = ('bgbl', 'bgbl-1', 'bgbl-2', 'banz-at')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich "bgbl", "bgbl-1", "bgbl-2" sowie "banz-at".</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRnumber/@value">
      <sch:assert id="SCH-VERK-valueLiterals.work.FRBRnumber"
                  test="if ($ist-verkündungsfassung) then (matches(., '^((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))$')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRsubtype/@value">
      <sch:assert id="SCH-VERK-valueLiterals.work.FRBRsubtype"
                  test="if ($ist-verkündungsfassung) then (matches(., '^(anlage-regelungstext|rechtsetzungsdokument|regelungstext-neufassung|regelungstext-verkuendung|vereinbarung-verkuendung|bekanntmachungstext|bekanntmachungstext-berichtigung|bekanntmachungstext-entscheidung-des-bundesverfassungsgerichts|sonstiges-dokument)(-[0-9]+)$')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "(anlage-regelungstext|rechtsetzungsdokument|regelungstext-neufassung|regelungstext-verkuendung|vereinbarung-verkuendung|bekanntmachungstext|bekanntmachungstext-berichtigung|bekanntmachungstext-entscheidung-des-bundesverfassungsgerichts|sonstiges-dokument)(-[0-9]+)" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRthis/@value">
      <sch:assert id="SCH-VERK-valueLiterals.work.FRBRthis"
                  test="if ($ist-verkündungsfassung) then (matches(., '^eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))/[a-zöäüß\-]+-\d+$')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))/[a-zöäüß\-]+-\d+" entsprechen.</sch:assert>
   </sch:rule>
   <sch:rule role="error"
             context="akn:identification/akn:FRBRWork/akn:FRBRuri/@value">
      <sch:assert id="SCH-VERK-valueLiterals.work.FRBRuri"
                  test="if ($ist-verkündungsfassung) then (matches(., '^eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))$')) else true()">In der Verkündungsfassung ist das Literal "<sch:value-of select="."/>" an dieser Stelle nicht
                                    zulässig. Erlaubt sind ausschließlich Werte, die dem Muster "eli/bund/[-a-z0-9]+/\d{4}/((s[0-9]+[a-zäöüß]*)|([0-9]+[a-zäöüß]*(-\d+)?))" entsprechen.</sch:assert>
   </sch:rule>
</sch:pattern>
