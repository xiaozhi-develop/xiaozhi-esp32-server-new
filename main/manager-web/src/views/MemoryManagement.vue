<template>
  <div class="welcome">
    <HeaderBar />
    <div class="operation-bar">
      <h2 class="page-title">记忆体管理</h2>
      <div class="right-operations">
        <el-button type="success" @click="showAddDialog">新增记忆体</el-button>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card class="template-card" shadow="never">
            <el-table :data="list" style="width: 100%" v-loading="loading" class="transparent-table">
              <el-table-column label="名称" prop="name" min-width="150"></el-table-column>
              <el-table-column label="记忆模型" prop="memModelId" min-width="150">
                 <template slot-scope="scope">
                    {{ getModelName(scope.row.memModelId) }}
                 </template>
              </el-table-column>
              <el-table-column label="操作" min-width="150" align="center">
                <template slot-scope="scope">
                  <el-button type="text" @click="edit(scope.row)">编辑</el-button>
                  <el-button type="text" @click="del(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </div>
    </div>

    <!-- Dialog -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="记忆模型">
          <el-select v-model="form.memModelId" placeholder="请选择记忆模型">
             <el-option
                v-for="item in memoryModels"
                :key="item.id"
                :label="item.modelName"
                :value="item.id">
              </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="总结记忆">
          <el-input type="textarea" v-model="form.summaryMemory" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import HeaderBar from "@/components/HeaderBar";
import Api from "@/apis/api";

export default {
  name: "MemoryManagement",
  components: { HeaderBar },
  data() {
    return {
      list: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: "",
      form: {
        id: null,
        name: "",
        memModelId: "",
        summaryMemory: ""
      },
      memoryModels: []
    };
  },
  created() {
    this.loadList();
    this.loadMemoryModels();
  },
  methods: {
    loadList() {
      this.loading = true;
      Api.memory.getMemoryList((res) => {
        if (res.data.code === 0) {
          this.list = res.data.data;
        }
        this.loading = false;
      });
    },
    loadMemoryModels() {
      Api.model.getModelNames('memory', '', (res) => {
        if (res.data.code === 0) {
          this.memoryModels = res.data.data;
        }
      });
    },
    getModelName(id) {
        const model = this.memoryModels.find(m => m.id === id);
        return model ? model.modelName : id;
    },
    showAddDialog() {
      this.dialogTitle = "新增记忆体";
      this.form = { id: null, name: "", memModelId: "", summaryMemory: "" };
      this.dialogVisible = true;
    },
    edit(row) {
      this.dialogTitle = "编辑记忆体";
      this.form = { ...row };
      this.dialogVisible = true;
    },
    submit() {
      const api = this.form.id ? Api.memory.updateMemory : Api.memory.saveMemory;
      // Note: saveMemory and updateMemory in memory.js need to be bound to the object if we extract them like this,
      // or we just call them directly.
      // However, in memory.js I used `this.saveMemory` in reAjaxFun, so `this` context matters if reAjaxFun is triggered.
      // But here I am calling `api(this.form, callback)`.
      // Let's just call the method directly to be safe.
      
      const callback = (res) => {
        if (res.data.code === 0) {
          this.$message.success("操作成功");
          this.dialogVisible = false;
          this.loadList();
        } else {
          this.$message.error(res.data.msg);
        }
      };

      if (this.form.id) {
        Api.memory.updateMemory(this.form, callback);
      } else {
        Api.memory.saveMemory(this.form, callback);
      }
    },
    del(row) {
      this.$confirm("确认删除?", "提示", { type: "warning" }).then(() => {
        Api.memory.deleteMemory([row.id], (res) => {
          if (res.data.code === 0) {
            this.$message.success("删除成功");
            this.loadList();
          } else {
            this.$message.error(res.data.msg);
          }
        });
      });
    }
  }
};
</script>
<style scoped lang="scss">
.welcome {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  background: linear-gradient(to bottom right, #dce8ff, #e4eeff, #e6cbfd) center;
  background-size: cover;
  overflow: hidden;
  width: 100%;
}
.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
}
.page-title {
  font-size: 24px;
  margin: 0;
}
.main-wrapper {
  margin: 5px 22px;
  border-radius: 15px;
  min-height: calc(100vh - 24vh);
  height: auto;
  max-height: 80vh;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  background: rgba(237, 242, 255, 0.5);
  display: flex;
  flex-direction: column;
}
.content-panel {
  width: 100%;
  flex: 1;
  display: flex;
  overflow: hidden;
  border-radius: 15px;
  background: transparent;
  border: 1px solid #fff;
}
.content-area {
  flex: 1;
  min-width: 600px;
  overflow-x: auto;
  background-color: white;
  display: flex;
  flex-direction: column;
  position: relative;
}
.template-card {
  border: none;
  box-shadow: none;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.transparent-table {
  width: 100%;
  flex: 1;
  min-height: 0;
}
</style>
