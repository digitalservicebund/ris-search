import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { html } from "../../utils/tags";
import Badge, { BadgeColor } from "./Badge.vue";

const meta: Meta<typeof Badge> = {
  component: Badge,
  tags: ["autodocs"],
  args: {
    label: "Badge",
    color: BadgeColor.BLUE,
  },
  argTypes: {
    color: {
      control: "select",
      options: ["blue", "green", "yellow", "red"],
      mapping: {
        blue: BadgeColor.BLUE,
        green: BadgeColor.GREEN,
        yellow: BadgeColor.YELLOW,
        red: BadgeColor.RED,
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { Badge },
    setup() {
      return { args };
    },
    template: html`<Badge v-bind="args" />`,
  }),
};
