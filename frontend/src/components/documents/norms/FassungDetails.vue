<script setup lang="ts">
import GesamtausgabenList from "~/components/documents/norms/GesamtausgabenList.vue";
import { useShowMore } from "~/composables/useShowMore.ts";
import type { LegislationExpression } from "~/types/api.ts";

type FassungDetailsContent = {
  gesamtausgaben: LegislationExpression[];
  html?: string;
  error: boolean;
};

const { currentLegislationIdentifier, current, fassungEId, detailsContent } =
  defineProps<{
    currentLegislationIdentifier: string;
    current: boolean;
    fassungEId: string;
    /** Undefined while the content is loading */
    detailsContent?: FassungDetailsContent;
  }>();

const route = useRoute();

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
  const to = {
    path: legislationArticlePath(
      gesamtausgabe.legislationIdentifier,
      fassungEId,
    ),
    query: { from: route.query.from },
  };

  return {
    label,
    to,
    alreadyOpen:
      current &&
      gesamtausgabe.legislationIdentifier === currentLegislationIdentifier,
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

type GesamtausgabenListInstance = InstanceType<typeof GesamtausgabenList>;
const desktopList = useTemplateRef<GesamtausgabenListInstance>("desktopList");
const mobileList = useTemplateRef<GesamtausgabenListInstance>("mobileList");

const { toggle: toggleShowMoreDesktop, state: showMoreDesktop } = useShowMore(
  gesamtausgabenSorted,
  5,
  desktopList,
);
const { toggle: toggleShowMoreMobile, state: showMoreMobile } = useShowMore(
  gesamtausgabenSorted,
  3,
  mobileList,
);

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
    <div class="mx-16 mt-16" :class="{ 'mb-16': current }">
      <template v-if="gesamtausgabeLink">
        <span v-if="gesamtausgabeLink.alreadyOpen" class="typo-label1-regular"
          >Fassung bereits geöffnet.
          <NuxtLink
            :to="{ query: { ...route.query, view: 'text' } }"
            class="typo-link1-regular"
            >Zum Text-Tab</NuxtLink
          ></span
        >
        <NuxtLink v-else :to="gesamtausgabeLink.to" class="typo-link1-regular">
          {{ gesamtausgabeLink.label }}
        </NuxtLink>
      </template>
      <template v-else>
        <UiButton
          ref="drawerTriggerRef"
          :aria-controls="drawerId"
          :aria-expanded="drawerVisible"
          aria-haspopup="dialog"
          @click="drawerVisible = true"
          severity="info"
          size="small"
          class="mb-8 w-full md:hidden"
          >Gesamtausgabe auswählen</UiButton
        >
        <UiDrawer
          v-model:visible="drawerVisible"
          aria-label="Gesamtausgaben"
          header="Gesamtausgaben"
          :id="drawerId"
        >
          <GesamtausgabenList
            ref="mobileList"
            :current-legislation-identifier="currentLegislationIdentifier"
            :gesamtausgaben="showMoreMobile.itemsToDisplay"
            :article-e-id="fassungEId"
            hideHeader
            class="-mb-8"
          />
          <template v-if="showMoreMobile.hasMore" #footer>
            <div class="flex">
              <UiButton
                severity="info"
                :label="
                  showMoreMobile.showingAll
                    ? 'Weniger anzeigen'
                    : 'Alles anzeigen'
                "
                class="flex-1"
                @click="toggleShowMoreMobile"
              />
            </div>
          </template>
        </UiDrawer>
        <UiAccordion
          class="hidden border border-blue-500 bg-white p-12 md:block"
          header-collapsed="Gesamtausgabe auswählen"
          header-expanded="Gesamtausgabe auswählen"
          :model-value="current"
        >
          <div class="ml-32">
            <GesamtausgabenList
              ref="desktopList"
              :current-legislation-identifier="currentLegislationIdentifier"
              :gesamtausgaben="showMoreDesktop.itemsToDisplay"
              :article-e-id="fassungEId"
              hideHeader
              reducedFirstColumnPadding
              class="mt-16"
            />
            <UiButton
              v-if="showMoreDesktop.hasMore"
              severity="info"
              size="small"
              :label="
                showMoreDesktop.showingAll
                  ? 'Weniger anzeigen'
                  : 'Alles anzeigen'
              "
              class="mt-8 mb-4 scroll-my-16"
              @click="toggleShowMoreDesktop"
            />
          </div>
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
