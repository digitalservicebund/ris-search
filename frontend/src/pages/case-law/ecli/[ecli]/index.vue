<script setup lang="ts">
import DetailsList from "~/components/DetailsList.vue";
import DetailsListEntry from "~/components/DetailsListEntry.vue";

definePageMeta({ alias: "/ecli/:ecli" });

const route = useRoute();

const ecli = computed(() => {
  if (Array.isArray(route.params.ecli)) return route.params.ecli[0] || "-";
  return route.params.ecli || "-";
});
</script>

<template>
  <h1
    class="ris-heading1-regular mb-16 inline-block hyphens-auto max-sm:leading-48"
  >
    Platzhalter
  </h1>

  <DetailsList>
    <DetailsListEntry label="ECLI" :value="ecli" />
  </DetailsList>

  <div>
    <div class="border border-yellow-300 bg-yellow-100 px-16 py-12">
      <p class="leading-6">Platzhalter für die Anzeige einer Rechtsprechung.</p>
    </div>
  </div>
</template>
