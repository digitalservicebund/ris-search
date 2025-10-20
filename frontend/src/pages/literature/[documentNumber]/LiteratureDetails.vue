<script setup lang="ts">
import { formatArrayProperty } from "~/utils/textFormatting";

interface Props {
  languages: string[];
  authors: string[];
  collaborators: string[];
  conferenceNotes: string[];
  normReferences: string[];
}

const props = defineProps<Props>();

const properties = computed(() => {
  return [{ label: "Sprache", value: formatArrayProperty(props.languages) }];
});
</script>

<template>
  <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">Details</h2>
  <IncompleteDataMessage class="my-24" />
  <Properties v-for="property in properties" :key="property.label">
    <PropertiesItem :label="property.label" :value="property.value" />
  </Properties>
</template>
