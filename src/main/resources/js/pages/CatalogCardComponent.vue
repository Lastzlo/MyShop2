<template>
    <v-container fluid>
        <v-container id="catalogCard">
            <v-card
                    class="mx-auto"

                    v-resize="onResize"
            >
                <v-container>
                    <v-row dense>
                        <v-col

                                v-for="item in items"
                                :key="item.id"
                                :cols=cols
                        >
                            <v-card
                                    class="d-flex align-end justify-center pa-0 mx-auto"
                                    height="125px"
                                    width="150px"
                                    @click="openPage(item)"
                                    outlined
                            >
                                <div :class="`text-body-2 mx-2`">
                                    {{item.name}}
                                </div>
                            </v-card>
                        </v-col>
                    </v-row>
                </v-container>
            </v-card>
        </v-container>

    </v-container>

</template>

<script>
    import directoriesApi from 'api/directories'

    export default {
        name: "CatalogCardComponent",
        data: () => ({
            items: [],
            cols: 4,
        }),
        mounted () {
            this.onResize()
        },
        methods:{
            showProductList(){
                this.$router.push('/setting')
            },
            openPage(item){
                console.log("openPage")


                //переход на страницу и обработана ошибка
                this.$router.push({ path: `/filter/${item.id}` }, () => {})
            },
            onResize () {
                let x = window.innerWidth;

                //console.log("window.innerWidth = "+window.innerWidth)

                if(x<360){
                    this.cols = 12;
                }else if(x>=360 && x<480){
                    this.cols = 4;
                }else if(x>=480 && x<720){
                    this.cols = 3;
                } else if(x>=720 && x<1280){
                    this.cols = 2;
                } else if(x>=1280){
                    this.cols = 1.5;
                }

            },
        },
        created: function () {
            //запрос на сервер
            directoriesApi.getCore().then(result =>
                result.json().then(data => {
                    //this.items = data.children
                    if(data.children){
                        data.children.forEach(
                            item => {
                                this.items.push(item)
                            }
                        )
                    }
                })
            )
            // this.$resource('/directory/getCore').get().then(result =>
            //     result.json().then(data => {
            //         //this.items = data.children
            //         if(data.children){
            //             data.children.forEach(
            //                 item => {
            //                     this.items.push(item)
            //                 }
            //             )
            //         }
            //     })
            // )
        },
    }
</script>

