<script setup lang="ts">
import { useData } from "vitepress";
import { computed } from "vue";
import { data } from "../endpoints.data";
import RequestParam from "./RequestParam.vue";

const props = defineProps<{ path: string; method: string; in?: string }>();
const { lang } = useData();

const endpoint = computed(() =>
  data.paths[props.path] ? data.paths[props.path][props.method] : null
);

const params = computed(() =>
  endpoint
    ? endpoint.value!.parameters.filter((p) =>
        props.in ? p.in === props.in : true
      )
    : []
);

interface RequestParametersTranslations {
  [lang: string]: {
    parameter: string;
    defaultValue: string;
  };
}

const translations: RequestParametersTranslations = {
  en: {
    parameter: "Parameter",
    defaultValue: "Default Value",
  },
  de: {
    parameter: "Parameter",
    defaultValue: "Standardwert",
  },
};
</script>

<template>
  <div class="breakout">
    <table v-if="!!endpoint" class="not-prose w-full">
      <thead>
        <tr>
          <th
            class="text-left text-xs text-gray-900 py-6 border-b border-gray-400"
          >
            {{ translations[lang]?.parameter ?? "Parameter" }}
          </th>
          <th
            class="text-left text-xs text-gray-900 py-6 border-b border-gray-400"
          >
            {{ translations[lang]?.defaultValue ?? "Default Value" }}
          </th>
          <th
            class="text-left text-xs text-gray-900 py-6 border-b border-gray-400"
          ></th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-400">
        <template v-for="param in params" :key="param.name">
          <RequestParam :param="param" />
        </template>
      </tbody>
    </table>
    <div v-else>
      <div
        class="relative rounded-lg border border-dashed border-gray-400 p-16 text-center"
      >
        Endpoint
        <code>{{ props.method.toLocaleUpperCase() }} {{ props.path }}</code>
        was not found.
      </div>
    </div>
  </div>
</template>
