<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="手机号" prop="mobile">
        <el-input
          v-model="queryParams.mobile"
          placeholder="请输入I茅台手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户id" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入I茅台用户id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!-- <el-form-item label="toekn" prop="token">
        <el-input
          v-model="queryParams.token"
          placeholder="请输入用户toekn"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <!-- <el-form-item label="商品预约code，用@间隔" prop="itemCode">
        <el-input
          v-model="queryParams.itemCode"
          placeholder="请输入商品预约code，用@间隔"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <el-form-item label="省份" prop="provinceName">
        <el-input
          v-model="queryParams.provinceName"
          placeholder="请输入省份"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="城市" prop="cityName">
        <el-input
          v-model="queryParams.cityName"
          placeholder="请输入城市"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!-- <el-form-item label="完整地址" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入完整地址"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <!-- <el-form-item label="纬度" prop="lat">
        <el-input
          v-model="queryParams.lat"
          placeholder="请输入纬度"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="经度" prop="lng">
        <el-input
          v-model="queryParams.lng"
          placeholder="请输入经度"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      <!-- <el-form-item label="到期时间" prop="expireTime">
        <el-date-picker
          clearable
          v-model="queryParams.expireTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择到期时间"
        >
        </el-date-picker>
      </el-form-item> -->
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAddIUser"
          v-hasPermi="['imt:user:add']"
          >添加账号</el-button
        >
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['imt:user:add']"
          >直接新增</el-button
        >
      </el-col>

      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['imt:user:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['imt:user:remove']"
          >删除</el-button
        >
      </el-col>

      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="userList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="手机号" align="center" prop="mobile" />
      <el-table-column label="I茅台用户id" align="center" prop="userId" />
      <el-table-column
        label="toekn"
        align="center"
        prop="token"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="预约项目code" align="center" prop="itemCode" />
      <el-table-column label="省份" align="center" prop="provinceName" />
      <el-table-column label="城市" align="center" prop="cityName" />
      <!-- <el-table-column label="完整地址" align="center" prop="address" /> -->
      <el-table-column label="纬度" align="center" prop="lat" />
      <el-table-column label="经度" align="center" prop="lng" />
      <el-table-column label="类型" align="center" prop="shopType" />
      <!-- <el-table-column label="返回参数" align="center" prop="jsonResult" /> -->
      <el-table-column
        label="到期时间"
        align="center"
        prop="expireTime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expireTime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-thumb"
            @click="reservation(scope.row)"
            v-hasPermi="['imt:user:edit']"
            >预约</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['imt:user:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['imt:user:remove']"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改I茅台用户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item v-if="toAdd != 1" label="手机号" prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入I茅台用户手机号" />
        </el-form-item>
        <el-form-item label="用户id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入I茅台用户id" />
        </el-form-item>
        <el-form-item label="toekn" prop="token">
          <el-input v-model="form.token" placeholder="请输入I茅台toekn" />
        </el-form-item>
        <el-form-item label="预约code" prop="itemCode">
          <el-input
            v-model="form.itemCode"
            placeholder="请输入商品预约code，用@间隔"
          />
        </el-form-item>
        <el-form-item label="省份" prop="provinceName">
          <el-input v-model="form.provinceName" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="cityName">
          <el-input v-model="form.cityName" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="类型" prop="shopType">
          <el-input
            v-model="form.shopType"
            placeholder="1:预约本市出货量最大的门店;2:预约你的位置(经纬度)附近门店;"
          />
        </el-form-item>
        <!-- <el-form-item label="完整地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入完整地址" />
        </el-form-item> -->
        <el-form-item label="纬度" prop="lat">
          <el-input v-model="form.lat" placeholder="请输入纬度" />
        </el-form-item>
        <el-form-item label="经度" prop="lng">
          <el-input v-model="form.lng" placeholder="请输入经度" />
        </el-form-item>
        <!-- <el-form-item label="返回参数" prop="jsonResult">
          <el-input v-model="form.jsonResult" type="textarea" placeholder="请输入内容" />
        </el-form-item> -->
        <el-form-item label="到期时间" prop="expireTime">
          <el-date-picker
            clearable
            v-model="form.expireTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择到期时间"
          >
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="添加\更新用户"
      :visible.sync="openUser"
      width="500px"
      append-to-body
    >
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入I茅台用户手机号" />
          <div style="margin-top: 10px">
            <el-button type="primary" @click="sendCode(form.mobile)"
              >发送验证码</el-button
            >
          </div>
        </el-form-item>

        <el-form-item label="验证码" prop="userId">
          <el-input v-model="form.code" placeholder="请输入验证码" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="login(form.mobile, form.code)"
          >登 录</el-button
        >
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listUser,
  getUser,
  delUser,
  addUser,
  updateUser,
  sendCode,
  login,
  reservation,
} from "@/api/imt/user";

export default {
  name: "User",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // I茅台用户表格数据
      userList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      openUser:false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        mobile: null,
        userId: null,
        token: null,
        itemCode: null,
        provinceName: null,
        cityName: null,
        address: null,
        lat: null,
        lng: null,
        shopType: null,
        jsonResult: null,
        expireTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        mobile: [
          { required: true, message: "手机号不能为空", trigger: "blur" },
        ],
      },
      //0:新增，1:修改
      toAdd: 0,
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询I茅台用户列表 */
    getList() {
      this.loading = true;
      listUser(this.queryParams).then((response) => {
        this.userList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.openUser = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        mobile: null,
        userId: null,
        token: null,
        itemCode: null,
        provinceName: null,
        cityName: null,
        address: null,
        lat: null,
        lng: null,
        shopType: null,
        jsonResult: null,
        createTime: null,
        expireTime: null,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.mobile);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加I茅台用户";
      this.toAdd = 0;
    },
    handleAddIUser() {
      this.reset();
      this.openUser = true;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const mobile = row.mobile || this.ids;
      getUser(mobile).then((response) => {
        this.toAdd = 1;
        this.form = response.data;
        this.open = true;
        this.title = "修改I茅台用户";
      });
    },
    //预约
    reservation(row) {
      const mobile = row.mobile || this.ids;
      reservation(mobile).then((response) => {
        this.$modal.msgSuccess("请求成功，结果看日志");
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.toAdd != 0) {
            updateUser(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addUser(this.form).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    //发生验证码
    sendCode(mobile) {
      sendCode(mobile).then((response) => {
        this.$modal.msgSuccess("发送成功");
      });
    },
    //登录
    login(mobile, code) {
      login(mobile, code).then((response) => {
        this.$modal.msgSuccess("登录成功");
        this.getList();
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const mobiles = row.mobile || this.ids;
      this.$modal
        .confirm('是否确认删除I茅台用户编号为"' + mobiles + '"的数据项？')
        .then(function () {
          return delUser(mobiles);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
  },
};
</script>
