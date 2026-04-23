import type { ContentData, SiteConfig } from "vitepress";
import { createContentLoader } from "vitepress";
import type { NavItem, ThemeConfig } from "./types";

const config: SiteConfig = (globalThis as any).VITEPRESS_CONFIG;

declare const data: NavItem[];
export { data };

const buildNavItem = (i: ContentData): NavItem => {
  return { text: i.frontmatter.title, link: i.url };
};

const primaryNavigation: NavItem[] = [
  { text: "Get Started", link: "/get-started" },
  { text: "Standards", link: "/standards/" },
  {
    text: "Guides",
    link: "/guides/",
    items: [
      { text: "Formats", link: "/guides/formats" },
      { text: "Pagination", link: "/guides/pagination" },
      { text: "Filters", link: "/guides/filters" },
      { text: "Rate Limiting", link: "/guides/rate-limiting" },
      { text: "Error codes", link: "/guides/error-codes" },
    ],
  },
  {
    text: "Changelog",
    link: "/changelog/",
  },
  {
    text: "Feedback",
    link: "/feedback",
  },
];

/**
 * Enrich the primary navigation, as defined in the theme, with
 * the resources defined by the content loader.
 */
export default createContentLoader("resourceArchive/*/*.md", {
  transform(resources): NavItem[] {
    const themeConfig = config.userConfig.themeConfig as ThemeConfig;
    if (!themeConfig) throw new Error("no themeConfig found");
    return primaryNavigation.map((item) => {
      if (item.text === "Resource Archive") {
        return {
          ...item,
          items: resources.map(buildNavItem),
        };
      }
      return item;
    });
  },
});
