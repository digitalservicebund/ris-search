# 9. Consolidate Frontend Code into a Single Folder Structure

Date: 2024-12-05

## Status

Accepted

## Context

Our frontend codebase is currently divided into three separate folders:

- **`_shared`**: Contains most of the common components used across the application.
- **`public`**: Holds code specific to the public-facing portal.
- **`internal`**: Contains code exclusive to the internal portal.

While this structure was initially intended to organize code based on its usage context, it has led to several challenges:

- **Minimal Differences**: The public and internal portals have very few differences. Maintaining separate folders for them results in a lot of duplicated code and effort.

- **Complex Development Process**: Managing three different folders complicates the development workflow. We had to implement workspaces to handle dependencies across these folders, leading to the maintenance of three separate `package.json` files.

- **Dependency Management**: Adding or updating dependencies requires changes in multiple places, increasing the risk of inconsistencies and errors.

- **Testing and Pipeline Difficulties**: Running tests and configuring CI/CD pipelines for both streams is cumbersome due to the fragmented codebase.

- **Code Duplication**: Few components and utilities are duplicated between the `public` and `internal` folders, causing redundancy and increasing maintenance overhead.

These issues have highlighted the need for a more streamlined and efficient codebase structure.

## Decision

We will **refactor the frontend codebase** to merge the `_shared`, `public`, and `internal` folders into a **single unified folder**. This consolidated structure will simplify the codebase and eliminate unnecessary duplication.

To handle the minimal differences between the public and internal portals, we will introduce a `PROFILE` configuration variable in the `.env` file:

```env
NUXT_PUBLIC_PROFILE=public  # or 'internal'
```

This variable will be accessed in `nuxt.config.js` and throughout the application to determine which components or features to render based on the specified profile.

**Implementation Steps:**

1. **Merge Folders**: Combine the contents of `_shared`, `public`, and `internal` into one unified directory.

2. **Update Configuration**: Modify `nuxt.config.js` to include the `PROFILE` variable from the `.env` file:

   ```javascript
   // nuxt.config.js
   export default defineNuxtConfig({
     runtimeConfig: {
       public: {
         profile: 'public', // can be overwritten in .env
       },
     },
     // ... other configurations ...
   });
   ```

3. **Conditional Rendering**: Use the `isPublicProfile` method within the application to conditionally render components or apply specific logic:

   ```vue
   <template>
     <div>
       <PublicComponent v-if="isPublicProfile" />
       <InternalComponent v-else />
     </div>
   </template>

   <script setup lang="ts">
   import { useRuntimeConfig } from '#imports';

   const config = useRuntimeConfig();
   const isPublicProfile = config.public.profile === 'public';
   </script>
   ```

4. **Update Imports and References**: Adjust all import statements and references to match the new folder structure.

5. **Consolidate Dependencies**: Merge the `package.json` files into one, resolving any version conflicts and ensuring all necessary dependencies are included.

6. **Adjust Tests and Pipelines**: Update test configurations and CI/CD pipelines to align with the new unified codebase.

## Consequences

- **Simplified Development Workflow**: Developers will work within a single codebase, reducing complexity and potential confusion.

- **Easier Dependency Management**: Maintaining one `package.json` simplifies adding, updating, and managing dependencies.

- **Reduced Code Duplication**: Consolidating the codebase eliminates redundant code, making maintenance more efficient.

- **Streamlined Testing and CI/CD**: Unified tests and pipelines reduce overhead and simplify continuous integration and deployment processes.

- **Conditional Logic Maintenance**: Introducing conditional rendering based on the `PROFILE` variable adds complexity to the codebase. Developers must ensure that components and features correctly respect the profile settings.

- **Risk of Profile-Specific Bugs**: Changes in shared components may unintentionally affect both profiles. Rigorous testing is required to prevent regressions.

- **Configuration Management**: Relying on environment variables necessitates proper management of `.env` files across different environments (development, staging, production).

## Example

**Before Refactoring:**

- **Folder Structure:**

  ```
  frontend/
  ├── _shared/
  ├── public/
  └── internal/
  ```

- **Separate `package.json` Files:**

    - `frontend/_shared/package.json`
    - `frontend/public/package.json`
    - `frontend/internal/package.json`

**After Refactoring:**

- **Unified Folder Structure:**

  ```
  frontend/
  ├── src/
    ├──── components/
    ├──── pages/
    ├──── assets/
    ├──── utils/
  └── package.json
  ```

- **Using `isInternalProfile` in Code:**

  ```vue
  <!-- ExampleComponent.vue -->
  <template>
    <div>
      <h1>Welcome to Our Portal</h1>
      <InternalDashboard v-if="isInternalProfile" />
      <PublicDashboard v-else />
    </div>
  </template>

  <script setup lang="ts">
  import { useRuntimeConfig } from '#imports';

  const config = useRuntimeConfig();
  const isInternalProfile = config.public.profile === 'internal';
  </script>
  ```

- **Accessing `PROFILE` in `.env`:**

  ```env
  # .env
  NUXT_PUBLIC_PROFILE=internal
  ```

**Testing and Deployment:**

- **Tests**: Adjust test scripts to run against the unified codebase, ensuring both profiles are adequately covered.

- **CI/CD Pipelines**: Update pipeline configurations to build and deploy based on the `PROFILE` variable, allowing for profile-specific deployments without separate codebases.

---

By consolidating our frontend code into a single folder structure and leveraging the `PROFILE` configuration, we aim to streamline development, reduce duplication, and simplify our overall workflow. This change requires careful implementation and testing but is expected to significantly improve maintainability and efficiency in the long run.
