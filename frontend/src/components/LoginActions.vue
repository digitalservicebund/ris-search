<script lang="ts" setup>
import { useRoute } from "nuxt/app";
import IconPermIdentity from "virtual:icons/ic/baseline-perm-identity";
import { redirectToLogin } from "~/utils/redirectToLogin";

const { loggedIn, user, clear } = useUserSession();
const route = useRoute();
const logout = () => {
  fetch("/auth/keycloak", { method: "DELETE" })
    .then(clear)
    .then(() => {
      redirectToLogin(route.fullPath);
    });
};
</script>
<template>
  <div v-if="loggedIn" class="flex gap-4">
    <IconPermIdentity class="text-black" />
    <span class="text-black">{{ user?.name }}</span>
    <button class="ris-link1-regular text-black" @click="logout">
      Abmelden
    </button>
  </div>
  <NuxtLink v-else to="/auth" external>Login</NuxtLink>
</template>
