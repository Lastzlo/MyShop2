<template>
    <v-navigation-drawer
            id="catalogDrawer"
            v-model="onCatalog"
            style="position:fixed; overflow-y:scroll;"
            absolute
            temporary
    >
        <!--<v-navigation-drawer
                dark
                v-model="drawer"
                width="400"
                style="position:fixed; top:0; left:0; overflow-y:scroll;"
        >-->
        <catalog-list></catalog-list>
    </v-navigation-drawer>
</template>
<script>
    import CatalogList from "pages/CatalogList.vue";
    import {mapState, mapMutations} from 'vuex'
    //import CatalogList from "./CatalogList";

    export default {
        name: 'CatalogDrawerComponent',
        components:{CatalogList},
        data: () => ({
            onCatalog: false
        }),
        computed: mapState(['onCatalogDrawer']),
        watch: {
            onCatalogDrawer: function (newVal, oldVal) {
                this.onCatalog = newVal
            },
            onCatalog: function (newVal, oldVal) {
                if(newVal!==this.onCatalogDrawer){
                    this.changeCatalogDrawerMutation()
                }
            },
        },
        methods:{
            ...mapMutations(['changeCatalogDrawerMutation']),
        }
    }
</script>