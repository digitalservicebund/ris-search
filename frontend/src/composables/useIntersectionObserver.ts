export const useIntersectionObserver = () => {
  const route = useRoute();
  const hash = computed(() => route.hash);
  const selectedEntry = ref<string | undefined>(hash.value.substring(1));
  const lastScrollY = ref(0);
  const visibleIds = ref<string[]>([]);
  const allIds = ref<string[]>([]);

  const initialLoad = ref(true);

  function handleIntersection(entries: IntersectionObserverEntry[]) {
    const currentScrollY = window.scrollY;
    const scrollingDown = currentScrollY > lastScrollY.value;
    lastScrollY.value = currentScrollY;

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
    const sorted = visibleIds.value.toSorted();

    // prevent overriding the entry selected based on the hash, only set it if none is selected
    if (initialLoad.value && selectedEntry.value?.length) {
      initialLoad.value = false;
      return;
    }

    if (sorted.length > 0) {
      selectedEntry.value = sorted[0] ?? undefined;
    }
    // handle the case when no headers are currently visible
    else if (!scrollingDown && selectedEntry.value) {
      const currentIndex = allIds.value.indexOf(selectedEntry.value);
      if (currentIndex > 0) {
        selectedEntry.value = allIds.value[currentIndex - 1];
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
      selectedEntry.value = hash.value?.substring(1);
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
