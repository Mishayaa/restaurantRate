<template>
  <div class="register">
    <div class="register-form">
      <h1>Страница регистрации</h1>
      <form @submit.prevent="register">
        <div class="form-group">
          <label>Логин пользователя:</label>
          <input type="text" v-model="username" required>
        </div>
        <div class="form-group">
          <label>Email:</label>
          <input type="text" v-model="email" required>
        </div>
        <div class="form-group">
          <label>Имя пользователя:</label>
          <input type="text" v-model="name" required>
        </div>
        <div class="form-group">
          <label>Пароль:</label>
          <input type="password" v-model="password" required>
        </div>
        <button type="submit">Зарегистрироваться</button>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

export default {
  name: 'RegisterPage',
  setup() {
    const email = ref('');
    const name = ref('');
    const username = ref('');
    const password = ref('');
    const router = useRouter();

    const register = async () => {
      await axios.post('/api/users', {
        email: email.value,
        name: name.value,
        username: username.value,
        password: password.value
      })
          .then(response => {
            console.log(response.data);
          })
          .catch(error => {
            console.error(error);
          });

      router.push('/');
    };

    return {
      email,
      name,
      username,
      password,
      register
    };
  }
};

</script>
<style scoped>
body, html {
  margin: 0;
  padding: 0;

}
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #121212;
}

.register-form {
  width: 300px;
  color: white;
  text-align: center;
}

.register-form h1 {
  margin-bottom: 20px;
}

.register-form .form-group {
  margin-bottom: 15px;
}

.register-form .form-group label {
  display: block;
  margin-bottom: 5px;
}

.register-form .form-group input {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 30px;
}

.register-form button {
  display: block;
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 30px;
  background-color: #1db954;
  color: white;
  cursor: pointer;
}
</style>