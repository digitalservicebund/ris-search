import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { html } from "../../utils/tags";
import UiBadge, { BadgeColor } from "./Badge.vue";

const meta: Meta<typeof UiBadge> = {
  component: UiBadge,
  tags: ["autodocs"],
  args: {
    label: "Badge",
    color: BadgeColor.BLUE,
  },
  argTypes: {
    color: {
      control: "select",
      options: ["blue", "green", "yellow", "red", "gray"],
      mapping: {
        blue: BadgeColor.BLUE,
        green: BadgeColor.GREEN,
        yellow: BadgeColor.YELLOW,
        red: BadgeColor.RED,
        gray: BadgeColor.GRAY,
      },
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
