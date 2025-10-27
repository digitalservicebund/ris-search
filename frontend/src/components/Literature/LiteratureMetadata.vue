<script setup lang="ts">
import MetadataField from "~/components/MetadataField.vue";
import { formatArrayProperty, formatNames } from "~/utils/textFormatting";

interface Props {
  documentTypes: string[];
  references: string[];
  authors: string[];
  yearsOfPublication: string[];
}

const props = defineProps<Props>();

const documentType = computed(() => formatArrayProperty(props.documentTypes));
const reference = computed(() => formatArrayProperty(props.references));

const author = computed(() => {
  const convertedNames = formatNames(props.authors);
  return formatArrayProperty(convertedNames);
});

const yearOfPublication = computed(() =>
  formatArrayProperty(props.yearsOfPublication),
);
</script>

<template>
  <div class="mb-48 flex flex-row flex-wrap gap-24">
    <MetadataField
      id="document_type"
      label="Dokumenttyp"
      :value="documentType"
    />
    <MetadataField id="reference" label="Fundstelle" :value="reference" />
    <MetadataField id="author" label="Author" :value="author" />
    <MetadataField
      id="year_of_publication"
      label="Veröffentlichungsjahr"
      :value="yearOfPublication"
    />
  </div>
</template>
