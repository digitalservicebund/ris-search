<script setup lang="ts">
import { useRoute } from "nuxt/app";
import IconPermIdentity from "virtual:icons/ic/baseline-perm-identity";
import { useUserSession } from "#imports";
import { redirectToLogin } from "~/utils/redirectToLogin";

const { clear, loggedIn, user } = useUserSession();

const route = useRoute();
const login = () => {
  redirectToLogin(route.fullPath);
};
const logout = () => {
  const fullPath = route.fullPath as string;
  fetch("/auth/keycloak", { method: "DELETE" })
    .then(clear)
    .then(() => {
      redirectToLogin(fullPath);
    });
};
</script>

<template>
  <nav
    class="flex items-center justify-between border-b border-gray-400 bg-white px-16 py-24"
  >
    <div class="flex items-center gap-44">
      <NuxtLink to="/" class="ris-link1-bold">
        <div class="flex items-center">
          <NuxtImg alt="" src="/neuRIS-logo.svg" />
          <span class="text-16 px-[1rem] leading-20 text-black">
            <span aria-hidden="true" class="font-bold">
              Rechtsinformationen
            </span>
            <br />
            <span aria-hidden="true">des Bundes</span>
          </span>
        </div>
      </NuxtLink>

      <NuxtLink to="/search" class="p-8 hover:bg-yellow-500 hover:underline">
        Suche
      </NuxtLink>
    </div>

    <div v-if="loggedIn" class="grid grid-cols-[auto_1fr] gap-10">
      <IconPermIdentity />
      <div>
        <div class="ris-label1-bold text-16">{{ user?.name }}</div>
        <div class="text-16 grid grid-cols-[auto_1fr] gap-10">
          <button @click="logout">Logout</button>
        </div>
      </div>
    </div>
    <div v-else class="grid grid-cols-[auto_1fr] gap-10">
      <button @click="login">Login</button>
    </div>
  </nav>
</template>
