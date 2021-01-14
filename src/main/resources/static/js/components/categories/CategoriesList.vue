<template>
    <v-container>
        <v-toolbar flat color="white">
            <v-toolbar-title>Category</v-toolbar-title>
            <v-divider
                    class="mx-4"
                    inset
                    vertical
            ></v-divider>
            <v-spacer></v-spacer>
            <v-dialog v-model="dialog" max-width="500px">
                <template v-slot:activator="{ on, attrs }">
                    <v-btn
                            color="primary"
                            dark
                            class="mb-2"
                            v-bind="attrs"
                            v-on="on"
                    >New Category</v-btn>
                </template>
                <v-card>
                    <v-card-title>
                        <span class="headline">{{ formTitle }}</span>
                    </v-card-title>

                    <v-card-text>
                        <v-container>
                            <v-row>
                                <v-col cols="12" sm="6" md="4">
                                    <v-text-field
                                            v-model="editedItem.categoryName"
                                            label="Category name"
                                            autofocus
                                    ></v-text-field>
                                </v-col>
                            </v-row>
                        </v-container>
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="blue darken-1" text @click="close">Cancel</v-btn>
                        <v-btn color="blue darken-1" text @click="save">Save</v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>

        </v-toolbar>

        <v-simple-table
                :fixed-header="true"
                :height="300"
        >
            <template v-slot:default>
                <thead>
                <tr>
                    <th class="text-left">#</th>
                    <th class="text-left">Name</th>
                    <th class="text-left">Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="category in categories" :key="category.id">
                    <td>{{ category.id }}</td>
                    <td>{{ category.categoryName }}</td>
                    <td>
                        <v-icon
                                small
                                class="mr-2"
                                @click="editCategory(category)"
                        >
                            mdi-pencil
                        </v-icon>
                        <v-icon
                                small
                                @click="deleteCategory(category)"
                        >
                            mdi-delete
                        </v-icon>
                    </td>
                </tr>
                </tbody>
            </template>
        </v-simple-table>

    </v-container>

</template>

<script>


    function getIndex(list, id) {
        for (var i=0; i<list.length; i++){
            if(list[i].id === id){
                return i;
            }
        }
        return -1;
    }

    export default {
        name: "CategoriesList",
        props: ['categories'],
        data: () => ({
            dialog: false,
            id: -1,
            editedItem: {
                categoryName: '',
                brands:[]
            },
            defaultItem: {
                categoryName: '',
                brands:[]
            },
        }),

        computed: {

            formTitle () {
                return this.id === -1 ? 'New Category' : 'Edit Category'
            },
        },
        watch: {
            dialog (val) {
                val || this.close()
            },
        },

        methods: {
            close () {
                this.dialog = false
                this.$nextTick(() => {
                    this.editedItem = Object.assign({}, this.defaultItem)
                    this.id = -1
                })
            },
            save () {
                let category = this.editedItem

                if (this.id > -1) {
                    this.$resource('/category{/id}').update({id: this.id}, category).then(result =>
                        result.json().then(data => {
                            //var index = getIndex(this.categories, data.id)
                            const index = this.categories.findIndex(item => item.id === data.id)


                            this.categories.splice(index, 1, data)


                        })
                    )
                } else {
                    this.$resource('/category{/id}').save({}, category).then(
                        result =>
                            result.json().then(data => {
                                this.categories.push(data)
                            })
                    )
                }
                this.close()
            },
            editCategory (category) {
                this.id = category.id
                this.editedItem = Object.assign({}, category)
                this.dialog = true
            },
            deleteCategory(category) {
                this.$resource('/category{/id}').remove({id: category.id}).then(result => {
                    if (result.ok) {
                        this.categories.splice(this.categories.indexOf(category), 1)

                    }
                })
            }

        }
    }

</script>

<style scoped>

</style>