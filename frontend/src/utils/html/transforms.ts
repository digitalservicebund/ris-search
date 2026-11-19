// Convention: transforms mutate the document.

import type { HtmlTransform } from "./processHtml";

/**
 * Rewrites the `href` of every link in the document.
 *
 * @param rewrite Receives the current `href` (null if the link has none) and
 *   returns the new one. Returning null removes the `href`.
 */
export function rewriteLinks<TContext = undefined>(
  rewrite: (href: string | null, context: TContext) => string | null,
): HtmlTransform<TContext> {
  return (document, context) => {
    for (const link of document.body.querySelectorAll("a")) {
      const newHref = rewrite(link.getAttribute("href"), context);
      if (newHref === null) link.removeAttribute("href");
      else link.setAttribute("href", newHref);
    }
  };
}

const BRACKETED_BLOCK_SEPARATOR = /\+\+\+\)\s+\(\+\+\+/g;

/**
 * Lists all text nodes inside the node, in document order.
 *
 * This doesn't use a TreeWalker, because that needs the `NodeFilter` global,
 * which doesn't exist on the server, where documents come from jsdom.
 */
function getTextNodes(node: Node): Text[] {
  if (node.nodeType === node.TEXT_NODE) return [node as Text];
  return [...node.childNodes].flatMap(getTextNodes);
}

/**
 * Puts consecutive "(+++ ... +++)" blocks on separate lines by inserting a
 * `<br>` between them. Only affects text inside elements matching the
 * selector.
 */
export function insertLineBreaksBetweenBracketedBlocks<TContext = undefined>(
  selector: string,
): HtmlTransform<TContext> {
  return (document) => {
    for (const root of document.querySelectorAll(selector)) {
      for (const node of getTextNodes(root)) {
        const text = node.data;
        BRACKETED_BLOCK_SEPARATOR.lastIndex = 0;
        if (!BRACKETED_BLOCK_SEPARATOR.test(text)) continue;

        const fragment = document.createDocumentFragment();
        let rest = text;
        let match: RegExpExecArray | null;
        BRACKETED_BLOCK_SEPARATOR.lastIndex = 0;
        while ((match = BRACKETED_BLOCK_SEPARATOR.exec(rest))) {
          // Keep "+++)" with the first block and "(+++" with the next one.
          const splitAt = match.index + "+++)".length;
          fragment.append(rest.slice(0, splitAt), document.createElement("br"));
          rest = rest.slice(match.index + match[0].length - "(+++".length);
          BRACKETED_BLOCK_SEPARATOR.lastIndex = 0;
        }
        fragment.append(rest);
        node.replaceWith(fragment);
      }
    }
  };
}
