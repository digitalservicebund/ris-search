import type { Preview } from "@storybook/vue3-vite";
import { create as createTheme } from "storybook/theming/create";
import "../src/assets/main.css";
import { brandTheme } from "./theme";

const preview: Preview = {
  parameters: {
    docs: { theme: createTheme(brandTheme) },
    options: {
      storySort: { order: ["Startseite", "Styleguide", "Komponenten"] },
    },
  },
};

export default preview;
