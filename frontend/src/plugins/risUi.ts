import "@digitalservicebund/ris-ui/fonts.css";
import { RisUiLocale, RisUiTheme } from "@digitalservicebund/ris-ui/primevue";
import PrimeVue from "primevue/config";
import { usePassThrough } from "primevue/passthrough";
import ToastService from "primevue/toastservice";
import Tooltip from "primevue/tooltip";

const theme = usePassThrough(
  RisUiTheme,
  {
    // Currently we don't use any customizations
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
