import fs from "node:fs";
import jsonDefinition from "../../docs/data/openapi.json";
import { compareFn } from "./openapi";
import type {NavItem} from "./types";

type Operation = (typeof jsonDefinition)["paths"]["/v1/case-law"]["get"] & {path: string};

export default {
  watch: ["../../docs/data/openapi.json"],
  load(watchedFiles: string[]) {
    if (watchedFiles.length !== 1) {
      throw new Error("Unexpected number of files: " + watchedFiles);
    }
    const file = watchedFiles[0];
    return extractOpenApiStructure(file)
  },
};


export function extractOpenApiStructure(filename: string): NavItem[] {
  const content = JSON.parse(
      fs.readFileSync(filename, "utf-8")
  ) as typeof jsonDefinition;

  const tagNames = content.tags
      .map((t: { name: string }) => t.name)
      .sort(compareFn);

  const tags: Record<string, { name: string; operations: Operation[] }> =
      Object.fromEntries(
          tagNames.map((name) => [name, { name, operations: [] }])
      );

  for (const [pathName, path] of Object.entries(content.paths)) {
    for (const operation of Object.values(path) as Operation[]) {
      for (const tagName of operation.tags) {
        if (!tags[tagName]) {
          console.error(
              "Unexpected tagName in operation not found in root",
              operation,
              tagName
          );
          continue;
        }
        const operationWithPath: Operation = {...operation, path: pathName}
        tags[tagName].operations.push(operationWithPath);
      }
    }
  }

  return Object.values(tags).map(({ name, operations }) => {
    let tagNameHash = name.replace(" ", "-").toLowerCase();
    return ({
      text: name,
      link: `/endpoints/#${tagNameHash}`,
      items: operations.toSorted((a, b) => compareFn(a.path, b.path)).map((op) => ({
        text: op.summary,
        link: `/endpoints/#${op.operationId}`,
        key: `/endpoints/#${tagNameHash}_${op.operationId}`
      })),
    } as NavItem);
  });
}
