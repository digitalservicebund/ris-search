import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { html } from "../../utils/tags";
import ProgressSpinner from "./ProgressSpinner.vue";

const meta: Meta<typeof ProgressSpinner> = {
  component: ProgressSpinner,
  tags: ["autodocs"],
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: () => ({
    components: { ProgressSpinner },
    template: html`<ProgressSpinner />`,
  }),
};
