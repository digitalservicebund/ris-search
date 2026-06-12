/**
 * Composable for searching within the norm text content and highlighting
 * matches using the CSS Custom Highlight API.
 *
 * @param contentRef - A ref pointing to the DOM element containing the norm text.
 */
export function useNormTextSearch(contentRef: Ref<HTMLElement | null>) {
  const HIGHLIGHT_NAME = "norm-search"

  const query = ref("")
  const matchCount = ref(0)

  function highlight() {
    if (!CSS.highlights) return

    CSS.highlights.delete(HIGHLIGHT_NAME)
    matchCount.value = 0

    const container = contentRef.value
    const str = query.value.trim().toLowerCase()
    if (!container || !str) return

    // Collect all text nodes inside the container
    const walker = document.createTreeWalker(
      container,
      NodeFilter.SHOW_TEXT,
    )
    const textNodes: Text[] = []
    let node: Node | null
    while ((node = walker.nextNode())) {
      textNodes.push(node as Text)
    }

    // Find all matches and create Range objects
    const ranges: Range[] = []
    for (const textNode of textNodes) {
      const text = textNode.textContent?.toLowerCase() ?? ""
      let startPos = 0
      while (startPos < text.length) {
        const index = text.indexOf(str, startPos)
        if (index === -1) break
        const range = new Range()
        range.setStart(textNode, index)
        range.setEnd(textNode, index + str.length)
        ranges.push(range)
        startPos = index + str.length
      }
    }

    matchCount.value = ranges.length
    if (ranges.length > 0) {
      CSS.highlights.set(HIGHLIGHT_NAME, new Highlight(...ranges))
    }
  }

  function clear() {
    query.value = ""
    if (CSS.highlights) {
      CSS.highlights.delete(HIGHLIGHT_NAME)
    }
    matchCount.value = 0
  }

  // Re-run the highlight whenever the query changes
  watch(query, highlight)

  // Clean up when the component using this composable is unmounted
  onUnmounted(clear)

  return { query, matchCount, clear }
}
