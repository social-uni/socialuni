<template>
  <el-form-item :label="label">
    <y-select
      v-if="type === $const.DataTableColumnTypeEnum.select"
      :model="model"
      :readonly="readonly"
      :options="options"
      :label="optionLabel"
      :value="optionValue"
      v-on="$listeners"
      @change.native.stop="change"
    />
    <!--      ? option.label : optionValue-->
    <el-input
      v-else-if="type === $const.DataTableColumnTypeEnum.input"
      :value="model"
      :readonly="readonly"
      size="small"
      placeholder="请输入"
      @click.native.stop
      v-on="$listeners"
      @input="change"
    />
  </el-form-item>
</template>

<script lang="ts">
import { Component, Emit, Model, Prop, Vue } from 'vue-property-decorator'
import YSelect from '@/components/YComponent/YSelect/YSelect.vue'

/**
 * @author 秦开远
 * @date 2021/1/21 15:25
 *
 * 在数据源业务基础上，封装基础table
 */
@Component({
  components: { YSelect }
})
export default class YFormItem extends Vue {
  @Model('change') readonly model!: any

  @Prop() readonly type: string

  @Prop() readonly label: string

  @Prop() readonly options: []
  @Prop() readonly optionLabel: string
  @Prop() readonly readonly: boolean
  @Prop() readonly optionValue: string

  @Emit()
  change(value) {
    console.log(value)
    return value
  }
}
</script>
