import ttl2jsonld from "@frogcat/ttl2jsonld";
import merge from "deepmerge";
import * as fs from "node:fs/promises";
import { defineLoader } from "vitepress";
import { createEnv, resolveClasses, type Class } from "./utils/classes";

export type Data = Record<string, Class>;
declare const data: Data;

export { data };

export default defineLoader({
  watch: ["../../docs/data/*.ttl"],
  async load(files) {
    const env = createEnv();

    await fs.mkdir(env.cacheDir, { recursive: true });

    for (const file of files) {
      const buffer = await fs.readFile(file, "utf-8");

      let jsonld = ttl2jsonld.parse(buffer);
      if (!jsonld.hasOwnProperty("@graph")) {
        const graph = { ...jsonld };
        delete graph["@context"];
        jsonld = { "@context": jsonld["@context"], "@graph": [graph] };
      }

      env.contexts.set("recht", merge(env.contexts.get("recht") || {}, jsonld));
    }

    const classes = await resolveClasses(env);
    return classes.reduce((acc, cls) => {
      acc[cls.id] = cls;
      return acc;
    }, {} as Data);
  },
});
