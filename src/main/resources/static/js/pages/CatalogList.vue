<template>
    <div>
        <v-list-item>
            <v-list-item-content>
                <v-list-item-title class="title">
                    Каталог товаров
                </v-list-item-title>
            </v-list-item-content>
        </v-list-item>

        <v-divider></v-divider>

        <v-list
                nav
                dense
        >
            <v-list-item
                    v-for="item in items"
                    :key="item.title"
                    link
                    @click="openPage(item)"
            >
                <v-list-item-content>
                    <v-list-item-title>{{ item.name }}</v-list-item-title>
                </v-list-item-content>
            </v-list-item>
        </v-list>
    </div>
</template>

<script>
    import directoriesApi from 'api/directories.js'

    export default {
        name: "CatalogList",
        data: () => ({
            items: [],
        }),
        methods:{
            openPage(item){
                console.log("openCatalogPage")
                //переход на страницу и обработана ошибка
                this.$router.push({ path: `/filter/${item.id}` }, () => {})
            },
        },
        created: function () {
            //запрос на сервер
            directoriesApi.getCore().then(result =>
                result.json().then(data => {
                    if(data.children){
                        data.children.forEach(
                            item => {
                                this.items.push(item)
                            }
                        )
                    }
                })
            )
        },
    }
</script>

<style scoped>

</style>