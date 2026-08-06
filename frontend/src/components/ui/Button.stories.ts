import type { Meta, StoryObj } from "@storybook/vue3-vite";
import IcBaselineCheck from "~icons/ic/baseline-check";
import { html } from "../../utils/tags";
// Imported as UiButton, not Button: the formatter rewrites the lowercase-able
// `<Button>` tag to the native `<button>` inside the html`` templates, which
// silently breaks the stories.
import UiButton from "./Button.vue";

const meta: Meta<typeof UiButton> = {
  component: UiButton,
  tags: ["autodocs"],
  args: {
    disabled: false,
    label: "Button",
    loading: false,
    severity: undefined,
    size: undefined,
    text: false,
    rounded: false,
  },
  argTypes: {
    size: { control: "select", options: ["small", "normal", "large"] },
    severity: {
      control: "select",
      options: ["primary", "secondary", "danger", "warn", "info"],
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiButton },
    setup() {
      return { args };
    },
    template: html`<UiButton v-bind="args" />`,
  }),
};

export const Primary: Story = {
  args: { severity: "primary" },
  render: (args) => ({
    components: { UiButton },
    setup() {
      return { args };
    },
    template: html`<UiButton v-bind="args" />`,
  }),
};

export const Secondary: Story = {
  args: { severity: "secondary" },
  render: (args) => ({
    components: { UiButton },
    setup() {
      return { args };
    },
    template: html`<UiButton v-bind="args" />`,
  }),
};

export const Danger: Story = {
  args: { severity: "danger" },
  render: (args) => ({
    components: { UiButton },
    setup() {
      return { args };
    },
    template: html`<UiButton v-bind="args" />`,
  }),
};

export const SkipLink: Story = {
  args: { severity: "warn" },
  render: (args) => ({
    components: { UiButton },
    setup() {
      return { args };
    },
    template: html`<UiButton v-bind="args" as="a" />`,
  }),
};

export const Text: Story = {
  args: { text: true },
  render: (args) => ({
    components: { UiButton },
    setup() {
      return { args };
    },
    template: html`<UiButton v-bind="args" />`,
  }),
};

export const WithIcon: Story = {
  args: { iconPos: undefined },
  argTypes: {
    iconPos: { control: "select", options: ["left", "right"] },
  },
  render: (args) => ({
    components: { UiButton, IcBaselineCheck },
    setup() {
      return { args };
    },
    template: html`
      <UiButton v-bind="args">
        <template #icon="slotProps">
          <IcBaselineCheck :class="slotProps.class" />
        </template>
      </UiButton>
    `,
  }),
};

export const IconOnly: Story = {
  args: { label: undefined },
  render: (args) => ({
    components: { UiButton, IcBaselineCheck },
    setup() {
      return { args };
    },
    template: html`
      <UiButton v-bind="args" aria-label="Button">
        <template #icon>
          <IcBaselineCheck />
        </template>
      </UiButton>
    `,
  }),
};

export const Loading: Story = {
  args: { loading: true },
  render: (args) => ({
    components: { UiButton },
    setup() {
      return { args };
    },
    template: html`<UiButton v-bind="args" />`,
  }),
};

export const Pill: Story = {
  args: { size: "small", rounded: true, severity: "info" },
  render: (args) => ({
    components: { UiButton, IcBaselineCheck },
    setup() {
      return { args };
    },
    template: html`
      <UiButton v-bind="args">
        <template #icon>
          <IcBaselineCheck />
        </template>
      </UiButton>
    `,
  }),
};
