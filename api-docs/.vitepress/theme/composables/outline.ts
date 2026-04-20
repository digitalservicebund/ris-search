import type { ThemeConfig } from "../types";

export interface OutlineItem {
  level: number;
  title: string;
  link: string;
  suffix?: string;
  children: OutlineItem[];
}

export function getHeadings(outline: ThemeConfig["outline"]): OutlineItem[] {
  const headers = [
    ...document.querySelectorAll(".content :where(h1,h2,h3,h4,h5,h6)"),
  ]
    .filter((el) => el.id && el.hasChildNodes())
    .map((el) => {
      return {
        title: serializeHeader(el),
        link: "#" + el.id,
        level: Number(el.tagName[1]),
        suffix:
          (el as HTMLElement).dataset && (el as HTMLElement).dataset.method
            ? (el as HTMLElement).dataset.method
            : undefined,
        children: [],
      };
    });

  return resolveHeaders(headers, outline);
}

function serializeHeader(h: Element): string {
  let result = "";

  for (const node of h.childNodes) {
    if (node.nodeType === 1) {
      if (
        (node as Element).classList.contains("header-anchor") ||
        (node as Element).classList.contains("ignore-header")
      ) {
        continue;
      }
      result += node.textContent;
    } else if (node.nodeType === 3) {
      result += node.textContent;
    }
  }

  return result.trim();
}

function resolveHeaders(
  headers: OutlineItem[],
  range: ThemeConfig["outline"]
): OutlineItem[] {
  headers = headers.filter((h) => h.level >= range[0] && h.level <= range[1]);

  const result: OutlineItem[] = [];
  outer: for (let i = 0; i < headers.length; i++) {
    const cur = headers[i];
    if (i === 0) {
      result.push(cur);
    } else {
      for (let j = i - 1; j >= 0; j--) {
        const prev = headers[j];
        if (prev.level < cur.level) {
          (prev.children || (prev.children = [])).push(cur);
          continue outer;
        }
      }
      result.push(cur);
    }
  }

  return result;
}
