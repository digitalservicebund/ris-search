import type { NavItem } from "./MenuItem.vue";

export const data: NavItem[] = [
  { text: "Get Started", link: "/docs/get-started" },
  { text: "Standards", link: "/docs/standards/" },
  {
    text: "Guides",
    link: "/docs/guides/",
    items: [
      { text: "Formats", link: "/docs/guides/formats" },
      { text: "Pagination", link: "/docs/guides/pagination" },
      { text: "Filters", link: "/docs/guides/filters" },
      { text: "Rate Limiting", link: "/docs/guides/rate-limiting" },
      { text: "Error codes", link: "/docs/guides/error-codes" },
    ],
  },
  {
    text: "Changelog",
    link: "/docs/changelog/",
  },
  {
    text: "Endpoints",
    link: "/docs/endpoints/",
    items: [],
  },
  {
    text: "Feedback",
    link: "/docs/feedback",
  },
];
