<template>
  <el-dialog :visible="visible" @close="handleClose" width="24%" center append-to-body>
    <!-- Header -->
    <div style="margin: 0 10px 10px;display: flex;align-items: center;gap: 10px;font-weight: 700;font-size: 20px;text-align: left;color: #3d4566;">
      <div style="width: 40px;height: 40px;border-radius: 50%;background: #5778ff;display: flex;align-items: center;justify-content: center;">
        <img src="@/assets/home/setting-user.png" alt="" style="width: 18px;height: 18px;" />
      </div>
      {{ $t('agentConfig.title') }}
    </div>
    <div style="height: 1px;background: #e8f0ff;" />

    <!-- Content -->
    <div style="margin: 22px 15px;">
      <!-- Select Agent -->
      <div style="font-weight: 400;font-size: 14px;text-align: left;color: #3d4566;margin-bottom: 12px;">
        <span style="font-size: 11px">{{ $t('agentConfig.selectTemplate') }}</span>
      </div>
      <div class="input-46">
        <el-select v-model="selectedAgentId" @change="handleAgentChange" style="width: 100%" filterable :placeholder="$t('agentConfig.selectTemplate')">
          <el-option
            v-for="item in agentList"
            :key="item.id"
            :label="item.agentName"
            :value="item.id"
          >
          </el-option>
          <!-- Create New Agent Option -->
          <el-option value="NEW_AGENT" :label="'+ ' + $t('home.addAgent')" style="color: #5778ff; font-weight: bold; border-top: 1px solid #eee;"></el-option>
        </el-select>
      </div>

      <!-- Advanced Config Link -->
      <div style="margin-top: 20px; text-align: right;">
        <el-link type="primary" @click="goToRoleConfig" :underline="false" style="font-size: 12px;">
          {{ $t('agentConfig.advancedConfig') }} <i class="el-icon-d-arrow-right"></i>
        </el-link>
      </div>
    </div>

    <!-- Footer Buttons -->
    <div style="display: flex;margin: 15px 15px;gap: 7px;">
      <div class="dialog-btn" @click="handleSave">
        {{ $t('common.confirm') }}
      </div>
      <div class="dialog-btn" style="background: #e6ebff;border: 1px solid #adbdff;color: #5778ff;" @click="handleClose">
        {{ $t('common.cancel') }}
      </div>
    </div>

    <!-- Add Agent Dialog -->
    <AddWisdomBodyDialog :visible.sync="addAgentVisible" @confirm="onAgentAdded" />
  </el-dialog>
</template>

<script>
import agentApi from '@/apis/module/agent';
import deviceApi from '@/apis/module/device';
import AddWisdomBodyDialog from '@/components/AddWisdomBodyDialog.vue';

export default {
  name: 'AgentConfigDialog',
  components: {
    AddWisdomBodyDialog
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    agentId: {
      type: String,
      required: true
    },
    deviceId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      loading: false,
      saving: false,
      agentList: [],
      selectedAgentId: '',
      addAgentVisible: false,
    };
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      }
    }
  },
  created() {
    this.fetchAgents();
    // Pre-select the current agent
    this.selectedAgentId = this.agentId;
  },
  methods: {
    fetchAgents() {
      agentApi.getAgentList((res) => {
        if (res && res.data && res.data.code === 0) {
          this.agentList = res.data.data || [];
        }
      });
    },
    handleAgentChange(val) {
      if (val === 'NEW_AGENT') {
        // Reset selection to avoid showing "NEW_AGENT" as selected value if user cancels
        this.selectedAgentId = this.agentId; 
        this.addAgentVisible = true;
      }
    },
    onAgentAdded(res) {
      // Refresh agent list and select the new agent
      this.fetchAgents();
      if (res && res.data && res.data.data) {
        // Assuming the response contains the new agent ID in data.data
        // Check the structure of addAgent response. Usually it returns the ID or the object.
        // Based on addAgent in agent.js, it returns res.
        // Let's assume res.data.data is the ID or object with ID.
        // If it's just ID:
        const newAgentId = res.data.data.id || res.data.data; 
        this.selectedAgentId = newAgentId;
      }
    },
    handleSave() {
      if (!this.selectedAgentId) {
        this.$message.warning(this.$t('agentConfig.selectTemplate'));
        return;
      }

      // If the selected agent is the same as current, maybe just close?
      // But user might want to confirm binding even if same.
      
      this.saving = true;
      
      // Update device info to bind to the new agent
      deviceApi.updateDeviceInfo(this.deviceId, { agentId: this.selectedAgentId }, (ok, res) => {
        this.saving = false;
        if (ok) {
          this.$message.success(this.$t('common.saveSuccess'));
          this.handleClose();
          this.$emit('refresh');
        } else {
          this.$message.error(res?.msg || this.$t('common.saveFailed'));
        }
      });
    },
    handleClose() {
      this.$emit('update:visible', false);
    },
    goToRoleConfig() {
      this.handleClose();
      this.$router.push({
        path: '/role-config',
        query: { agentId: this.selectedAgentId || this.agentId }
      });
    }
  }
};
</script>

<style scoped>
.input-46 {
  border: 1px solid #e4e6ef;
  background: #f6f8fb;
  border-radius: 10px;
  padding: 2px;
}

.dialog-btn {
  cursor: pointer;
  flex: 1;
  border-radius: 23px;
  background: #5778ff;
  height: 40px;
  font-weight: 500;
  font-size: 12px;
  color: #fff;
  line-height: 40px;
  text-align: center;
}

::v-deep .el-dialog {
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

::v-deep .el-dialog__headerbtn {
  display: none;
}

::v-deep .el-dialog__body {
  padding: 4px 6px;
}

::v-deep .el-dialog__header {
  padding: 10px;
}

::v-deep .el-input__inner {
  background-color: transparent !important;
  border: none !important;
}
</style>
