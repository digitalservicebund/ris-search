import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { html } from "../../utils/tags";
import UiButton from "./Button.vue";
import UiTooltip from "./Tooltip.vue";

const meta: Meta<typeof UiTooltip> = {
  component: UiTooltip,
  tags: ["autodocs"],
  args: {
    text: "Look at these fields",
    side: "top",
  },
  argTypes: {
    side: { control: "select", options: ["top", "right", "bottom", "left"] },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiTooltip, UiButton },
    setup() {
      return { args };
    },
    template: html`
      <div class="flex justify-center p-64">
        <UiTooltip v-bind="args">
          <UiButton>Hover me</UiButton>
        </UiTooltip>
      </div>
    `,
  }),
};

export const Placements: Story = {
  render: () => ({
    components: { UiTooltip, UiButton },
    template: html`
      <div class="flex flex-wrap justify-items-start gap-64 p-64">
        <UiTooltip text="Nach oben" side="top">
          <UiButton>Top</UiButton>
        </UiTooltip>
        <UiTooltip text="Nach rechts" side="right">
          <UiButton>Right</UiButton>
        </UiTooltip>
        <UiTooltip text="Nach unten" side="bottom">
          <UiButton>Bottom</UiButton>
        </UiTooltip>
        <UiTooltip text="Nach links" side="left">
          <UiButton>Left</UiButton>
        </UiTooltip>
      </div>
    `,
  }),
};

export const Disabled: Story = {
  args: { text: undefined },
  render: (args) => ({
    components: { UiTooltip, UiButton },
    setup() {
      return { args };
    },
    template: html`
      <div class="flex justify-center p-64">
        <UiTooltip v-bind="args">
          <UiButton>No tooltip</UiButton>
        </UiTooltip>
      </div>
    `,
  }),
};
