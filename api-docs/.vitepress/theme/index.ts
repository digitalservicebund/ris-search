import type { Theme } from "vitepress";
import ButtonGroup from "./components/ButtonGroup.vue";
import ButtonLink from "./components/ButtonLink.vue";
import CardGroup from "./components/CardGroup.vue";
import CardLink from "./components/CardLink.vue";
import ClassContainer from "./components/ClassContainer.vue";
import CodeBadge from "./components/CodeBadge.vue";
import FeedbackInlineSurvey from "./components/FeedbackInlineSurvey.vue";
import FeedbackSurvey from "./components/FeedbackSurvey.vue";
import FileExplorer from "./components/FileExplorer.vue";
import FileExplorerItem from "./components/FileExplorerItem.vue";
import Icon from "./components/Icon.vue";
import Note from "./components/Note.vue";
import RequestParams from "./components/RequestParams.vue";
import DocumentedCodeExampleSection from "./components/DocumentedCodeExampleSection.vue";
import DocsPart from "./components/DocsPart.vue";
import CodePart from "./components/CodePart.vue";
import Layout from "./layout/Layout.vue";
import PrimeVue from "primevue/config";
import {theme, useOpenapi} from "vitepress-openapi/client";
import spec from "./openapi";

import "./styles/anchors.css";
import "vitepress/dist/client/theme-default/styles/components/vp-code-group.css";
import "vitepress/dist/client/theme-default/styles/components/vp-code.css";
import "vitepress/dist/client/theme-default/styles/components/vp-doc.css";
import "vitepress/dist/client/theme-default/styles/icons.css";
import "./styles/code-group.css";
import "./styles/code.css";
import "vitepress-openapi/dist/style.css";
import "./styles/index.css";
import "./styles/openapi.css";
import "./styles/prose.css";

import {Button} from "primevue";
import {RisUiLocale} from "@digitalservicebund/ris-ui/primevue";
import InfoPanel from "./components/InfoPanel.vue";
import FeedbackForm from "./components/FeedbackForm.vue";

export default {
  Layout,
  async enhanceApp({ app }) {
    app.component("ButtonGroup", ButtonGroup);
    app.component("ButtonLink", ButtonLink);
    app.component("CardGroup", CardGroup);
    app.component("CardLink", CardLink);
    app.component("ClassContainer", ClassContainer);
    app.component("CodeBadge", CodeBadge);
    app.component("FeedbackInlineSurvey", FeedbackInlineSurvey);
    app.component("FeedbackSurvey", FeedbackSurvey);
    app.component("FeedbackForm", FeedbackForm)
    app.component("FileExplorer", FileExplorer);
    app.component("FileExplorerItem", FileExplorerItem);
    app.component("Button", Button)
    app.component("Icon", Icon);
    app.component("Note", Note);
    app.component("InfoPanel", InfoPanel)
    app.component("RequestParams", RequestParams);
    app.component("DocumentedCodeExampleSection", DocumentedCodeExampleSection);
    app.component("DocsPart", DocsPart);
    app.component("CodePart", CodePart);

    app.use(PrimeVue, {
      unstyled: true,
      locale: RisUiLocale.deDE
    });

    // Set the OpenAPI specification.
    const openapi = useOpenapi({
      spec,
      config: {
        operation: {
          hiddenSlots: ["playground"],
          cols: 1,
        },
        codeSamples: {
          defaultHeaders: {},
        },
      }
    });

    // Use the theme.
    theme.enhanceApp({ app, openapi });
  },
} satisfies Theme;
