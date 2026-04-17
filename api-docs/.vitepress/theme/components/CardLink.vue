<script setup lang="ts">
import { useData } from "vitepress";
import Icon from "../components/Icon.vue";
import { prependBase } from "../utils";

const base = useData().site.value.base || "/";
const props = defineProps<{ title: string; href: string; icon: string }>();
</script>

<template>
  <a
    class="block no-underline border border-neutral-tertiary p-16 md:p-24 leading-relaxed hover:shadow-sm rounded"
    :href="prependBase(props.href, base)"
  >
    <div
      class="ris-heading3-regular text-brand-primary mb-16 flex flex-row gap-8 items-center leading-tight"
    >
      <Icon :id="props.icon" size="1.5em" />
      {{ props.title }}
    </div>
    <slot />
  </a>
</template>
