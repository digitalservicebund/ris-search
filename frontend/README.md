# Frontend

[Vue](https://vuejs.org/) + [Nuxt](https://nuxt.com/) application using [TypeScript](https://www.typescriptlang.org/), [Tailwind](https://tailwindcss.com/), and [RIS UI](https://github.com/digitalservicebund/ris-ui), a component library and theme for [PrimeVue](https://primevue.org/).

The frontend also includes our E2E tests.

Before diving into the code, please get familiar with our 🔒 [code conventions](https://digitalservicebund.atlassian.net/wiki/x/BIC1N).

## Running

Make sure your system meets the [prerequisites](../README.md#prerequisites). Then, install the dependencies:

```sh
pnpm install
```

Copy the necessary environment variables:

```sh
cp .env.example .env
```

You can now start the application:

```sh
pnpm dev
```

In order to use the search and to view files, the backend and backend dependencies will need to be running. Refer to the [main README](../README.md) for further instructions.

The frontend will be available at [localhost:3000](http://localhost:3000).

### Feature Flags

The frontend has a feature flag that enables or disables private features:

- **public**: features that will be available in the public prototype ("Testphase")
- **private**: internal features

The default is that private features are disabled. Set `NUXT_PUBLIC_PRIVATE_FEATURES_ENABLED=true` to enable them.

## Testing

We cover all code outside of `pages/` with unit tests (pages are too complex for unit testing and are covered in E2E tests instead). We use [Vitest](https://vitest.dev/) and [Vue Testing Library](https://testing-library.com/docs/vue-testing-library/intro/).

### Unit Tests

To run unit tests once:

```bash
pnpm test
```

To run unit tests in watch mode (re-runs tests on code changes and gives you additional options like filtering):

```bash
pnpm test:watch
```

### E2E Tests

The end-to-end tests use [Playwright](https://playwright.dev/) and are located in the `e2e/` directory.

Make sure the backend and other required services are [running](../README.md#quickstart) before executing the tests. Then, install the browsers:

```sh
pnpm exec playwright install --with-deps chromium firefox webkit
```

Once setup is complete, run the end-to-end tests:

```bash
pnpm exec playwright test
```

Or with the UI:

```bash
pnpm exec playwright test --ui
```

## Code Quality

We use TypeScript, ESLint, and Prettier to support code quality and consistent formatting. To run ESLint and Prettier:

```sh
node --run style:check  # Check if code follows conventions and is formatted
node --run style:fix    # Check + try to fix violations automatically
```

To run type checking:

```sh
node --run typecheck
```

## Icons

All icons in the [Google Material](https://icon-sets.iconify.design/ic) sets can be used. To make the icon available in your code:

- Find and select the icon in the catalog. We usually use the baseline or outline styles, depending on the icon.
- In the icon detail panel, select "Component" as the format on the left, and "Unplugin Icons" as the framework on the top
- Copy the resulting code. It should look something like this:

```js
import IcBaselineAccessAlarms from "~icons/ic/baseline-access-alarms";
```
