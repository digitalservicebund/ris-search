import type { InjectionKey } from "vue";

/** Link targeting a landmark on the same page. */
export type SkipLink = {
  label: string;
  to: `#${string}`;
};

/** Collection of skip links on a page. */
export type SkipLinksRegistry = {
  links: Readonly<Ref<SkipLink[]>>;

  /**
   * Adds a list of skip links to the page. Returns a cleanup method to remove
   * them again.
   */
  register: (links: SkipLink[]) => () => void;
};

export const injectSkipLinksRegistry =
  Symbol() as InjectionKey<SkipLinksRegistry>;

/**
 * When used in a component, the component will provide a registry of skip links
 * to its children, which they can insert by calling `useSkipLinks`. Can also be
 * used by the provider to gain access to skip links registered by children to
 * render them.
 */
export function provideSkipLinks(): SkipLinksRegistry {
  const registry = reactive(new Set<SkipLink>());

  const register: SkipLinksRegistry["register"] = (links) => {
    links.forEach((i) => registry.add(i));
    return () => {
      links.forEach((i) => registry.delete(i));
    };
  };

  const instance = {
    register,
    links: computed(() => Array.from(registry.values())),
  };
  provide(injectSkipLinksRegistry, instance);
  return instance;
}

/**
 * Allows a component to register skip links.
 *
 * @throws when used outside of a component tree that provides a skip link registry.
 */
export function useSkipLinks(links?: MaybeRefOrGetter<SkipLink[]>) {
  const registry = inject(injectSkipLinksRegistry, null);
  if (!registry) throw new Error("skip links registry has not been provided");

  let cleanup: ReturnType<SkipLinksRegistry["register"]>;

  watchEffect(() => {
    const linksVal = toValue(links);
    if (!linksVal) return;

    cleanup?.();
    cleanup = registry.register(linksVal);
  });

  onUnmounted(() => {
    cleanup?.();
  });

  return registry.links;
}
