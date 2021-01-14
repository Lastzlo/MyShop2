import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({

    state: {
        //храняться данные
        //можно получить от сюда напрямую
            //import {mapState, mapMutations} from 'vuex'
            //computed
            //computed: mapState(['onAutorizationDrawer']),
        onCatalogDrawer: false,
        onAutorizationDrawer: false,
        selectedDirectories: [],
        directoriesForFilterForm: [],
    },
    getters:{
        //Иногда может потребоваться вычислять производное состояние на основе состояния хранилища, например, отфильтровать список и затем подсчитать количество элементо
        //computed
        // catalogDrawer: state => state.onCatalogDrawer,
        // autorizationDrawer: state => state.onAutorizationDrawer
    },
    mutations: {
        //вызиваетья чтобы изменить данные
        //methods

        changeAutorizationDrawerMutation(state){
            state.onAutorizationDrawer = !state.onAutorizationDrawer
        },
        changeCatalogDrawerMutation(state){
            state.onCatalogDrawer = !state.onCatalogDrawer
        },
        setSelectedDirectories(state, directories){
            state.selectedDirectories = directories
        },
        setDirectoriesForFilterForm(state, directories){
            state.directoriesForFilterForm = directories
        },
        clearDirectoriesForFilterForm(state){
            state.directoriesForFilterForm = []
        }
    },
    actions: {
        //Вместо того, чтобы напрямую менять состояние, действия инициируют мутации;
        // Действия могут использоваться для асинхронных операций.
        //methods
    }
})