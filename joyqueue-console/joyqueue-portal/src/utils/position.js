export function getToDocT (el) {
  // let top = el.offsetTop;
  // while (el) {
  //   top += el.offsetTop;
  //   el = el.offsetParent;
  // }
  // return top;
  let rect = el.getBoundingClientRect()
  // 获取元素距离文档顶部的距离
  let top = rect.top + (window.pageYOffset || document.documentElement.scrollTop) - (document.documentElement.clientTop || 0)
  return Math.floor(top)
}

export function getToDocL (el) {
  // let left = el.offsetLeft;
  // while (el) {
  //   left += el.offsetLeft;
  //   el = el.offsetParent;
  // }
  // return left;
  let rect = el.getBoundingClientRect()
  // 获取元素距离文档顶部的距离
  let left = rect.left + (window.pageXOffset || document.documentElement.scrollLeft) - (document.documentElement.clientLeft || 0)
  return Math.floor(left)
}

export function getDocScrollT () {
  return document.documentElement.scrollTop || document.body.scrollTop
}

export function getDocScrollL () {
  return document.documentElement.scrollLeft || document.body.scrollLeft
}

export function getWindowW () {
  return window.innerWidth
}

export function getWindowH () {
  return window.innerHeight
}

/**
 * Get bounding client rect of given element
 * @function
 * @ignore
 * @param {HTMLElement} element
 * @return {Object} client rect
 */
export function getBoundingClientRect (element) {
  var rect = element.getBoundingClientRect()
  // whether the IE version is lower than 11
  var isIE = navigator.userAgent.indexOf('MSIE') !== -1
  // fix ie document bounding top always 0 bug
  var rectTop = isIE && element.tagName === 'HTML'
    ? -element.scrollTop
    : rect.top

  return {
    left: rect.left,
    top: rectTop,
    right: rect.right,
    bottom: rect.bottom,
    width: rect.right - rect.left,
    height: rect.bottom - rectTop
  }
}

/* 获取当前元素的left、top偏移
*   left：元素最左侧距离文档左侧的距离
*   top:元素最顶端距离文档顶端的距离
*   right:元素最右侧距离文档右侧的距离
*   bottom：元素最底端距离文档底端的距离
*   right2：元素最左侧距离文档右侧的距离
*   bottom2：元素最底端距离文档最底部的距离
* */
export function getViewportOffset (element) {
  const doc = document.documentElement
  const box = typeof element.getBoundingClientRect !== 'undefined' ? element.getBoundingClientRect() : 0
  const scrollLeft = (window.pageXOffset || doc.scrollLeft) - (doc.clientLeft || 0)
  const scrollTop = (window.pageYOffset || doc.scrollTop) - (doc.clientTop || 0)
  const offsetLeft = box.left + window.pageXOffset
  const offsetTop = box.top + window.pageYOffset

  const left = offsetLeft - scrollLeft
  const top = offsetTop - scrollTop

  return {
    left: left,
    top: top,
    right: window.document.documentElement.clientWidth - box.width - left,
    bottom: window.document.documentElement.clientHeight - box.height - top,
    right2: window.document.documentElement.clientWidth - left,
    bottom2: window.document.documentElement.clientHeight - top
  }
}
