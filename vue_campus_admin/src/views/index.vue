<template>
  <div class="app-container home">
    <p>
      本项目中所有内容只供学习和研究使用，不得将本项目中任何内容用于违反国家/地区/组织等的法律法规或相关规定的其他用途。
    </p>
    <p>
      所有直接或间接使用本项目的个人和组织，应24小时内完成学习和研究，并及时删除本项目中的所有内容。如对本项目的功能有需求，应自行开发相关功能。
    </p>
    <!-- 教程链接 -->
    <div class="tutorial-link">
      <p>查看教程文档，请访问：<a href="https://richy-help.daijc.cn/imaotai_auto.html" target="_blank">打开教程</a></p>
    </div>
    <p></p>
<!--    <el-card class="box-card">
      <p>版本情况:</p>
      <p>campus-imaotai:{{ version }}</p>
      <p>campus框架:{{ frameworkVersion }}</p>
    </el-card>-->
    <!-- 添加二维码部分 -->
    <div class="wechat-qrcode">
      <p>加入我们的微信群：</p>
      <img src="https://richy-image.daijc.cn/help/image/wx/qun.jpg" alt="微信群二维码"/>
    </div>
  </div>
</template>

<script>
import { getVersion } from '@/api/system/index'
import * as https from 'https'

export default {
  name: 'Index',
  computed: {
    https() {
      return https
    }
  },
  data() {
    return {
      // 版本号
      version: '',
      frameworkVersion: ''
    }
  },
  created() {
    this.getVersion()
  },
  methods: {
    getVersion() {
      getVersion().then((response) => {
          this.version = response.data.version
          this.frameworkVersion = response.data.frameworkVersion
        }
      )
    }
  }
}
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }

  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }

  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }

  /* 添加一些样式使二维码看起来更好 */
  .wechat-qrcode {
    text-align: center; /* 居中显示 */
    margin-top: 20px; /* 与上方内容有一定间距 */
  }

  .wechat-qrcode img {
    max-width: 200px; /* 设置一个最大宽度 */
    height: auto; /* 图片高度自动 */
  }

  .tutorial-link p {
    color: #4CAF50; /* 更新文字颜色为较深的绿色，以提高可读性 */
    box-shadow: 0 2px 4px rgba(0,0,0,0.2); /* 稍微增强阴影效果，增加深度感 */
    padding: 10px; /* 内边距 */
    border-radius: 5px; /* 边框圆角 */
  }

  .tutorial-link a {
    color: #009688; /* 设置链接默认颜色为青绿色，以增加视觉吸引力 */
    text-decoration: underline; /* 保留下划线，提高链接的可识别性 */
  }

  .tutorial-link a:hover {
    color: #00695C; /* 鼠标悬停时的链接颜色调整为更深的青绿色，增加交互性 */
    text-decoration: none; /* 鼠标悬停时去除下划线，平滑视觉效果 */
  }

}
</style>

