export default defineNitroPlugin((nitro) => {
  nitro.hooks.hook("error", async (error, { event }) => {
    console.error(`server error at ${event?.path}:`, error);
  });
});
