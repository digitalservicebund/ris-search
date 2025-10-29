<script setup lang="ts">
import { formatArray, formatNames } from "~/utils/textFormatting";

interface Props {
  normReferences: string[];
  collaborators: string[];
  originators: string[];
  languages: string[];
  conferenceNotes: string[];
}

const props = defineProps<Props>();

const properties = computed(() => {
  return [
    {
      label: "Norm:",
      value: formatArray(props.normReferences),
    },
    {
      label: "Mitarbeiter:",
      value: formatArray(formatNames(props.collaborators)),
    },
    {
      label: "Urheber:",
      value: formatArray(props.originators),
    },
    {
      label: "Sprache:",
      value: formatArray(props.languages),
    },
    {
      label: "Kongress:",
      value: formatArray(props.conferenceNotes),
    },
  ];
});
</script>

<template>
  <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">Details</h2>
  <IncompleteDataMessage class="my-24" />
  <Properties aria-labelledby="detailsTabPanelTitle">
    <template v-for="property in properties" :key="property.label">
      <PropertiesItem :label="property.label" :value="property.value" />
    </template>
  </Properties>
</template>
