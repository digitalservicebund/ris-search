<script setup lang="ts">
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";

definePageMeta({ alias: ["/ueber"], layout: false });

useStaticPageSeo("ueber");
</script>

<template>
  <div>
    <NuxtLayout name="static-page">
      <template #breadcrumb>
        <Breadcrumbs :items="[{ label: 'Über den Service' }]" />
      </template>

      <template #pageTitle>Rechtsinformationen zentral an einem Ort</template>

      <div class="space-y-48">
        <section class="space-y-24">
          <p>
            Das Rechtsinformationsportal erleichtert künftig den Zugang zu
            Gesetzen und Verordnungen, Gerichtsentscheidungen,
            Verwaltungsvorschriften und Literaturnachweisen des Bundes.
          </p>
          <p>
            Der Service befindet sich in der Testphase. Sie haben die
            Möglichkeit, frühzeitig erste Funktionen zu testen und uns über das
            <NuxtLink class="ris-link1-regular" :to="{ name: 'feedback' }"
              >Feedback-Formular</NuxtLink
            >
            Feedback zu geben. Die Funktionen und der Umfang der Daten werden
            schrittweise erweitert.
          </p>
          <p>
            Der Service wird gemeinsam vom Bundesministerium der Justiz und für
            Verbraucherschutz, dem Bundesamt für Justiz und der DigitalService
            GmbH des Bundes entwickelt.
          </p>
        </section>
        <section class="space-y-24">
          <h2 class="ris-heading2-regular">Was gibt es bereits?</h2>
          <p>
            Der Service enthält bereits eine erste Auswahl an Gesetzen,
            Verordnungen und Gerichtsentscheidungen. Während der Testphase kann
            es noch zu fehlenden Inhalten oder Darstellungsfehlern kommen. Ihr
            Feedback hilft uns, den Service weiter zu verbessern.
          </p>
          <div
            class="grid auto-rows-fr grid-cols-1 gap-24 md:grid-cols-3 lg:-mr-288"
          >
            <StaticContentStatusCard status="implemented">
              <template #header>Gesetze und Verordnungen</template>
              in der jeweils aktuell gültigen Fassung inklusive Tabellen und
              Anhängen sowie den wichtigsten Metadaten
            </StaticContentStatusCard>
            <StaticContentStatusCard status="implemented">
              <template #header>Gerichtsentscheidungen</template>
              mit den wichtigsten Metadaten und verlinkten Randnummern
            </StaticContentStatusCard>
            <StaticContentStatusCard status="implemented">
              <template #header>Eine umfangreiche Suche</template>
              die sowohl für gezielte Anfragen als auch für die Volltextsuche
              geeignet ist
            </StaticContentStatusCard>
            <StaticContentStatusCard status="implemented">
              <template #header>XML-Ansichten</template>
              für Gesetze, Verordnungen und Gerichtsentscheidungen
            </StaticContentStatusCard>
            <StaticContentStatusCard status="implemented">
              <template #header>Programmierschnittstelle [API]</template>
              mit der unsere Daten direkt abgerufen werden können
            </StaticContentStatusCard>
          </div>
        </section>
        <section class="space-y-24">
          <h2 class="ris-heading2-regular">Woran wir noch arbeiten</h2>
          <p>
            Wir entwickeln den Service stetig weiter und arbeiten an folgenden
            Inhalten und Funktionen:
          </p>
          <div
            class="grid max-w-5xl auto-rows-fr grid-cols-1 gap-24 md:grid-cols-3 lg:-mr-288"
          >
            <StaticContentStatusCard status="in_progress">
              <template #header>Verwaltungsvorschriften</template>
              um den Zugang zu allen relevanten Rechtsinformationen zu
              vervollständigen
            </StaticContentStatusCard>
            <StaticContentStatusCard status="in_progress">
              <template #header>Optimierung der Suche</template>
              auf Basis von Nutzerfeedback, um weitere Anwendungsfälle
              abzudecken
            </StaticContentStatusCard>
            <StaticContentStatusCard status="planned">
              <template #header>Fassungen von Gesetzen</template>
              einschließlich historischer und noch nicht in Kraft getretener
              Fassungen
            </StaticContentStatusCard>
            <StaticContentStatusCard status="planned">
              <template #header>PDF-Downloads</template>
              um Dokumente einfacher speichern und teilen zu können
            </StaticContentStatusCard>
            <StaticContentStatusCard status="planned">
              <template #header>Nutzungstests zur Barrierefreiheit</template>
              mit Menschen, die assistive Technologien verwenden
            </StaticContentStatusCard>
            <StaticContentStatusCard status="in_progress">
              <template #header>Optimierung der API-Dokumentation</template>
              um die API und den Datenstandard genauer zu erklären
            </StaticContentStatusCard>
            <StaticContentStatusCard status="in_progress">
              <template #header>Englische Übersetzungen</template>
              einiger ausgewählter Gesetze
            </StaticContentStatusCard>
          </div>
          <div>
            Ihnen fehlen noch Funktionen?
            <NuxtLink class="ris-link1-regular" :to="{ name: 'feedback' }"
              >Teilen Sie uns mit</NuxtLink
            >, welche Funktionen wir noch berücksichtigen oder priorisieren
            sollten.
          </div>
        </section>
        <section class="space-y-24">
          <h2 class="ris-heading2-regular">Ablösung bestehender Services</h2>
          <p>
            Die bestehenden Webseiten
            <ExternalLink
              class="ris-link1-regular"
              url="https://www.gesetze-im-internet.de/"
              >Gesetze-im-Internet</ExternalLink
            >,
            <ExternalLink
              class="ris-link1-regular"
              url="https://www.rechtsprechung-im-internet.de/"
              >Rechtsprechung-im-Internet</ExternalLink
            >
            und
            <ExternalLink
              class="ris-link1-regular"
              url="https://www.verwaltungsvorschriften-im-internet.de/"
              >Verwaltungsvorschriften-im-Internet</ExternalLink
            >
            werden im Rechtsinformationsportal zukünftig zusammengeführt und von
            diesem abgelöst. Damit wird ein zentraler und einheitlicher Zugang
            zu Rechtsinformationen geschaffen. Bis dahin nutzen Sie für
            Recherchen bitte weiterhin die bestehenden Webseiten.
          </p>
          <div>Der genaue Zeitpunkt der Umstellung steht noch nicht fest.</div>
        </section>
      </div>
    </NuxtLayout>
  </div>
</template>
