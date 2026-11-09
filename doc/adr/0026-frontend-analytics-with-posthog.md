# 26. Frontend analytics with PostHog

Date: 2026-10-29

## Status

Accepted

## Context

The frontend uses PostHog to learn how people use the portal. Our requirements for analytics have changed, and the new requirements are narrower and more specific than the old ones. We built the previous integration for the old requirements, so it did more than we now need.

It also did a lot by hand. It set up the PostHog library and managed the consent cookie in the browser. One central module held consent, feedback and all events. As a result, adding a new metric meant touching unrelated code.

We rebuilt the integration in [RISDEV-12927](https://digitalservicebund.atlassian.net/browse/RISDEV-12927) and added the first metrics back in [RISDEV-12930](https://digitalservicebund.atlassian.net/browse/RISDEV-12930) and [RISDEV-12931](https://digitalservicebund.atlassian.net/browse/RISDEV-12931). This ADR records the decisions behind that rebuild.

## Decision

### Collect only what we need

Tracking is opt-in. PostHog starts opted out and sends nothing until the user accepts tracking in the cookie banner.

We turn off every PostHog feature by default. We enable selected features and use custom events only when we have a metric that needs them.

Feedback from the feedback form is always anonymous. The backend sends it to PostHog without a user ID.

### PostHog handles consent

PostHog stores the user's decision in its own cookie. In the browser, we leave that cookie to PostHog. During server-side rendering, we read the cookie to decide whether to show the banner, so the page renders correctly before hydration.

Tracking always needs JavaScript, but giving or refusing consent does not. Without JavaScript, the consent buttons submit a form to the server, which writes the same cookie in the same format as PostHog. Users can close the banner and change their decision either way. PostHog follows that decision once JavaScript is available.

### Keep the integration small

We use PostHog's official Nuxt module instead of setting up the library ourselves. All PostHog configuration lives in one place.

Consent, feedback and custom events each have their own composable. Features that need tracking build on these composables instead of calling PostHog directly.

We declare every custom event and its properties in a central, typed list. The frontend can only send events from that list, so adding an event means adding it there first.

### Always use the PostHog EU cloud

The frontend and the backend both hardcode the PostHog EU cloud as the host. The only remaining setting is the project key. PostHog is off when the key is empty.

## Consequences

- Everything we collect is visible in two places in the code: the PostHog configuration and the list of custom events.

- Old dashboards built on removed events stop getting data.

- Visitor data stays in the EU. Moving to a different PostHog region or to a proxy needs a code change.

- We depend on PostHog's consent handling and cookie format. We read and write the cookie the same way PostHog does, so a change to its format could break the banner and consent without JavaScript.

- We cannot link feedback to a user's other activity.
