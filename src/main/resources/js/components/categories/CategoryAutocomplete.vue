<template>
    <v-autocomplete
            v-model="category"
            :items="categories"
            dense
            label="Select category"
            solo
    >
        <template v-slot:selection="{item}">
            {{ item.categoryName }}
        </template>
        <template v-slot:item="{item}">
            {{ item.categoryName }}
        </template>

    </v-autocomplete>
</template>

<script>
    export default {
        name: "CategoryAutocomplete",
        props: ['categories'],
        data() {
            return{
                category:null
            }
        },
        watch: {
            category() {
                this.$emit("select-category", this.category);
            }
        },
    }
</script>

<style scoped>

</style>