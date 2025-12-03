<script setup lang="ts">
import { formatArray, formatNames } from "~/utils/textFormatting";

export interface LiteratureDetails {
  normReferences: string[];
  collaborators: string[];
  originators: string[];
  languages: string[];
  conferenceNotes: string[];
}

const props = defineProps<{ details: LiteratureDetails }>();

const properties = computed(() => {
  return [
    {
      label: "Norm:",
      value: formatArray(props.details.normReferences),
    },
    {
      label: "Mitarbeiter:",
      value: formatArray(formatNames(props.details.collaborators)),
    },
    {
      label: "Urheber:",
      value: formatArray(props.details.originators),
    },
    {
      label: "Sprache:",
      value: formatArray(props.details.languages),
    },
    {
      label: "Kongress:",
      value: formatArray(props.details.conferenceNotes),
    },
  ];
});
</script>

<template>
  <h2 id="detailsTabPanelTitle" class="ris-heading3-bold my-24">Details</h2>
  <IncompleteDataMessage class="my-24" />
  <DetailsList aria-labelledby="detailsTabPanelTitle">
    <template v-for="property in properties" :key="property.label">
      <DetailsListEntry :label="property.label" :value="property.value" />
    </template>
  </DetailsList>
</template>
