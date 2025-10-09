<script setup lang="ts">
import MetadataField from "~/components/MetadataField.vue";

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
  const convertedNames = props.authors.map((author) => {
    const [lastName, firstName] = author.split(",");
    return `${firstName} ${lastName}`;
  });

  return formatArrayProperty(convertedNames);
});

const yearOfPublication = computed(() =>
  formatArrayProperty(props.yearsOfPublication),
);

function formatArrayProperty(property: string[]): string {
  return property.join(", ") || "-";
}
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
