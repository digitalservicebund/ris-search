import { fileURLToPath } from "url";
import { includeIgnoreFile } from "@eslint/compat";
import { createConfigForNuxt } from "@nuxt/eslint-config/flat";
import unusedImports from "eslint-plugin-unused-imports";

export default createConfigForNuxt(undefined, [
  {
    rules: {
      "@typescript-eslint/no-require-imports": "off",
      "vue/html-self-closing": "off",
      "vue/multi-word-component-names": "off",
      "vue/no-multiple-template-root": "off",
      "vue/no-v-html": "off",
      "vue/require-default-prop": "off",
      "unused-imports/no-unused-imports": "error",
      "unused-imports/no-unused-vars": [
        "warn",
        {
          vars: "all",
          varsIgnorePattern: "^_",
          args: "after-used",
          argsIgnorePattern: "^_",
        },
      ],
      "@typescript-eslint/no-unused-expressions": [
        "error",
        {
          allowShortCircuit: true,
          allowTernary: true,
          allowTaggedTemplates: true,
          enforceForJSX: true,
        },
      ],
      "import/order": [
        "error",
        {
          "newlines-between": "never",
          alphabetize: {
            order: "asc",
            caseInsensitive: true,
          },
        },
      ],
      "no-restricted-imports": [
        "error",
        {
          patterns: [
            {
              group: ["@/**"],
              message:
                'Please use "~/..." alias instead of "@/..." for project-specific imports.',
            },
          ],
        },
      ],
    },
    plugins: {
      "unused-imports": unusedImports,
    },
  },
  includeIgnoreFile(fileURLToPath(new URL(".gitignore", import.meta.url))),
  {
    files: ["**/**.spec.ts"],
    rules: {
      "@typescript-eslint/ban-ts-comment": "off",
    },
  },
]);
