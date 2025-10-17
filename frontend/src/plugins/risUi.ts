import "@digitalservicebund/ris-ui/fonts.css";
import { RisUiLocale, RisUiTheme } from "@digitalservicebund/ris-ui/primevue";
import PrimeVue from "primevue/config";
import { usePassThrough } from "primevue/passthrough";
import ToastService from "primevue/toastservice";
import Tooltip from "primevue/tooltip";

const theme = usePassThrough(
  RisUiTheme,
  {
    panelmenu: {
      headercontent: { class: "group" },
      headerlink: { class: "no-underline group-hover:underline" },
      itemlink: { class: "no-underline group-hover:underline" },
    },
  },
  { mergeProps: false, mergeSections: true },
);

export default defineNuxtPlugin(({ vueApp: app }) => {
  app.use(PrimeVue, {
    pt: theme,
    unstyled: true,
    locale: RisUiLocale.deDE,
  });

  app.use(ToastService);

  app.directive("tooltip", Tooltip);
});
