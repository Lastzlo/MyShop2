<template>
    <v-container fluid>
        <v-container id="productForm">
            <v-card>
                <v-card-title>
                    <span class="headline">{{ formTitle }}</span>
                </v-card-title>

                <v-card-text>
                    <v-container>

                        <v-text-field
                                v-model="editedItem.productName"
                                label="Product name"
                                autofocus
                        ></v-text-field>
                        <v-text-field
                                v-model="editedItem.price"
                                label="Product price"
                                autofocus
                        ></v-text-field>
                        <v-text-field
                                v-model="editedItem.productDiscription"
                                label="Product discription"
                                autofocus
                        ></v-text-field>

                        <v-container
                                id="editedItemPhoto"
                                fluid
                                v-if="this.editedItemPhoto.length>0"
                        >
                            <v-row>
                                <span class="headline">editedItemPhoto</span>
                            </v-row>
                            <v-row>
                                <v-col
                                        v-for="photo in this.editedItemPhoto"
                                        :key="photo.src"
                                        class="d-flex child-flex"
                                        cols="2"
                                >
                                    <v-container
                                            fluid
                                            height="150"
                                            width="150"
                                    >
                                        <v-img
                                                :src="photo"
                                                contain
                                        >
                                            <v-btn
                                                    small color="white">
                                                <v-icon @click="deletePicture(photo)">
                                                    mdi-close
                                                </v-icon>
                                            </v-btn>
                                        </v-img>
                                    </v-container>
                                </v-col>
                            </v-row>
                        </v-container>

                        <v-container
                                id="newPhoto"
                                fluid
                        >
                            <v-row>
                                <span class="headline">Загрузить фото</span>
                            </v-row>
                            <v-row>
                                <v-file-input
                                        v-model="files"
                                        multiple
                                        label="Загрузите фото"
                                ></v-file-input>
                            </v-row>
                            <v-row v-if="this.newPhotoToShow.length>0">
                                <v-col
                                        v-for="photo in this.newPhotoToShow"
                                        :key="photo"
                                        class="d-flex child-flex"
                                        cols="2"
                                >
                                    <v-img
                                            :src="photo"
                                            contain
                                            height="150"
                                            width="150"

                                    ></v-img>

                                </v-col>
                            </v-row>
                        </v-container>

                        <div>
                            <v-row>
                                <v-col
                                        class="pa-0"
                                        cols="8"
                                >
                                    #таблица заполнения характеристик
                                </v-col>
                                <v-col
                                        class="pa-0"
                                        cols="4"
                                >
                                    #таблица заполнения тегов
                                    <directory-list :directoriesFromProduct="directoriesFromProduct"/>

                                </v-col>
                            </v-row>
                        </div>


                    </v-container>
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="blue darken-1" text @click="clearForm">Clear</v-btn>
                    <v-btn color="blue darken-1" text @click="save">{{ saveButtonTitle }}</v-btn>
                </v-card-actions>
            </v-card>
        </v-container>

        <v-container id="productTable">
            <!--тут будет список товаров-->
            <template>
                <v-data-table
                        :headers="headers"
                        :items="products"
                        sort-by="calories"
                        class="elevation-1"
                >
                    <template v-slot:top>
                        <v-toolbar flat color="white">
                            <v-toolbar-title>Product list</v-toolbar-title>
                            <v-divider
                                    class="mx-4"
                                    inset
                                    vertical
                            ></v-divider>
                        </v-toolbar>
                    </template>
                    <template v-slot:item.actions="{ item }">
                        <v-icon
                                small
                                class="mr-2"
                                @click="editItem(item)"
                        >
                            mdi-pencil
                        </v-icon>
                        <v-icon
                                small
                                @click="deleteItem(item)"
                        >
                            mdi-delete
                        </v-icon>
                    </template>
                    <template v-slot:no-data>
                        <v-btn color="primary" @click="initialize">Reset</v-btn>
                    </template>
                </v-data-table>
            </template>
        </v-container>
    </v-container>
</template>

