<script setup lang="ts">
import { formatNames } from "~/utils/textFormatting";

interface Props {
  normReferences: string[];
  collaborators: string[];
  originators: string[];
  languages: string[];
  conferenceNotes: string[];
}

const props = defineProps<Props>();

function format(arrayProperty: string[]) {
  return arrayProperty.join(", ");
}

const properties = computed(() => {
  return [
    {
      label: "Norm:",
      value: format(props.normReferences),
    },
    {
      label: "Mitarbeiter:",
      value: format(formatNames(props.collaborators)),
    },
    {
      label: "Urheber:",
      value: format(props.originators),
    },
    {
      label: "Sprache:",
      value: format(props.languages),
    },
    {
      label: "Kongress:",
      value: format(props.conferenceNotes),
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
