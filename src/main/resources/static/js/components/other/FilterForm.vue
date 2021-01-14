<template>
    <div>
        <div>
            <v-treeview
                    v-model="selection"
                    hoverable
                    open-on-click
                    :items="items"
                    :open.sync="open"
                    selection-type="independent"
                    selectable
                    return-object
            >
                <template v-slot:append="{item}">

                    <div id="snackbar">
                        <v-snackbar
                                v-model="item.snackbarOpen"

                                absolute

                        >
                            {{ text }}

                            <v-btn
                                    color="pink"

                                    @click="this.snackbarClose(item)"
                            >
                                Close
                            </v-btn>
                        </v-snackbar>
                    </div>

                </template>
            </v-treeview>
            <template v-if="!selection.length">
                No nodes selected.
            </template>
            <template v-else>
                <div v-for="node in selection" :key="node.id">
                    {{ node.name }}
                </div>
            </template>



            <!--<v-card
                    height="300"
                    flat
            >
                <v-btn
                        dark
                        @click="snackbar = true"
                >
                    Open Snackbar
                </v-btn>
                <v-snackbar
                        v-model="snackbar"
                        absolute
                        left
                        top
                >
                    {{ text }}

                    <template v-slot:action="{ attrs }">
                        <v-btn
                                color="pink"
                                text
                                v-bind="attrs"
                                @click="snackbar = false"
                        >
                            Close
                        </v-btn>
                    </template>
                </v-snackbar>
            </v-card>-->

        </div>
    </div>


</template>

<script>
    //используем апи
    import directoriesApi from 'api/directories'
    import {mapState, mapMutations} from 'vuex'

    export default {
        name: "FilterForm",
        data: () => ({
            selection: [],
            open: [],

            items: [],
            oldLastDirectory: null,
            text: 'Hello, I\'m a snackbar',
        }),
        computed: {
            ...mapState(['tagsForFilterForm']),
        },
        watch: {
            selection(newVal, oldVal) {
                if(newVal!==oldVal){
                    this.setDirectorysForFilterForm(this.selection)

                    if(newVal.length>0){

                        //показать окно возле последнего элемента массива
                        let lastDirectory = newVal[newVal.length-1]
                        console.log("последний элемент массива это = "+lastdDrectory.name)

                        if(this.oldLastDirectory !==null){

                            this.closeOldLastDirectory(this.oldLastDirectory)
                        }
                        this.oldLastDirectory = lastDirectory

                        this.openLastDirectory(lastDirectory)
                    }
                }
            },
            $route(to, from) {
                // обрабатываем изменение параметров маршрута...
                let directoryId = to.params.id
                //console.log("newDirectoryId = "+this.directoryId)
                this.setItems(directoryId)
            }
        },
        methods: {
            ...mapMutations(['setDirectorysForFilterForm']),
            //раскрывает все папки
            openAll(items){
                items.forEach(item => {
                    this.open.push(item)
                    if(item.children){
                        this.openAll(item.children)
                    }

                })
            },
            openLastDirectory(lastDirectory){
                console.log("openLastDirectory")
                console.log("lastDirectory.name = "+lastDirectory.name)
                lastDirectory.snackbarOpen = false
                lastDirectory.snackbarOpen = true
            },
            snackbarClose(item){
                console.log("snackbarClose ")
                console.log("item.name = "+item.name)
                item.snackbarOpen = false
            },
            closeOldLastDirectory(oldLastDirectory){
                console.log("closeOldLastDirectory")
                console.log("oldLastDirectory.name = "+oldLastDirectory.name)
            },
            setItems(directoryId){

                this.items = []
                //запрос на сервер
                directoriesApi.getOne(directoryId).then(result =>
                    result.json().then(data => {
                        data.children.forEach(
                            item => {
                                //добавили значение isOpened
                                item.snackbarOpen = false

                                //obj.param125 = '123';


                                this.items.push(item)
                            }
                        )

                        this.openAll(this.items)
                    })
                )
            },
        },
        created: function () {
            if(this.$route.params.id !==null){
                let directoryId = this.$route.params.id

                this.setItems(directoryId)
            }
        },
    }
</script>

<style scoped>

</style>