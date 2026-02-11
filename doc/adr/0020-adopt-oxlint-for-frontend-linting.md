# 20. Adopt Oxlint for frontend linting

Date: 2026-01-22

## Status

Accepted (experimental support added in [PR #1600](https://github.com/digitalservicebund/ris-search/pull/1600))

## Context

The frontend uses ESLint for linting JavaScript, TypeScript, and Vue files. Several challenges have emerged with this setup:

- ESLint is slow to run, particularly on large codebases
- ESLint configuration has become complicated to maintain
- A new major version of ESLint is approaching, requiring additional migration effort
- The current ESLint configuration is neither lightweight nor comprehensive, lacking a clear philosophy
- The team prefers a simpler linting setup that prioritizes correctness checks while not being overly opinionated about code style

Most of the conventions and patterns the team relies on cannot be validated by a linter anyway, making an extensive rule set less valuable.

## Decision

We will adopt [Oxlint](https://oxc.rs/docs/guide/usage/linter.html) as a replacement for ESLint in the frontend.

Oxlint is part of the Vite ecosystem (which we already heavily use) and offers several advantages:

- Significantly faster than ESLint ([benchmarks](https://oxc.rs/docs/guide/benchmarks.html#linter))
- Better defaults: Simpler configuration with out-of-the-box support for many file types and popular rules
- Low configuration overhead: Requires minimal setup to get started
- Supports ESLint rules and plugins (experimental feature we plan to avoid, but good to know it exists)

We configure Oxlint such that it:

- Enables all plugins applicable to our stack
- Turns on the `correctness` and `suspicious` categories ([more on categories](https://oxc.rs/docs/guide/usage/linter/config.html#enable-groups-of-rules-with-categories))
- Enables approximately 25% of available rules, which is reasonably close to the previous ESLint setup with only minor code changes required

### Tradeoffs and Considerations

Oxlint itself is stable, though it's relatively new:

- Editor integration may not be as robust as ESLint initially
- Some individual features are in alpha/experimental stage:
  - [Third-party plugin support](https://oxc.rs/docs/guide/usage/linter/js-plugins.html) (which we plan to avoid anyway)
  - [Type-aware linting](https://oxc.rs/docs/guide/usage/linter/type-aware.html#type-aware-linting) (nice future upgrade, but not crucial)
- Oxlint currently supports only a subset of Vue-related rules
  - It cannot yet parse `.vue` files fully, only analyzing script and CSS blocks in isolation
  - This means fewer checks for Vue components
  - The team is experienced enough with Vue to tolerate less strict linting, and this will improve over time

## Consequences

- Faster linting in local development and CI
- Simpler configuration that's easier to understand and maintain
- Reduced dependency count
- Fewer Vue-specific linting rules until Oxlint improves `.vue` file support
- Team members need to install new IDE extensions for Oxlint
- Slightly less mature ecosystem compared to ESLint
