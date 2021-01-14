<template>
    <v-autocomplete
            v-model="brand"
            :items="brands"
            dense
            label="Select brand"
            solo
    >
        <template v-slot:selection="{item}">
            {{ item.brandName }}
        </template>
        <template v-slot:item="{item}">
            {{ item.brandName }}
        </template>

    </v-autocomplete>
</template>

<script>
    export default {
        name: "BrandAutocomplete",
        props: ['brands'],
        data() {
            return{
                brand:null
            }
        },
        watch: {
            brand() {
                this.$emit("select-brand", this.brand);
            }
        },
    }
</script>

<style scoped>

</style>