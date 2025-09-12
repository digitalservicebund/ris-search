<script setup lang="ts">
import { HttpStatusCode } from "axios";
import type { NuxtError } from "#app";
import SimpleSearchInput from "~/components/Search/SimpleSearch/SimpleSearchInput.vue";
import { useRedirectToSearch } from "~/composables/useRedirectToSearch";
const onSearchInputChange = useRedirectToSearch();

const props = defineProps({
  error: {
    type: Object as () => NuxtError,
    required: true,
  },
});

const isNotFoundError = computed(
  () => props.error?.statusCode === HttpStatusCode.NotFound,
);
const isTokenRefreshError = computed(
  () => props.error?.statusCode === HttpStatusCode.Unauthorized,
);

const isInternalServerError = computed(
  () => props.error?.statusCode === HttpStatusCode.InternalServerError,
);
const locationClientOnly = computed(() => window.location.href);

const route = useRoute();
onMounted(() => {
  // handle the login redirect client-side in order to preserve the
  // query hash, which isn't available during SSR
  if (isTokenRefreshError.value) {
    redirectToLogin(route.fullPath);
  }
});
const pageTitle = computed(() => {
  return `${isNotFoundError.value ? "Diese Seite existiert nicht" : "Es gab leider einen Fehler"}`;
});
useHead({ title: pageTitle.value });
</script>

<template>
  <NuxtLayout>
    <div class="container pt-48 pb-24">
      <h1 class="sr-only">Fehlerseite</h1>
      <template v-if="isNotFoundError">
        <h2 class="ris-heading2-regular inline-block font-semibold">
          Diese Seite existiert nicht
        </h2>
        <p class="ris-body1-regular mt-8">
          Überprüfen Sie den eingegebenen Link<ClientOnly
            >: {{ locationClientOnly }}</ClientOnly
          >
        </p>
        <SimpleSearchInput
          class="mt-48"
          model-value=""
          @update:model-value="
            (query?: string) => onSearchInputChange({ query })
          "
          @empty-search="() => onSearchInputChange()"
        />
      </template>
      <template v-else-if="isTokenRefreshError">
        <div class="flex items-center gap-24">
          <LoadingSpinner />
          <h2 class="ris-heading2-regular inline-block">
            Sie werden angemeldet…
          </h2>
        </div>
      </template>
      <template v-else>
        <h2 class="ris-heading2-regular inline-block font-semibold">
          Es gab leider einen Fehler
        </h2>
        <p v-if="isInternalServerError" class="ris-body1-regular mt-24">
          Probieren Sie es zu einem späteren Moment noch einmal.<br />Wir haben
          den Fehler dokumentiert und an unsere Entwickler:innen weitergeleitet.
        </p>
        <p v-else class="ris-body1-regular mt-24">
          Ein unbekannter Fehler ist aufgetreten: {{ error?.statusCode }}
          {{ error?.statusMessage }}
        </p>
      </template>
    </div>
  </NuxtLayout>
</template>
