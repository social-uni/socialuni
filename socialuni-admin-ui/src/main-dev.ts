import Vue from 'vue'
import App from './App.vue'
import store from './store'
import router from './plugins/router/router'
import '@/plugins/router/interceptor'
import '@/styles/index.scss'
import './plugins/element'
import components from '@/components'
import GlobalConst from '@/constants/GlobalConst'

Vue.prototype.$const = GlobalConst

Vue.use(components)

Vue.config.productionTip = false

// register global utility filters.

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')