<script lang="ts" setup>
import { computed } from "vue";
import TableOfContents from "../components/TableOfContents.vue";
import { useData } from "../composables/data";
import Footer from "./Footer.vue";
import Hero from "./Hero.vue";
import Sidebar from "./Sidebar.vue";
import Banner from "./Banner.vue";
import MobileNav from "./MobileNav.vue";

const { frontmatter } = useData();
const hasOutline = computed(() =>
  typeof frontmatter.value.outline !== "undefined"
    ? !!frontmatter.value.outline
    : true
);
const hasProse = computed(() =>
  typeof frontmatter.value.prose === "boolean" ? frontmatter.value.prose : true
);
</script>

<template>
  <div>
    <Banner class="lg:hidden" />
    <Sidebar
        class="hidden lg:inline-block h-full fixed w-[var(--sidebar-width)] border-r-[1px] border-neutral-tertiary"
    />
    <MobileNav class="lg:hidden" />
    <div class="flex flex-col gap-32 lg:ml-[var(--sidebar-width)] lg:w-[calc(100%-var(--sidebar-width))]">
      <div class="min-h-screen dark:text-white">
        <div class="top-0 bg-background-primary z-40">
          <Banner class="hidden lg:block" />
        </div>
        <Hero v-bind="frontmatter.hero" v-if="!!frontmatter.hero">
          <TableOfContents v-if="hasOutline" />
        </Hero>
        <article class="container pt-32">
          <Content class="content" :class="hasProse && 'prose max-w-prose'" />
        </article>
      </div>
      <Footer />
    </div>
  </div>
</template>
