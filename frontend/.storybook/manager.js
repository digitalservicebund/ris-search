import { addons } from "storybook/manager-api";
import { create as createTheme } from "storybook/theming/create";
import logo from "./logo.svg";
import { brandTheme } from "./theme";

const theme = createTheme({
  ...brandTheme,
  // Used as the alt text of brandImage.
  brandTitle: "RIS UI",
  brandImage: logo,
});

addons.setConfig({ theme });
