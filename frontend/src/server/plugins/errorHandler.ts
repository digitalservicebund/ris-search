export default defineNitroPlugin((nitro) => {
  nitro.hooks.hook("error", (error, { event }) => {
    console.error(`server error at ${event?.path}:`, error);
  });
});
