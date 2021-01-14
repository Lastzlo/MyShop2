import Vue from 'vue'
import Vuetify from 'vuetify'
//для использования
import '@babel/polyfill'
//для более удобного обращения
import 'api/resource'
//vuex хранилище
import store from "store/store"
//import VueResource from 'vue-resource'
import App from 'pages/App.vue'
//import MainPage from "pages/MainPage.vue";

//для переходов между страницами
import router from "router/router.js"

Vue.use(Vuetify)
//Vue.use(VueResource);

new Vue({
    el: '#app',
    store,
    router,
    vuetify: new Vuetify(),
    render: a => a(App)
})



