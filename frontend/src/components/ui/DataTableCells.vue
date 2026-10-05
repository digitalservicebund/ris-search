<script setup lang="ts" generic="T">
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

const { columns, row } = defineProps<{
  /** Columns to render, in order. */
  columns: DataTableColumn<T>[];
  /** Row whose values are rendered. */
  row: T;
}>();

defineSlots<{
  /**
   * Overrides how the cell of the column with that key is rendered. Without it,
   * the row's value for that key is rendered as plain text.
   */
  [key in `cell-${string}`]?: (props: {
    row: T;
    column: DataTableColumn<T>;
  }) => unknown;
}>();
</script>

<template>
  <!-- Renders a label and a value per column as direct grid items, so the
       parent must be the grid: two columns (label, value) on narrow viewports,
       and on wide ones a subgrid with one track per column, as the labels are
       then visually hidden. -->
  <template v-for="column in columns" :key="column.key">
    <span class="typo-label1-bold flex min-h-32 items-center md:sr-only"
      >{{ column.label }}:</span
    >
    {{ " " }}
    <span
      class="typo-label1-regular flex min-h-32 items-center md:min-h-48 md:px-16 md:py-10"
    >
      <slot :name="`cell-${column.key}`" :row="row" :column="column">
        {{ row[column.key] }}
      </slot>
    </span>
    {{ " " }}
  </template>
</template>
