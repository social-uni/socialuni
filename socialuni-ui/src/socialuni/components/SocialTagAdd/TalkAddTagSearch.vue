<template>
  <view>
    <view class="cu-bar bg-white search">
      <view class="search-form round">
        <q-icon class="mx-5 text-gray" icon="search"></q-icon>
        <input v-model="searchContent" :adjust-position="false" type="text" @focus="showSearchView"
               placeholder="输入话题中文名称进行筛选" confirm-type="search"/>
        <q-icon v-if="searchContent" class="mr text-gray" icon="close"
                @click="clearSearchContent"
        ></q-icon>
      </view>
      <view class="action" @click="closeSearch">
        <text>取消</text>
      </view>
    </view>
    <view v-if="searchContent || showSearch">
      <view v-if="searchContent&&isAdd" class="article-row row-col-center solid-bottom color-blue" @click="addTag">
        未找到相关标签，可点击
        <q-icon class="ml-sm" icon="arrow-rightward"></q-icon>
        此处去创建
      </view>
      <view v-for="tag in showTags" :key="tag.id" @click="checkTag(tag)"
            class="article-row row-between solid-bottom">
        <text>
          #{{tag.name}}
        </text>
        <view v-if="tag.count" class="row-col-center">
          <q-icon addClass="color-red" icon="mdi-fire"></q-icon>
          {{tag.count}}
        </view>
      </view>
    </view>
    <view v-else>
      <view class="article">
        <view>
          已选话题：
        </view>
        <view class="grid">
          <view v-for="tag in selectTags" :key="tag.id" class="row-col-center pb-10 px-mn">
            <view class="q-tag q-round bg-pink-plain">
              #{{tag.name}}
              <q-icon v-if="tag.id" class="ml-5" icon="close-circle-fill" size="16"
                      @click="deleteTag(tag)"></q-icon>
            </view>
          </view>
        </view>
        <view>
          热门话题：
        </view>
        <view class="grid">
          <view v-for="tag in showTags" :key="tag.id" class="row-center pb-10 px-mn">
            <view class="q-tag q-round bg-pink-plain"
                  @click="checkTag(tag)">
              #{{tag.name}}
              <block v-if="tag.count">
                <q-icon addClass="text-cyan" icon="mdi-fire"></q-icon>
                {{tag.count}}
              </block>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script lang="ts">
import { Component, Emit, Prop, Vue, Watch } from 'vue-property-decorator'
import TagVO from '../../model/community/tag/TagVO'
import CenterUserDetailRO from '../../model/social/CenterUserDetailRO'
import QIcon from '../../../qing-ui/components/QIcon/QIcon.vue'

@Component({
  components: { QIcon }
})
export default class TalkAddTagSearch extends Vue {
    searchContent = ''
    showSearch = false
    @socialUserStore.State('user') readonly user: CenterUserDetailRO
    // 全部tag,因为需要与外部联动，所以使用外部传进来的tags
    @Prop() readonly tags: TagVO []
    @Prop({ type: Boolean, default: false }) readonly isAdd: boolean
    // 已选tag
    @Prop() readonly selectTags: TagVO []
    // @appModule.State('userTags') readonly userTags: TagVO []

    get showTags (): TagVO[] {
      if (this.searchContent) {
        return this.tags.filter(tag => tag.name.indexOf(this.searchContent) > -1)
      } else {
        return this.tags
      }
    }

    closeSearch () {
      this.$emit('close')
    }

    @Watch('searchContent')
    watchSearchContent () {
      if (!this.searchContent) {
        this.showSearch = false
      }
    }

    @Emit()
    deleteTag (tag: TagVO) {
      return tag
    }

    @Emit()
    addTag () {
      return ''
    }

    showSearchView () {
      this.showSearch = true
    }

    checkTag (tag: TagVO) {
      this.searchContent = ''
      this.$emit('change', tag)
    }

    clearSearchContent () {
      this.searchContent = ''
    }
}
</script>
