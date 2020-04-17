// 列宽度拖动
import Config from '../config'
import { on, off } from '../utils/assist'
import { getViewportOffset } from '../utils/position.js'

const prefixCls = `${Config.clsPrefix}table`

export default {
  data () {
    return {
      draggingColumn: null, // 当前拖动的列
      isDragging: false, // 是否正在拖动
      draggingStartX: 0, // 拖动开始横坐标
      draggingEndX: 0, // 拖动结束横坐标
      minColumnWidth: 15 // 列允许拖动后的最小宽度
    }
  },
  methods: {
    handleTitleMouseMove (event, column) {
      if (!this.colWidthDrag) {
        return false
      }
      let target, rect
      if (this.isDragging) {
        this.setDragLinePosition(event)
      }
      if (!this.border) {
        return false
      }
      target = event.target
      while (target && target.tagName !== 'TH') {
        target = target.parentNode
      }
      /* while (target && ((target.className && !hasClass(target, `${prefixCls}__cell`)) || !target.className)) {
        target = target.parentNode;
      } */
      rect = target.getBoundingClientRect()
      const bodyStyle = document.body.style
      if (rect.width >= this.minColumnWidth && rect.right - event.pageX < 10) {
        if (!this.isDragging) { // 拖动中不设置
          this.draggingColumn = this.columnsWidth[column._index]
        }
        bodyStyle.cursor = 'col-resize'
      } else {
        if (!this.isDragging) { // 拖动中不设置
          this.draggingColumn = null
          bodyStyle.cursor = ''
        }
      }
    },
    handleTitleMouseOut () {
      if (!this.isDragging) {
        document.body.style.cursor = ''
      }
    },
    handleTitleMouseDown (event, column) {
      if (!this.draggingColumn || !this.border) {
        return false
      }
      this.isDragging = true
      this.draggingStartX = event.clientX
      this.setDragLinePosition(event)
      document.onselectstart = function () {
        return false
      }
      document.ondragstart = function () {
        return false
      }

      on(document, 'mousemove', this.handleDragMouseMove)
      on(document, 'mouseup', this.handleDragMouseUp)
    },
    handleDragMouseMove (e) {
      if (!this.isDragging) {
        return false
      }

      this.setDragLinePosition(e)
    },
    setDragLinePosition (e) {
      const tableLeft = getViewportOffset(this.$el).left
      const dragLine = this.$el.querySelector(`.${prefixCls}__drag-line`)
      const clientX = e.clientX

      if (this.draggingColumn.width + (clientX - this.draggingStartX) <= this.minColumnWidth) {
        return
      }

      dragLine.style.left = (clientX - tableLeft) + 'px'
    },

    // 拖动时mouseup
    handleDragMouseUp (e) {
      if (!this.isDragging) {
        return false
      }

      this.draggingEndX = e.clientX

      const differ = this.draggingEndX - this.draggingStartX

      // 差值大于1才处理
      if (Math.abs(differ) > 1) {
        let draggingColumn = this.draggingColumn

        if (draggingColumn.width + differ < this.minColumnWidth) {
          draggingColumn.width = this.minColumnWidth
        } else {
          draggingColumn.width += differ
        }
      }

      this.draggingColumn = null
      document.body.style.cursor = ''
      this.isDragging = false

      document.onselectstart = function () {
        return true
      }
      document.ondragstart = function () {
        return true
      }

      off(document, 'mousemove', this.handleDragMouseMove)
      off(document, 'mouseup', this.handleDragMouseUp)
    }

  }

}
