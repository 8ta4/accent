import globals from "globals";
import pluginJs from "@eslint/js";

export default [
  {
    languageOptions: {
      globals: {
        ...globals.browser,
        // Added to avoid 'module' is not defined error in .prettierrc.js
        ...globals.node,
      },
    },
    rules: {
      "no-unused-vars": ["error", { argsIgnorePattern: "^_" }],
    },
  },
  pluginJs.configs.recommended,
];
