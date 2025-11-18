<script setup lang="ts">
import Properties from "~/components/Properties.vue";
import PropertiesItem from "~/components/PropertiesItem.vue";

definePageMeta({ alias: "/ecli/:ecli" });

const route = useRoute();

const ecli = computed(() => {
  if (Array.isArray(route.params.ecli)) return route.params.ecli[0] || "-";
  return route.params.ecli || "-";
});
</script>

<template>
  <PageHeader title="Platzhalter">
    <Properties>
      <PropertiesItem label="ECLI" :value="ecli" />
    </Properties>
  </PageHeader>
  <div>
    <main class="border border-yellow-300 bg-yellow-100 px-16 py-12">
      <p class="leading-6">Platzhalter für die Anzeige einer Rechtsprechung.</p>
    </main>
  </div>
</template>
