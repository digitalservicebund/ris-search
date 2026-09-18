import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { html } from "../../utils/tags";
import UiProgressSpinner from "./ProgressSpinner.vue";

const meta: Meta<typeof UiProgressSpinner> = {
  component: UiProgressSpinner,
  tags: ["autodocs"],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: () => ({
    components: { UiProgressSpinner },
    template: html`<UiProgressSpinner />`,
  }),
};
