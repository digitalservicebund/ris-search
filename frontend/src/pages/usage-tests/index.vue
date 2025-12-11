<script setup lang="ts">
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";

definePageMeta({ alias: ["/nutzungstests"], layout: false });

useStaticPageSeo("nutzungstests");
</script>

<template>
  <div>
    <NuxtLayout name="static-page">
      <template #breadcrumb>
        <RisBreadcrumb :items="[{ label: 'Für Nutzungstests registrieren' }]" />
      </template>

      <template #pageTitle>
        Helfen Sie uns, Rechtsinformationen leichter zugänglich zu machen
      </template>

      <div class="space-y-64">
        <section>
          <h2 class="ris-heading2-regular mb-16">Darum brauchen wir Sie</h2>
          <p>
            Unser Ziel ist es, Rechtsinformationen für Bürgerinnen und Bürger
            leichter zugänglich zu machen. Deshalb suchen wir Menschen, die ihre
            Erfahrungen mit uns teilen und unseren Service testen.
          </p>
        </section>
        <section>
          <h2 class="ris-heading2-regular mb-24">So können Sie mitmachen</h2>
          <ol class="list-custom list-outside space-y-28">
            <li>
              <h3 class="ris-body1-bold">Unverbindlich registrieren</h3>
              <p>
                Registrieren Sie sich in wenigen Minuten, um über neue
                Interviews und Nutzungstests informiert zu werden.
              </p>
              <ExternalLink
                url="https://app.formbricks.com/s/cm1gatpbv0001zm7bryylwzyw"
                class="ris-link1-bold"
              >
                Jetzt registrieren
              </ExternalLink>
            </li>
            <li>
              <h3 class="ris-body1-bold">Einladungen erhalten</h3>
              <p>
                Sobald wir weitere Interviews und Nutzungstests planen, erhalten
                Sie eine Einladung per E-Mail mit allen Details. Mit wenigen
                Klicks können Sie sich anmelden.
              </p>
            </li>
            <li>
              <h3 class="ris-body1-bold">
                An Interviews und Nutzungstests teilnehmen
              </h3>
              <p>
                Je nach Studie erhalten Sie einen kurzen Fragebogen oder werden
                zu einem Videointerview eingeladen. Die Teilnahme dauert in der
                Regel zwischen 20 und 60 Minuten.
              </p>
            </li>
          </ol>
        </section>
        <section class="space-y-16">
          <h2 class="ris-heading2-regular mb-24">Gut zu wissen</h2>
          <div>
            <h3 class="ris-body1-bold">Bequem von zu Hause aus teilnehmen</h3>
            <p>
              Die meisten Interviews und Nutzungstests führen wir online durch.
              Sie brauchen für die Teilnahme einen Computer oder ein Smartphone,
              eine stabile Internetverbindung und eine ruhige Umgebung.
            </p>
          </div>
          <div>
            <h3 class="ris-body1-bold">
              Unverbindlich registrieren und wieder abmelden
            </h3>
            <p>
              Die Teilnahme ist freiwillig. Wenn Sie keine Einladungen mehr
              erhalten möchten, senden Sie uns eine Nachricht an
              <a
                href="mailto:rechtsinformationen@digitalservice.bund.de"
                class="ris-link1-regular"
                >rechtsinformationen@digitalservice.bund.de</a
              >.
            </p>
          </div>
        </section>
      </div>
    </NuxtLayout>
  </div>
</template>
