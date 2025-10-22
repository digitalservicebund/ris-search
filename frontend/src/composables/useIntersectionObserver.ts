import type { RouteQueryAndHash } from "vue-router";

export const useIntersectionObserver = () => {
  const route = useRoute() as RouteQueryAndHash;
  const hash = route.hash;
  const selectedEntry = ref<string | undefined>();

  onMounted(() => {
    selectedEntry.value = hash?.substring(1);
    lastScrollY = window.scrollY;

    window?.addEventListener("hashchange", () => {
      selectedEntry.value = location.hash?.substring(1);
    });
  });

  let lastScrollY = 0;
  const visibleIds = ref<string[]>([]);
  const allIds = ref<string[]>([]);

  const initialLoad = ref(true);

  function handleIntersection(entries: IntersectionObserverEntry[]) {
    const currentScrollY = window.scrollY;
    const scrollingDown = currentScrollY > lastScrollY;
    lastScrollY = currentScrollY;
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        if (scrollingDown) {
          visibleIds.value = [...visibleIds.value, entry.target.id];
        } else {
          visibleIds.value = [entry.target.id, ...visibleIds.value];
        }
      } else {
        // stopped intersecting
        visibleIds.value =
          visibleIds.value?.filter((id) => id !== entry.target.id) ?? [];
      }
    });
    const sorted = visibleIds.value.sort();

    // prevent overriding the entry selected based on the hash, only set it if none is selected
    if (initialLoad.value && selectedEntry.value?.length) {
      initialLoad.value = false;
      return;
    }

    if (sorted.length > 0) {
      selectedEntry.value = sorted[0] ?? undefined;
    } else {
      // handle the case when no headers are currently visible
      if (!scrollingDown) {
        const currentIndex = allIds.value.findIndex(
          (id) => id === selectedEntry.value,
        );
        if (currentIndex > 0) {
          selectedEntry.value = allIds.value[currentIndex - 1];
        }
      }
    }
  }

  // don't use IntersectionObserver in SSR mode
  const observer = ref<IntersectionObserver | null>(
    import.meta.client
      ? new IntersectionObserver(handleIntersection, {
          threshold: 0.05,
        })
      : null,
  );

  function observeSections(element: HTMLElement) {
    const sections = element.querySelectorAll("section[id]");
    sections.forEach((section: Element) => {
      allIds.value.push(section.id);
      observer.value?.observe(section);
    });
  }

  const vObserveElements = {
    mounted: (element: HTMLElement) => {
      if (import.meta.server) return;
      selectedEntry.value = hash?.substring(1);
      observeSections(element);
    },
    updated: (element: HTMLElement) => {
      if (import.meta.server) return;
      observer.value?.disconnect();
      allIds.value = [];
      observeSections(element);
    },
    beforeUnmount: () => {
      if (import.meta.server) return;
      observer.value?.disconnect();
    },
  };

  return { vObserveElements, selectedEntry, handleIntersection };
};
