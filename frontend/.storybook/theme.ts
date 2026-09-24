import type { ThemeVarsPartial } from "storybook/theming";

// Mirrors the brand tokens from src/assets/tokens.css.
export const brandTheme: ThemeVarsPartial = {
  base: "light",
  colorPrimary: "#004b76", // blue-800
  colorSecondary: "#004b76", // blue-800
  appBg: "#f6f7f8", // gray-100
  appBorderColor: "#dcdee1", // gray-400
  textColor: "#0b0c0c", // gray-1000
  textMutedColor: "#4e596a", // gray-900
};
