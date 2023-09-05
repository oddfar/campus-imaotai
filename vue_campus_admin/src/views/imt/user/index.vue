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

      <el-form-item label="到期时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
        >搜索
        </el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
        >重置
        </el-button
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
        >添加账号
        </el-button
        >
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >直接新增
        </el-button
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
        >修改
        </el-button
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
        >删除
        </el-button
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
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="手机号">
              <span>{{ props.row.mobile }}</span>
            </el-form-item>
            <el-form-item label="I茅台用户id">
              <span>{{ props.row.userId }}</span>
            </el-form-item>
            <el-form-item label="备注">
              <span>{{ props.row.remark }}</span>
            </el-form-item>
            <el-form-item label="i茅台token">
              <span v-if="props.row.token">{{
                  props.row.token.substring(0, 5) + "......"
                }}</span>
            </el-form-item>
            <el-form-item label="i茅台cookie">
              <span v-if="props.row.cookie">{{
                  props.row.cookie.substring(0, 5) + "......"
                }}</span>
            </el-form-item>
            <el-form-item label="pulsh推送token">
              <span v-if="props.row.pushPlusToken">{{
                  props.row.pushPlusToken.substring(0, 5) + "......"
                }}</span>
            </el-form-item>
            <el-form-item label="设备id">
              <span>{{ props.row.deviceId }}</span>
            </el-form-item>
            <el-form-item label="预约项目code">
              <span>{{ props.row.itemCode }}</span>
            </el-form-item>
            <el-form-item label="省份">
              <span>{{ props.row.provinceName }}</span>
            </el-form-item>
            <el-form-item label="城市">
              <span>{{ props.row.cityName }}</span>
            </el-form-item>
            <el-form-item label="纬度">
              <span>{{ props.row.lat }}</span>
            </el-form-item>
            <el-form-item label="经度">
              <span>{{ props.row.lng }}</span>
            </el-form-item>
            <el-form-item label="经度">
              <span>{{ props.row.lng }}</span>
            </el-form-item>
            <el-form-item label="随机预约">
              <dict-tag
                :options="dict.type.sys_normal_disable"
                :value="props.row.randomMinute"
              />
            </el-form-item>
            <el-form-item label="创建时间">
              <span>{{ props.row.createTime }}</span>
            </el-form-item>
            <el-form-item label="到期时间">
              <span>{{ props.row.expireTime }}</span>
            </el-form-item>
            <el-form-item label="创建人">
              <span>{{ props.row.createUser }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>

      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="手机号" align="center" prop="mobile"/>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="预约项目code" align="center" prop="itemCode"/>
      <el-table-column label="省份" align="center" prop="provinceName"/>
      <el-table-column
        label="类型"
        align="center"
        prop="shopType"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope">
          <span>{{
              scope.row.shopType == 1 ? "预约出货量最大门店" : "预约附近门店"
            }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预约执行分钟" align="center" prop="minute"/>

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
          >预约
          </el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-thumb"
            @click="travelReward(scope.row)"
          >旅行
          </el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改
          </el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleUpdateToken(scope.row)"
          >
            刷新token
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除
          </el-button
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
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item v-if="toAdd != 1" label="手机号" prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入I茅台用户手机号"/>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" placeholder="请输入备注"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户id" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入I茅台用户id"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="toekn" prop="token">
          <el-input v-model="form.token" placeholder="请输入I茅台toekn"/>
        </el-form-item>
        <el-form-item label="cookie" prop="cookie">
          <el-input v-model="form.cookie" placeholder="请输入I茅台cookie"/>
        </el-form-item>
        <el-form-item label="设备id" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备id"/>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="预约code" prop="itemCode">
              <!-- <el-input
                v-model="form.itemCode"
                placeholder="请输入商品预约code，用@间隔"
              /> -->
              <el-select
                v-model="itemSelect"
                multiple
                placeholder="请选择"
                @change="changeItem"
              >
                <el-option
                  v-for="item in itemList"
                  :key="item.itemCode"
                  :label="item.title"
                  :value="item.itemCode"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分钟" prop="minute">
              <el-input
                v-model="form.minute"
                placeholder="预约执行的时间(单位分)，例如15分执行"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="随机时间预约" prop="randomMinute">
              <el-select
                style="width: 150px"
                v-model="form.randomMinute"
                placeholder="随机时间预约"
              >
                <el-option
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item
            >
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型" prop="shopType">
              <el-select v-model="form.shopType" placeholder="请选择">
                <el-option
                  v-for="item in typeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- <el-row>
          <el-col :span="12">
            <el-form-item label="省份" prop="provinceName">
              <el-input
                v-model="form.provinceName"
                placeholder="请输入省份"
              /> </el-form-item
          ></el-col>
          <el-col :span="12">
            <el-form-item label="城市" prop="cityName">
              <el-input
                v-model="form.cityName"
                placeholder="请输入城市"
              /> </el-form-item
          ></el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="纬度" prop="lat">
              <el-input v-model="form.lat" placeholder="请输入纬度" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经度" prop="lng">
              <el-input v-model="form.lng" placeholder="请输入经度" />
            </el-form-item>
          </el-col>
        </el-row> -->
        <el-row>
          <el-col :span="12">
            <el-form-item label="门店商品ID" prop="ishopId">
              <el-input v-model="form.ishopId" placeholder="请输入门店商品ID"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="推送token" prop="pushPlusToken">
              <el-input
                v-model="form.pushPlusToken"
                placeholder="请输入推送token"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- <el-form-item label="完整地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入完整地址" />
        </el-form-item> -->

        <el-form-item label="到期时间" prop="expireTime">
          <el-date-picker
            v-model="form.expireTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间"
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
      <el-form ref="form" :model="form">
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入I茅台用户手机号"/>
          <div style="margin-top: 10px">
            <el-button
              type="primary"
              @click="sendCode(form.mobile)"
              :disabled="state"
            >发送验证码<span v-if="state">({{ stateNum }})</span>
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="验证码" prop="userId">
          <el-input v-model="form.code" placeholder="请输入验证码"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="login(form.mobile, form.code)"
        >登 录
        </el-button
        >
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="refreshToken" width="500px">
      <el-form ref="form" :model="form">
        <el-form-item label="手机号" prop="mobile">
          <el-input
            v-model="form.mobile"
            readonly
            placeholder="请输入I茅台用户手机号"
          />
          <div style="margin-top: 10px">
            <el-button
              type="primary"
              @click="sendCode(form.mobile, form.deviceId)"
              :disabled="state"
            >发送验证码<span v-if="state">({{ stateNum }})</span>
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="验证码" prop="code">
          <el-input v-model="form.code" placeholder="请输入验证码"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          @click="refresh(form.mobile, form.code, form.deviceId, 1)"
        >刷 新
        </el-button>
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
  travelReward,
} from "@/api/imt/user";

import {listItem} from "@/api/imt/item";

export default {
  name: "User",
  dicts: ["sys_normal_disable"],
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
      // 日期范围
      dateRange: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      openUser: false,
      refreshToken: false,
      // 发送短信按钮倒计时
      state: false,
      stateNum: 60,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        mobile: null,
        userId: null,
        token: null,
        itemCode: null,
        deviceId: null,
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
          {required: true, message: "手机号不能为空", trigger: "blur"},
        ],
      },
      //0:新增，1:修改
      toAdd: 0,
      typeOptions: [
        {
          value: 1,
          label: "预约本市出货量最大的门店",
        },
        {
          value: 2,
          label: "预约你的位置(经纬度)附近门店",
        },
      ],
      // I茅台预约商品列表格数据
      itemList: [],
      //选择的数据
      itemSelect: [],
    };
  },
  created() {
    this.getList();
    listItem().then((response) => {
      this.itemList = response.data;
    });
  },
  methods: {
    //item下拉框选择
    changeItem(e) {
      this.form.itemCode = "";
      this.itemSelect.forEach((e) => {
        this.form.itemCode += e + "@";
      });
    },
    guid() {
      return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(
        /[xy]/g,
        function (c) {
          var r = (Math.random() * 16) | 0,
            v = c == "x" ? r : (r & 0x3) | 0x8;
          return v.toString(16);
        }
      );
    },
    /** 查询I茅台用户列表 */
    getList() {
      this.loading = true;
      listUser(this.addDateRange(this.queryParams, this.dateRange)).then(
        (response) => {
          this.userList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.openUser = false;
      this.refreshToken = false;
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
        jsonResult: null,
        createTime: null,
        minute: 5,
        shopType: 1,
        ishopId: null,
        randomMinute: "0",
        remark: null,
        expireTime: null,
        pushPlusToken: null,
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
      this.dateRange = [];
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
      this.itemSelect = [];
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
        this.itemSelect = [];
        if (
          this.form.itemCode.indexOf("@") == -1 &&
          this.form.itemCode !== ""
        ) {
          this.itemSelect.push(this.form.itemCode);
        } else {
          let arr = this.form.itemCode.split("@");
          arr.forEach((e) => {
            if (e !== "") {
              this.itemSelect.push(e);
            }
          });
        }
      });
    },
    //预约
    reservation(row) {
      const mobile = row.mobile || this.ids;
      reservation(mobile).then((response) => {
        this.$modal.msgSuccess("请求成功，结果看日志");
      });
    },
    //小茅运旅行活动
    travelReward(row) {
      const mobile = row.mobile || this.ids;
      travelReward(mobile).then((response) => {
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
    sendCode(mobile, deviceId) {
      if (deviceId == undefined || deviceId == "") {
        this.form.deviceId = this.guid();
      } else {
        this.form.deviceId = deviceId;
      }
      sendCode(mobile, this.form.deviceId).then((response) => {
        this.$modal.msgSuccess("发送成功");
        this.state = true;
        let timer = setInterval(() => {
          this.stateNum--;
          if (this.stateNum === 0) {
            clearInterval(timer);
            this.state = false;
            this.stateNum = 60;
          }
        }, 1000);
      });
    },
    //登录
    login(mobile, code) {
      this.refresh(mobile, code, this.form.deviceId, 0);
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
        .catch(() => {
        });
    },
    refresh(mobile, code, deviceId, status) {
      const msg = status ? "刷新成功" : "登录成功";
      login(mobile, code, deviceId).then((response) => {
        this.$modal.msgSuccess(msg);
        this.open = false;
        this.openUser = false;
        this.refreshToken = false;
        this.getList();
      });
    },
    handleUpdateToken(row) {
      this.refreshToken = true;
      this.form = {
        mobile: row.mobile,
        deviceId: row.deviceId,
      };
      this.title = "刷新用户:" + row.remark + "(" + row.mobile + ")登录信息";
    },
  },
};
</script>
<style>
.demo-table-expand {
  font-size: 0;
}

.demo-table-expand label {
  width: 120px;
  color: #99a9bf;
}

.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
</style>
