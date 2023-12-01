import {createRouter, createWebHistory} from 'vue-router'

import Home from './components/HomePage.vue'
import Login from './components/LoginPage.vue'
import Register from './components/RegisterPage.vue'
import UserPage from './components/UserPage.vue'


const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    },
    {
        path: '/userPage',
        name: 'UserPage',
        component: UserPage
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router