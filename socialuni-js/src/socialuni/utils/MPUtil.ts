export default class MPUtil {
  static checkUpdate () {
    // 如果小程序平台则需要更新
    const updateManager = uni.getUpdateManager()
    updateManager.onCheckForUpdate(() => {
    })
    updateManager.onUpdateReady(() => {
      uni.showModal({
        title: '更新提示',
        content: '新版本已经准备好，是否重启应用？',
        success (res) {
          if (res.confirm) {
            // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
            updateManager.applyUpdate()
          }
        }
      })
    })
  }
}
