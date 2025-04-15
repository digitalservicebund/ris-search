import { createConfigForNuxt } from "@nuxt/eslint-config/flat";
import unusedImports from "eslint-plugin-unused-imports";
import { includeIgnoreFile } from "@eslint/compat";
import { fileURLToPath } from "url";

export default createConfigForNuxt(undefined, [
  {
    rules: {
      "@typescript-eslint/no-require-imports": "off",
      "vue/html-self-closing": "off",
      "vue/multi-word-component-names": "off",
      "vue/no-multiple-template-root": "off",
      "vue/no-v-html": "off",
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
