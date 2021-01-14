<template>
    <v-navigation-drawer
            id="autorizationDrawer"
            v-model="onAutorization"
            style="position:fixed; overflow-y:scroll;"
            absolute
            temporary
            right
    >
        <autorization-list></autorization-list>
    </v-navigation-drawer>
</template>
<script>
    import AutorizationList from "pages/AutorizationList.vue";
    import {mapState, mapMutations} from 'vuex'

    export default {
        name: 'AutorizationDrawerComponent',
        components: {AutorizationList},
        data: () => ({
            onAutorization: false
        }),
        computed: mapState(['onAutorizationDrawer']),
        watch: {
            onAutorizationDrawer: function (newVal, oldVal) {
                this.onAutorization = newVal
            },
            onAutorization: function (newVal, oldVal) {
                if(newVal!==this.onAutorizationDrawer){
                    this.changeAutorizationDrawerMutation()
                }
            },
        },
        methods:{
            ...mapMutations(['changeAutorizationDrawerMutation']),
        }
    }
</script>
