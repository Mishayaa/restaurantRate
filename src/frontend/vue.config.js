// vue.config.js
module.exports = {
  // https://cli.vuejs.org/config/#devserver-proxy
  devServer: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'https://restaurant-rate.azurewebsites.net',
        ws: true,
        changeOrigin: true
      }
    }
  }
}