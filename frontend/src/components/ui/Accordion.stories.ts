import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { ref } from "vue";
import { html } from "../../utils/tags";
import UiAccordion from "./Accordion.vue";

const meta: Meta<typeof UiAccordion> = {
  component: UiAccordion,

  tags: ["autodocs"],

  args: {
    headerCollapsed: "Fußnote anzeigen",
    headerExpanded: "Fußnote ausblenden",
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: (args) => ({
    components: { UiAccordion },
    setup() {
      const open = ref(false);
      return { args, open };
    },
    template: html`
      <UiAccordion v-bind="args" v-model="open">
        <p class="bg-gray-100 p-24">Content of the accordion</p>
      </UiAccordion>
    `,
  }),
};

export const Expanded: Story = {
  render: (args) => ({
    components: { UiAccordion },
    setup() {
      const open = ref(true);
      return { args, open };
    },
    template: html`
      <UiAccordion v-bind="args" v-model="open">
        <p class="bg-gray-100 p-24">Content of the accordion</p>
      </UiAccordion>
    `,
  }),
};

/**
 * The header can describe the action rather than the content, in which case
 * both labels differ. The expanded label also names the content region for
 * assistive technology.
 */
export const WithRichContent: Story = {
  args: {
    headerCollapsed: "Amtliches Inhaltsverzeichnis einblenden",
    headerExpanded: "Amtliches Inhaltsverzeichnis ausblenden",
  },
  render: (args) => ({
    components: { UiAccordion },
    setup() {
      const open = ref(true);
      return { args, open };
    },
    template: html`
      <UiAccordion v-bind="args" v-model="open">
        <div class="bg-gray-100 p-24">
          <ul class="typo-body-regular list-disc pl-24">
            <li>Erster Abschnitt</li>
            <li>Zweiter Abschnitt</li>
            <li>Dritter Abschnitt</li>
          </ul>
        </div>
      </UiAccordion>
    `,
  }),
};
