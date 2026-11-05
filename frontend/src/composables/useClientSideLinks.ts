import type { MaybeRefOrGetter } from "vue";
import type { Router } from "#vue-router";

/**
 * Lets the router handle clicks on links inside HTML that Vue doesn't render
 * itself, such as server-rendered documents shown with `v-html`. Without this,
 * every internal link in that HTML causes a full page load.
 *
 * @param target The element containing the links. Usually a template ref.
 */
export function useClientSideLinks(
  target: MaybeRefOrGetter<HTMLElement | null | undefined>,
) {
  const router = useRouter();

  // Browsers also emit this when pressing enter on a focused link.
  async function onClick(event: MouseEvent) {
    const path = getClientSidePath(event, router);
    if (!path) return;

    event.preventDefault();
    await navigateTo(path);
  }

  watch(
    () => toValue(target),
    (element, _, onCleanup) => {
      if (!element) return;

      element.addEventListener("click", onClick);
      onCleanup(() => element.removeEventListener("click", onClick));
    },
    { immediate: true, flush: "post" },
  );
}

/**
 * Returns the path the router should navigate to for a click on a link, or
 * undefined if the browser should handle the click.
 */
function getClientSidePath(
  event: MouseEvent,
  router: Router,
): string | undefined {
  // Should the event be handled?
  if (event.defaultPrevented || event.button !== 0) return undefined;
  if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
    return undefined;
  }

  // Is the link a candidate for handling? We'll only consider links that look like regular document
  // navigation.
  if (!(event.target instanceof Element)) return undefined;
  const anchor = event.target.closest("a");
  if (!(anchor instanceof HTMLAnchorElement)) return undefined;
  if (!anchor.hasAttribute("href")) return undefined;
  if (anchor.hasAttribute("download")) return undefined;

  const linkTarget = anchor.getAttribute("target");
  if (linkTarget && linkTarget !== "_self") return undefined;

  // Is the URL a different document and could it potentially be resolved by the router?
  const url = new URL(anchor.href, globalThis.location.href);
  if (url.origin !== globalThis.location.origin) return undefined;

  const isSamePage =
    url.pathname === globalThis.location.pathname &&
    url.search === globalThis.location.search;
  if (isSamePage && url.hash) return undefined;

  // Attempt resolving
  const resolved = router.resolve(url.pathname + url.search + url.hash);
  if (!resolved.matched.length) return undefined;

  return resolved.fullPath;
}
