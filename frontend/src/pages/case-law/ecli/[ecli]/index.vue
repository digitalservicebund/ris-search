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
  <PageHeader title="Platzhalter">
    <DetailsList>
      <DetailsListEntry label="ECLI" :value="ecli" />
    </DetailsList>
  </PageHeader>

  <div>
    <div class="border border-yellow-300 bg-yellow-100 px-16 py-12">
      <p class="leading-6">Platzhalter für die Anzeige einer Rechtsprechung.</p>
    </div>
  </div>
</template>
