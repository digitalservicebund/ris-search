<script setup lang="ts" generic="T extends DataTableRow">
import { computed, type Component } from "vue";

export type DataTableColumn<T> = {
  /**
   * Property of the row rendered in this column. Doubles as the suffix of the
   * `cell-<key>` slot name.
   */
  key: Extract<keyof T, string>;
  /**
   * Column header on wide viewports, and the label in front of the value on
   * narrow ones.
   */
  label: string;
};

export type DataTableRow = {
  /** Stable identity of the row. */
  key: string;
  /**
   * Attributes applied onto the row element. Use this to turn rows into links,
   * e.g. `{ to: "/target" }` together with a link component as `rowAs`, or `{
   * href: "/target" }` together with `rowAs="a"`.
   */
  attrs?: Record<string, unknown>;
  /**
   * Marks the row as the one currently being displayed. It is highlighted and
   * announced as the current page.
   */
  current?: boolean;
};

const {
  columns,
  rowAs = "div",
  rows,
} = defineProps<{
  /** Columns to render, in order. */
  columns: DataTableColumn<T>[];
  /**
   * Element or component used for each row. Pass a link component to make whole
   * rows navigable; use `row.attrs` to apply props to the component.
   */
  rowAs?: string | Component;
  /** Rows to render, in order. */
  rows: T[];
}>();

defineSlots<
  {
    /** Shown in place of the rows when there are none. */
    empty?: () => unknown;
  } & {
    /**
     * Overrides how the cell of the column with that key is rendered. Without
     * it, the row's value for that key is rendered as plain text.
     */
    [key in `cell-${string}`]?: (props: {
      row: T;
      column: DataTableColumn<T>;
    }) => unknown;
  }
>();

const gridTemplateColumns = computed(() =>
  // Columns are sized to their content, except for the last one which takes up
  // the remaining space.
  columns.length > 1
    ? `repeat(${columns.length - 1}, auto) minmax(0, 1fr)`
    : "minmax(0, 1fr)",
);
</script>

<template>
  <ul
    class="border-t border-gray-400 md:grid md:[grid-template-columns:var(--data-table-columns)] md:border-t-0"
    :style="{ '--data-table-columns': gridTemplateColumns }"
  >
    <!-- Decorative: every row repeats the column labels for assistive
         technology, so exposing them here as well would only add noise. -->
    <li
      v-if="columns.length"
      class="hidden border-b border-gray-400 md:col-span-full md:grid md:grid-cols-subgrid"
      aria-hidden="true"
    >
      <span
        v-for="column in columns"
        :key="column.key"
        class="typo-label1-bold p-16"
      >
        {{ column.label }}
      </span>
    </li>

    <template v-if="rows.length">
      <li
        v-for="row in rows"
        :key="row.key"
        :class="{ 'bg-gray-100': row.current }"
        class="border-b border-gray-400 hover:bg-gray-100 md:col-span-full md:grid md:grid-cols-subgrid"
      >
        <component
          :is="rowAs"
          :aria-current="row.current ? 'page' : undefined"
          class="grid grid-cols-[max-content_minmax(0,1fr)] items-center gap-16 p-16 focus-visible:outline-4 focus-visible:-outline-offset-4 focus-visible:outline-blue-800 md:col-span-full md:grid-cols-subgrid md:gap-0 md:p-0"
          v-bind="row.attrs"
        >
          <template v-for="column in columns" :key="column.key">
            <span class="typo-label1-bold md:sr-only">{{ column.label }}:</span>
            {{ " " }}
            <span class="typo-label1-regular md:p-16">
              <slot :name="`cell-${column.key}`" :row="row" :column="column">
                {{ row[column.key] }}
              </slot>
            </span>
            {{ " " }}
          </template>
        </component>
      </li>
    </template>

    <template v-else>
      <li class="px-16 py-12 text-left text-gray-900 md:col-span-full">
        <slot name="empty" />
      </li>
    </template>
  </ul>
</template>
