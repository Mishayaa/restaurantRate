<template>
  <div class="container">
    <input type="color" v-model="backgroundColor" @change="changeBackgroundColor">
    <div class="restaurant-card" v-for="restaurant in restaurants" :key="restaurant.id" @click="getRestaurantInfo(restaurant.id)">
      <div class="image-container">
        <img class="restaurant-image" :src="restaurant.posterUrl" :alt="'Photo of ' + restaurant.name">
      </div>
      <h3 class="restaurant-name">{{restaurant.name}}</h3>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      restaurants: [
        {
          id: 1,
          name: 'Restaurant 1',
          photoUrl: '/images/restaurant1.jpg'
        },
        {
          id: 2,
          name: 'Restaurant 2',
          photoUrl: '/images/restaurant2.jpg'
        },
        // ...
      ],
      backgroundColor: '#02020e' // Значение по умолчанию для цвета фона
    }
  },
  methods: {
    getRestaurantInfo(id) {
      fetch(`/api/restaurant/${id}`)
          .then(response => response.json())
          .then(data => {
            console.log(data);
            // Здесь вы можете обработать полученные данные
          })
    },
    changeBackgroundColor() {
      document.body.style.backgroundColor = this.backgroundColor;
    }
  },
  mounted() {
    fetch('/api/restaurant/popular')
        .then(response => response.json())
        .then(data => {
          this.restaurants = data;
        })
    this.changeBackgroundColor();
  },
  watch: {
    backgroundColor() {
      this.changeBackgroundColor();
    }
  }
}
</script>

<style scoped>
body {
  color: #ac7260;
  margin: 0;
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.container {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  padding: 20px;
  margin-top: 170px; /* Добавлен верхний отступ */

}

.restaurant-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border: 1px solid #c71545;
  border-radius: 10px;
  width: 200px;
  cursor: pointer;
  transition: transform 0.3s;
  background-color: #ac7260;
}

.restaurant-card:hover {
  transform: scale(1.1);
}

.image-container {
  width: 100%;
  height: 0;
  padding-bottom: 75%;
  position: relative;
}

.restaurant-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.restaurant-name {
  margin-top: 10px;
  font-size: 18px;
  font-weight: bold;
}
</style>
