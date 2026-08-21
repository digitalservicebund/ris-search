<script setup lang="ts">
import IcOutlineFileDownload from "~icons/ic/outline-file-download";

export type TextEntry = {
  type: "text";
  label: string;
  value?: string;
  valueClass?: string;
};

export type ListEntry = {
  type: "list";
  label: string;
  values: string[];
};

export type BadgeEntry = {
  type: "badge";
  label: string;
  values: string[];
};

export type HtmlEntry = {
  type: "html";
  label: string;
  html?: string;
  htmlClass?: string;
};

export type LinkEntry = {
  type: "link";
  label: string;
  url?: string;
  text: string;
  dataAttr?: string;
};

export type DetailsListItem =
  | TextEntry
  | ListEntry
  | BadgeEntry
  | HtmlEntry
  | LinkEntry;

const props = defineProps<{
  items: DetailsListItem[];
}>();

const visibleItems = computed(() =>
  props.items.filter((item): boolean => {
    switch (item.type) {
      case "text":
        return !isStringEmpty(item.value);
      case "list":
        return item.values.length > 0;
      case "badge":
        return item.values.length > 0;
      case "html":
        return !isStringEmpty(item.html);
      case "link":
        return !isStringEmpty(item.url);
    }
  }),
);
</script>

<template>
  <dl data-testid="details-list" class="content-grid gap-y-24">
    <div
      v-for="item in visibleItems"
      :key="item.label"
      class="col-span-12 grid grid-cols-subgrid items-baseline"
    >
      <dt
        class="typo-label1-bold col-span-12 hyphens-auto md:col-span-3 xl:col-span-2"
      >
        {{ item.label }}
      </dt>

      <dd
        v-if="item.type === 'text'"
        class="typo-label1-regular col-span-12 md:col-span-9 md:col-start-4 lg:col-span-6 lg:col-start-4"
        :class="item.valueClass"
      >
        {{ item.value }}
      </dd>

      <template v-else-if="item.type === 'list'">
        <dd
          v-for="value in item.values"
          :key="value"
          class="typo-label1-regular col-span-12 md:col-span-9 md:col-start-4 lg:col-span-6 lg:col-start-4"
        >
          {{ value }}
        </dd>
      </template>

      <template v-else-if="item.type === 'badge'">
        <dd
          class="col-span-12 md:col-span-9 md:col-start-4 lg:col-span-6 lg:col-start-4"
        >
          <div class="mt-4 flex flex-wrap gap-4 md:mt-0">
            <UiBadge
              v-for="value in item.values"
              :key="value"
              color="gray"
              variant="large"
              :label="value"
            />
          </div>
        </dd>
      </template>

      <dd
        v-else-if="item.type === 'html'"
        class="typo-label1-regular col-span-12 md:col-span-9 md:col-start-4 lg:col-span-6 lg:col-start-4"
        :class="item.htmlClass"
      >
        <div v-html="item.html" />
      </dd>

      <dd
        v-else-if="item.type === 'link'"
        class="typo-label1-regular col-span-12 md:col-span-9 md:col-start-4 lg:col-span-6 lg:col-start-4"
      >
        <NuxtLink
          class="typo-link-regular"
          external
          :data-attr="item.dataAttr"
          :to="item.url!"
        >
          <IcOutlineFileDownload class="mr-2 inline" />
          {{ item.text }}
        </NuxtLink>
      </dd>
    </div>
  </dl>
</template>
