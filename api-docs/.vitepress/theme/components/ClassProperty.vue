<script setup lang="ts">
import { useData } from "vitepress";
import type { ClassProp } from "../utils/classes";
import CodeBadge from "./CodeBadge.vue";
import Icon from "./Icon.vue";

const { lang } = useData();
const props = defineProps<{ prop: ClassProp }>();
const prop = props.prop;

// TODO: The rendering of the types needs to be improved (especially for complex types)
// const type = computed(() => prop.range.map((r) => r.id).join(" | "));
</script>

<template>
  <div
    class="flex flex-row items-start gap-6 px-16 py-12 bg-background-primary target:bg-gray-100"
    :id="prop.slug"
  >
    <div class="w-20 text-center flex-shrink-0">
      <Icon id="circle" size="0.85em" class="text-green-800" />
    </div>
    <div class="flex flex-col gap-6 flex-grow overflow-x-hidden">
      <dt class="flex flex-col items-start md:flex-row gap-x-4">
        <a :href="'#' + prop.slug" class="link-anchor">
          <CodeBadge tint="green">
            {{ prop.id.replaceAll("recht:", "") }}
          </CodeBadge>
        </a>
        <CodeBadge class="text-gray-900" v-if="!!prop.subPropertyOf">
          subPropertyOf {{ prop.subPropertyOf }}
        </CodeBadge>
      </dt>
      <dd
        class="text-gray-800 text-sm"
        v-html="prop.descriptions[lang]"
        v-if="!!prop.descriptions[lang]"
      />
    </div>
  </div>
</template>
