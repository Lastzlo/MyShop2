<template>
    <div>
        <v-list-item>
            <v-list-item-content>
                <v-list-item-title class="title">
                    Авторизация
                </v-list-item-title>
            </v-list-item-content>
        </v-list-item>
        <v-divider></v-divider>

        <v-list
                nav
                dense
        >
            <v-list-item @click="openPage">
                <v-list-item-title>Войдти через Google</v-list-item-title>
            </v-list-item>

            <v-list-item @click="openPage">
                <v-list-item-title>Войдти через Facebook</v-list-item-title>
            </v-list-item>

            <v-list-item @click="openPage">
                <v-list-item-title>Регистрация</v-list-item-title>
            </v-list-item>

            <v-list-item @click="showProductList">
                <v-list-item-title>Страница управления товарами</v-list-item-title>
            </v-list-item>
        </v-list>
    </div>
</template>

<script>
    import {mapMutations} from "vuex";

    export default {
        name: "AutorizationList",
        methods:{
            openPage(item){
                console.log("openPage")
            },
            showProductList(){
                this.$router.push('/setting', () => {})
            },
        }
    }
</script>

<style scoped>

</style>