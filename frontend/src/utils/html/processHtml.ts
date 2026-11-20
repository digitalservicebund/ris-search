import sanitizeHtml from "sanitize-html";
import { isDocumentEmpty, parseDocument } from "~/utils/htmlParser";
import type { Section } from "./extract";

/** Pass as `allowlist` to explicitly skip sanitizing. */
export const DANGEROUSLY_SKIP_SANITIZATION = Symbol(
  "DANGEROUSLY_SKIP_SANITIZATION",
);

/** A document without any content, e.g. as a fallback for an empty response. */
export const EMPTY_DOCUMENT_HTML = "<html><body></body></html>";

/** HTML fragments extracted from a document */
export type HtmlParts = Record<string, string | undefined>;

/**
 * Plain text and data extracted from a document, e.g. with `getText` or
 * `getSections`. This is not sanitized, because it's not meant to be HTML. Use
 * `HtmlParts` for anything that contains markup.
 */
export type TextParts = Record<
  string,
  string | string[] | number | Section[] | undefined
>;

/**
 * Changes the document in place, e.g. by rewriting links. The context is
 * whatever the caller passes as `context` to `processHtml`, for information the
 * transform needs but can't get from the document, like the current route.
 */
export type HtmlTransform<TContext = undefined> = (
  document: Document,
  context: TContext,
) => void;

export type HtmlExtraction<
  THtml extends HtmlParts = HtmlParts,
  TText extends TextParts = TextParts,
> = {
  html: THtml;
  text: TText;
};

export type ProcessHtmlOptions<
  THtml extends HtmlParts = HtmlParts,
  TText extends TextParts = TextParts,
  TContext = undefined,
> = {
  /** Applied in order, before anything is extracted. */
  transforms?: HtmlTransform<TContext>[];

  /**
   * Read parts and text from the document, and remove elements that should no
   * longer be in the document after processing (see `takeElements`).
   */
  extract?: (document: Document) => HtmlExtraction<THtml, TText>;

  /**
   * List of elements that are allowed to remain in the document after
   * sanitization. You can also opt-out of sanitization here, however this
   * carries security risks and should be avoided.
   */
  allowlist: sanitizeHtml.IOptions | typeof DANGEROUSLY_SKIP_SANITIZATION;

  /**
   * Whether the document has no content worth showing. Checked after the
   * transforms and before extracting, so it sees the whole document. Defaults
   * to `isDocumentEmpty`.
   */
  isEmpty?: (document: Document) => boolean;

  /** Passed to every transform. */
  context?: TContext;
};

export type ProcessedHtml<
  THtml extends HtmlParts = HtmlParts,
  TText extends TextParts = TextParts,
> = {
  /** What remains of the document after extracting, as HTML. */
  body: string;
  parts: THtml;
  text: TText;
  isEmpty: boolean;
};

/**
 * Turns HTML from the backend into plain data that can be safely displayed:
 *
 * 1. Parses the HTML into a DOM tree
 * 2. Applies the transforms
 * 3. Checks if the document is empty
 * 4. Extracts parts and text, removing the parts from the DOM
 * 5. Serializes what remains as the body
 * 6. Sanitizes the body and the parts (unless told to skip)
 */
export function processHtml<
  THtml extends HtmlParts = Record<never, never>,
  TText extends TextParts = Record<never, never>,
  TContext = undefined,
>(
  html: string,
  options: ProcessHtmlOptions<THtml, TText, TContext>,
): ProcessedHtml<THtml, TText> {
  const document = parseDocument(html);

  for (const transform of options.transforms ?? []) {
    transform(document, options.context as TContext);
  }

  const isEmpty = (options.isEmpty ?? isDocumentEmpty)(document);

  const extraction = options.extract?.(document) ?? {
    html: {} as THtml,
    text: {} as TText,
  };

  const body = document.body.innerHTML;

  const { allowlist } = options;
  if (allowlist === DANGEROUSLY_SKIP_SANITIZATION) {
    return { body, parts: extraction.html, text: extraction.text, isEmpty };
  }

  const parts = Object.fromEntries(
    Object.entries(extraction.html).map(([name, part]) => [
      name,
      part === undefined ? undefined : sanitizeHtml(part, allowlist),
    ]),
  ) as THtml;

  return {
    body: sanitizeHtml(body, allowlist),
    parts,
    text: extraction.text,
    isEmpty,
  };
}
