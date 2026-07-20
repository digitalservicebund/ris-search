<script setup lang="ts">
import type { NuxtError } from "#app";
import SimpleSearchInput from "~/components/search/SimpleSearchInput.vue";

useHead({
  titleTemplate: (pageTitle) =>
    pageTitle
      ? `${pageTitle} — Rechtsinformationen des Bundes`
      : "Rechtsinformationen des Bundes",
});

function redirectToSearch(searchStr?: string) {
  navigateTo({ name: "suche", query: searchStr ? { query: searchStr } : {} });
}

const props = defineProps({
  error: {
    type: Object as () => NuxtError,
    required: true,
  },
});

const isNotFoundError = computed(
  () => props.error?.statusCode === HttpStatusCodes.NotFound,
);

const isInternalServerError = computed(
  () => props.error?.statusCode === HttpStatusCodes.InternalServerError,
);

const locationClientOnly = computed(() => location.href);

const pageTitle = computed(() => {
  return isNotFoundError.value
    ? "Diese Seite existiert nicht"
    : "Es gab leider einen Fehler";
});

useHead({ title: pageTitle });
</script>

<template>
  <AppSkipLinks
    :links="[
      { label: 'Zum Inhalt', to: '#main' },
      { label: 'Zum Fußbereich', to: '#footer' },
    ]"
  />

  <NuxtLayout name="default">
    <div class="content-wrapper pt-48 pb-24" data-testid="error-message">
      <template v-if="isNotFoundError">
        <h1 class="typo-headline2-bold inline-block">
          Diese Seite existiert nicht
        </h1>
        <p class="mt-8">
          Überprüfen Sie den eingegebenen Link<ClientOnly
            >: {{ locationClientOnly }}</ClientOnly
          >
        </p>
        <SimpleSearchInput
          class="mt-48"
          model-value=""
          @update:model-value="(query) => redirectToSearch(query)"
          @empty-search="redirectToSearch()"
        />
      </template>
      <template v-else>
        <h1 class="typo-headline2-bold inline-block">
          Es gab leider einen Fehler
        </h1>
        <p v-if="isInternalServerError" class="mt-24">
          Probieren Sie es zu einem späteren Moment noch einmal.<br />Wir haben
          den Fehler dokumentiert und an unsere Entwickler:innen weitergeleitet.
        </p>
        <p v-else class="mt-24">
          Ein unbekannter Fehler ist aufgetreten: {{ error?.statusCode }}
          {{ error?.statusMessage }}
        </p>
      </template>
    </div>
  </NuxtLayout>
</template>
