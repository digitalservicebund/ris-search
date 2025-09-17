const formatter = new Intl.NumberFormat("de-DE", {
  style: "decimal",
  useGrouping: true,
});

/**
 * Format a number based on the German locale.
 *
 * @param num Number to be formatted
 * @returns Formatted number
 */
export function formatNumberWithSeparators(num: number): string {
  return formatter.format(num);
}
