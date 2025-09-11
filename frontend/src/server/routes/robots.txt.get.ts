import { promises } from "node:fs";
import { join } from "node:path";
import { isPublicProfile } from "~/utils/config";

export default defineEventHandler(async (event) => {
  const userAgent = (getHeader(event, "User-Agent") ?? "").toUpperCase();
  let assetPath = isPublicProfile()
    ? "robots.public.txt"
    : "robots.staging.txt";
  if (userAgent === "DG_JUSTICE_CRAWLER") assetPath = "robots.dg.txt";

  setHeader(event, "Content-Type", "text/plain; charset=utf-8");

  const filePath = join(process.cwd(), "src/public", assetPath);
  return await promises.readFile(filePath, "utf-8");
});
