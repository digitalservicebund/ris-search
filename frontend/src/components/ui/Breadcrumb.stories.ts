import type { Meta, StoryObj } from "@storybook/vue3-vite";
import type { ConcreteComponent } from "vue";
import IcOutlineChevronRight from "~icons/ic/outline-chevron-right";
import { html } from "../../utils/tags";
// Imported as UiBreadcrumb, not Breadcrumb: the formatter rewrites HTML-lowercase-able
// tags inside html`` templates, which can silently break stories.
import UiBreadcrumb, { type BreadcrumbItem } from "./Breadcrumb.vue";

const items: BreadcrumbItem[] = [
  { label: "Startseite", url: "/" },
  { label: "Gesetze & Verordnungen", url: "/laws" },
  { label: "BGB Bürgerliches Gesetzbuch", url: "/bgb" },
  { label: "Buch 2", url: "/book-2" },
  { label: "Abschnitt 3" },
  { label: "Untertitel 2" },
  { label: "Kapitel 2" },
  { label: "§ 312e Verletzung von Informationspflichten über Kosten" },
];

type BreadcrumbArgs = {
  model: BreadcrumbItem[];
};

const meta: Meta<BreadcrumbArgs> = {
  title: "Breadcrumb",
  component: UiBreadcrumb as unknown as ConcreteComponent<BreadcrumbArgs>,
  tags: ["autodocs"],
  args: {
    model: items,
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// Mirrors how the app's `Breadcrumbs` component actually consumes this: a custom
// `item` slot rendering non-last items as links, and a custom `separator` icon.
export const Default: Story = {
  render: (args) => ({
    components: { UiBreadcrumb, IcOutlineChevronRight },
    setup() {
      return { args };
    },
    template: html`
      <UiBreadcrumb v-bind="args">
        <template #item="{ item, index }">
          <a
            v-if="item.url && index !== args.model.length - 1"
            :href="item.url"
          >
            {{ item.label }}
          </a>
          <span v-else>{{ item.label }}</span>
        </template>
        <template #separator>
          <IcOutlineChevronRight width="1rem" height="1rem" />
        </template>
      </UiBreadcrumb>
    `,
  }),
};

// The default rendering without any slots: plain item labels, "/" separator.
export const Simple: Story = {
  render: (args) => ({
    components: { UiBreadcrumb },
    setup() {
      return { args };
    },
    template: html`<UiBreadcrumb v-bind="args" />`,
  }),
};
