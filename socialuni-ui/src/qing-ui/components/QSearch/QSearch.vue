<template>
    <view class="row-col-center bd bd-round h30 font-sm bg-default">
        <slot></slot>
    </view>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'

/*
显示出来已经选了的城市，给她画上钩
* */
  @Component
export default class QSearch extends Vue {

}
</script>
