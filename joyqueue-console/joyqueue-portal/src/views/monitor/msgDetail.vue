<template>
  <div>
      <div class="ml20 mt30">
        <d-input v-model="searchData.partition" placeholder="请输入partition" class="left mr5"
                 style="width: 213px" @on-enter="getList">
          <span slot="prepend">分区</span>
        </d-input>
        <!--<d-input v-model="searchData.timestamp" placeholder="请输入时间戳" class="left mr5"-->
                 <!--style="width: 213px" @on-enter="getList">-->
          <!--<span slot="prepend">时间戳</span>-->
        <!--</d-input>-->
      <d-date-picker
        v-model="searchData.timestamp"
        type="datetime"
        placeholder="选择日期时间"
        value-format="timestamp"
        @on-enter="getList">
        <span slot="prepend">开始时间</span>
      </d-date-picker>
      <d-input v-model="searchData.index" placeholder="请输入位点" class="left mr5"
               style="width: 213px" @on-enter="getList">
        <span slot="prepend">位点</span>
      </d-input>
        <d-button type="primary" color="success" @click="getList">查询<icon name="search" style="margin-left: 5px;"></icon></d-button>
      </div>
    <my-table :data="tableData" :showPin="showTablePin" style="height: 400px;overflow-y:auto" :showPagination=false
                :page="page" @on-size-change="handleSizeChange"  @on-current-change="handleCurrentChange"/>
      <label >共 {{page.total}} 条记录</label>
  </div>
</template>

<script>
import MyTable from '../../components/common/myTable'
import apiRequest from '../../utils/apiRequest.js'
import crud from '../../mixins/crud.js'

import {timeStampToString} from '../../utils/dateTimeUtils'

export default {
  name: 'msg-detail',
  components: {MyTable},
  mixins: [crud],
  props: {
    doSearch: {
      type: Boolean,
      default: false
    },
    app: {
      id: 0,
      code: ''
    },
    subscribeGroup: '',
    topic: {
      id: '',
      code: ''
    },
    namespace: {
      id: '',
      code: ''
    },
    type: {
      type: Number,
      default: 0
    },
    colData: {
      type: Array,
      default: function () {
        return [
          {
            title: 'index',
            key: 'msgIndexNo'
          },
          {
            title: '开始时间',
            key: 'startTime',
            formatter (item) {
              return timeStampToString(item.startTime)
            }
          },

          {
            title: '服务端收到时间',
            key: 'storeTime',
            formatter (item) {
              return timeStampToString(item.startTime + item.storeTime)
            }
          },
          {
            title: '已被消费',
            key: 'flag'
          },
          {
            title: 'BusinessID',
            key: 'businessId'
          },
          {
            title: '内容',
            key: 'body',
            render: (h, params) => {
              return h('d-input', {
                props: {
                  disabled: true,
                  type: 'textarea',
                  value: params.item.body
                }
              })
            }
          }
        ]
      }
    }
  },
  data () {
    return {
      searchData: {
        partition: '',
        index: '',
        timestamp: ''
      },
      urls: {
        getMsgDetail: '/monitor/view/message'
      },
      tableData: {
        rowData: [],
        colData: this.colData
      },
      page: {
        total: 0
      }
    }
  },
  methods: {
    getList () {
      if (!this.searchData.partition) {
        this.$Message.error('验证不通过: 分区必填')
        return
      }
      if (!this.searchData.timestamp && !this.searchData.index) {
        this.$Message.error('验证不通过: 开始时间或位点至少填写一项')
        return
      }
      this.showTablePin = true
      let data = {
        topic: {
          id: this.topic.id,
          code: this.topic.code
        },
        namespace: {
          id: this.namespace.id,
          code: this.namespace.code
        },
        app: {
          id: this.app.id,
          code: this.app.code
        },
        subscribeGroup: this.subscribeGroup || '',
        type: this.type
      }

      apiRequest.post(this.urls.getMsgDetail, this.searchData, data, false).then((data) => {
        data.data = data.data || []
        this.tableData.rowData = data.data
        this.page.total = this.tableData.rowData.length
        this.showTablePin = false
      })
    }
  },
  watch: {
    doSearch: {
      handler (curVal, oldVal) {
        if (curVal) {
          this.getList()
          this.doSearch = false
        }
      },
      deep: true
    }
  },
  mounted () {
    // this.getList()
  }

}
</script>

<style scoped>

</style>
