import spec from "../../docs/data/openapi.json" with {type: "json"};

/**
 * Generates a sort key for a given path name.
 * For example, /v1/documents should appear before /api/legacy, even though it would come second alphabetically.
 * Therefore, the prefixes are stripped and the values are replaced with single letters.
 */
const generateSortKey = (value: string) => {
  return value
    .replace(/^\/api/gi, "")
    .replace(/^\/v1/gi, "")
    .replace(/(all )?documents?/gi, "a")
    .replace(/legislation/gi, "b")
    .replace(/case[- ]law/gi, "c")
    .replace(/export/gi, "d")
    .replace(/open/gi, "e")
    .replace(/legacy/gi, "f");
};

export const compareFn = (a: string, b: string) => {
  const aValue = generateSortKey(a);
  const bValue = generateSortKey(b);
  // localeCompare compares strings alphabetically
  return aValue.localeCompare(bValue);
};

const orderedPaths = Object.keys(spec.paths)
  .sort(compareFn)
  .reduce((obj, key) => {
    // @ts-expect-error
    obj[key] = spec.paths[key];
    return obj;
  }, {} as (typeof spec)["paths"]);

const modifiedSpec = {
  ...spec,
  info: { ...spec.info, contact: null },
  paths: orderedPaths,
};

export default modifiedSpec;
