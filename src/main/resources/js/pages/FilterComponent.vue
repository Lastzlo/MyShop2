<template>
    <v-container fluid>
        <!--//добавить панель для фильтра, она будет видна если экран больше 960-->

        <v-container
                fluid
                id="productList"

        >
            <v-row >
                <!--<v-col
                        class="pa-0"
                        cols="6"
                >
                    <filter-form></filter-form>
                </v-col>-->
                <v-col
                        class="pa-0"
                        cols="6"
                >
                    <filter-form2></filter-form2>

                    <!--<v-col
                            class="pa-1 mb-5"
                            v-for="product in products"
                            :key="product.id"

                            v-resize="onResize"

                    >
                        <v-hover
                                v-slot:default="{ hover }"
                                open-delay="200"
                        >
                            <v-card
                                    :elevation="hover ? 16 : 2"

                                    :class="{ 'on-hover': hover }"
                                    class="d-lg-flex d-xl-flex"
                                    color="grey lighten-2"
                                    flat
                                    tile
                            >
                                <v-card
                                        class="align-self-center"
                                >
                                    <v-img
                                            v-if="product.photos.length>0"
                                            :src="product.photos[0]"
                                            contain
                                            height="150"
                                            width="150"
                                            @click="openProductPage(product)"

                                    ></v-img>

                                </v-card>

                                <v-card
                                        outlined
                                        tile
                                        max-width="800"
                                >
                                    <v-card-title
                                            v-text="product.productName"
                                            @click="openProductPage(product)"
                                    >
                                        Name of product
                                    </v-card-title>
                                    <v-card-subtitle
                                            v-text="product.productDiscription"
                                    >
                                        Discription of product
                                    </v-card-subtitle>
                                    <v-card-text>
                                        <v-chip-group
                                                column
                                        >
                                            <v-chip
                                                    v-for="directory in product.directories"
                                                    :key="directory.id"
                                                    @click="openFilerPageByDirectory(directory)"
                                                    small
                                            >
                                                {{directory.name}}
                                            </v-chip>

                                        </v-chip-group>
                                    </v-card-text>
                                </v-card>

                                <v-card
                                        class="ml-auto"
                                        outlined
                                        tile
                                >
                                    <div class="d-flex flex-column align-center">
                                        <v-card-title
                                                v-text="product.price+' грн'"
                                                @click="openProductPage(product)"
                                        >
                                            Price
                                        </v-card-title>
                                        <v-btn
                                                text
                                                @click="openProductPage(product)"
                                        >
                                            Купить
                                        </v-btn>
                                    </div>
                                </v-card>
                            </v-card>
                        </v-hover>
                    </v-col>-->
                </v-col>
            </v-row>
        </v-container>
    </v-container>

</template>

<script>
    import directoriesApi from "api/directories.js";
    import FilterForm from "components/other/FilterForm.vue"
    import FilterForm2 from "components/other/FilterForm2.vue"

    export default {
        name: "FilterComponent",
        components: {
            FilterForm, FilterForm2
        },
        data: () => ({
            products: [],
            directoryId: null,
            vCradClass: "",
        }),
        mounted () {
            this.onResize()
        },
        watch: {
            // обрабатываем изменение параметров маршрута...
            $route(to, from) {

                this.directoryId = to.params.id
                console.log("newDirectoryId = "+this.directoryId)

                this.setProducts(this.directoryId)
            }
        },
        methods :{

            setProducts(directoryId){

                this.products= []
                directoriesApi.getProductByDirectoryId(directoryId).then(result =>
                    result.json().then(data =>
                        //записать данные в products
                        data.forEach(product => this.products.push(product))
                    )
                )
            },
            //переход на страницу связаную с даным тегом
            openFilerPageByDirectory(directory){
                console.log("openFilerPageByDirectory")
                //переход на страницу и обработана ошибка
                this.$router.push({ path: `/filter/${directory.id}` }, () => {})
            },
            //переход на странищу товара
            openProductPage(product){
                console.log("openProductPage")
            },
            //при изминении размеров экрана
            onResize () {
                let x = window.innerWidth;

                //console.log("window.innerWidth = "+window.innerWidth)

                if(x>660){
                    this.vCradClass = "d-flex"
                } else {
                    this.vCradClass = ""
                }

            },
        },
        created() {
            /*this.directoryId = this.$route.params.id
            console.log("directoryId = "+this.directoryId)

            this.setProducts(this.directoryId)*/
        }

    }
</script>

<style lang="sass" scoped>
    .v-card.on-hover.theme--dark
        background-color: rgba(#FFF, 0.8)
        >.v-card__text
            color: #000
</style>