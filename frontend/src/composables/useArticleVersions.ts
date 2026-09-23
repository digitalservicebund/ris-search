import type { AsyncData, NuxtError } from "#app";
import type { Article, JSONLDList } from "~/types/api.ts";

export function useArticleVersions(
  expressionEli: string,
  articleEId: string,
): AsyncData<Article[], NuxtError<Article[]> | NuxtError<null> | undefined> {
  const { $risBackend } = useNuxtApp();
  return useAsyncData(
    `article versions for ${expressionEli}/${articleEId}`,
    async () => {
      const fetchUrl = `/v1/article/work-example/eli/${expressionEli}/${articleEId}`;
      const articleVersionsCollection =
        await $risBackend<JSONLDList<Article>>(fetchUrl);
      return articleVersionsCollection.member;
    },
    { immediate: !!articleEId, server: true, lazy: false },
  );
}
