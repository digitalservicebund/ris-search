const plugin = require("tailwindcss/plugin");
import { RisUiPreset, RisUiPlugin } from "@digitalservicebund/ris-ui/tailwind";

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["node_modules/vitepress-openapi/dist/**/*.{js,css}", "content/**/*.md", ".vitepress/theme/**/*.{vue,ts}"],
  presets: [RisUiPreset],
  theme: {
    container: {
      center: true,
      screens: {
        DEFAULT: "80rem",
      },
      padding: {
        DEFAULT: "2rem",
      },
    },
    extend: {
      colors: {
        neutral: {
          primary: "rgb(var(--color-primary))",
          secondary: "rgb(var(--color-secondary))",
          tertiary: "var(--vp-c-border-hard)",
        },
        brand: {
          primary: "var(--vp-c-accent)",
          secondary: "var(--color-brand-secondary)",
        },
        background: {
          primary: "var(--vp-c-bg)",
        },
        alert: {
          secondary: "var(--color-alert-secondary)",
        },
      },
      fontFamily: {
        sans: [
          "BundesSansWeb",
          "Calibri",
          "Verdana",
          "Arial",
          "Helvetica",
          "sans-serif",
        ],
        serif: [
          "BundesSerifWeb",
          "Cambria",
          "Georgia",
          '"Times New Roman"',
          "serif",
        ],
        mono: [
          "ui-monospace",
          "Cascadia Code",
          "Source Code Pro",
          "Menlo",
          "Consolas",
          "DejaVu Sans Mono",
          "monospace",
        ],
      },
      maxWidth: {
        prose: "85ch",
      },
    },
  },
  plugins: [
    RisUiPlugin,
    require("@digitalservice4germany/angie"),
    // require("@tailwindcss/typography"),
    plugin(function ({ addComponents }) {
      addComponents({
        ".breakout": {
          minWidth:
            "calc(min(100dvw - var(--sidebar-width), var(--container-max-width)) - var(--container-padding) * 2)",
        },
      });
    }),
  ],
};
