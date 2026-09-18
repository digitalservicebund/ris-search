import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { html } from "../../utils/tags";
import UiBadge from "./Badge.vue";

const meta: Meta<typeof UiBadge> = {
  component: UiBadge,
  tags: ["autodocs"],
  args: {
    label: "Badge",
    color: "blue",
  },
  argTypes: {
    color: {
      control: "select",
      options: ["blue", "green", "yellow", "red", "gray"],
    },
    variant: {
      control: "select",
      options: ["extraSmall", "small", "medium", "large"],
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiBadge },
    setup() {
      return { args };
    },
    template: html`<UiBadge v-bind="args" />`,
  }),
};
