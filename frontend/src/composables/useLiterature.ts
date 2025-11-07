import { useFetch } from "#app";
import { useBackendURL } from "~/composables/useBackendURL";
import type { Literature } from "~/types";

export async function useLiterature() {
  const route = useRoute();
  const documentNumber = route.params.documentNumber as string;
  const documentMetadataUrl = `${useBackendURL()}/v1/literature/${documentNumber}`;

  const {
    status: dataStatus,
    data,
    error: metadataError,
  } = await useFetch<Literature>(documentMetadataUrl);

  const {
    status: htmlStatus,
    data: html,
    error: htmlError,
  } = await useFetch<string>(`${documentMetadataUrl}.html`, {
    headers: { Accept: "text/html" },
  });

  const loading = computed(() => {
    return dataStatus.value == "pending" || htmlStatus.value == "pending";
  });

  const error = computed(() => {
    return metadataError.value ?? htmlError.value;
  });

  return {
    loading,
    error,
    data,
    html,
  };
}
