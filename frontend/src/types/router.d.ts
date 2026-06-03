declare module "vue-router" {
  interface RouteMeta {
    skipLinks?: SkipLink[];
  }
}

/** Link targeting a landmark on the same page. */
export type SkipLink = {
  label: string;
  to: `#${string}`;
};
