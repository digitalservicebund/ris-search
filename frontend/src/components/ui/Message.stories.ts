import type { Meta, StoryObj } from "@storybook/vue3-vite";
import IcBaselineHistory from "~icons/ic/baseline-history";
import { html } from "../../utils/tags";
import UiMessage from "./Message.vue";

const meta: Meta<typeof UiMessage> = {
  component: UiMessage,

  tags: ["autodocs"],

  args: {
    severity: undefined,
  },

  argTypes: {
    severity: {
      control: "select",
      options: ["success", "info", "warn", "error"],
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiMessage },
    setup() {
      return { args };
    },
    template: html`<UiMessage v-bind="args">Message content</UiMessage>`,
  }),
};

export const Success: Story = {
  args: { severity: "success" },
  render: (args) => ({
    components: { UiMessage },
    setup() {
      return { args };
    },
    template: html`<UiMessage v-bind="args">Message content</UiMessage>`,
  }),
};

export const Info: Story = {
  args: { severity: "info" },
  render: (args) => ({
    components: { UiMessage },
    setup() {
      return { args };
    },
    template: html`<UiMessage v-bind="args">Message content</UiMessage>`,
  }),
};

export const Warn: Story = {
  args: { severity: "warn" },
  render: (args) => ({
    components: { UiMessage },
    setup() {
      return { args };
    },
    template: html`<UiMessage v-bind="args">Message content</UiMessage>`,
  }),
};

export const WithError: Story = {
  args: { severity: "error" },
  render: (args) => ({
    components: { UiMessage },
    setup() {
      return { args };
    },
    template: html`<UiMessage v-bind="args">Message content</UiMessage>`,
  }),
};

export const WithHeadingAndContent: Story = {
  render: (args) => ({
    components: { UiMessage },
    setup() {
      return { args };
    },
    template: html`
      <UiMessage v-bind="args">
        <p>Heading</p>
        <p>Message content</p>
      </UiMessage>
    `,
  }),
};

/**
 * Each severity comes with a default icon. Use the `icon` slot to replace it,
 * for example when the message reflects a state rather than a severity.
 */
export const WithCustomIcon: Story = {
  render: (args) => ({
    components: { UiMessage, IcBaselineHistory },
    setup() {
      return { args };
    },
    template: html`
      <UiMessage v-bind="args">
        <template #icon>
          <IcBaselineHistory class="text-blue-800" />
        </template>
        Message content
      </UiMessage>
    `,
  }),
};
