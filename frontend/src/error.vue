<script setup lang="ts">
import type { NuxtError } from "#app";
import SimpleSearchInput from "~/components/search/SimpleSearchInput.vue";

function redirectToSearch(searchStr?: string) {
  navigateTo({ name: "search", query: searchStr ? { query: searchStr } : {} });
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

const locationClientOnly = computed(() => window.location.href);

const pageTitle = computed(() => {
  return `${isNotFoundError.value ? "Diese Seite existiert nicht" : "Es gab leider einen Fehler"}`;
});
useHead({ title: pageTitle.value });
</script>

<template>
  <NuxtLayout name="default">
    <div class="container pt-48 pb-24">
      <template v-if="isNotFoundError">
        <h1 class="ris-heading2-bold inline-block">
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
        <h1 class="ris-heading2-bold inline-block">
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
