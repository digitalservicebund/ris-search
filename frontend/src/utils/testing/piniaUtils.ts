import { useSimpleSearchParamsStore } from "~/stores/searchParams";

export async function setStoreValues(
  values: Partial<ReturnType<typeof useSimpleSearchParamsStore>>,
) {
  const store = useSimpleSearchParamsStore(); // uses testing pinia
  Object.assign(store, values);
  await nextTick();

  return store;
}
