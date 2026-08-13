import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { ref } from "vue";
import { html } from "../../utils/tags";
import UiRadioButton from "./RadioButton.vue";

const meta: Meta<typeof UiRadioButton> = {
  component: UiRadioButton,

  tags: ["autodocs"],

  args: {
    value: "radio",
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiRadioButton },
    setup() {
      const checked = ref("");
      return { args, checked };
    },
    template: html`<UiRadioButton
      v-bind="args"
      v-model="checked"
      name="radio"
    />`,
  }),
};

export const WithLabel: Story = {
  render: (args) => ({
    components: { UiRadioButton },
    setup() {
      const checked = ref("radio");
      return { args, checked };
    },
    template: html`
      <div class="flex items-center">
        <UiRadioButton
          v-bind="args"
          v-model="checked"
          id="radio-with-label"
          name="radio"
        />
        <label for="radio-with-label">Radio with label</label>
      </div>
    `,
  }),
};

export const Group: Story = {
  render: () => ({
    components: { UiRadioButton },
    setup() {
      const checked = ref("radio-1");
      return { checked };
    },
    template: html`
      <div class="flex gap-32">
        <div class="flex items-center">
          <UiRadioButton
            v-model="checked"
            id="radio-1"
            name="radios"
            value="radio-1"
          />
          <label for="radio-1">One</label>
        </div>
        <div class="flex items-center">
          <UiRadioButton
            v-model="checked"
            id="radio-2"
            name="radios"
            value="radio-2"
          />
          <label for="radio-2">Two</label>
        </div>
        <div class="flex items-center">
          <UiRadioButton
            v-model="checked"
            id="radio-3"
            name="radios"
            value="radio-3"
          />
          <label for="radio-3">Three</label>
        </div>
      </div>
    `,
  }),
};

export const Disabled: Story = {
  render: (args) => ({
    components: { UiRadioButton },
    setup() {
      const checked = ref("");
      return { args, checked };
    },
    template: html`
      <div class="flex items-center">
        <UiRadioButton
          v-bind="args"
          v-model="checked"
          disabled
          id="radio-disabled"
          name="radio"
        />
        <label for="radio-disabled">Disabled radio</label>
      </div>
    `,
  }),
};

export const Invalid: Story = {
  render: (args) => ({
    components: { UiRadioButton },
    setup() {
      const checked = ref("");
      return { args, checked };
    },
    template: html`
      <div class="mb-4 flex items-center">
        <UiRadioButton
          v-bind="args"
          v-model="checked"
          aria-describedby="radio-error"
          aria-invalid="true"
          id="radio-invalid"
          name="radio"
        />
        <label for="radio-invalid">Radio with error</label>
      </div>
      <div id="radio-error" class="typo-label2-regular text-red-800">
        Error description
      </div>
    `,
  }),
};
