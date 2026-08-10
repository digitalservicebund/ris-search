import type { Meta, StoryObj } from "@storybook/vue3-vite";
import type { ConcreteComponent } from "vue";
import { html } from "../../utils/tags";
import UiBadge, { BadgeColor } from "./Badge.vue";
import UiDataTable, { type DataTableColumn } from "./DataTable.vue";

type VersionRow = {
  key: string;
  attrs?: Record<string, unknown>;
  current?: boolean;
  fromDate: string;
  toDate: string;
  status: string;
};

const columns: DataTableColumn<VersionRow>[] = [
  { key: "fromDate", label: "Gültig ab" },
  { key: "toDate", label: "Gültig bis" },
  { key: "status", label: "Status" },
];

const rows: VersionRow[] = [
  {
    key: "unknown",
    fromDate: "–",
    toDate: "–",
    status: "Unbekannt",
  },
  {
    key: "future",
    fromDate: "27.10.2026",
    toDate: "–",
    status: "Zukünftig in Kraft",
  },
  {
    key: "current",
    fromDate: "15.08.2025",
    toDate: "26.10.2026",
    status: "Aktuell gültig",
  },
  {
    key: "expired",
    fromDate: "28.02.2025",
    toDate: "14.08.2025",
    status: "Außer Kraft",
  },
];

/**
 * The component is generic over the row type. Spelling the args out explicitly
 * keeps Storybook from resolving the type parameter to its constraint, which
 * would reject the column keys below.
 */
type DataTableArgs = {
  columns: DataTableColumn<VersionRow>[];
  rowAs?: string;
  rows: VersionRow[];
};

const meta: Meta<DataTableArgs> = {
  // The component is generic, so its type doesn't match the concrete component
  // signature Storybook expects. `DataTableArgs` above describes the props.
  component: UiDataTable as unknown as ConcreteComponent<DataTableArgs>,
  tags: ["autodocs"],
  args: {
    columns,
    rowAs: undefined,
    rows,
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiDataTable },
    setup() {
      return { args };
    },
    template: html`<UiDataTable v-bind="args" aria-label="Fassungen" />`,
  }),
};

/**
 * Whole rows become links by passing a link component (or a plain `a`) as
 * `rowAs`. The per-row target lives in `row.attrs`, so the browser's own
 * context menu works — unlike a click handler on the row.
 */
export const Links: Story = {
  args: {
    rowAs: "a",
    rows: rows.map((row) => ({ ...row, attrs: { href: `#${row.key}` } })),
  },
  render: (args) => ({
    components: { UiDataTable },
    setup() {
      return { args };
    },
    template: html`<UiDataTable v-bind="args" aria-label="Fassungen" />`,
  }),
};

/** The row the user is currently on is highlighted and marked `aria-current`. */
export const CurrentRow: Story = {
  args: {
    rowAs: "a",
    rows: rows.map((row) => ({
      ...row,
      attrs: { href: `#${row.key}` },
      current: row.key === "current",
    })),
  },
  render: (args) => ({
    components: { UiDataTable },
    setup() {
      return { args };
    },
    template: html`<UiDataTable v-bind="args" aria-label="Fassungen" />`,
  }),
};

/**
 * Cells render as plain text by default. A `cell-<key>` slot overrides a single
 * column, here to render the status as a badge.
 */
export const CustomCell: Story = {
  render: () => ({
    components: { UiDataTable, UiBadge },
    setup() {
      return {
        columns,
        rows: [
          {
            key: "unknown",
            fromDate: "–",
            toDate: "–",
            status: { label: "Unbekannt", color: BadgeColor.BLUE },
          },
          {
            key: "future",
            fromDate: "27.10.2026",
            toDate: "–",
            status: { label: "Zukünftig in Kraft", color: BadgeColor.YELLOW },
          },
          {
            key: "current",
            fromDate: "15.08.2025",
            toDate: "26.10.2026",
            status: { label: "Aktuell gültig", color: BadgeColor.GREEN },
          },
          {
            key: "expired",
            fromDate: "28.02.2025",
            toDate: "14.08.2025",
            status: { label: "Außer Kraft", color: BadgeColor.RED },
          },
        ],
      };
    },
    template: html`
      <UiDataTable :columns="columns" :rows="rows" aria-label="Fassungen">
        <template #cell-status="{ row }">
          <UiBadge :label="row.status.label" :color="row.status.color" />
        </template>
      </UiDataTable>
    `,
  }),
};

export const Empty: Story = {
  args: { rows: [] },
  render: (args) => ({
    components: { UiDataTable },
    setup() {
      return { args };
    },
    template: html`
      <UiDataTable v-bind="args" aria-label="Fassungen">
        <template #empty>Keine Ergebnisse gefunden</template>
      </UiDataTable>
    `,
  }),
};
