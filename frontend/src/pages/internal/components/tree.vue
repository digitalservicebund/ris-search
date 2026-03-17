<script setup lang="ts">
import type { TreeItem } from "~/components/TreeView.vue";

definePageMeta({
  layout: "norm",
  middleware: () => {
    const config = useRuntimeConfig();
    if (!config.public.privateFeaturesEnabled) return abortNavigation();
  },
});

const sampleTree: TreeItem[] = [
  {
    key: "buch-1",
    title: "Erstes Buch",
    subtitle: "Allgemeine Vorschriften",
    children: [
      {
        key: "abschnitt-1",
        title: "Erster Abschnitt",
        subtitle: "Grundlagen",
        to: { hash: "#example" },
        children: [
          {
            key: "para-1",
            title: "§ 1",
            subtitle: "Grundsatz",
            to: { hash: "#example2" },
          },
          { key: "para-2", title: "§ 2", subtitle: "Begriffsbestimmungen" },
          { key: "para-3", title: "§ 3", subtitle: "Anwendungsbereich" },
        ],
      },
      {
        key: "abschnitt-2",
        title: "Zweiter Abschnitt",
        subtitle: "Ausnahmen und Sonderregelungen",
        children: [
          {
            key: "para-4",
            title: "§ 4",
            subtitle:
              "Festlegung und Anpassung der Werte nach § 46 Absatz 8 Satz 1 des Zweiten Buches",
          },
          { key: "para-5", title: "§ 5", subtitle: "Ausnahmen" },
        ],
      },
    ],
  },
  {
    key: "buch-2",
    title: "Zweites Buch",
    subtitle: "Schlussvorschriften",
    children: [
      { key: "para-6", title: "§ 6", subtitle: "Übergangsvorschriften" },
      { key: "para-7", title: "§ 7", subtitle: "Inkrafttreten" },
    ],
  },
  { key: "anhang", title: "Anhang" },
];

const expandedKeys = ref<string[]>([]);
const selectedKey = ref<string>();
const lastClicked = ref<TreeItem>();
</script>

<template>
  <div class="container flex gap-64 py-32 lg:py-64">
    <div class="flex-1">
      <TreeView
        :items="sampleTree"
        heading="Inhaltsverzeichnis"
        subheading="Subtitle for the heading"
        v-model:expanded-keys="expandedKeys"
        v-model:selected="selectedKey"
        @click="lastClicked = $event"
      />
    </div>
  </div>
</template>
