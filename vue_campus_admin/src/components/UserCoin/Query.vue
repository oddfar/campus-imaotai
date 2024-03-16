<template>
  <el-dialog title="小茅运&体力值" :visible.sync="dialogVisible" width="500px">
    <el-table :data="userCoinInfoList" style="width: 100%" :max-height="600" stripe :close-on-click-modal="false">
      <el-table-column prop="mobile" label="手机号" width="140" />
      <el-table-column prop="xmyNum" label="小茅运" width="100" />
      <el-table-column prop="energy" label="体力值" width="100" />
      <el-table-column prop="remark" label="备注" />
    </el-table>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="dialogVisible = false;" style="width: 100%;">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import * as imtUserService from "@/api/imt/user";
export default {
  data() {
    return {
      dialogVisible: false,
      userCoinInfoList: []
    };
  },
  methods: {
    /** 查询用户小茅运&体力值 */
    queryUserCoin(mobiles) {
      imtUserService.getUserCoin(mobiles).then(response => {
        this.userCoinInfoList = response?.data || []
        this.dialogVisible = true
      })
    },
  },
};
</script>

<style lang="scss" scoped></style>