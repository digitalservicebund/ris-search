<script setup lang="ts">
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";
import ContactDetails from "~/pages/contact/ContactDetails.vue";

useHead({ title: "Erklärung zur Barrierefreiheit" });
definePageMeta({ alias: ["/barrierefreiheit"] });
</script>

<template>
  <StaticPageWrapper>
    <RisBreadcrumb title="Barrierefreiheit" base-path="/" />
    <PageHeader title="Erklärung zur Barrierefreiheit" />
    <main class="max-w-prose space-y-64">
      <section class="space-y-16">
        <p>
          Diese Erklärung zur Barrierefreiheit gilt für die unter
          <a
            href="https://testphase.rechtsinformationen.bund.de"
            class="ris-link1-regular"
          >
            testphase.rechtsinformationen.bund.de</a
          >
          veröffentlichte Website des Bundesministeriums der Justiz und für
          Verbraucherschutz.
        </p>
        <p>
          Als öffentliche Stelle im Sinne der Richtlinie (EU) 2016/2102 sind wir
          bemüht, unsere Website und unsere mobilen Anwendungen im Einklang mit
          den Bestimmungen des Behindertengleichstellungsgesetzes
          <a
            href="https://www.gesetze-im-internet.de/bgg/BJNR146800002.html"
            class="ris-link1-regular"
            >(BGG)</a
          >
          und der Barrierefreie-Informationstechnik-Verordnung
          <a
            href="https://www.gesetze-im-internet.de/bitv_2_0/"
            class="ris-link1-regular"
            >(BITV 2.0)</a
          >
          zur Umsetzung der Richtlinie (EU) 2016/2102 barrierefrei zu machen.
        </p>
        <p>
          Bitte beachten Sie, dass sich diese Website derzeit in der Testphase
          befindet und wir kontinuierlich an der Verbesserung der
          Barrierefreiheit arbeiten. Erläuterungen in Deutscher Gebärdensprache
          und Leichter Sprache werden zu einem späteren Zeitpunkt ergänzt.
        </p>
        <dl>
          <dd class="ris-body1-bold">
            Datum der Erstellung bzw. der letzten Aktualisierung der Erklärung
          </dd>
          <dt>Diese Erklärung wurde am 31.03.2025 erstellt.</dt>
        </dl>
      </section>
      <section class="space-y-16">
        <h2 class="ris-heading2-regular">
          Welche Bereiche sind nicht barrierefrei?
        </h2>
        <ul class="list-bullet">
          <li>
            Bilder in Gesetzen, Verordnungen und Gerichtsentscheidungen
            (insbesondere in älteren Versionen) verfügen teilweise über keine
            oder unzureichende Alternativtexte.
          </li>
          <li>
            Anlagen zu Gesetzen, Verordnungen und Gerichtsentscheidungen
            (insbesondere in älteren Versionen) sind teilweise als
            vorformatierter Text kodiert. Diese Art der Kodierung erschwert oder
            verhindert die korrekte Interpretation und das Vorlesen durch
            Screenreader, da die logische Struktur und die Beziehungen zwischen
            den Textteilen nicht klar erkennbar sind. Dies betrifft insbesondere
            Tabellen, Listen und andere strukturierte Daten, die in Anlagen
            häufig vorkommen.
          </li>
        </ul>
      </section>
      <section class="space-y-16">
        <h2 class="ris-heading2-regular">Barrieren melden</h2>
        <p>
          Sie möchten uns bestehende Barrieren melden oder Informationen zur
          Umsetzung der Barrierefreiheit erfragen? Schreiben Sie uns gern eine
          Nachricht.
        </p>
        <ContactDetails />
      </section>
      <section class="space-y-16">
        <h2 class="ris-heading2-regular">Schlichtungsverfahren</h2>
        <p>
          Wenn auch nach Ihrem Feedback an den oben genannten Kontakt keine
          zufriedenstellende Lösung gefunden wurde, können Sie sich an die
          Schlichtungsstelle nach § 16 BGG wenden. Die Schlichtungsstelle BGG
          hat die Aufgabe, bei Konflikten zum Thema Barrierefreiheit zwischen
          Menschen mit Behinderungen und öffentlichen Stellen des Bundes eine
          außergerichtliche Streitbeilegung zu unterstützen. Das
          Schlichtungsverfahren ist kostenlos. Es muss kein Rechtsbeistand
          eingeschaltet werden. Weitere Informationen zum Schlichtungsverfahren
          und den Möglichkeiten der Antragstellung erhalten Sie unter:
          <a
            href="https://www.schlichtungsstelle-bgg.de"
            class="ris-link1-regular"
            >www.schlichtungsstelle-bgg.de</a
          >
        </p>
        <p>
          Direkt kontaktieren können Sie die Schlichtungsstelle BGG unter:<br />
          <a
            href="mailto:info@schlichtungsstelle-bgg.de"
            class="ris-link1-regular"
            >info@schlichtungsstelle-bgg.de</a
          >
        </p>
      </section>
    </main></StaticPageWrapper
  >
</template>
