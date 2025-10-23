export const TEST_URL = "https://testphase.rechtsinformationen.bund.de/example";

export const createExpectedHeadCall = (
  title: string,
  description: string,
  url: string,
) =>
  expect.objectContaining({
    title,
    link: [{ rel: "canonical", href: url }],
    meta: [
      { name: "description", content: description },
      { property: "og:type", content: "article" },
      { property: "og:title", content: title },
      { property: "og:description", content: description },
      { property: "og:url", content: url },
      { name: "twitter:title", content: title },
      { name: "twitter:description", content: description },
    ],
  });
