<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleRefresh"
          >刷新i茅台预约商品列表</el-button
        >
      </el-col>

      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="itemList">
      <!-- <el-table-column label="id" align="center" prop="itemId" /> -->
      <el-table-column label="预约商品code" align="center" prop="itemCode" />
      <el-table-column label="标题" align="center" prop="title" />
      <el-table-column
        label="内容"
        align="center"
        prop="content"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="图片" align="center" prop="picture">
        <template slot-scope="scope">
          <el-image
            style="width: 100px; "
            :src="scope.row.picture"
            fit="fit"
          ></el-image>
        </template>
      </el-table-column>
      <el-table-column
        label="创建时间"
        align="center"
        prop="createTime"
        width="180"
      />
    </el-table>
  </div>
</template>

<script>
import { listItem, refreshItem } from "@/api/imt/item";

export default {
  name: "Item",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],

      // 显示搜索条件
      showSearch: true,
      // I茅台预约商品列表格数据
      itemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,

      // 表单参数
      form: {},
      // 表单校验
      rules: {},
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询I茅台预约商品列列表 */
    getList() {
      this.loading = true;
      listItem().then((response) => {
        this.itemList = response.data;
        this.loading = false;
      });
    },

    /** 刷新i茅台预约商品列表 */
    handleRefresh() {
      refreshItem().then(() => {
        this.getList();
        this.$modal.msgSuccess("刷新成功");
      });
    },
  },
};
</script>
