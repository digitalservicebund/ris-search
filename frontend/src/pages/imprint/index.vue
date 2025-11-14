<script setup lang="ts">
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";

definePageMeta({ alias: ["/impressum"] });

useStaticPageSeo("impressum");
</script>

<template>
  <StaticPageWrapper>
    <template #breadcrumb>
      <RisBreadcrumb :items="[{ label: 'Impressum' }]" />
    </template>
    <PageHeader title="Impressum" />
    <div class="flex flex-col gap-32">
      <div class="max-w-prose space-y-48">
        <section>
          <h2 class="ris-heading3-regular mb-8">Anbieter</h2>
          <p>
            Bundesministerium der Justiz und für Verbraucherschutz (BMJV)<br
              class="mb-8"
            />Anton-Wilhelm-Amo-Straße 37, 10117 Berlin<br />Telefon: 030/ 18
            580 - 0<br />Fax: 030/ 18 580 - 95 25<br />E-Mail:
            <a href="mailto:poststelle@bmj.bund.de" class="ris-link1-regular"
              >poststelle@bmj.bund.de</a
            ><br />Internet:
            <NuxtLink to="https://www.bmj.de" class="ris-link1-regular">
              www.bmj.de
            </NuxtLink>
          </p>
        </section>
        <section>
          <h2 class="ris-heading3-regular mb-8">Gesetzlicher Vertreter</h2>
          <p>
            Die Bundesministerin der Justiz und für Verbraucherschutz<br />Anton-Wilhelm-Amo-Straße
            37, 10117 Berlin
          </p>
        </section>
        <section>
          <h2 class="ris-heading3-regular mb-8">Verantwortlich</h2>
          <p class="mb-8">Dr. Stephanie Krüger, Referat ZC4</p>
          <p>
            Bundesministerium der Justiz und für Verbraucherschutz (BMJV)<br />Anton-Wilhelm-Amo-Straße
            37, 10117 Berlin
          </p>
        </section>
        <section>
          <h2 class="ris-heading3-regular mb-8">Technische Umsetzung</h2>
          <p>DigitalService GmbH des Bundes</p>
        </section>
        <section>
          <h2 class="ris-heading3-regular mb-8">Hosting und Betrieb</h2>
          <p>DigitalService GmbH des Bundes</p>
        </section>
        <section class="space-y-8">
          <h2 class="ris-heading3-regular mb-8">Rechtliche Hinweise</h2>
          <p>
            Alle Angaben unseres Internetangebotes wurden sorgfältig geprüft.
            Wir bemühen uns, dieses Informationsangebot aktuell und inhaltlich
            richtig sowie vollständig anzubieten. Dennoch ist das Auftreten von
            Fehlern nicht völlig auszuschließen. Eine Garantie für die
            Vollständigkeit, Richtigkeit und letzte Aktualität kann daher nicht
            übernommen werden.
          </p>
          <p>
            Der Anbieter kann diese Website nach eigenem Ermessen jederzeit ohne
            Ankündigung verändern und / oder deren Betrieb einstellen. Er ist
            nicht verpflichtet, Inhalte dieser Website zu aktualisieren.
          </p>
          <p>
            Der Zugang und die Benutzung dieser Website geschehen auf eigene
            Gefahr des Benutzers. Der Anbieter ist nicht verantwortlich und
            übernimmt keinerlei Haftung für Schäden, u.a. für direkte,
            indirekte, zufällige, vorab konkret zu bestimmende oder
            Folgeschäden, die angeblich durch den oder in Verbindung mit dem
            Zugang und/oder der Benutzung dieser Website aufgetreten sind.
          </p>
          <p>
            Der Anbieter übernimmt keine Verantwortung für die Inhalte und die
            Verfügbarkeit von Websites Dritter, die über externe Links dieses
            Informationsangebotes erreicht werden. Der Anbieter distanziert sich
            ausdrücklich von allen Inhalten, die möglicherweise straf- oder
            haftungsrechtlich relevant sind oder gegen die guten Sitten
            verstoßen.
          </p>
        </section>
      </div>
    </div></StaticPageWrapper
  >
</template>
