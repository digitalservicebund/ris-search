<script setup lang="ts">
import type { ZitierungLink, Zitierungen } from "~/utils/zitierungen";

const { zitierungen } = defineProps<{ zitierungen: Zitierungen }>();

type Group = { heading: string; links: ZitierungLink[] };
type Section = { heading: string; groups: Group[] };

function hasLinks(group: Group): boolean {
  return group.links.length > 0;
}

const sections = computed<Section[]>(() => {
  const passiv: Section = {
    heading: "Diese Entscheidung wird zitiert in",
    groups: [
      {
        heading: "Gerichtsentscheidungen",
        links: zitierungen.passivRechtsprechung,
      },
      { heading: "Literaturnachweise", links: zitierungen.passivLiteratur },
    ].filter(hasLinks),
  };
  const aktiv: Section = {
    heading: "Diese Entscheidung zitiert",
    groups: [
      {
        heading: "Gerichtsentscheidungen",
        links: zitierungen.aktivRechtsprechung,
      },
    ].filter(hasLinks),
  };

  return [passiv, aktiv].filter((section) => section.groups.length > 0);
});
</script>

<template>
  <div class="content-grid gap-y-24 hyphens-auto 2xl:gap-y-40">
    <div
      v-for="section in sections"
      :key="section.heading"
      class="col-span-12 md:col-span-8 xl:col-span-7"
    >
      <h3 class="typo-headline3-bold pb-16 2xl:pb-24">
        {{ section.heading }}
      </h3>
      <div class="flex flex-col gap-16 2xl:gap-32">
        <div v-for="group in section.groups" :key="group.heading">
          <h4 class="typo-headline3-regular pb-8 2xl:pb-16">
            {{ group.heading }}
          </h4>
          <ul class="flex flex-col gap-8">
            <li
              v-for="(link, index) in group.links"
              :key="index"
              class="typo-label1-regular wrap-break-word"
            >
              <NuxtLink
                v-if="link.to"
                class="typo-link1-regular link-hover"
                :to="link.to"
              >
                {{ link.text }}
              </NuxtLink>
              <template v-else>{{ link.text }}</template>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
