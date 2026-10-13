<script setup lang="ts">
import { NuxtLink } from "#components";
import GesamtausgabenList from "~/components/documents/norms/GesamtausgabenList.vue";
import { useShowMore } from "~/composables/useShowMore.ts";
import type { LegislationExpression } from "~/types/api.ts";

export type FassungDetailsContent = {
  gesamtausgaben: LegislationExpression[];
  html?: string;
  error: boolean;
};

const { currentLegislationIdentifier, current, detailsContent } = defineProps<{
  currentLegislationIdentifier: string;
  current: boolean;
  /** Undefined while the content is loading */
  detailsContent?: FassungDetailsContent;
}>();

const gesamtausgabenSorted = computed(() =>
  (detailsContent?.gesamtausgaben ?? []).toSorted((a, b) =>
    b.temporalCoverage.localeCompare(a.temporalCoverage),
  ),
);

const gesamtausgabeLink = computed(() => {
  if (gesamtausgabenSorted.value.length !== 1) return undefined;

  const gesamtausgabe = gesamtausgabenSorted.value[0]!;
  const formattedTemporalCoverage = formatTemporalCoverage(
    gesamtausgabe.temporalCoverage,
  );
  const label = `Gesamtausgabe ${formattedTemporalCoverage}öffnen`;
  const to = `/gesetze/${gesamtausgabe.legislationIdentifier}`;

  return {
    label,
    to,
  };
});

function formatTemporalCoverage(temporalCoverage?: string) {
  const coverage = temporalCoverageToValidityInterval(temporalCoverage);
  const from = dateFormattedDDMMYYYY(coverage?.from);
  const to = dateFormattedDDMMYYYY(coverage?.to);

  if (from && to) {
    return `${from} - ${to} `;
  } else if (from) {
    return `gültig ab ${from} `;
  } else if (to) {
    return `gültig bis ${to} `;
  } else {
    return "";
  }
}

const showMoreDesktop = reactive(useShowMore(gesamtausgabenSorted, 5));
const showMoreMobile = reactive(useShowMore(gesamtausgabenSorted, 3));

const {
  visible: drawerVisible,
  // @ts-expect-error -- usage in template not detected
  triggerRef: drawerTriggerRef,
} = useDrawer();

const drawerId = useId();
</script>

<template>
  <UiMessage
    v-if="detailsContent?.error"
    severity="error"
    class="m-16"
    role="alert"
  >
    Es ist ein Fehler aufgetreten.
  </UiMessage>
  <template v-else-if="detailsContent">
    <div class="mx-16 mt-8 flex flex-col" :class="{ 'mb-8': current }">
      <NuxtLink
        v-if="gesamtausgabeLink"
        class="typo-link1-bold link-hover border border-blue-500 bg-white p-16"
        :to="gesamtausgabeLink.to"
        >{{ gesamtausgabeLink.label }}</NuxtLink
      >
      <template v-else>
        <UiButton
          ref="drawerTriggerRef"
          :aria-controls="drawerId"
          :aria-expanded="drawerVisible"
          aria-haspopup="dialog"
          @click="drawerVisible = true"
          severity="info"
          class="typo-label1-bold! mb-8 md:hidden"
          >Gesamtausgabe auswählen</UiButton
        >
        <UiDrawer
          v-model:visible="drawerVisible"
          aria-label="Gesamtausgaben"
          header="Gesamtausgaben"
          :id="drawerId"
        >
          <GesamtausgabenList
            :current-legislation-identifier="currentLegislationIdentifier"
            :gesamtausgaben="showMoreMobile.itemsToDisplay"
            hideHeader
            class="-mb-8"
          />
          <template v-if="showMoreMobile.hasMore" #footer>
            <div class="flex">
              <UiButton
                severity="secondary"
                size="normal"
                :label="
                  showMoreMobile.showingAll
                    ? 'Weniger anzeigen'
                    : 'Alles anzeigen'
                "
                class="flex-1"
                @click="showMoreMobile.toggle"
              />
            </div>
          </template>
        </UiDrawer>
        <UiAccordion
          class="hidden border border-blue-500 bg-white p-16 md:block"
          header-collapsed="Gesamtausgabe auswählen"
          header-expanded="Gesamtausgabe auswählen"
          :model-value="current"
        >
          <GesamtausgabenList
            :current-legislation-identifier="currentLegislationIdentifier"
            :gesamtausgaben="showMoreDesktop.itemsToDisplay"
            hideHeader
            class="mt-16 border-t border-gray-400"
          />
          <UiButton
            v-if="showMoreDesktop.hasMore"
            severity="info"
            size="small"
            :label="
              showMoreDesktop.showingAll ? 'Weniger anzeigen' : 'Alles anzeigen'
            "
            class="mt-8"
            @click="showMoreDesktop.toggle"
          />
        </UiAccordion>
      </template>
    </div>
    <DocumentsNormsLegislationContent v-if="!current">
      <div class="akn-act px-16" v-html="detailsContent.html" />
    </DocumentsNormsLegislationContent>
  </template>
  <div v-else class="flex justify-center p-16">
    <UiProgressSpinner />
  </div>
</template>

<style scoped>
@reference "~/assets/main.css";

:deep(h2.einzelvorschrift) {
  @apply typo-body-bold mt-0 mb-16;
}
</style>
