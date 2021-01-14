import Vue from 'vue'
//апи для работы с продуктами
const products = Vue.resource('/product{/id}')

export default {
    add: product => products.save({}, product),
    update: product => products.update({}, product),
    remove: id => products.remove({id: id}),
    getAll: () => products.get(),
    //getProductsByTagId: id => products.get({id: id}),
}