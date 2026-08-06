import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { ref } from "vue";
import { html } from "../../utils/tags";
import UiYearInput from "./YearInput.vue";

const meta: Meta<typeof UiYearInput> = {
  component: UiYearInput,
  tags: ["autodocs"],
  args: {
    id: "year-input",
    isReadOnly: false,
    showClear: true,
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiYearInput },
    setup() {
      const model = ref("");
      return { args, model };
    },
    template: html`<UiYearInput v-model="model" v-bind="args" />`,
  }),
};

export const WithValue: Story = {
  render: (args) => ({
    components: { UiYearInput },
    setup() {
      const model = ref("2024");
      return { args, model };
    },
    template: html`<UiYearInput v-model="model" v-bind="args" />`,
  }),
};

export const Readonly: Story = {
  args: { isReadOnly: true },
  render: (args) => ({
    components: { UiYearInput },
    setup() {
      const model = ref("2024");
      return { args, model };
    },
    template: html`<UiYearInput v-model="model" v-bind="args" />`,
  }),
};
