type AccessibilityRelatedMetrics = {
  zoomLevel: number;
  defaultTextSize: string;
  themePreference: string;
};

/**
 * Retrieves default information about the user browsing preferences,
 * which helps with measuring the accessibility of the portal.
 * This function creates a temporary DOM element to measure the default text size,
 * checks the current zoom level, and determines the user's theme preference (light or dark mode).
 * The collected information is used by PostHog to send along the $pageview event.
 *
 * @returns {Object} An object containing the following properties:
 * - `zoomLevel` {number}: The current zoom level of the browser window.
 * - `defaultTextSize` {string}: The default text size of the webpage in px.
 * - `themePreference` {string}: The user's theme preference, either "dark" or "light".
 */
export function getAccessibilityRelatedMetrics(): AccessibilityRelatedMetrics {
  const tempElement = document.createElement("div");
  tempElement.style = "display: block; visibility: hidden; font-size: medium;";
  tempElement.innerText = "M";
  document.body.appendChild(tempElement);
  const defaultTextSize = window.getComputedStyle(tempElement).fontSize;
  document.body.removeChild(tempElement);
  const zoomLevel = Math.round(window.devicePixelRatio * 100);
  const isDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
  const themePreference = isDark ? "dark" : "light";
  return {
    zoomLevel: zoomLevel,
    defaultTextSize: defaultTextSize,
    themePreference: themePreference,
  };
}
