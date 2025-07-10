<script setup lang="ts">
import AdvancedSearch from "~/components/Search/AdvancedSearch.vue";
import { isPublicProfile, isInternalProfile } from "~/utils/config";
const isInternal = isInternalProfile();
const isPublic = isPublicProfile();
const isPrototype = isPrototypeProfile();
definePageMeta({ alias: "/erweiterte-suche" });
useHead({ title: "Suche" });

if (isPrototype) {
  throw createError({
    statusCode: 404,
  });
}
</script>
<template>
  <PageHeader v-if="isInternal" title="Suche">
    <div class="flex flex-col">
      <span class="ris-label2-bold">Die Suche ist aktuell ein Prototyp</span>
      <span class="ris-body1-regular">
        Die Funktionalität der Suche befindet sich gerade in der Entwicklung und
        ist zum Testen für
        <strong>veröffentlichte</strong> Dokumentationseinheiten verfügbar.
        <router-link
          class="underline focus:outline-hidden focus-visible:outline-4 focus-visible:outline-offset-4 focus-visible:outline-blue-800"
          target="_blank"
          to="/erweiterte-suche/hilfe"
        >
          Lesen Sie hier mehr über die aktuelle Funktionalität.
        </router-link>
      </span>
    </div>
  </PageHeader>
  <PageHeader v-if="isPublic" title="Erweiterte Suche">
    Über die erweiterte Suche können Sie gezielt nach Dokumenten suchen. Wählen
    Sie die Dokumentart und den Sucheingabemodus aus und geben Sie Ihre
    Suchanfrage ein.
    <router-link class="" target="_blank" to="/erweiterte-suche/hilfe">
      Erfahren Sie hier mehr über die Funktionalität der Suche.
    </router-link>
  </PageHeader>
  <AdvancedSearch />
</template>
