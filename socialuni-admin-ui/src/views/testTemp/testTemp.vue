<template>
  <div class="w100r h100r flex-col">
    <div class="w1000">
      <el-input v-model="content" type="textarea" rows="6" />
    </div>
    <div>
      <el-button @click="testAPI">测试</el-button>

      <a href="weixin://dl/business/?t=jI5iGuT1ers">123</a>

      <wx-open-launch-weapp
        id="launch-btn"
        username="gh_51546f2d5ace"
      >
        <template>
          <button class="btn">打开小程序</button>
        </template>
      </wx-open-launch-weapp>
    </div>

    <!--      <iframe class="h50r w50r" src="https://www.baidu.com"></iframe>-->
    <div class="flex-auto overflow-auto">
      <div v-for="talk in talks">
        {{ talk.content }}
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import Talk from '@/model/talk/Talk'

@Component
export default class TestFlex extends Vue {
  content = '5'
  talks: Talk[] = []

  testAPI() {
    /* TestAPI.testKeywordsAPI(this.content).then(res => {
      this.talks = res.data
    })*/
  }
}
</script>

<style scoped>

</style>