<script>
    //используем апи
    import productsApi from 'api/products'
    import DirectoryList from "components/other/DirectoryList.vue"

    import {mapState, mapMutations} from 'vuex'

    export default {
        name: "ProductManagementComponent",
        components: {
            DirectoryList
        },
        data: () => ({
            headers: [
                {
                    text: 'Product name',
                    align: 'start',
                    sortable: false,
                    value: 'productName',
                },{
                    text: 'Creation date',
                    sortable: false,
                    value: 'creationDate',
                },
                { text: 'Actions', value: 'actions', sortable: false },
            ],
            products: [],
            directoriesFromProduct:[],
            id: -1,
            editedItem: {
                productName: '',
                price: '',
                productDiscription: '',
                directories: [],
                photos: [],
                photoToDelete:[],

            },
            defaultItem: {
                productName: '',
                price: '',
                productDiscription: '',
                directories: [],
                photos:[],
                photoToDelete:[],
                //price: '',
                //productDiscription: '',
            },
            //переменная для файла
            files: [],
            newPhotoToShow:[],
            editedItemPhoto:[],
            photoToDelete:[],
        }),
        computed: {
            ...mapState(['selectedDirectories']),
            formTitle () {
                return this.id === -1 ? 'New Item' : 'Edit Item'
            },
            saveButtonTitle () {
                return this.id === -1 ? 'Save' : 'Update'
            },

        },
        watch: {
            files(newVal, oldVal){
                this.newPhotoToShow = []

                newVal.forEach(
                    item => this.newPhotoToShow.push(window.URL.createObjectURL(item))
                )
            },
            selectedDirectories: function (newVal, oldVal) {
                //this.onAutorization = newVal
                this.editedItem.directories = newVal;
            },
        },
        methods: {
            ...mapMutations(['setSelectedDirectories']),
            deletePicture(photo){
                const index = this.editedItemPhoto.findIndex(item => item === photo)
                this.editedItemPhoto.splice(index, 1)


                //this.editedItem.photoToDelete.push(photo)
                this.photoToDelete.push(photo)


            },
            // selectedDirectories(directories){
            //     this.editedItem.directories = directories;
            // },
            save () {
                let product = this.editedItem

                if (this.id > -1) {
                    //update


                    product.photoToDelete = this.photoToDelete

                    let formData = new FormData();

                    this.files.forEach(
                        file => {
                            formData.append('files', file)
                        }
                    )
                    formData.append(
                        'product',
                        new Blob(
                            [JSON.stringify(product)
                            ],
                            {
                                type: "application/json"
                            }
                        )
                    )

                    // this.$resource('/product').update({}, formData).then(result =>
                    //     result.json().then(data => {
                    //         const index = this.products.findIndex(item => item.id === data.id)
                    //         this.products.splice(index, 1, data)
                    //     })
                    // )

                    productsApi.update(formData).then(result =>
                        result.json().then(data => {
                            const index = this.products.findIndex(item => item.id === data.id)
                            this.products.splice(index, 1, data)
                        })
                    )


                } else {
                    //save
                    let formData = new FormData();

                    this.files.forEach(
                        file => {
                            formData.append('files', file)
                        }
                    )
                    formData.append(
                        'product',
                        new Blob(
                            [JSON.stringify(product)
                            ],
                            {
                                type: "application/json"
                            }
                        )
                    )

                    productsApi.add(formData).then(
                        result =>
                            result.json().then(data => {
                                this.products.push(data)
                            })
                    )

                    // this.$resource('/product').save({}, formData).then(
                    //     result =>
                    //         result.json().then(data => {
                    //             this.products.push(data)
                    //         })
                    // )
                }

                this.clearForm()
            },
            clearForm () {
                this.photoToDelete = []
                this.directoriesFromProduct = []
                this.editedItem = Object.assign({}, this.defaultItem)
                this.id = -1
                this.editedItemPhoto = []
                this.files = []

                // this.$nextTick(() => {
                //     //очищает список отмеченых тегов
                //     this.directoriesFromProduct = []
                //
                //     this.editedItem = Object.assign({}, this.defaultItem)
                //     this.id = -1
                //     //
                //     this.editedItemPhoto = []
                //     //
                //     //this.photoToDelete = []
                //     //
                //     this.files = []
                // })
            },
            initialize () {
                productsApi.getAll().then(result =>
                    result.json().then(data =>
                        //записать данные в products
                        data.forEach(product => this.products.push(product))
                    )
                )
                // this.$resource('/product').get().then(result =>
                //     result.json().then(data =>
                //         //записать данные в products
                //         data.forEach(product => this.products.push(product))
                //     )
                // )
            },
            deleteItem(item){
                productsApi.remove(item.id).then(result => {
                    if (result.ok) {
                        this.products.splice(this.products.indexOf(item), 1)
                    }
                })

                this.clearForm()

                // this.$resource('/product{/id}').remove({id: item.id}).then(result => {
                //     if (result.ok) {
                //         this.products.splice(this.products.indexOf(item), 1)
                //     }
                // })
            },
            editItem(item){
                //очистка
                this.clearForm()

                this.id = item.id
                this.directoriesFromProduct = item.directories

                this.editedItem = Object.assign({}, item)

                if(item.photos !== null){
                    item.photos.forEach(photo => this.editedItemPhoto.push(photo))
                }
            },
        },
        created: function () {
            this.initialize()
        },
    }
</script>

<style scoped>

</style>