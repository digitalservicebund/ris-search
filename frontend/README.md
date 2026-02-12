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

> [!NOTE]
>
> Instead of running a local backend, you can also connect the frontend dev server to our staging backend. This is slower, but will give you access to realistic data. Learn more 🔒 [here](<https://digitalservicebund.atlassian.net/wiki/spaces/VER/pages/1215889433/Portal+Infrastructure+and+Deployment+Guide#Local-Development-(Port-Forwarding)>).

### Feature Flags

The frontend has a feature flag that enables or disables private features:

- **public**: features that will be available in the public prototype ("Testphase")
- **private**: internal features

The default is that private features are disabled. Set `NUXT_PUBLIC_PRIVATE_FEATURES_ENABLED=true` to enable them.

### Configuration

The frontend can be configured through environment variables at different points in its lifecycle. Variables marked with ⭐️ are required for the application to function.

- **Runtime:** when the application is run, either through Docker or standalone. All of this configuration is read in `nuxt.config.ts` or one of its related modules. With the exception of the `PORT`, this configuration is made available to the application through the `useRuntimeConfig` composable.
  - ⭐️ `NUXT_PUBLIC_RIS_BACKEND_URL`: URL under which the Portal API can be reached
  - `NUXT_BASIC_AUTH`: When basic auth is enabled on the infrastructure level, this needs to be set to the basic auth credentials so the Nuxt server can talk to the Portal API.
  - `NUXT_PUBLIC_PRIVATE_FEATURES_ENABLED`: If set to `true`, private features are enabled. Make sure to also set this when running E2E tests, so the test suite is in sync with the running application. By default, private features are disabled.
  - `NUXT_PUBLIC_SENTRY_DSN`: Sentry Data Source. Setting this will enable Sentry.
  - `NUXT_PUBLIC_ANALYTICS_POSTHOG_KEY` and `NUXT_PUBLIC_ANALYTICS_POSTHOG_HOST`: Key and host for PostHog. Both need to be used together. Setting this will enable PostHog.
  - `PORT`: Port to run the application on

- **Buildtime:** when the application is built, either in the Docker container or manually. This configuration will be baked into the application, shared in all environments, and the variables don't need to exist at runtime.
  - `SENTRY_AUTH_TOKEN`: Used for uploading source maps to Sentry during build. Recommended when Sentry is enabled.
  - `SENTRY_RELEASE`: Name of the current release to show up in Sentry.
  - `NODE_ENV`: Automatically set by Node based on the task to either `development` or `production`. This will enable/disable some features in the configuration, see `nuxt.config.ts`.

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

We use TypeScript, Oxlint, and Prettier to support code quality and consistent formatting. To run Oxlint and Prettier:

```sh
pnpm style:check  # Check if code follows conventions and is formatted
pnpm style:fix    # Check + try to fix violations automatically
```

To run type checking:

```sh
pnpm typecheck
```

## Icons

All icons in the [Google Material](https://icon-sets.iconify.design/ic) sets can be used. To make the icon available in your code:

- Find and select the icon in the catalog. We usually use the baseline or outline styles, depending on the icon.
- In the icon detail panel, select "Component" as the format on the left, and "Unplugin Icons" as the framework on the top
- Copy the resulting code. It should look something like this:

```js
import IcBaselineAccessAlarms from "~icons/ic/baseline-access-alarms";
```
