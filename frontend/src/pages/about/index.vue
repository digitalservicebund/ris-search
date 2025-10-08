<script setup lang="ts">
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";
import { StatusCardType } from "~/components/types";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";

definePageMeta({ alias: ["/ueber"] });

useStaticPageSeo("ueber");
</script>

<template>
  <StaticPageWrapper>
    <main class="space-y-48">
      <section class="max-w-prose">
        <RisBreadcrumb title="Über den Service" base-path="/" />
        <PageHeader
          class="pb-8"
          title="Rechtsinformationen zentral an einem Ort"
        />
      </section>
      <section class="max-w-prose space-y-24">
        <p>
          Das Rechtsinformationsportal erleichtert künftig den Zugang zu
          Gesetzen und Verordnungen, Gerichtsentscheidungen,
          Verwaltungsvorschriften und Literaturnachweisen des Bundes.
        </p>
        <p>
          Der Service befindet sich in der Testphase. Sie haben die Möglichkeit,
          frühzeitig erste Funktionen zu testen und uns
          <NuxtLink class="ris-link1-regular" to="/feedback"
            >Feedback zu geben</NuxtLink
          >. Die Funktionen und der Umfang der Daten werden schrittweise
          erweitert.
        </p>
        <p>
          Der Service wird gemeinsam vom Bundesministerium der Justiz und für
          Verbraucherschutz, dem Bundesamt für Justiz und der DigitalService
          GmbH des Bundes entwickelt.
        </p>
      </section>
      <section class="space-y-24">
        <h2 class="ris-heading2-regular">Was gibt es bereits?</h2>
        <p class="max-w-prose">
          Der Service enthält bereits eine erste Auswahl an Gesetzen,
          Verordnungen und Gerichtsentscheidungen. Während der Testphase kann es
          noch zu fehlenden Inhalten oder Darstellungsfehlern kommen. Ihr
          Feedback hilft uns, den Service weiter zu verbessern.
        </p>
        <div
          class="grid max-w-5xl auto-rows-fr grid-cols-1 gap-24 md:grid-cols-3"
        >
          <StatusCard
            header="Gesetze und Verordnungen"
            content="in der jeweils aktuell gültigen Fassung inklusive Tabellen und Anhängen sowie den wichtigsten Metadaten"
            :status="StatusCardType.IMPLEMENTED"
          />
          <StatusCard
            header="Gerichtsentscheidungen"
            content="mit den wichtigsten Metadaten und verlinkten Randnummern"
            :status="StatusCardType.IMPLEMENTED"
          />
          <StatusCard
            header="Eine umfangreiche Suche"
            content="die sowohl für gezielte Anfragen als auch für die Volltextsuche geeignet ist"
            :status="StatusCardType.IMPLEMENTED"
          />
          <StatusCard
            header="XML-Ansichten"
            content="für Gesetze, Verordnungen und Gerichtsentscheidungen"
            :status="StatusCardType.IMPLEMENTED"
          />
          <StatusCard
            header="Programmierschnittstelle [API]"
            content="mit der unsere Daten direkt abgerufen werden können"
            :status="StatusCardType.IMPLEMENTED"
          />
        </div>
      </section>
      <section class="space-y-24">
        <h2 class="ris-heading2-regular">Woran wir noch arbeiten</h2>
        <p class="max-w-prose">
          Wir entwickeln den Service stetig weiter und arbeiten an folgenden
          Inhalten und Funktionen:
        </p>
        <div
          class="grid max-w-5xl auto-rows-fr grid-cols-1 gap-24 md:grid-cols-3"
        >
          <StatusCard
            header="Verwaltungsvorschriften"
            content="um den Zugang zu allen relevanten Rechtsinformationen zu vervollständigen"
            :status="StatusCardType.IN_PROGRESS"
          />
          <StatusCard
            header="Optimierung der Suche"
            content="auf Basis von Nutzerfeedback, um weitere Anwendungsfälle abzudecken"
            :status="StatusCardType.IN_PROGRESS"
          />
          <StatusCard
            header="Fassungen von Gesetzen"
            content="einschließlich historischer und noch nicht in Kraft getretener Fassungen"
            :status="StatusCardType.PLANNED"
          />
          <StatusCard
            header="PDF-Downloads"
            content="um Dokumente einfacher speichern und teilen zu können"
            :status="StatusCardType.PLANNED"
          />
          <StatusCard
            header="Nutzungstests zur Barrierefreiheit"
            content="mit Menschen, die assistive Technologien verwenden"
            :status="StatusCardType.PLANNED"
          />
          <StatusCard
            header="Optimierung der API-Dokumentation"
            content="um die API und den Datenstandard genauer zu erklären"
            :status="StatusCardType.IN_PROGRESS"
          />
          <StatusCard
            header="Englische Übersetzungen"
            content="einiger ausgewählter Gesetze"
            :status="StatusCardType.IN_PROGRESS"
          />
        </div>
        <div class="max-w-prose">
          Ihnen fehlen noch Funktionen?
          <NuxtLink class="ris-link1-regular" to="/feedback"
            >Teilen Sie uns mit</NuxtLink
          >, welche Funktionen wir noch berücksichtigen oder priorisieren
          sollten.
        </div>
      </section>
      <section class="max-w-prose space-y-24">
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
          diesem abgelöst. Damit wird ein zentraler und einheitlicher Zugang zu
          Rechtsinformationen geschaffen. Bis dahin nutzen Sie für Recherchen
          bitte weiterhin die bestehenden Webseiten.
        </p>
        <div>Der genaue Zeitpunkt der Umstellung steht noch nicht fest.</div>
      </section>
    </main></StaticPageWrapper
  >
</template>
