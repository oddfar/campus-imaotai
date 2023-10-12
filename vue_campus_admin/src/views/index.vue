<template>
  <div class="app-container home">
    <p>
      本项目中所有内容只供学习和研究使用，不得将本项目中任何内容用于违反国家/地区/组织等的法律法规或相关规定的其他用途。
    </p>
    <p>
      所有直接或间接使用本项目的个人和组织，应24小时内完成学习和研究，并及时删除本项目中的所有内容。如对本项目的功能有需求，应自行开发相关功能。
    </p>
    <p></p>
    <p>本项目免费，无任何盈利</p>
    <p>项目完全开源，更新地址：https://github.com/oddfar/campus-imaotai</p>

    <el-card class="box-card">
      <p>版本情况:</p>
      <p>campus-imaotai:{{ version }}</p>
      <p>campus框架:{{ frameworkVersion }}</p>
    </el-card>
  </div>
</template>

<script>
import {getVersion} from "@/api/system/index";

export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "",
      frameworkVersion: "",
    };
  },
  created() {
    this.getVersion();
  },
  methods: {
    getVersion() {
      getVersion().then((response) => {
          this.version = response.data.version;
          this.frameworkVersion = response.data.frameworkVersion;
        }
      );
    }
  },
};
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
}
</style>

