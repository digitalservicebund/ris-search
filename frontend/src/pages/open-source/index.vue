<script setup lang="ts">
import IcBaselineLaunch from "~icons/ic/baseline-launch";
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";
useHead({ title: "Open Source" });
definePageMeta({ alias: ["/opensource"] });
</script>

<template>
  <StaticPageWrapper>
    <RisBreadcrumb title="Open Source" base-path="/" />
    <PageHeader title="Open Source" />
    <div class="flex flex-col gap-32">
      <main class="max-w-prose space-y-24">
        <p>
          Open Source bedeutet für uns Transparenz, Vertrauen, Sicherheit,
          Qualität und Flexibilität. Aus diesem Grund wird unsere Arbeit bald in
          unserem Github-Repository frei zugänglich und offen für Mitarbeit
          sein.
        </p>
        <p>
          Wir planen, das Github-Repository in den nächsten Wochen zu
          veröffentlichen und werden dann den Link auf dieser Seite teilen.
        </p>
        <ButtonLink
          href="https://github.com/digitalservicebund/ris-search"
          target="_blank"
          >GitHub-Repository<IcBaselineLaunch
        /></ButtonLink>
      </main></div
  ></StaticPageWrapper>
</template>
