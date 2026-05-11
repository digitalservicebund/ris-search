import type { InjectionKey } from "vue";

/** Link targeting a landmark on the same page. */
export type SkipLink = {
  label: string;
  to: `#${string}`;
};

/** Collection of skip links on a page. */
type SkipLinksRegistry = {
  links: Readonly<Ref<SkipLink[]>>;
  set: (links: SkipLink[]) => void;
  clear: () => void;
};

const injectSkipLinksRegistry = Symbol() as InjectionKey<SkipLinksRegistry>;

/**
 * When used in a component, the component will provide a registry of skip links
 * to its children, which they can insert by calling `useSkipLinks`. Can also be
 * used by the provider to gain access to skip links registered by children to
 * render them.
 */
export function provideSkipLinks() {
  const links = ref<SkipLink[]>([]);

  function set(newLinks: SkipLink[]) {
    links.value = newLinks;
  }

  function clear() {
    links.value = [];
  }

  const registry: SkipLinksRegistry = { links, set, clear };
  provide(injectSkipLinksRegistry, registry);

  return registry;
}

/**
 * Allows a component to register skip links.
 *
 * Assumes that skip links are only registered once per page. Any registration
 * of skip links will replace the list of existing skip links.
 *
 * @throws When used outside of a component tree that provides a skip link
 *  registry.
 */
export function useSkipLinks(links: MaybeRefOrGetter<SkipLink[]>) {
  const registry = inject(injectSkipLinksRegistry, null);
  if (!registry) throw new Error("Skip links registry is not available");

  watch(
    () => toValue(links),
    (newLinks) => {
      registry.set(newLinks);
    },
    { deep: true, immediate: true },
  );

  return registry.links;
}
