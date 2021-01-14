<template>
    <v-container fluid>
        <v-row
                align="center"
        >
            <v-col cols="12">
                <v-container>
                    <category-autocomplete :categories="categories" @select-category="selectCategory"/>
                </v-container>
            </v-col>
            <v-col
                    v-if="selectedCategory"
                    cols="12"
            >
                <v-toolbar flat color="white">
                    <v-toolbar-title>Brand</v-toolbar-title>
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
                            >New Brand</v-btn>
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
                                                    v-model="editedItem.brandName"
                                                    label="Brand name"
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
                        <tr v-for="brand in selectedCategory.brands" :key="brand.id">
                            <td>{{ brand.id }}</td>
                            <td>{{ brand.brandName }}</td>
                            <td>
                                <v-icon
                                        small
                                        class="mr-2"
                                        @click="editBrand(brand)"
                                >
                                    mdi-pencil
                                </v-icon>
                                <v-icon
                                        small
                                        @click="deleteBrand(brand)"
                                >
                                    mdi-delete
                                </v-icon>
                            </td>
                        </tr>
                        </tbody>
                    </template>
                </v-simple-table>
            </v-col>

        </v-row>
    </v-container>


</template>

<script>
    import CategoryAutocomplete from "../categories/CategoryAutocomplete.vue";

    function getIndex(list, id) {
        for (var i=0; i<list.length; i++){
            if(list[i].id === id){
                return i;
            }
        }
        return -1;
    }

    export default {
        name: "BrandsList",
        components: {
            CategoryAutocomplete,
        },
        props: ['categories'],
        data: () => ({
            selectedCategory: null,
            dialog: false,
            id: -1,
            editedItem: {
                brandName: '',
                category: null

            },
            defaultItem: {
                brandName: '',
                category: null
            },
        }),
        computed: {

            formTitle () {
                return this.id === -1 ? 'New Brand' : 'Edit Brand'
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
                this.editedItem.category = this.selectedCategory
                let brand = this.editedItem

                if (this.id > -1) {
                    this.$resource('/brand{/id}').update({id: this.id}, brand).then(result =>
                        result.json().then(data => {
                            var index = getIndex(this.selectedCategory.brands, data.id)
                            this.selectedCategory.brands.splice(index, 1, data)
                        })
                    )
                } else {
                    this.$resource('/brand{/id}').save({}, brand).then(
                        result =>
                            result.json().then(data => {
                                this.selectedCategory.brands.push(data)
                            })
                    )
                }
                this.close()
            },
            editBrand (brand) {
                this.id = brand.id
                this.editedItem = Object.assign({}, brand)
                this.dialog = true
            },
            deleteBrand(brand) {
                this.$resource('/brand{/id}').remove({id: brand.id}).then(result => {
                    if (result.ok) {
                        this.selectedCategory.brands.splice(this.selectedCategory.brands.indexOf(brand), 1)

                    }
                })
            },
            selectCategory(category) {
                this.selectedCategory = category;
            }
        }
    }
</script>

<style scoped>

</style>