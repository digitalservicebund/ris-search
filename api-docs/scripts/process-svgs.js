import * as fs from "node:fs/promises";
import * as path from "node:path";
import * as url from "node:url";
import SVGSpriter from "svg-sprite";
import { globby } from "globby";

const __dirname = path.dirname(url.fileURLToPath(import.meta.url));

const ROOT_DIR = path.resolve(__dirname, "..");
const PUBLIC_DIR = path.join(ROOT_DIR, "docs", "public");
const ICON_DIR = path.join(PUBLIC_DIR, "icons");

const spriter = new SVGSpriter({
  dest: PUBLIC_DIR,
  mode: { defs: true },
});

// Download the SVG from: https://icon-sets.iconify.design/material-symbols
const files = await globby("*.svg", { cwd: ICON_DIR });
for (const file of files) {
  console.log("Processing %s", file);

  const svg = await fs.readFile(path.join(ICON_DIR, file), "utf-8");
  const output = svg
    .replaceAll(`fill="black"`, `fill="currentColor"`)
    .replaceAll(`stroke="black"`, `stroke="currentColor"`);

  spriter.add(file, null, output);
}

const { result } = await spriter.compileAsync();

await fs.writeFile(
  path.join(PUBLIC_DIR, `icons.svg`),
  result.defs.sprite.contents
);
