import { useSimpleSearchParamsStore } from "@/stores/searchParams";
import type { LocationQueryRaw } from "vue-router";

export const useRedirectToSearch = () => {
  const router = useRouter();
  const store = useSimpleSearchParamsStore();

  return async (query?: LocationQueryRaw) => {
    await router.push({ path: "/search", query });
    store.$reset(); // trigger the store (re-)reading the query from the URL
  };
};
