<template>
  <div class="login">
    <h1>Страница входа</h1>
    <form @submit.prevent="login">
      <label>
        Имя пользователя:
        <input type="text" v-model="username" required>
      </label>
      <label>
        Пароль:
        <input type="password" v-model="password" required>
      </label>
      <button type="submit">Войти</button>
    </form>
  </div>
</template>


<script>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

export default {
  name: 'LoginPage',
  setup() {
    const username = ref('');
    const password = ref('');
    const router = useRouter();

    const login = async () => {
      const response = await axios.post('/api/auth/login', {
        username: username.value,
        password: password.value
      });

      console.log(response.data);
      await router.push('/');
    };

    return {
      username,
      password,
      login
    };
  }
};
</script>