import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { html } from "../../utils/tags";
// Imported as UiCombobox, not Combobox: the formatter would otherwise be free
// to reformat the tag oddly across the html`` templates. Kept consistent with
// Button.stories.ts's UiButton convention.
import UiCombobox, { type ComboboxOption } from "./Combobox.vue";

const options: ComboboxOption[] = [
  { id: "BVerfG", label: "Bundesverfassungsgericht" },
  { id: "BGH", label: "Bundesgerichtshof", secondaryLabel: "BGH" },
  { id: "BVerwG", label: "Bundesverwaltungsgericht", secondaryLabel: "BVerwG" },
  { id: "BFH", label: "Bundesfinanzhof", secondaryLabel: "BFH" },
];

const meta: Meta<typeof UiCombobox> = {
  component: UiCombobox,
  tags: ["autodocs"],
  args: {
    options,
    loading: false,
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiCombobox },
    setup() {
      return { args };
    },
    template: html`<UiCombobox
      v-bind="args"
      placeholder="Auswählen oder suchen"
    />`,
  }),
};

export const Preselected: Story = {
  args: { modelValue: "BGH", initialLabel: "Bundesgerichtshof" },
  render: (args) => ({
    components: { UiCombobox },
    setup() {
      return { args };
    },
    template: html`<UiCombobox
      v-bind="args"
      placeholder="Auswählen oder suchen"
    />`,
  }),
};

export const Loading: Story = {
  args: { loading: true },
  render: (args) => ({
    components: { UiCombobox },
    setup() {
      return { args };
    },
    template: html`<UiCombobox
      v-bind="args"
      placeholder="Auswählen oder suchen"
    />`,
  }),
};

export const Empty: Story = {
  args: { options: [] },
  render: (args) => ({
    components: { UiCombobox },
    setup() {
      return { args };
    },
    template: html`<UiCombobox
      v-bind="args"
      placeholder="Auswählen oder suchen"
    />`,
  }),
};
