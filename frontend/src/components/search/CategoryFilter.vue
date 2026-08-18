<script setup lang="ts">
import type { RadioTreeItem } from "~/components/ui/RadioTree.vue";
import { DocumentKind } from "~/types/api";

const model = defineModel<string>({ required: true });

const items: RadioTreeItem[] = [
  {
    label: "Alle Dokumentarten",
    value: DocumentKind.All,
  },
  {
    label: "Gesetze & Verordnungen",
    value: DocumentKind.Norm,
  },
  {
    label: "Gerichtsentscheidungen",
    value: DocumentKind.CaseLaw,
    selfLabel: "Alle Gerichtsentscheidungen",
    children: [
      {
        label: "Urteil",
        value: `${DocumentKind.CaseLaw}.urteil`,
      },
      {
        label: "Beschluss",
        value: `${DocumentKind.CaseLaw}.beschluss`,
      },
      {
        label: "Sonstige Entscheidungen",
        value: `${DocumentKind.CaseLaw}.other`,
      },
    ],
  },
  {
    label: "Verwaltungsvorschriften",
    value: DocumentKind.AdministrativeDirective,
  },
  {
    label: "Literaturnachweise",
    value: DocumentKind.Literature,
  },
];
</script>

<template>
  <fieldset class="w-full md:w-200">
    <legend class="sr-only">Dokumentarten</legend>
    <UiRadioTree :items="items" v-model="model" />
  </fieldset>
</template>
