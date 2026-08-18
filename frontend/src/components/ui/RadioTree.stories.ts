import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { ref } from "vue";
import { html } from "../../utils/tags";
import UiRadioTree, { type RadioTreeItem } from "./RadioTree.vue";

const meta: Meta<typeof UiRadioTree> = {
  component: UiRadioTree,
  tags: ["autodocs"],
};

export default meta;
type Story = StoryObj<typeof meta>;

const flatItems: RadioTreeItem[] = [
  { value: "N", label: "Gesetze & Verordnungen" },
  { value: "R", label: "Gerichtsentscheidungen" },
  { value: "V", label: "Verwaltungsvorschriften" },
  { value: "L", label: "Literaturnachweise" },
];

const nestedItems: RadioTreeItem[] = [
  { value: "A", label: "Alle Dokumentarten" },
  { value: "N", label: "Gesetze & Verordnungen" },
  {
    value: "R",
    label: "Gerichtsentscheidungen",
    selfLabel: "Alle Gerichtsentscheidungen",
    children: [
      { value: "R.urteil", label: "Urteil" },
      { value: "R.beschluss", label: "Beschluss" },
      { value: "R.other", label: "Sonstige Entscheidungen" },
    ],
  },
  { value: "V", label: "Verwaltungsvorschriften" },
  { value: "L", label: "Literaturnachweise" },
];

const template = html`
  <fieldset class="w-full md:w-200">
    <legend class="typo-label1-bold mb-8">Dokumentart</legend>
    <UiRadioTree v-bind="args" v-model="selected" />
  </fieldset>
`;

export const Default: Story = {
  args: { items: flatItems },
  render: (args) => ({
    components: { UiRadioTree },
    setup() {
      const selected = ref("N");
      return { args, selected };
    },
    template,
  }),
};

export const Nested: Story = {
  args: { items: nestedItems },
  render: (args) => ({
    components: { UiRadioTree },
    setup() {
      const selected = ref("R.urteil");
      return { args, selected };
    },
    template,
  }),
};

/**
 * Selecting an item with children opens its branch. The item's own row marks
 * the open branch while its `selfLabel` row carries the selection — both are
 * labels of the same radio.
 */
export const ParentSelected: Story = {
  args: { items: nestedItems },
  render: (args) => ({
    components: { UiRadioTree },
    setup() {
      const selected = ref("R");
      return { args, selected };
    },
    template,
  }),
};
