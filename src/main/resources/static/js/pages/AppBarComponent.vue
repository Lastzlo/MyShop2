<template>
    <v-container>
        <v-app-bar app dense>

            <v-app-bar-nav-icon @click.stop="openCatalogDrawer"></v-app-bar-nav-icon>

            <v-btn icon>
                <v-icon>mdi-magnify</v-icon>
            </v-btn>

            <v-spacer></v-spacer>

            <v-btn @click="showMainPage" text>
                My Shop
            </v-btn>

            <v-spacer></v-spacer>

            <v-btn icon @click="openAutorizationDrawer">
                <v-icon>mdi-dots-vertical</v-icon>
            </v-btn>



        </v-app-bar>

        <CatalogDrawerComponent/>
        <AutorizationDrawerComponent
        />
    </v-container>
</template>
<script>
    import {mapState, mapMutations} from 'vuex'
    import CatalogDrawerComponent from "pages/CatalogDrawerComponent.vue";
    import AutorizationDrawerComponent from "pages/AutorizationDrawerComponent.vue";


    export default {
        name: 'AppBarComponent',
        components: {CatalogDrawerComponent, AutorizationDrawerComponent},
        computed: mapState(['onAutorizationDrawer']),
        methods: {
            ...mapMutations(['changeAutorizationDrawerMutation', 'changeCatalogDrawerMutation']),
            openAutorizationDrawer(){
                this.changeAutorizationDrawerMutation()
            },
            openCatalogDrawer(){
                this.changeCatalogDrawerMutation()
            },
            showMainPage(){
                //this.$router.push('/')
                this.$router.push('/', () => {})

            }
        },

    }
</script>