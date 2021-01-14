import Vue from 'vue'
//апи для работы с директориями
const directories = Vue.resource('/directory{/id}')

export default {
    add: directory => directories.save({}, directory),
    //update: directory => directories.update({id: directory.id}, directory),
    update: (id, directory) => directories.update({id: id}, directory),
    remove: id => directories.remove({id: id}),
    getCore: () => Vue.resource('/directory/getCore').get(),
    //getProductByDirectoryId: id => Vue.resource('/directory/getProductByDirectoryId/'+id).get(),
    getProductByDirectoryId: id => Vue.http.get('/directory/getProductByDirectoryId/'+id),
    getOne: id => Vue.http.get('/directory/'+id),
}