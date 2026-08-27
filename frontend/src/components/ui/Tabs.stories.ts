import type { Meta, StoryObj } from "@storybook/vue3-vite";
import { ref } from "vue";
import { html } from "../../utils/tags";
import UiTab from "./Tab.vue";
import UiTabs from "./Tabs.vue";

const meta: Meta<typeof UiTabs> = {
  component: UiTabs,

  tags: ["autodocs"],
};

export default meta;

// The tab strip sits on the page background, the panel below it is white
const wrapper = "bg-gray-100";

const panel = "min-h-96 bg-white p-24";

export const Default: StoryObj<typeof meta> = {
  render: () => ({
    components: { UiTabs, UiTab },
    setup() {
      const tabs = ["Lorem ipsum", "Duis aute", "Nam liber"];
      const selected = ref(tabs[0]);
      return { tabs, selected, wrapper, panel };
    },
    template: html`<div :class="wrapper">
      <UiTabs aria-label="Beispiel">
        <UiTab
          v-for="tab in tabs"
          :key="tab"
          :active="tab === selected"
          @click="selected = tab"
        >
          {{ tab }}
        </UiTab>
      </UiTabs>
      <div :class="panel">Inhalt von „{{ selected }}“</div>
    </div>`,
  }),
};

/**
 * Tabs that navigate instead of toggling in place render as links. The active
 * tab is derived from the current location by the consumer.
 */
export const AsLinks: StoryObj<typeof meta> = {
  render: () => ({
    components: { UiTabs, UiTab },
    setup() {
      return { wrapper, panel };
    },
    template: html`<div :class="wrapper">
      <UiTabs aria-label="Beispiel">
        <UiTab as="a" href="#text" active>Text</UiTab>
        <UiTab as="a" href="#details">Details</UiTab>
      </UiTabs>
      <div :class="panel">Inhalt von „Text“</div>
    </div>`,
  }),
};

/**
 * When the tabs are wider than the available space, the tab list scrolls
 * horizontally. Gutters belong on the tab list — a class passed to `UiTabs`
 * lands there — so that they scroll along with the tabs instead of clipping
 * them.
 */
export const Overflowing: StoryObj<typeof meta> = {
  render: () => ({
    components: { UiTabs, UiTab },
    setup() {
      const tabs = [
        "Text",
        "Details",
        "Fassungen",
        "Begründung",
        "Anlagen",
        "Verwaltungsvorschriften",
        "Literaturnachweise",
      ];
      const selected = ref(tabs[0]);
      return { tabs, selected, wrapper, panel };
    },
    template: html`<div :class="[wrapper, 'max-w-sm']">
      <UiTabs aria-label="Beispiel" class="px-16">
        <UiTab
          v-for="tab in tabs"
          :key="tab"
          :active="tab === selected"
          @click="selected = tab"
        >
          {{ tab }}
        </UiTab>
      </UiTabs>
      <div :class="panel">Inhalt von „{{ selected }}“</div>
    </div>`,
  }),
};
