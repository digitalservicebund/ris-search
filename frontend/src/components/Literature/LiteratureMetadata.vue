<script setup lang="ts">
import MetadataField from "~/components/MetadataField.vue";
import { formatArray, formatNames } from "~/utils/textFormatting";

interface Props {
  documentTypes: string[];
  references: string[];
  authors: string[];
  yearsOfPublication: string[];
}

const props = defineProps<Props>();

const documentType = computed(() => formatArray(props.documentTypes));
const reference = computed(() => formatArray(props.references));
const author = computed(() => formatArray(formatNames(props.authors)));
const yearOfPublication = computed(() => formatArray(props.yearsOfPublication));
</script>

<template>
  <div class="mb-48 flex flex-row flex-wrap gap-24">
    <MetadataField label="Dokumenttyp" :value="documentType" />
    <MetadataField label="Fundstelle" :value="reference" />
    <MetadataField label="Autor" :value="author" />
    <MetadataField label="Veröffentlichungsjahr" :value="yearOfPublication" />
  </div>
</template>
