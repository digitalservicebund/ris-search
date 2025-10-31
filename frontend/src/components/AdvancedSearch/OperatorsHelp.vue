<script setup lang="ts">
import { RisSingleAccordion } from "@digitalservicebund/ris-ui/components";

const data = [
  {
    operator: "AND",
    description: "Zeigt nur Ergebnisse, die beide Begriffe enthalten",
  },
  {
    operator: "OR",
    description: "Zeigt Ergebnisse mit einem oder beiden Begriffen",
  },
  {
    operator: "NOT",
    description: "Schließt Ergebnisse mit dem folgenden Begriff aus",
  },
  {
    operator: '"genaue Phrase"',
    description: "Findet genau diese Wortfolge",
  },
  {
    operator: "~5",
    description:
      "Findet Treffer, in denen zwei Wörter max. 5 Wörter auseinander stehen ",
  },
];

const isExpanded = ref(true);
</script>

<template>
  <RisSingleAccordion
    v-model="isExpanded"
    header-collapsed="Hilfestellung zu Suchoperatoren"
    header-expanded="Hilfestellung zu Suchoperatoren"
  >
    <div class="bg-white p-24">
      <table class="grid grid-cols-[min-content_auto] text-left">
        <caption class="sr-only">
          Hilfestellung zu Suchoperatoren
        </caption>
        <thead class="ris-label3-bold contents text-gray-900">
          <tr class="contents">
            <th scope="col">Operator</th>
            <th scope="col">Bedeutung</th>
          </tr>
        </thead>
        <tbody class="contents">
          <tr v-for="(row, i) in data" :key="i" class="contents">
            <td class="border-b border-b-gray-200 py-6 pr-24">
              <span
                class="ris-label3-bold content-center rounded-lg border border-blue-300 bg-blue-200 px-8 py-4 text-nowrap text-blue-900"
              >
                {{ row.operator }}
              </span>
            </td>
            <td class="border-b border-b-gray-200 py-6">
              <span class="ris-label3-regular content-center text-gray-900">
                {{ row.description }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </RisSingleAccordion>
</template>
