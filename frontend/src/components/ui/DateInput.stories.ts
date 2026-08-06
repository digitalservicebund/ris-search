import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { ref } from "vue";
import { html } from "../../utils/tags";
import UiDateInput from "./DateInput.vue";

const meta: Meta<typeof UiDateInput> = {
  component: UiDateInput,
  tags: ["autodocs"],
  args: {
    id: "date-input",
    isReadOnly: false,
    showClear: false,
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiDateInput },
    setup() {
      const model = ref("");
      return { args, model };
    },
    template: html`<UiDateInput v-model="model" v-bind="args" />`,
  }),
};

export const WithValue: Story = {
  render: (args) => ({
    components: { UiDateInput },
    setup() {
      const model = ref("2024-04-22");
      return { args, model };
    },
    template: html`<UiDateInput v-model="model" v-bind="args" />`,
  }),
};

export const Clearable: Story = {
  args: { showClear: true },
  render: (args) => ({
    components: { UiDateInput },
    setup() {
      const model = ref("2024-04-22");
      return { args, model };
    },
    template: html`<UiDateInput v-model="model" v-bind="args" />`,
  }),
};

export const Readonly: Story = {
  args: { isReadOnly: true },
  render: (args) => ({
    components: { UiDateInput },
    setup() {
      const model = ref("2024-04-22");
      return { args, model };
    },
    template: html`<UiDateInput v-model="model" v-bind="args" />`,
  }),
};
