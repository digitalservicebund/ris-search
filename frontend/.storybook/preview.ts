import type { Preview } from "@storybook/vue3-vite";
import "../src/assets/main.css";

const preview: Preview = {
  parameters: {
    options: {
      storySort: { order: ["Startseite", "Styleguide", "Komponenten"] },
    },
  },
};

export default preview;
