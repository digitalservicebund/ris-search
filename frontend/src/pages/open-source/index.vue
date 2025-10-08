<script setup lang="ts">
import StaticPageWrapper from "~/components/CustomLayouts/StaticPageWrapper.vue";
import { useStaticPageSeo } from "~/composables/useStaticPageSeo";
import IcBaselineLaunch from "~icons/ic/baseline-launch";

definePageMeta({ alias: ["/opensource"] });

useStaticPageSeo("opensource");
</script>

<template>
  <StaticPageWrapper>
    <template #breadcrumb>
      <RisBreadcrumb title="Open Source" base-path="/" />
    </template>
    <PageHeader title="Open Source" />
    <div class="flex flex-col gap-32">
      <div class="max-w-prose space-y-24">
        <p>
          Bei der Entwicklung setzen wir vollständig auf Open Source. Open
          Source bedeutet für uns Transparenz, Vertrauen, Sicherheit, Qualität
          und Flexibilität. Aus diesem Grund ist unsere Arbeit in unserem
          Github-Repository frei zugänglich und offen für Mitarbeit.
        </p>
        <ButtonLink
          href="https://github.com/digitalservicebund/ris-search"
          target="_blank"
          >Zum Github Repository<IcBaselineLaunch
        /></ButtonLink>
      </div></div
  ></StaticPageWrapper>
</template>
