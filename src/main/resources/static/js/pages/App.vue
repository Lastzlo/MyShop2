<template>
        <v-app>
                <AppBarComponent/>

                <v-main>
                        <router-view></router-view>
                </v-main>
        </v-app>

</template>

<script>
        import AppBarComponent from "pages/AppBarComponent.vue"

        export default {
                name: "App.vue",
                components: {AppBarComponent},
        }
</script>

<style scoped>

</style>