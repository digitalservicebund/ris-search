<script setup lang="ts">
import { useData } from "vitepress";
import { computed } from "vue";
import type { EndpointParameter } from "../endpoints.data";
import CodeBadge from "./CodeBadge.vue";
import Icon from "./Icon.vue";
import { Disclosure, DisclosureButton, DisclosurePanel } from "@headlessui/vue";

const props = defineProps<{ param: EndpointParameter }>();
const { lang } = useData();

const param = props.param;
const type = computed(() =>
  param.type === "array" ? `array[${param.items!.type}]` : param.type,
);
</script>

<template>
  <tr :id="param.id" class="target:bg-gray-100">
    <td class="py-6 flex flex-col gap-8 justify-start">
      <div class="flex flex-col md:flex-row gap-6 items-start">
        <a :href="'#' + param.id" class="link-anchor">
          <CodeBadge tint="green">
            {{ param.name }}
          </CodeBadge>
        </a>
        <CodeBadge tint="gray">
          {{ type }},
          {{ param.required ? "required" : "optional" }}
        </CodeBadge>
        <CodeBadge>
          {{ param.in }}
        </CodeBadge>
      </div>

      <span
        class="block text-gray-800 text-sm"
        v-html="param.descriptions[lang]"
      />

      <div>
        <Disclosure
          v-if="param.enum && param.enum.length > 0"
          v-slot="{ open }"
        >
          <DisclosureButton>
            <CodeBadge tint="blue" :class="{ 'mb-6': open }">
              Values
              <Icon
                id="chevron-right"
                :class="open ? 'rotate-90 transform' : ''"
              />
            </CodeBadge>
          </DisclosureButton>
          <DisclosurePanel>
            <div class="flex flex-row gap-6 flex-wrap">
              <template v-for="value in param.enum">
                <CodeBadge tint="blue">
                  {{ value }}
                </CodeBadge>
              </template>
            </div>
          </DisclosurePanel>
        </Disclosure>
      </div>
    </td>
    <td class="py-8">
      <code v-if="param.default !== undefined && param.default !== null">
        {{ JSON.stringify(param.default) }}
      </code>
      <span v-else> - </span>
    </td>
  </tr>
</template>
