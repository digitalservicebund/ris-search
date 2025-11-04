import type { TabPassThroughOptions } from "primevue/tab";
import type { TabListPassThroughOptions } from "primevue/tablist";
import type { TabPanelPassThroughOptions } from "primevue/tabpanel";

export const tabBaseClasses =
  "ris-body2-bold h-64 py-4 pl-20 pr-24 border-b-4 border-b-transparent outline-blue-800 outline-0 -outline-offset-4 focus-visible:outline-4 inline-flex items-center gap-8 no-underline";

export const tabActiveClasses =
  "border-gray-600 text-black shadow-active-tab bg-white z-10";

export const tabInactiveClasses =
  "text-blue-800 hover:border-b-blue-800 cursor-pointer";

export const tabStyles: TabPassThroughOptions = {
  root: ({ context }) => {
    return {
      class: {
        [tabBaseClasses]: true,
        [tabActiveClasses]: context.active,
        [tabInactiveClasses]: !context.active,
      },
    };
  },
};

export const tabPanelClass = "py-24 min-h-96 bg-white print:py-0";

export const tabListContentClass =
  "relative before:absolute before:left-[50%] before:-translate-x-1/2 before:w-full before:h-px before:bottom-0 before:bg-gray-600 print:hidden";

export const tabListClass = "flex container";

export const tabPanelStyles: TabPanelPassThroughOptions = {
  root: { class: tabPanelClass },
};

export const tabListStyles: TabListPassThroughOptions = {
  content: {
    class: tabListContentClass,
  },
  tabList: {
    class: tabListClass,
  },
};
