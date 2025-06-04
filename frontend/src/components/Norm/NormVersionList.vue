<script setup lang="ts">
import IncompleteDataMessage from "~/components/IncompleteDataMessage.vue";
import { useFetch } from "#app";
import type { JSONLDList, LegislationWork, SearchResult } from "~/types";
import NormVersionListRow from "./NormVersionListRow.vue";
import _ from "lodash";

const props = defineProps<{
  workEli: string;
}>();

const backendURL = useBackendURL();
const {
  status,
  data,
  error: loadError,
} = await useFetch<JSONLDList<SearchResult<LegislationWork>>>(
  `${backendURL}/v1/legislation`,
  {
    params: {
      eli: props.workEli,
    },
  },
);

if (loadError?.value) {
  showError(loadError.value);
}

const sortedMembers = computed(() =>
  _.sortBy(
    data.value?.member,
    (member) => member.item.workExample.temporalCoverage,
  ),
);
</script>

<template>
  <section aria-labelledby="fassungenTabPanelTitle">
    <h2 id="fassungenTabPanelTitle" class="ris-heading3-bold my-24">
      Fassungen
    </h2>
    <IncompleteDataMessage class="my-24" />

    <div v-if="status === 'pending'">
      <DelayedLoadingMessage class="my-24 w-24">Lade...</DelayedLoadingMessage>
    </div>
    <table class="border-separate border-spacing-x-24 border-spacing-y-16">
      <caption class="sr-only">
        Fassungshistorie
      </caption>
      <thead class="sr-only">
        <tr>
          <th scope="col">Gültig von</th>
          <th scope="col">Metadaten der Fassung</th>
        </tr>
      </thead>
      <tbody>
        <NormVersionListRow
          v-for="member in sortedMembers"
          :key="member.item.workExample['@id']"
          :item="member.item"
        />
      </tbody>
    </table>
  </section>
</template>
